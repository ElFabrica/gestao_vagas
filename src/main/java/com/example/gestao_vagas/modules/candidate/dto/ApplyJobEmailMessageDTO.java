package com.example.gestao_vagas.modules.candidate.dto;

import java.io.Serializable;
import java.util.UUID;

public record ApplyJobEmailMessageDTO(
        UUID candidateId,
        String candidateName,
        String candidateEmail,
        UUID jobId,
        String jobDescription,
        String companyName,
        String companyEmail

) implements Serializable {
}
