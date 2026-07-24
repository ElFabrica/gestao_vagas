package com.example.gestao_vagas.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateCandidateResponseDTO(
        @Schema(example = "Arthur Fabricyo")
        String name,
        @Schema(example = "ElFabrica")
        String username,
        @Schema(example = "arthur.fabricyo@gmail.com")
        String email,
        @Schema(example = "Sou dev full stack")
        String description,
        UUID curriculumId,
        LocalDateTime createdAt
) {
}
