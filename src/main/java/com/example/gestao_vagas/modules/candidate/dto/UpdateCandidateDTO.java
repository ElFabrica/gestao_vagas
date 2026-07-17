package com.example.gestao_vagas.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;


public record UpdateCandidateDTO(

        @Schema(example = "viva ao teste")
        String name,
        @Pattern(
                regexp = "\\S+",
                message = "O campo [username] não deve conter spaços"
        )
        @Schema(example = "viva_ao_teste")
        String username,
        @Email(message = "teste@gmail.com")
        String email,
        @Length(min = 10, max = 100)
        @Schema(example = "senha123")
        String password,
        @Schema(example = "dev java")
        String description,
        @Schema(example = "javam spring boot, docker")
        String curriculum


) {


}
