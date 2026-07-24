package com.example.gestao_vagas.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCandidateResponseDTO {

    @Schema(example = "Desenvolvedor Java")
    private String description;
    @Schema(example = "maria")
    private String username;
    @Schema(example = "Maria@gmail.com")
    private String email;
    private UUID id;
    @Schema(example = "Maria")
    private String name;
    @Schema(
            description = "URL pré-assinada do currículo ativo no R2 (15 min). Null se não houver currículo ou se o arquivo não existir no storage.",
            example = "https://<account>.r2.cloudflarestorage.com/..."
    )
    private String candidateUrl;
}
