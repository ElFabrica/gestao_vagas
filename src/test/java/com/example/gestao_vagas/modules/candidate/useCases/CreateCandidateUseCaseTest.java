package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.dto.CreateCandidateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        var dto = new CreateCandidateDTO(
                "Arthur",
                "teste123",
                "teste@gmail.com",
                "senha12345",
                "Dev Java"
        );

        when(candidateRepository.findByUsernameOrEmail("teste123", "teste@gmail.com"))
                .thenReturn(Optional.of(new CandidateEntity()));

        assertThrows(CompanyNotFoundException.UserFoundException.class,
                () -> createCandidateUseCase.execute(dto));

        verify(candidateRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should create candidate encoding the password")
    void shouldCreateCandidateSuccessfully() {
        var dto = new CreateCandidateDTO(
                "Arthur",
                "teste123",
                "teste@gmail.com",
                "senha12345",
                "Dev Java"
        );

        var saved = new CandidateEntity();
        saved.setId(UUID.randomUUID());
        saved.setUsername("teste123");
        saved.setEmail("teste@gmail.com");
        saved.setPassword("encoded-password");
        saved.setName("Arthur");
        saved.setDescription("Dev Java");

        when(candidateRepository.findByUsernameOrEmail("teste123", "teste@gmail.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha12345")).thenReturn("encoded-password");
        when(candidateRepository.save(any(CandidateEntity.class))).thenReturn(saved);

        var result = createCandidateUseCase.execute(dto);

        ArgumentCaptor<CandidateEntity> captor = ArgumentCaptor.forClass(CandidateEntity.class);
        verify(candidateRepository).save(captor.capture());

        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-password");
        assertThat(captor.getValue().getId()).isNull();
        assertThat(result.username()).isEqualTo("teste123");
        assertThat(result.email()).isEqualTo("teste@gmail.com");
        assertThat(result.name()).isEqualTo("Arthur");
    }
}
