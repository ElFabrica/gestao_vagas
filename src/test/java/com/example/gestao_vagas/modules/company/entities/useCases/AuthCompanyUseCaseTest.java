package com.example.gestao_vagas.modules.company.entities.useCases;

import com.example.gestao_vagas.modules.company.entities.CompanyEntity;
import com.example.gestao_vagas.modules.company.entities.dto.AuthCompanyDTO;
import com.example.gestao_vagas.modules.company.entities.repositories.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCompanyUseCaseTest {

    @InjectMocks
    private AuthCompanyUseCase authCompanyUseCase;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authCompanyUseCase, "secretKey", "test-company-secret");
    }

    @Test
    @DisplayName("Should not authenticate when company username is not found")
    void shouldNotAuthenticateWhenCompanyNotFound() {
        var request = new AuthCompanyDTO("senha12345", "inexistente");

        when(companyRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authCompanyUseCase.execute(request));
    }

    @Test
    @DisplayName("Should not authenticate when password does not match")
    void shouldNotAuthenticateWhenPasswordDoesNotMatch() {
        var company = CompanyEntity.builder()
                .id(UUID.randomUUID())
                .username("empresa")
                .password("encoded")
                .build();

        var request = new AuthCompanyDTO("senha-errada", "empresa");

        when(companyRepository.findByUsername("empresa")).thenReturn(Optional.of(company));
        when(passwordEncoder.matches("senha-errada", "encoded")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authCompanyUseCase.execute(request));
    }

    @Test
    @DisplayName("Should authenticate and return access token")
    void shouldAuthenticateSuccessfully() {
        var company = CompanyEntity.builder()
                .id(UUID.randomUUID())
                .username("empresa")
                .password("encoded")
                .build();

        var request = new AuthCompanyDTO("senha12345", "empresa");

        when(companyRepository.findByUsername("empresa")).thenReturn(Optional.of(company));
        when(passwordEncoder.matches("senha12345", "encoded")).thenReturn(true);

        var result = authCompanyUseCase.execute(request);

        assertThat(result.getAccess_token()).isNotBlank();
        assertThat(result.getExpires_in()).isPositive();
    }
}
