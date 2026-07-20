package com.example.gestao_vagas.modules.logs.repositories;

import com.example.gestao_vagas.modules.logs.entities.AccessLogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccessLogRepository extends MongoRepository<AccessLogEntity, String> {
}
