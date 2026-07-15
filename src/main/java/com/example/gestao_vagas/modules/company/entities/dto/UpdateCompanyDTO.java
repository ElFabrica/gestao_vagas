package com.example.gestao_vagas.modules.company.entities.dto;
public record UpdateCompanyDTO(
        String name,
        String email,
        String username,
        String website,
        String description,
        String password
) {}
