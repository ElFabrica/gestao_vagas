package com.example.gestao_vagas.modules.company.entities.useCases;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.modules.company.entities.CompanyEntity;
import com.example.gestao_vagas.modules.company.entities.repositories.CompanyRepository;
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
class CreateCompanyUseCaseTest {

    @InjectMocks
    private CreateCompanyUseCase createCompanyUseCase;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should not create company when username or email already exists")
    void shouldNotCreateWhenUserAlreadyExists() {
        var company = CompanyEntity.builder()
                .username("empresa")
                .email("empresa@email.com")
                .password("senha12345")
                .build();

        when(companyRepository.findByUsernameOrEmail("empresa", "empresa@email.com"))
                .thenReturn(Optional.of(new CompanyEntity()));

        assertThrows(CompanyNotFoundException.UserFoundException.class,
                () -> createCompanyUseCase.execute(company));

        verify(companyRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should create company encoding the password")
    void shouldCreateCompanySuccessfully() {
        var company = CompanyEntity.builder()
                .username("empresa")
                .email("empresa@email.com")
                .password("senha12345")
                .name("Empresa X")
                .build();

        var saved = CompanyEntity.builder()
                .id(UUID.randomUUID())
                .username("empresa")
                .email("empresa@email.com")
                .password("encoded-password")
                .name("Empresa X")
                .build();

        when(companyRepository.findByUsernameOrEmail("empresa", "empresa@email.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha12345")).thenReturn("encoded-password");
        when(companyRepository.save(any(CompanyEntity.class))).thenReturn(saved);

        var result = createCompanyUseCase.execute(company);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(company.getPassword()).isEqualTo("encoded-password");
        verify(companyRepository).save(company);
    }
}
