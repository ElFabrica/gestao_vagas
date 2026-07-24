package com.example.gestao_vagas.modules.upload.repositories;


import com.example.gestao_vagas.modules.upload.UploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UploadRepository extends JpaRepository<UploadEntity, UUID> {
}
