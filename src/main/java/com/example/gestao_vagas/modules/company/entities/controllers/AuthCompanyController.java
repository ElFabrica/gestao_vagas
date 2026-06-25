package com.example.gestao_vagas.modules.company.entities.controllers;

import com.example.gestao_vagas.modules.company.entities.dto.AuthCompanyDTO;
import com.example.gestao_vagas.modules.company.entities.dto.AuthCompanyResponseDTO;
import com.example.gestao_vagas.modules.company.entities.useCases.AuthCompanyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@Tag(name="Company", description = "Informações da company")
public class AuthCompanyController {

    @Autowired
    private AuthCompanyUseCase authCompanyUseCase;

    @PostMapping("/auth")
    @Operation(summary = "Login na empresa", description = "Essa função é responsável por fazer login na empresa")
    @ApiResponses({
            @ApiResponse(responseCode= "200", content = {
                    @Content(schema = @Schema(implementation = AuthCompanyResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Username ou senhas invalidas")
            })

    public ResponseEntity<Object> create(@RequestBody AuthCompanyDTO authCompanyDTO){
        try {
        var result = this.authCompanyUseCase.execute(authCompanyDTO);
        return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
