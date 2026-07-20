package com.example.gestao_vagas.modules.logs.filters;

import com.example.gestao_vagas.modules.logs.entities.AccessLogEntity;
import com.example.gestao_vagas.modules.logs.services.AccessLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public class RequestLogFilter extends OncePerRequestFilter {

    private static final Pattern UUID_PATH_SEGMENT = Pattern.compile(
            "(?i)/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}(?=/|$)");
    private static final String[] OBSERVABILITY_PATHS = {
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources"
    };

    private final AccessLogService accessLogService;

    public RequestLogFilter(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        var requestUri = request.getRequestURI();

        for (var path : OBSERVABILITY_PATHS) {
            if (requestUri.startsWith(path)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        var startedAt = System.currentTimeMillis();
        var requestId = UUID.randomUUID().toString();
        Exception requestException = null;

        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException exception) {
            requestException = exception;
            throw exception;
        } finally {
            saveAccessLog(request, response, requestId, startedAt, requestException);
        }
    }

    private void saveAccessLog(
            HttpServletRequest request,
            HttpServletResponse response,
            String requestId,
            long startedAt,
            Exception requestException) {

        var status = requestException == null ? response.getStatus() : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        var actorType = resolveActorType(request);

        var accessLog = AccessLogEntity.builder()
                .requestId(requestId)
                .method(request.getMethod())
                .path(sanitizePath(request.getRequestURI()))
                .status(status)
                .durationMs(System.currentTimeMillis() - startedAt)
                .actorType(actorType)
                .authenticated(!"ANONYMOUS".equals(actorType))
                .outcome(status >= 400 ? "ERROR" : "SUCCESS")
                .createdAt(Instant.now())
                .build();

        accessLogService.save(accessLog);
    }

    private String resolveActorType(HttpServletRequest request) {
        if (request.getAttribute("candidate_id") != null) {
            return "CANDIDATE";
        }

        if (request.getAttribute("company_id") != null) {
            return "COMPANY";
        }

        return "ANONYMOUS";
    }

    private String sanitizePath(String requestUri) {
        return UUID_PATH_SEGMENT.matcher(requestUri).replaceAll("/{uuid}");
    }
}
