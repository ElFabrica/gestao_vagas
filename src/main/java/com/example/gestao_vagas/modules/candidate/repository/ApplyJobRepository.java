package com.example.gestao_vagas.modules.candidate.repository;

import com.example.gestao_vagas.modules.candidate.entity.ApplyJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplyJobRepository extends JpaRepository<ApplyJobEntity, UUID> {
    Optional<ApplyJobEntity> findByJobIdAndCandidateId(UUID jobId, UUID candidateId);
}
