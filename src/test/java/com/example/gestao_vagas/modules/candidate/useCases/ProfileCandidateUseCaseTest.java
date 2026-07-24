package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.UserNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.upload.UploadEntity;
import com.example.gestao_vagas.modules.upload.repositories.UploadRepository;
import com.example.gestao_vagas.modules.upload.services.R2StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileCandidateUseCaseTest {

    @InjectMocks
    private ProfileCandidateUseCase profileCandidateUseCase;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private UploadRepository uploadRepository;

    @Mock
    private R2StorageService r2StorageService;

    @Test
    @DisplayName("Should not return profile when candidate is not found")
    void shouldNotReturnProfileWhenCandidateNotFound() {
        var id = UUID.randomUUID();
        when(candidateRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> profileCandidateUseCase.execute(id));
    }

    @Test
    @DisplayName("Should return profile without curriculum url when candidate has no curriculum")
    void shouldReturnProfileWithoutCurriculumUrlWhenNoCurriculum() {
        var id = UUID.randomUUID();
        var candidate = buildCandidate(id, null);

        when(candidateRepository.findById(id)).thenReturn(Optional.of(candidate));

        var result = profileCandidateUseCase.execute(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Maria");
        assertThat(result.getUsername()).isEqualTo("maria");
        assertThat(result.getEmail()).isEqualTo("maria@email.com");
        assertThat(result.getDescription()).isEqualTo("Dev Java");
        assertThat(result.getCandidateUrl()).isNull();
        verify(uploadRepository, never()).findById(any());
        verify(r2StorageService, never()).exists(any());
        verify(r2StorageService, never()).presignGet(any(), any());
    }

    @Test
    @DisplayName("Should return profile with curriculum url when curriculum exists in storage")
    void shouldReturnProfileWithCurriculumUrlWhenCurriculumExists() {
        var id = UUID.randomUUID();
        var curriculumId = UUID.randomUUID();
        var key = "curriculums/" + id + "/" + curriculumId + ".pdf";
        var candidate = buildCandidate(id, curriculumId);
        var upload = UploadEntity.builder()
                .id(curriculumId)
                .key(key)
                .ownerId(id)
                .purpose("CURRICULUM")
                .build();

        when(candidateRepository.findById(id)).thenReturn(Optional.of(candidate));
        when(uploadRepository.findById(curriculumId)).thenReturn(Optional.of(upload));
        when(r2StorageService.exists(key)).thenReturn(true);
        when(r2StorageService.presignGet(eq(key), eq(Duration.ofMinutes(15))))
                .thenReturn("https://r2.example/presigned-url");

        var result = profileCandidateUseCase.execute(id);

        assertThat(result.getCandidateUrl()).isEqualTo("https://r2.example/presigned-url");
        assertThat(result.getId()).isEqualTo(id);
        verify(r2StorageService).exists(key);
        verify(r2StorageService).presignGet(eq(key), eq(Duration.ofMinutes(15)));
    }

    @Test
    @DisplayName("Should return profile without curriculum url when upload record was deleted")
    void shouldReturnProfileWithoutCurriculumUrlWhenUploadRecordMissing() {
        var id = UUID.randomUUID();
        var curriculumId = UUID.randomUUID();
        var candidate = buildCandidate(id, curriculumId);

        when(candidateRepository.findById(id)).thenReturn(Optional.of(candidate));
        when(uploadRepository.findById(curriculumId)).thenReturn(Optional.empty());

        var result = profileCandidateUseCase.execute(id);

        assertThat(result.getCandidateUrl()).isNull();
        verify(r2StorageService, never()).exists(any());
        verify(r2StorageService, never()).presignGet(any(), any());
    }

    @Test
    @DisplayName("Should return profile without curriculum url when file was deleted from storage")
    void shouldReturnProfileWithoutCurriculumUrlWhenFileDeletedFromStorage() {
        var id = UUID.randomUUID();
        var curriculumId = UUID.randomUUID();
        var key = "curriculums/" + id + "/missing.pdf";
        var candidate = buildCandidate(id, curriculumId);
        var upload = UploadEntity.builder()
                .id(curriculumId)
                .key(key)
                .ownerId(id)
                .purpose("CURRICULUM")
                .build();

        when(candidateRepository.findById(id)).thenReturn(Optional.of(candidate));
        when(uploadRepository.findById(curriculumId)).thenReturn(Optional.of(upload));
        when(r2StorageService.exists(key)).thenReturn(false);

        var result = profileCandidateUseCase.execute(id);

        assertThat(result.getCandidateUrl()).isNull();
        verify(r2StorageService).exists(key);
        verify(r2StorageService, never()).presignGet(any(), any());
    }

    private CandidateEntity buildCandidate(UUID id, UUID curriculumId) {
        var candidate = new CandidateEntity();
        candidate.setId(id);
        candidate.setName("Maria");
        candidate.setUsername("maria");
        candidate.setEmail("maria@email.com");
        candidate.setDescription("Dev Java");
        candidate.setCurriculumId(curriculumId);
        return candidate;
    }
}
