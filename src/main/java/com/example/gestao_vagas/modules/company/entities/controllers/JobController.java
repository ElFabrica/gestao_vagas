package com.example.gestao_vagas.modules.company.entities.controllers;

import com.example.gestao_vagas.modules.company.entities.JobEntity;
import com.example.gestao_vagas.modules.company.entities.dto.CreateJobDTO;
import com.example.gestao_vagas.modules.company.entities.useCases.CreateJobUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/company/job")
@Tag(name = "Vagas", description = "Criação e gestão de vagas pela empresa.")
public class JobController {

    @Autowired
    private CreateJobUseCase createCompanyUseCase;

    @PostMapping("/")
    @PreAuthorize("hasRole('COMPANY')")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(
            summary = "Cadastrar vaga",
            description = "Cria uma nova vaga vinculada à empresa autenticada (company_id do JWT)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Vaga cadastrada com sucesso.",
                    content = @Content(schema = @Schema(implementation = JobEntity.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou empresa não encontrada."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT inválido ou expirado."
            )
    })
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateJobDTO createJobDTO,
            HttpServletRequest request) {
        var companyId = request.getAttribute("company_id");

        try {
            var jobEntity = JobEntity.builder()
                    .benefits(createJobDTO.getBenefits())
                    .companyId(UUID.fromString(companyId.toString()))
                    .description(createJobDTO.getDescription())
                    .level(createJobDTO.getLevel())
                    .build();
            var result = this.createCompanyUseCase.execute(jobEntity);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
