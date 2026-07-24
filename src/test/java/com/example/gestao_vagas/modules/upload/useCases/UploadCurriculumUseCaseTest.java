package com.example.gestao_vagas.modules.upload.useCases;

import com.example.gestao_vagas.exceptions.CandidateAppliedException;
import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.upload.UploadEntity;
import com.example.gestao_vagas.modules.upload.repositories.UploadRepository;
import com.example.gestao_vagas.modules.upload.services.R2StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadCurriculumUseCaseTest {

    private static final long FIVE_MB = 5 * 1024 * 1024;

    @InjectMocks
    private UploadCurriculumUseCase uploadCurriculumUseCase;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private UploadRepository uploadRepository;

    @Mock
    private R2StorageService r2StorageService;

    @Test
    @DisplayName("Should upload curriculum and update candidate curriculumId")
    void shouldUploadCurriculumSuccessfully() throws IOException {
        var candidateId = UUID.randomUUID();
        var uploadId = UUID.randomUUID();
        var candidate = new CandidateEntity();
        candidate.setId(candidateId);

        var file = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                "pdf-content".getBytes()
        );

        var savedUpload = UploadEntity.builder()
                .id(uploadId)
                .originFilename("cv.pdf")
                .contentType("application/pdf")
                .sizeByte(file.getSize())
                .key("curriculums/" + candidateId + "/file.pdf")
                .bucket("gestao-vagas")
                .ownerId(candidateId)
                .purpose("CURRICULUM")
                .build();

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(r2StorageService.getBucket()).thenReturn("gestao-vagas");
        when(uploadRepository.save(any(UploadEntity.class))).thenReturn(savedUpload);
        when(candidateRepository.save(any(CandidateEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(r2StorageService.presignGet(anyString(), eq(Duration.ofMinutes(15))))
                .thenReturn("https://r2.example/presigned");

        var result = uploadCurriculumUseCase.execute(candidateId, file);

        assertThat(result.id()).isEqualTo(uploadId);
        assertThat(result.originFilename()).isEqualTo("cv.pdf");
        assertThat(result.ContentType()).isEqualTo("application/pdf");
        assertThat(result.sizeByte()).isEqualTo(String.valueOf(file.getSize()));
        assertThat(result.url()).isEqualTo("https://r2.example/presigned");
        assertThat(candidate.getCurriculumId()).isEqualTo(uploadId);

        var uploadCaptor = ArgumentCaptor.forClass(UploadEntity.class);
        verify(uploadRepository).save(uploadCaptor.capture());
        assertThat(uploadCaptor.getValue().getOwnerId()).isEqualTo(candidateId);
        assertThat(uploadCaptor.getValue().getPurpose()).isEqualTo("CURRICULUM");
        assertThat(uploadCaptor.getValue().getKey()).startsWith("curriculums/" + candidateId + "/");

        verify(r2StorageService).upload(
                anyString(),
                any(InputStream.class),
                eq("application/pdf"),
                eq(file.getSize())
        );
        verify(candidateRepository).save(candidate);
    }

    @Test
    @DisplayName("Should replace previous curriculumId when uploading a new curriculum")
    void shouldReplacePreviousCurriculumId() {
        var candidateId = UUID.randomUUID();
        var oldCurriculumId = UUID.randomUUID();
        var newUploadId = UUID.randomUUID();

        var candidate = new CandidateEntity();
        candidate.setId(candidateId);
        candidate.setCurriculumId(oldCurriculumId);

        var file = new MockMultipartFile(
                "file",
                "novo-cv.pdf",
                "application/pdf",
                "pdf-content".getBytes()
        );

        var savedUpload = UploadEntity.builder()
                .id(newUploadId)
                .originFilename("novo-cv.pdf")
                .contentType("application/pdf")
                .sizeByte(file.getSize())
                .key("curriculums/" + candidateId + "/novo.pdf")
                .bucket("gestao-vagas")
                .ownerId(candidateId)
                .purpose("CURRICULUM")
                .build();

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(r2StorageService.getBucket()).thenReturn("gestao-vagas");
        when(uploadRepository.save(any(UploadEntity.class))).thenReturn(savedUpload);
        when(candidateRepository.save(any(CandidateEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(r2StorageService.presignGet(anyString(), eq(Duration.ofMinutes(15))))
                .thenReturn("https://r2.example/novo");

        var result = uploadCurriculumUseCase.execute(candidateId, file);

        assertThat(result.id()).isEqualTo(newUploadId);
        assertThat(candidate.getCurriculumId()).isEqualTo(newUploadId);
        assertThat(candidate.getCurriculumId()).isNotEqualTo(oldCurriculumId);
    }

    @Test
    @DisplayName("Should not upload when candidate is not found")
    void shouldNotUploadWhenCandidateNotFound() {
        var candidateId = UUID.randomUUID();
        var file = new MockMultipartFile("file", "cv.pdf", "application/pdf", "x".getBytes());

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        assertThrows(CandidateAppliedException.class,
                () -> uploadCurriculumUseCase.execute(candidateId, file));

        verify(r2StorageService, never()).upload(anyString(), any(), anyString(), anyLong());
        verify(uploadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not upload when file is empty")
    void shouldNotUploadWhenFileIsEmpty() {
        var candidateId = UUID.randomUUID();
        var candidate = new CandidateEntity();
        candidate.setId(candidateId);
        var file = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[0]);

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        assertThrows(IllegalArgumentException.class,
                () -> uploadCurriculumUseCase.execute(candidateId, file));

        verify(r2StorageService, never()).upload(anyString(), any(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Should not upload when file exceeds max size")
    void shouldNotUploadWhenFileExceedsMaxSize() {
        var candidateId = UUID.randomUUID();
        var candidate = new CandidateEntity();
        candidate.setId(candidateId);
        var file = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                new byte[(int) FIVE_MB + 1]
        );

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        assertThrows(IllegalArgumentException.class,
                () -> uploadCurriculumUseCase.execute(candidateId, file));

        verify(r2StorageService, never()).upload(anyString(), any(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Should not upload when content type is not PDF")
    void shouldNotUploadWhenContentTypeIsNotPdf() {
        var candidateId = UUID.randomUUID();
        var candidate = new CandidateEntity();
        candidate.setId(candidateId);
        var file = new MockMultipartFile(
                "file",
                "cv.docx",
                "application/msword",
                "doc-content".getBytes()
        );

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        assertThrows(IllegalArgumentException.class,
                () -> uploadCurriculumUseCase.execute(candidateId, file));

        verify(r2StorageService, never()).upload(anyString(), any(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Should wrap IOException when reading file fails")
    void shouldWrapIOExceptionWhenReadingFileFails() throws IOException {
        var candidateId = UUID.randomUUID();
        var candidate = new CandidateEntity();
        candidate.setId(candidateId);

        var file = org.mockito.Mockito.mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(10L);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getInputStream()).thenThrow(new IOException("disk error"));

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        var exception = assertThrows(RuntimeException.class,
                () -> uploadCurriculumUseCase.execute(candidateId, file));

        assertThat(exception.getMessage()).isEqualTo("fail to read the file");
        verify(uploadRepository, never()).save(any());
    }
}
