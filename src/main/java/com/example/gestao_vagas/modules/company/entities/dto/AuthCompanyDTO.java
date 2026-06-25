package com.example.gestao_vagas.modules.company.entities.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthCompanyDTO {
    @Schema(example = "Fala12345@")
    private String password;
    @Schema(example = "arthur_fabricyo")
    private String username;
}
