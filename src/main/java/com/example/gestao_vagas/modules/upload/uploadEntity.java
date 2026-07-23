package com.example.gestao_vagas.modules.upload;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class uploadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originFilename;
    private String contentType;
    private Long sizeByte;
    private String storageKey;
    private String key;
    private String bucket;
    private UUID ownerId;
    private String purpose;

    @CreationTimestamp
    private LocalDate createdAt;
}
