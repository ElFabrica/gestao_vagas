package com.example.gestao_vagas.modules.company.entities.controllers;

import com.example.gestao_vagas.modules.company.entities.CompanyEntity;
import com.example.gestao_vagas.modules.company.entities.dto.UpdateCompanyDTO;
import com.example.gestao_vagas.modules.company.entities.useCases.CreateCompanyUseCase;
import com.example.gestao_vagas.modules.company.entities.useCases.DeleteCompanyUseCase;
import com.example.gestao_vagas.modules.company.entities.useCases.GetCompanyUseCase;
import com.example.gestao_vagas.modules.company.entities.useCases.UpdateCompanyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/company")
@Tag(name = "Company", description = "Informações da company")
public class CompanyController {

    @Autowired
    private GetCompanyUseCase getCompanyUseCase;

    @Autowired
    private CreateCompanyUseCase createCompanyUseCase;

    @Autowired
    private UpdateCompanyUseCase updateCompanyUseCase;

    @Autowired
    private DeleteCompanyUseCase deleteCompanyUseCase;

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar empresa",
            description = "Busca uma empresa pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa encontrada",
                    content = @Content(schema = @Schema(implementation = CompanyEntity.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido ou empresa não encontrada"
            )
    })
    public ResponseEntity<Object> get(@PathVariable String id) {
        try {
            var company = this.getCompanyUseCase.execute(UUID.fromString(id));
            return ResponseEntity.ok().body(company);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/")
    @Operation(summary = "Cadastro de empresa", description = "Essa função é responsável por cadastrar uma empresa")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = CompanyEntity.class))
            }),
            @ApiResponse(responseCode = "400", description = "Usuário já existe")
    })
    public ResponseEntity<Object> create(@Valid @RequestBody CompanyEntity companyEntity) {
        try {
            var result = this.createCompanyUseCase.execute(companyEntity);
            return ResponseEntity.status(201).body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar empresa",
            description = "Atualiza os dados de uma empresa existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "203",
                    description = "Empresa atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = CompanyEntity.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido ou dados inválidos"
            )
    })
    public ResponseEntity<Object> update(@PathVariable String id, @Valid @RequestBody UpdateCompanyDTO updateDTO) {
        try {
            var result = updateCompanyUseCase.execute(UUID.fromString(id), updateDTO);
            return ResponseEntity.status(203).body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir empresa",
            description = "Remove uma empresa do sistema pelo ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa removida com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido ou empresa não encontrada"
            )
    })
    public ResponseEntity<Object> delete(@PathVariable String id) {
        try {
            this.deleteCompanyUseCase.execute(UUID.fromString(id));
            return ResponseEntity.ok().body("");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

