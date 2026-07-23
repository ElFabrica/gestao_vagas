package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CandidateNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.dto.UpdateCandidateDTO;
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
class UpdateCandidateUseCaseTest {

    @InjectMocks
    private UpdateCandidateUseCase updateCandidateUseCase;

    @Mock
    private CandidateRepository candidateRepository;

    @Test
    @DisplayName("Should not update when candidate is not found")
    void shouldNotUpdateWhenCandidateNotFound() {
        var id = UUID.randomUUID();
        var dto = new UpdateCandidateDTO(
                "Novo Nome",
                "novo_user",
                "novo@email.com",
                "senha12345",
                "nova desc",
                "novo curriculum"
        );

        when(candidateRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CandidateNotFoundException.class,
                () -> updateCandidateUseCase.execute(dto, id));

        verify(candidateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update candidate name, username and email")
    void shouldUpdateCandidateSuccessfully() {
        var id = UUID.randomUUID();
        var candidate = new CandidateEntity();
        candidate.setId(id);
        candidate.setName("Antigo");
        candidate.setUsername("antigo");
        candidate.setEmail("antigo@email.com");
        candidate.setDescription("desc antiga");
        candidate.setCurriculum("curriculum antigo");

        var dto = new UpdateCandidateDTO(
                "Novo Nome",
                "novo_user",
                "novo@email.com",
                "senha12345",
                "nova desc",
                "novo curriculum"
        );

        when(candidateRepository.findById(id)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(CandidateEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        updateCandidateUseCase.execute(dto, id);

        ArgumentCaptor<CandidateEntity> captor = ArgumentCaptor.forClass(CandidateEntity.class);
        verify(candidateRepository).save(captor.capture());

        var saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Novo Nome");
        assertThat(saved.getUsername()).isEqualTo("novo_user");
        assertThat(saved.getEmail()).isEqualTo("novo@email.com");
        assertThat(saved.getDescription()).isEqualTo("desc antiga");
        assertThat(saved.getCurriculum()).isEqualTo("curriculum antigo");
    }
}
