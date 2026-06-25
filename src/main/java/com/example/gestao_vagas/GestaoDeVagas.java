package com.example.gestao_vagas;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication


public class GestaoDeVagas {

    public static void main(String[] args) {
        SpringApplication.run(GestaoDeVagas.class, args);
    }

}
