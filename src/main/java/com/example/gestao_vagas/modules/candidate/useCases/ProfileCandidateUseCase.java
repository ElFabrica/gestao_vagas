package com.example.gestao_vagas.modules.candidate.useCases;

import com.example.gestao_vagas.exceptions.UserNotFoundException;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.candidate.dto.ProfileCandidateResponseDTO;
import com.example.gestao_vagas.modules.upload.repositories.UploadRepository;
import com.example.gestao_vagas.modules.upload.services.R2StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class ProfileCandidateUseCase {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UploadRepository uploadRepository;

    @Autowired
    private R2StorageService r2StorageService;

    public ProfileCandidateResponseDTO execute(UUID idCandidate) {
        var candidate = this.candidateRepository.findById(idCandidate)
                .orElseThrow(UserNotFoundException::new);

        String curriculumUrl = resolveCurriculumUrl(candidate.getCurriculumId());

        return ProfileCandidateResponseDTO.builder()
                .description(candidate.getDescription())
                .username(candidate.getUsername())
                .email(candidate.getEmail())
                .name(candidate.getName())
                .id(candidate.getId())
                .candidateUrl(curriculumUrl)
                .build();
    }

    private String resolveCurriculumUrl(UUID curriculumId) {
        if (curriculumId == null) {
            return null;
        }

        var upload = uploadRepository.findById(curriculumId).orElse(null);
        if (upload == null || upload.getKey() == null || upload.getKey().isBlank()) {
            return null;
        }

        if (!r2StorageService.exists(upload.getKey())) {
            return null;
        }

        return r2StorageService.presignGet(upload.getKey(), Duration.ofMinutes(15));
    }
}
