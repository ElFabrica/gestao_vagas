package com.example.gestao_vagas.modules.upload.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.sql.Driver;

@Service
public class R2StorageService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;

    private R2StorageService(
            S3Client s3Client,
            S3Presigner s3Presigner,
            @Value("${cloudflare.r2.bucket}") String bucket
    ) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucket = bucket;

    }

}
