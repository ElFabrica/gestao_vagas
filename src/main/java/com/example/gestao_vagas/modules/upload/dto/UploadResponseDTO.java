package com.example.gestao_vagas.modules.upload.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "UploadResponseDTO", description = "Metadados do arquivo enviado e URL pré-assinada de acesso")
public record UploadResponseDTO(
        @Schema(description = "Identificador do upload", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Nome original do arquivo", example = "curriculo.pdf")
        String originFilename,
        @Schema(description = "Content-Type do arquivo", example = "application/pdf")
        String ContentType,
        @Schema(description = "Tamanho do arquivo em bytes", example = "245760")
        String sizeByte,
        @Schema(description = "URL pré-assinada para download (válida por 15 minutos)")
        String url
) {
}
