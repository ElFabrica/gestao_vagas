package com.example.gestao_vagas.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(){
        super("Company not found");
    }

    @Data
    @AllArgsConstructor

    public static class ErrorMessageDTO {
        private String message;
        private String field;
    }

    @ControllerAdvice
    public static class ExceptionHandlerController {

        private final MessageSource messageSource;

        public ExceptionHandlerController(MessageSource message){
            this.messageSource = message;
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<List<ErrorMessageDTO>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
            List<ErrorMessageDTO> dto = new ArrayList<>();

            e.getBindingResult().getFieldErrors().forEach(err ->{
                String message = messageSource.getMessage(err, LocaleContextHolder.getLocale());
                ErrorMessageDTO error = new ErrorMessageDTO(message, err.getField());
                dto.add(error);
            });
            return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
        }
    }

    public static class JobNotFoundException extends RuntimeException {
        public JobNotFoundException(){
            super("Job not found");
        }
    }

    public static class UserFoundException extends RuntimeException {
        public UserFoundException(){
            super("Usuário já existe");
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(){
            super("User not found");
        }
    }
}
