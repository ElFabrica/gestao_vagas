package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.modules.company.entities.JobEntity;
import com.example.gestao_vagas.modules.company.entities.repositories.JobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAllJobsByFilterUseCaseTest {

    @InjectMocks
    private ListAllJobsByFilterUseCase listAllJobsByFilterUseCase;

    @Mock
    private JobRepository jobRepository;

    @Test
    @DisplayName("Should return jobs filtered by description")
    void shouldReturnJobsByFilter() {
        var filter = "java";
        var jobs = List.of(
                JobEntity.builder().id(UUID.randomUUID()).description("Dev Java").build(),
                JobEntity.builder().id(UUID.randomUUID()).description("Java Pleno").build()
        );

        when(jobRepository.findByDescriptionContainingIgnoreCase(filter)).thenReturn(jobs);

        var result = listAllJobsByFilterUseCase.execute(filter);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(JobEntity::getDescription)
                .containsExactly("Dev Java", "Java Pleno");
        verify(jobRepository).findByDescriptionContainingIgnoreCase(filter);
    }

    @Test
    @DisplayName("Should return empty list when no jobs match the filter")
    void shouldReturnEmptyListWhenNoJobsMatch() {
        when(jobRepository.findByDescriptionContainingIgnoreCase("xyz")).thenReturn(List.of());

        var result = listAllJobsByFilterUseCase.execute("xyz");

        assertThat(result).isEmpty();
    }
}
