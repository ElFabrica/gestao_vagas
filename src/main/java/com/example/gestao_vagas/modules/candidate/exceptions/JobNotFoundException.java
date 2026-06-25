package com.example.gestao_vagas.modules.candidate.exceptions;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(){
        super("Job not found");
    }
}
