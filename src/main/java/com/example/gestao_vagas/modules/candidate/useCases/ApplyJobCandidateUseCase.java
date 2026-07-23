package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.CandidateAppliedException;
import com.example.gestao_vagas.exceptions.CompanyNotFoundException;
import com.example.gestao_vagas.exceptions.UserNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.dto.ApplyJobEmailMessageDTO;
import com.example.gestao_vagas.modules.candidate.entity.ApplyJobEntity;
import com.example.gestao_vagas.modules.candidate.producers.ApplyJobEmailProducer;
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

    @Autowired
    private ApplyJobEmailProducer applyJobEmailProducer;

    public ApplyJobEntity execute(UUID idCandidate, UUID idJob) {

        var alreadyExists = this.applyJobRepository.findByJobIdAndCandidateId(idJob, idCandidate);
        if (alreadyExists.isPresent()) {
            throw new CandidateAppliedException();
        }

        var candidate = this.candidateRepository.findById(idCandidate)
                .orElseThrow(() -> {
                    throw new UserNotFoundException();
                });

        var job = this.jobRepository.findById(idJob)
                .orElseThrow(() -> {
                    throw new CompanyNotFoundException.JobNotFoundException();
                });


        var applyJob = ApplyJobEntity.builder()
                .candidateId(idCandidate)
                .jobId(idJob)
                .build();
        applyJob = applyJobRepository.save(applyJob);

        var company = job.getCompanyEntity();

        this.applyJobEmailProducer.send(new ApplyJobEmailMessageDTO(
                candidate.getId(),
                candidate.getName(),
                candidate.getEmail(),
                job.getId(),
                job.getDescription(),
                company.getName(),
                company.getEmail()
        ));

        return applyJob;
    }
}
