package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CandidateNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCandidateUseCaseTest {

    @InjectMocks
    private DeleteCandidateUseCase deleteCandidateUseCase;

    @Mock
    private CandidateRepository candidateRepository;

    @Test
    @DisplayName("Should not delete when candidate is not found")
    void shouldNotDeleteWhenCandidateNotFound() {
        var id = UUID.randomUUID();
        when(candidateRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CandidateNotFoundException.class,
                () -> deleteCandidateUseCase.delete(id));

        verify(candidateRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should delete candidate when found")
    void shouldDeleteCandidateSuccessfully() {
        var id = UUID.randomUUID();
        var candidate = new CandidateEntity();
        candidate.setId(id);

        when(candidateRepository.findById(id)).thenReturn(Optional.of(candidate));

        deleteCandidateUseCase.delete(id);

        verify(candidateRepository).delete(candidate);
    }
}
