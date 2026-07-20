package com.example.gestao_vagas.modules.logs.services;

import com.example.gestao_vagas.modules.logs.entities.AccessLogEntity;
import com.example.gestao_vagas.modules.logs.repositories.AccessLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccessLogService {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogService.class);

    private final AccessLogRepository accessLogRepository;
    private final boolean enabled;

    public AccessLogService(
            AccessLogRepository accessLogRepository,
            @Value("${application.logs.http.enabled:true}") boolean enabled) {
        this.accessLogRepository = accessLogRepository;
        this.enabled = enabled;
    }

    public void save(AccessLogEntity accessLogEntity) {
        if (!enabled) {
            return;
        }

        try {
            accessLogRepository.save(accessLogEntity);
        } catch (Exception exception) {
            logger.warn("HTTP access log was not persisted: {}", exception.getMessage());
        }
    }
}
