package com.example.gestao_vagas.modules.candidate.entity;

import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.company.entities.JobEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "apply_jobs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", insertable = false, updatable = false)
    private CandidateEntity candidate;

    @ManyToOne
    @JoinColumn(name = "Job_id", insertable = false, updatable = false)
    private JobEntity jobEntity;

    private UUID candidateId;

    private UUID jobId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
