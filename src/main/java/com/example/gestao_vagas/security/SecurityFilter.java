package com.example.gestao_vagas.security;

import com.example.gestao_vagas.providers.JWTProviders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JWTProviders jwtProviders;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if(request.getRequestURI().startsWith("/company")){
            if(header != null) {
                var token = this.jwtProviders.validateToken(header);
                if (token == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                var roles = token.getClaim("roles").asList(Object.class);
                var grants = roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()))
                                        .toList();

                request.setAttribute("company_id", token.getSubject());
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(token, null, grants);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request,response);

    }
}
