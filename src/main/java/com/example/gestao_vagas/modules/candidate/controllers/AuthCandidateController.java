package com.example.gestao_vagas.modules.candidate.controllers;

import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.dto.AuthCandidateRequestDTO;
import com.example.gestao_vagas.modules.candidate.dto.AuthCandidateResponseDTO;
import com.example.gestao_vagas.modules.candidate.useCases.AuthCandidateUseCase;
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
@RequestMapping("/candidate")
@Tag(name="Autenticar candidato", description = "Autenticação do usuário de tipo candidato")
public class AuthCandidateController {

    @Autowired
    private AuthCandidateUseCase authCandidateUseCase;

    @PostMapping("/auth")
    @Operation(summary = "Login de candidato",
            description = "Essa função é responsável por realizar login do candidato.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = AuthCandidateResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Username/passoword incorrect")})
    public ResponseEntity<Object> auth(@RequestBody AuthCandidateRequestDTO authCandidateRequestDTO){
        try{
            var token = this.authCandidateUseCase.execute(authCandidateRequestDTO);
            return ResponseEntity.ok().body(token);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

        }
    }
}
