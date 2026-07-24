package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateEntity;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.dto.CreateCandidateDTO;
import com.example.gestao_vagas.modules.candidate.dto.CreateCandidateResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateCandidateUseCase {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CreateCandidateResponseDTO execute(CreateCandidateDTO createCandidateDTO) {
        this.candidateRepository
                .findByUsernameOrEmail(createCandidateDTO.username(), createCandidateDTO.email())
                .ifPresent((user) -> {
                    throw new CompanyNotFoundException.UserFoundException();
                });

        var candidateEntity = new CandidateEntity();
        candidateEntity.setName(createCandidateDTO.name());
        candidateEntity.setUsername(createCandidateDTO.username());
        candidateEntity.setEmail(createCandidateDTO.email());
        candidateEntity.setDescription(createCandidateDTO.description());
        candidateEntity.setPassword(passwordEncoder.encode(createCandidateDTO.password()));

        var candidate = this.candidateRepository.save(candidateEntity);

        return new CreateCandidateResponseDTO(
                candidate.getName(),
                candidate.getUsername(),
                candidate.getEmail(),
                candidate.getDescription(),
                candidate.getCurriculumId(),
                candidate.getCreatedAt()
        );
    }
}
