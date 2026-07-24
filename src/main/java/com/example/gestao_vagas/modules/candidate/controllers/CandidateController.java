package com.example.gestao_vagas.modules.candidate.controllers;

import com.example.gestao_vagas.modules.candidate.dto.CreateCandidateDTO;
import com.example.gestao_vagas.modules.candidate.dto.CreateCandidateResponseDTO;
import com.example.gestao_vagas.modules.candidate.dto.ProfileCandidateResponseDTO;
import com.example.gestao_vagas.modules.candidate.dto.UpdateCandidateDTO;
import com.example.gestao_vagas.modules.candidate.useCases.*;
import com.example.gestao_vagas.modules.company.entities.JobEntity;
import com.example.gestao_vagas.modules.upload.dto.UploadResponseDTO;
import com.example.gestao_vagas.modules.upload.useCases.UploadCurriculumUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/candidate")
@Tag(name = "Candidato", description = "Gerenciamento de candidatos: cadastro, perfil, currículo (R2), vagas e candidaturas.")
public class CandidateController {

    @Autowired
    UpdateCandidateUseCase updateCandidateUseCase;

    @Autowired
    private CreateCandidateUseCase createCandidateUseCase;

    @Autowired
    private ProfileCandidateUseCase profileCandidateUseCase;

    @Autowired
    private ListAllJobsByFilterUseCase listAllJobsByFilterUseCase;

    @Autowired
    private ApplyJobCandidateUseCase applApplyJobCandidateUseCase;

    @Autowired
    private DeleteCandidateUseCase deleteCandidateUseCase;

    @Autowired
    private UploadCurriculumUseCase uploadCurriculumUseCase;

    @PostMapping("/")
    @Operation(
            summary = "Cadastrar candidato",
            description = "Realiza o cadastro de um novo candidato no sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Candidato cadastrado com sucesso.",
                    content = @Content(schema = @Schema(implementation = CreateCandidateResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou usuário já cadastrado."
            )
    })
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateCandidateDTO createCandidateDTO) {

        try {
            var result = this.createCandidateUseCase.execute(createCandidateDTO);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar candidato",
            description = "Atualiza as informações de um candidato através do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidato atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou candidato não encontrado.")
    })
    @SecurityRequirement(name = "jwt_auth")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Object> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateCandidateDTO updateCandidateDTO) {

        try {
            this.updateCandidateUseCase.execute(updateCandidateDTO, UUID.fromString(id));
            return ResponseEntity.ok().body("");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('CANDIDATE')")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(
            summary = "Consultar perfil",
            description = """
                    Retorna as informações do candidato autenticado.
                    O campo candidateUrl contém a URL pré-assinada do currículo ativo (válida por 15 minutos),
                    ou null quando não há currículo, o registro de upload não existe ou o arquivo foi removido do R2.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil encontrado.",
                    content = @Content(schema = @Schema(implementation = ProfileCandidateResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Candidato não encontrado."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT inválido ou expirado."
            )
    })
    public ResponseEntity<Object> get(HttpServletRequest request) {

        var idCandidate = request.getAttribute("candidate_id");

        try {
            var profile = this.profileCandidateUseCase.execute(UUID.fromString(idCandidate.toString()));
            return ResponseEntity.ok().body(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/job")
    @PreAuthorize("hasRole('CANDIDATE')")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(
            summary = "Listar vagas",
            description = "Lista todas as vagas disponíveis de acordo com o filtro informado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de vagas retornada com sucesso.",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = JobEntity.class)
                    ))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado."
            )
    })
    public List<JobEntity> findJobByFilter(
            @RequestParam String filter) {

        return this.listAllJobsByFilterUseCase.execute(filter);
    }

    @PostMapping("/job/apply")
    @PreAuthorize("hasRole('CANDIDATE')")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(
            summary = "Candidatar-se a uma vaga",
            description = "Permite que o candidato autenticado realize a inscrição em uma vaga. O body deve ser o UUID da vaga (JSON string)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscrição realizada com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro ao realizar a candidatura (vaga inexistente, já inscrito, etc.)."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado."
            )
    })
    public ResponseEntity<Object> applyJob(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "UUID da vaga",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UUID.class, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"))
            )
            @RequestBody UUID jobId) {

        var candidateId = request.getAttribute("candidate_id");

        try {
            var result = this.applApplyJobCandidateUseCase.execute(
                    UUID.fromString(candidateId.toString()),
                    jobId);

            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/")
    @PreAuthorize("hasRole('CANDIDATE')")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(
            summary = "Excluir conta",
            description = "Remove permanentemente a conta do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "203",
                    description = "Conta removida com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro ao remover a conta."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado."
            )
    })
    public ResponseEntity<Object> delete(HttpServletRequest request) {

        var candidateId = request.getAttribute("candidate_id");

        try {
            this.deleteCandidateUseCase.delete(UUID.fromString(candidateId.toString()));
            return ResponseEntity.status(203).body("");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/curriculum", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(
            summary = "Upload de currículo",
            description = """
                    Envia o currículo do candidato autenticado em PDF (máximo 5 MB).
                    O arquivo é armazenado no Cloudflare R2 e o curriculumId ativo do candidato é atualizado.
                    Um novo upload substitui o currículo ativo anterior.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Currículo enviado com sucesso.",
                    content = @Content(schema = @Schema(implementation = UploadResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Arquivo inválido (vazio, maior que 5 MB ou não PDF) ou candidato não encontrado."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT inválido ou expirado."
            )
    })
    public ResponseEntity<Object> uploadCurriculum(
            HttpServletRequest request,
            @Parameter(
                    description = "Arquivo PDF do currículo (campo form-data: file)",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file
    ) {
        try {
            var candidateId = request.getAttribute("candidate_id");
            var result = this.uploadCurriculumUseCase.execute(
                    UUID.fromString(candidateId.toString()),
                    file
            );
            return ResponseEntity.status(201).body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}