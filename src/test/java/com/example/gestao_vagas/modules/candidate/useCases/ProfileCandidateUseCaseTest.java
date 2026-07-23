package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.UserNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileCandidateUseCaseTest {

    @InjectMocks
    private ProfileCandidateUseCase profileCandidateUseCase;

    @Mock
    private CandidateRepository candidateRepository;

    @Test
    @DisplayName("Should not return profile when candidate is not found")
    void shouldNotReturnProfileWhenCandidateNotFound() {
        var id = UUID.randomUUID();
        when(candidateRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> profileCandidateUseCase.execute(id));
    }

    @Test
    @DisplayName("Should return candidate profile")
    void shouldReturnCandidateProfile() {
        var id = UUID.randomUUID();
        var candidate = new CandidateEntity();
        candidate.setId(id);
        candidate.setName("Maria");
        candidate.setUsername("maria");
        candidate.setEmail("maria@email.com");
        candidate.setDescription("Dev Java");

        when(candidateRepository.findById(id)).thenReturn(Optional.of(candidate));

        var result = profileCandidateUseCase.execute(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Maria");
        assertThat(result.getUsername()).isEqualTo("maria");
        assertThat(result.getEmail()).isEqualTo("maria@email.com");
        assertThat(result.getDescription()).isEqualTo("Dev Java");
    }
}
