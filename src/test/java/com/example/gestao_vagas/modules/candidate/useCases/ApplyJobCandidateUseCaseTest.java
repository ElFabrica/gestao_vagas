package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CandidateAppliedException;
import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.exceptions.UserNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.dto.ApplyJobEmailMessageDTO;
import com.example.gestao_vagas.modules.candidate.entity.ApplyJobEntity;
import com.example.gestao_vagas.modules.candidate.producers.ApplyJobEmailProducer;
import com.example.gestao_vagas.modules.candidate.repository.ApplyJobRepository;
import com.example.gestao_vagas.modules.company.entities.CompanyEntity;
import com.example.gestao_vagas.modules.company.entities.JobEntity;
import com.example.gestao_vagas.modules.company.entities.repositories.JobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplyJobCandidateUseCaseTest {

    @InjectMocks
    private ApplyJobCandidateUseCase applyJobCandidateUseCase;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplyJobRepository applyJobRepository;

    @Mock
    private ApplyJobEmailProducer applyJobEmailProducer;

    @Test
    @DisplayName("Should not apply when candidate already applied to the job")
    void shouldNotApplyWhenAlreadyApplied() {
        var idCandidate = UUID.randomUUID();
        var idJob = UUID.randomUUID();

        when(applyJobRepository.findByJobIdAndCandidateId(idJob, idCandidate))
                .thenReturn(Optional.of(new ApplyJobEntity()));

        assertThrows(CandidateAppliedException.class,
                () -> applyJobCandidateUseCase.execute(idCandidate, idJob));

        verify(candidateRepository, never()).findById(any());
        verify(applyJobEmailProducer, never()).send(any());
    }

    @Test
    @DisplayName("Should not apply when candidate is not found")
    void shouldNotApplyWhenCandidateNotFound() {
        var idCandidate = UUID.randomUUID();
        var idJob = UUID.randomUUID();

        when(applyJobRepository.findByJobIdAndCandidateId(idJob, idCandidate))
                .thenReturn(Optional.empty());
        when(candidateRepository.findById(idCandidate)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> applyJobCandidateUseCase.execute(idCandidate, idJob));

        verify(applyJobEmailProducer, never()).send(any());
    }

    @Test
    @DisplayName("Should not apply when job is not found")
    void shouldNotApplyWhenJobNotFound() {
        var idCandidate = UUID.randomUUID();
        var idJob = UUID.randomUUID();

        when(applyJobRepository.findByJobIdAndCandidateId(idJob, idCandidate))
                .thenReturn(Optional.empty());
        when(candidateRepository.findById(idCandidate)).thenReturn(Optional.of(new CandidateEntity()));
        when(jobRepository.findById(idJob)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.JobNotFoundException.class,
                () -> applyJobCandidateUseCase.execute(idCandidate, idJob));

        verify(applyJobEmailProducer, never()).send(any());
    }

    @Test
    @DisplayName("Should apply to job, persist and publish email message")
    void shouldApplyToJobSuccessfully() {
        var idCandidate = UUID.randomUUID();
        var idJob = UUID.randomUUID();
        var idCompany = UUID.randomUUID();

        var candidate = new CandidateEntity();
        candidate.setId(idCandidate);
        candidate.setName("Maria");
        candidate.setEmail("maria@email.com");

        var company = CompanyEntity.builder()
                .id(idCompany)
                .name("Empresa X")
                .email("empresa@email.com")
                .build();

        var job = JobEntity.builder()
                .id(idJob)
                .description("Dev Java")
                .companyId(idCompany)
                .companyEntity(company)
                .build();

        var applyJobCreated = ApplyJobEntity.builder()
                .id(UUID.randomUUID())
                .candidateId(idCandidate)
                .jobId(idJob)
                .build();

        when(applyJobRepository.findByJobIdAndCandidateId(idJob, idCandidate))
                .thenReturn(Optional.empty());
        when(candidateRepository.findById(idCandidate)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(idJob)).thenReturn(Optional.of(job));
        when(applyJobRepository.save(any(ApplyJobEntity.class))).thenReturn(applyJobCreated);

        var result = applyJobCandidateUseCase.execute(idCandidate, idJob);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getCandidateId()).isEqualTo(idCandidate);
        assertThat(result.getJobId()).isEqualTo(idJob);

        ArgumentCaptor<ApplyJobEmailMessageDTO> emailCaptor =
                ArgumentCaptor.forClass(ApplyJobEmailMessageDTO.class);
        verify(applyJobEmailProducer).send(emailCaptor.capture());

        var emailMessage = emailCaptor.getValue();
        assertThat(emailMessage.candidateId()).isEqualTo(idCandidate);
        assertThat(emailMessage.candidateName()).isEqualTo("Maria");
        assertThat(emailMessage.candidateEmail()).isEqualTo("maria@email.com");
        assertThat(emailMessage.jobId()).isEqualTo(idJob);
        assertThat(emailMessage.jobDescription()).isEqualTo("Dev Java");
        assertThat(emailMessage.companyName()).isEqualTo("Empresa X");
        assertThat(emailMessage.companyEmail()).isEqualTo("empresa@email.com");
    }
}
