package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CandidateNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Service
public class DeleteCandidateUseCase {
    @Autowired
    private CandidateRepository candidateRepository;

    public void delete(UUID id) {
        var candidate = this.candidateRepository.findById(id)
                .orElseThrow(CandidateNotFoundException::new);

        this.candidateRepository.delete(candidate);
    }
}
