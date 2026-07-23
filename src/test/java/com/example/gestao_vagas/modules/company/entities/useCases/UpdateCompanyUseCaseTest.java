package com.example.gestao_vagas.modules.company.entities.useCases;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.modules.company.entities.CompanyEntity;
import com.example.gestao_vagas.modules.company.entities.dto.UpdateCompanyDTO;
import com.example.gestao_vagas.modules.company.entities.repositories.CompanyRepository;
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
class UpdateCompanyUseCaseTest {

    @InjectMocks
    private UpdateCompanyUseCase updateCompanyUseCase;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should not update when company is not found")
    void shouldNotUpdateWhenCompanyNotFound() {
        var id = UUID.randomUUID();
        var dto = new UpdateCompanyDTO(
                "Nova Empresa",
                "nova@email.com",
                "nova_empresa",
                "https://site.com",
                "descricao",
                "senha12345"
        );

        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class,
                () -> updateCompanyUseCase.execute(id, dto));

        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update company without changing password when password is blank")
    void shouldUpdateWithoutChangingPasswordWhenBlank() {
        var id = UUID.randomUUID();
        var company = CompanyEntity.builder()
                .id(id)
                .name("Antiga")
                .email("antiga@email.com")
                .username("antiga")
                .website("https://antiga.com")
                .description("desc antiga")
                .password("senha-antiga")
                .build();

        var dto = new UpdateCompanyDTO(
                "Nova Empresa",
                "nova@email.com",
                "nova_empresa",
                "https://site.com",
                "descricao nova",
                "   "
        );

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(CompanyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = updateCompanyUseCase.execute(id, dto);

        assertThat(result.getName()).isEqualTo("Nova Empresa");
        assertThat(result.getEmail()).isEqualTo("nova@email.com");
        assertThat(result.getUsername()).isEqualTo("nova_empresa");
        assertThat(result.getWebsite()).isEqualTo("https://site.com");
        assertThat(result.getDescription()).isEqualTo("descricao nova");
        assertThat(result.getPassword()).isEqualTo("senha-antiga");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should update company and encode password when provided")
    void shouldUpdateAndEncodePasswordWhenProvided() {
        var id = UUID.randomUUID();
        var company = CompanyEntity.builder()
                .id(id)
                .name("Antiga")
                .email("antiga@email.com")
                .username("antiga")
                .password("senha-antiga")
                .build();

        var dto = new UpdateCompanyDTO(
                "Nova Empresa",
                "nova@email.com",
                "nova_empresa",
                "https://site.com",
                "descricao",
                "novaSenha123"
        );

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("encoded-nova");
        when(companyRepository.save(any(CompanyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = updateCompanyUseCase.execute(id, dto);

        ArgumentCaptor<CompanyEntity> captor = ArgumentCaptor.forClass(CompanyEntity.class);
        verify(companyRepository).save(captor.capture());

        assertThat(result.getPassword()).isEqualTo("encoded-nova");
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-nova");
    }
}
