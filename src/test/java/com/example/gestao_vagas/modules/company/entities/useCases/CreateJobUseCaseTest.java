package com.example.gestao_vagas.modules.company.entities.useCases;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.modules.company.entities.CompanyEntity;
import com.example.gestao_vagas.modules.company.entities.JobEntity;
import com.example.gestao_vagas.modules.company.entities.repositories.CompanyRepository;
import com.example.gestao_vagas.modules.company.entities.repositories.JobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class CreateJobUseCaseTest {

    @InjectMocks
    private CreateJobUseCase createJobUseCase;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Should not create job when company is not found")
    void shouldNotCreateWhenCompanyNotFound() {
        var companyId = UUID.randomUUID();
        var job = JobEntity.builder()
                .description("Dev Java")
                .companyId(companyId)
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class,
                () -> createJobUseCase.execute(job));

        verify(jobRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create job when company exists")
    void shouldCreateJobSuccessfully() {
        var companyId = UUID.randomUUID();
        var job = JobEntity.builder()
                .description("Dev Java")
                .benefits("VR")
                .level("JUNIOR")
                .companyId(companyId)
                .build();

        var saved = JobEntity.builder()
                .id(UUID.randomUUID())
                .description("Dev Java")
                .benefits("VR")
                .level("JUNIOR")
                .companyId(companyId)
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(new CompanyEntity()));
        when(jobRepository.save(job)).thenReturn(saved);

        var result = createJobUseCase.execute(job);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Dev Java");
        assertThat(result.getCompanyId()).isEqualTo(companyId);
        verify(jobRepository).save(job);
    }
}
