package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.dto.AuthCandidateRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.security.sasl.AuthenticationException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCandidateUseCaseTest {

    @InjectMocks
    private AuthCandidateUseCase authCandidateUseCase;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authCandidateUseCase, "secretKey", "test-candidate-secret");
    }

    @Test
    @DisplayName("Should not authenticate when username is not found")
    void shouldNotAuthenticateWhenUsernameNotFound() {
        var request = new AuthCandidateRequestDTO("inexistente", "senha12345");

        when(candidateRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authCandidateUseCase.execute(request));
    }

    @Test
    @DisplayName("Should not authenticate when password does not match")
    void shouldNotAuthenticateWhenPasswordDoesNotMatch() {
        var candidate = new CandidateEntity();
        candidate.setId(UUID.randomUUID());
        candidate.setUsername("elfabrica");
        candidate.setPassword("encoded");

        var request = new AuthCandidateRequestDTO("elfabrica", "senha-errada");

        when(candidateRepository.findByUsername("elfabrica")).thenReturn(Optional.of(candidate));
        when(passwordEncoder.matches("senha-errada", "encoded")).thenReturn(false);

        assertThrows(AuthenticationException.class,
                () -> authCandidateUseCase.execute(request));
    }

    @Test
    @DisplayName("Should authenticate and return access token")
    void shouldAuthenticateSuccessfully() throws AuthenticationException {
        var candidateId = UUID.randomUUID();
        var candidate = new CandidateEntity();
        candidate.setId(candidateId);
        candidate.setUsername("elfabrica");
        candidate.setPassword("encoded");

        var request = new AuthCandidateRequestDTO("elfabrica", "senha12345");

        when(candidateRepository.findByUsername("elfabrica")).thenReturn(Optional.of(candidate));
        when(passwordEncoder.matches("senha12345", "encoded")).thenReturn(true);

        var result = authCandidateUseCase.execute(request);

        assertThat(result.getAccess_token()).isNotBlank();
        assertThat(result.getExpires_in()).isPositive();
    }
}
