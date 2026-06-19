package com.example.gestao_vagas.candidate.exceptions;

public class UserFoundException extends RuntimeException {
    public UserFoundException(){
        super("Usuário já existe");
    }
}
