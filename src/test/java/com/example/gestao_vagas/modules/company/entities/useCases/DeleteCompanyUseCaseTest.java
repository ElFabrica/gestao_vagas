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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCompanyUseCaseTest {

    @InjectMocks
    private DeleteCompanyUseCase deleteCompanyUseCase;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Should not delete when company is not found")
    void shouldNotDeleteWhenCompanyNotFound() {
        var id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class,
                () -> deleteCompanyUseCase.execute(id));

        verify(companyRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should delete company when found")
    void shouldDeleteCompanySuccessfully() {
        var id = UUID.randomUUID();
        var company = CompanyEntity.builder().id(id).name("Empresa").build();

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));

        deleteCompanyUseCase.execute(id);

        verify(companyRepository).delete(company);
    }
}
