package com.example.gestao_vagas.modules.upload.useCases;

import com.example.gestao_vagas.exceptions.CandidateAppliedException;
import com.example.gestao_vagas.modules.candidate.CandidateRepository;
import com.example.gestao_vagas.modules.upload.UploadEntity;
import com.example.gestao_vagas.modules.upload.dto.UploadResponseDTO;
import com.example.gestao_vagas.modules.upload.repositories.UploadRepository;
import com.example.gestao_vagas.modules.upload.services.R2StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadCurriculumUseCase {

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("application/pdf");

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UploadRepository uploadRepository;

    @Autowired
    private R2StorageService r2StorageService;


    public UploadResponseDTO execute(UUID candidateId, MultipartFile file) {
        var candidate = candidateRepository.findById(candidateId)
                .orElseThrow(CandidateAppliedException::new);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("file must be no large than 5 MB");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF is accepted");
        }
        String key = "curriculums/" + candidateId + "/" + UUID.randomUUID() + ".pdf";

        try {
            r2StorageService.upload(
                    key,
                    file.getInputStream(),
                    file.getContentType(),
                    file.getSize()
            );

        } catch (IOException e) {
            throw new RuntimeException("fail to read the file");
        }
        var upload = UploadEntity.builder()
                .originFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .sizeByte(file.getSize())
                .key(key)
                .bucket(r2StorageService.getBucket())
                .ownerId(candidateId)
                .purpose("CURRICULUM")
                .build();

        upload = uploadRepository.save(upload);

        UUID oldCurriculumId = candidate.getCurriculumId();
        candidate.setCurriculumId(upload.getId());
        candidateRepository.save(candidate);

        String url = r2StorageService.presignGet(key, Duration.ofMinutes(15));

        return new UploadResponseDTO(
                upload.getId(),
                upload.getOriginFilename(),
                upload.getContentType(),
                String.valueOf(upload.getSizeByte()),
                url
        );
    }
}
