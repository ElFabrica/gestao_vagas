package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.exceptions.JobNotFoundException;
import com.example.gestao_vagas.modules.candidate.exceptions.UserNotFoundException;
import com.example.gestao_vagas.modules.company.entities.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class ApplyJobCandidateUseCase {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    public void execute(UUID idCandidate, UUID idJob){

        this.candidateRepository.findById(idCandidate)
                .orElseThrow(()->{
                    throw new UserNotFoundException();
                });

        this.jobRepository.findById(idJob)
                .orElseThrow(()->{
                    throw new JobNotFoundException();
                });
        

    }
}
