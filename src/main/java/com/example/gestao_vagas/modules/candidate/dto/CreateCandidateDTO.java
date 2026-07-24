package com.example.gestao_vagas.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CreateCandidateDTO(
        @Schema(example = "Arthur Fabricyo")
        String name,
        @NotBlank
        @Pattern(regexp = "\\S+", message = "O campo [username] não deve conter espaço")
        @Schema(example = "ElFabrica", description = "username do usuário")
        String username,
        @Email(message = "O campo [email] deve conter um e-mail valido")
        @Schema(example = "arthur.fabricyo@gmail.com")
        String email,
        @Length(min = 10, max = 100)
        @Schema(example = "Fala12345@", minLength = 10, maxLength = 100)
        String password,
        @Schema(example = "Sou dev full stack")
        String description
) {
}
