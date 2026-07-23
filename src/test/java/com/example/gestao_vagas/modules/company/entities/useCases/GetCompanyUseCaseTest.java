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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCompanyUseCaseTest {

    @InjectMocks
    private GetCompanyUseCase getCompanyUseCase;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Should not get company when it is not found")
    void shouldNotGetWhenCompanyNotFound() {
        var id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class,
                () -> getCompanyUseCase.execute(id));
    }

    @Test
    @DisplayName("Should return company when found")
    void shouldReturnCompanySuccessfully() {
        var id = UUID.randomUUID();
        var company = CompanyEntity.builder()
                .id(id)
                .name("Empresa X")
                .email("empresa@email.com")
                .username("empresa")
                .build();

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));

        var result = getCompanyUseCase.execute(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Empresa X");
        assertThat(result.getEmail()).isEqualTo("empresa@email.com");
        assertThat(result.getUsername()).isEqualTo("empresa");
    }
}
