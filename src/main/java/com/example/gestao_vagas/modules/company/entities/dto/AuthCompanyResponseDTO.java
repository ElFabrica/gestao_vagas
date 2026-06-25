package com.example.gestao_vagas.modules.company.entities.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthCompanyResponseDTO {
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJqYXZhZ2FzIiwic3ViIjoiMzRiODU2MjgtOWE4OC00ZmM1LWJhMWEtMjc2MmI0MjZjNTgwIiwicm9sZXMiOlsiQ0FORElEQVRFIl0sImV4cCI6MTc4MjM1NTUxN30.7zRHRdtapQWhbFXTJdm4LsHIyaCMK-oMGL0XSXsgEtA")
    private String access_token;
    @Schema(example = "1782355517851")
    private Long expires_in;
}
