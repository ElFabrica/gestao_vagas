package com.example.gestao_vagas.modules.company.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "company")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Schema(example = "El Fabrica Company")
    private String name;

    @NotBlank
    @Pattern(regexp = "\\S+", message = "O campo [username] não deve conter espaço")
    @Schema(example = "ElFabricaCompany06Oficial")
    private String username;

    @Email(message = "O campo[(email], deve conter um e-mail valido")
    @Schema(example = "Elfabrica06_@gmail.com")
    private String email;

    @Length(min = 10, max = 100)
    @Schema(example = "ElFabrica06_")
    private String password;
    @Schema(example = "https://elfabrica06_")
    private String website;
    @Schema(example = "bem vindo ao elfabrica company")
    private  String description;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
