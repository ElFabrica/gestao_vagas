package com.example.gestao_vagas.modules.logs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "access_logs")
public class AccessLogEntity {

    @Id
    private String id;

    private String requestId;
    private String method;
    private String path;
    private Integer status;
    private Long durationMs;
    private String actorType;
    private Boolean authenticated;
    private String outcome;

    @CreatedDate
    private Instant createdAt;
}
