package com.example.gestao_vagas.modules.candidate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "candidate")
public class CandidateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Schema(example = "Arthur Fabricyo")
    private String name;

    @NotBlank
    @Pattern(regexp = "\\S+", message = "O campo [username] não deve conter espaço")
    @Schema(example = "ElFabrica", description = "username do usuário")
    private String username;

    @Email(message = "O campo[(email], deve conter um e-mail valido")
    @Schema(example = "arthur.fabricyo@gmail.com")
    private String email;

    @Length(min = 10, max = 100)
    @Schema(example = "Fala12345@", minLength = 10, maxLength = 100)
    private String password;

    @Schema(example = "Sou dev full stack com sonho em trabalhar na linguagem de melhor salário do mercado. Java")
    private String description;
    @Schema(example = "Sênior em Typescript, Junior em java")
    private UUID curriculumId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
