package com.example.gestao_vagas.modules.company.entities.useCases;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.modules.company.entities.CompanyEntity;
import com.example.gestao_vagas.modules.company.entities.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteCompanyUseCase {
    @Autowired
    private CompanyRepository companyRepository;

    public void execute(UUID companyId) {
        CompanyEntity companyEntity = this.companyRepository.findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);

        this.companyRepository.delete(companyEntity);
    }
}
