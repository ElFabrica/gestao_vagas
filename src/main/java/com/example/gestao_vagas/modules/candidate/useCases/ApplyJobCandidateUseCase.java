package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.exceptions.UserNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.entity.ApplyJobEntity;
import com.example.gestao_vagas.modules.candidate.repository.ApplyJobRepository;
import com.example.gestao_vagas.modules.company.entities.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ApplyJobCandidateUseCase {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplyJobRepository applyJobRepository;

    public ApplyJobEntity execute(UUID idCandidate, UUID idJob){

        this.candidateRepository.findById(idCandidate)
                .orElseThrow(()->{
                    throw new UserNotFoundException();
                });

        this.jobRepository.findById(idJob)
                .orElseThrow(()->{
                    throw new CompanyNotFoundException.JobNotFoundException();
                });

        var applyJob = ApplyJobEntity.builder()
                .candidateId(idCandidate)
                .jobId(idJob)
                .build();
        applyJob =  applyJobRepository.save(applyJob);
        return applyJob;
    }
}
