package com.example.gestao_vagas.candidate.useCases;

import com.example.gestao_vagas.candidate.CandidateEntity;
import com.example.gestao_vagas.candidate.CandidateRepository;
import com.example.gestao_vagas.candidate.exceptions.UserFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateCandidateUseCase {

    @Autowired
    private CandidateRepository candidateRepository;

    public CandidateEntity execute(CandidateEntity candidateEntity){
        this.candidateRepository.findByUsernameOrEmail(candidateEntity.getUsername(), candidateEntity.getEmail())
                .ifPresent((user)->{
                    throw new UserFoundException();
                });
        return this.candidateRepository.save(candidateEntity);
    }
}