package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CandidateNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.dto.UpdateCandidateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateCandidateUseCase {
    @Autowired
    private CandidateRepository candidateRepository;

    public void execute(UpdateCandidateDTO updateCandidateDTO, UUID id) {

        var candidate = this.candidateRepository.findById(id)
                .orElseThrow(CandidateNotFoundException::new);
        candidate.setName(updateCandidateDTO.name());
        candidate.setUsername(updateCandidateDTO.username());
        candidate.setEmail(updateCandidateDTO.email());
        candidate.setDescription(candidate.getDescription());

        this.candidateRepository.save(candidate);
    }


}
