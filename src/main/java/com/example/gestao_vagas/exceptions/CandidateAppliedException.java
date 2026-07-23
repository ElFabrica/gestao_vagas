package com.example.gestao_vagas.exceptions;

public class CandidateAppliedException extends RuntimeException {
    public CandidateAppliedException() {
        super("Vaga já aplicada com este candidato");
    }
}
