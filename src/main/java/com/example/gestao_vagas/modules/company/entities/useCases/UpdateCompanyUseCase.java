package com.example.gestao_vagas.modules.company.entities.useCases;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.modules.company.entities.CompanyEntity;
import com.example.gestao_vagas.modules.company.entities.dto.UpdateCompanyDTO;
import com.example.gestao_vagas.modules.company.entities.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateCompanyUseCase {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CompanyEntity execute(UUID id, UpdateCompanyDTO updateCompanyDTO){
        CompanyEntity company = this.companyRepository.findById(id)
                .orElseThrow(CompanyNotFoundException::new);

        company.setName(updateCompanyDTO.name());
        company.setEmail(updateCompanyDTO.email());
        company.setUsername(updateCompanyDTO.username());
        company.setWebsite(updateCompanyDTO.website());
        company.setDescription(updateCompanyDTO.description());

        if(updateCompanyDTO.password() != null && !updateCompanyDTO.password().isBlank()) {
            company.setPassword(
                    passwordEncoder.encode(updateCompanyDTO.password())
            );
        }

        return this.companyRepository.save(company);

    }
}
