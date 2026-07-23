package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCandidateUseCaseTest {

    @InjectMocks
    private CreateCandidateUseCase createCandidateUseCase;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should not create candidate when username or email already exists")
    void shouldNotCreateWhenUserAlreadyExists() {
        var candidate = new CandidateEntity();
        candidate.setUsername("elfabrica");
        candidate.setEmail("teste@email.com");
        candidate.setPassword("senha12345");

        when(candidateRepository.findByUsernameOrEmail("elfabrica", "teste@email.com"))
                .thenReturn(Optional.of(new CandidateEntity()));

        assertThrows(CompanyNotFoundException.UserFoundException.class,
                () -> createCandidateUseCase.execute(candidate));

        verify(candidateRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should create candidate encoding the password")
    void shouldCreateCandidateSuccessfully() {
        var candidate = new CandidateEntity();
        candidate.setUsername("elfabrica");
        candidate.setEmail("teste@email.com");
        candidate.setPassword("senha12345");
        candidate.setName("Arthur");

        var saved = new CandidateEntity();
        saved.setId(UUID.randomUUID());
        saved.setUsername("elfabrica");
        saved.setEmail("teste@email.com");
        saved.setPassword("encoded-password");
        saved.setName("Arthur");

        when(candidateRepository.findByUsernameOrEmail("elfabrica", "teste@email.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha12345")).thenReturn("encoded-password");
        when(candidateRepository.save(any(CandidateEntity.class))).thenReturn(saved);

        var result = createCandidateUseCase.execute(candidate);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(candidate.getPassword()).isEqualTo("encoded-password");
        verify(candidateRepository).save(candidate);
    }
}
