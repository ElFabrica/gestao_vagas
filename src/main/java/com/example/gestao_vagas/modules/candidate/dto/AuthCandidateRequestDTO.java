package com.example.gestao_vagas.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthCandidateRequestDTO(@Schema(example = "ElFabrica") String username, @Schema(example = "Fala12345@") String password) {



}
