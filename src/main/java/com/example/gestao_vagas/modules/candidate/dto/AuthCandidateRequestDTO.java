package com.example.gestao_vagas.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthCandidateRequestDTO(
        @Schema(example = "teste123") String username,
        @Schema(example = "Teste12345@") String password) {


}
