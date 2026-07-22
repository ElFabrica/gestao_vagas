package com.example.gestao_vagas.modules.candidate.consumer;

import com.example.gestao_vagas.modules.candidate.dto.ApplyJobEmailMessageDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class ApplyJobEmailConsumer {
    private final JavaMailSender mailSender;

    public ApplyJobEmailConsumer(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    @RabbitListener(queues = "${app.rabbitmq.email-queue}")
    public void consumer(ApplyJobEmailMessageDTO message) {
        sendCandidateEmail(message);
        sendCompanyEmail(message);
    }

    private void sendCandidateEmail(ApplyJobEmailMessageDTO message) {
        var email = new SimpleMailMessage();
        email.setTo(message.candidateEmail());
        email.setSubject("Candidatura enviada com sucesso!");
        email.setText("""
                Olá %s,
                
                Sua candidatura para a vaga "%s" foi enviada com sucesso.
                
                Empresa: %s
                """.formatted(message.candidateName()
                , message.jobDescription()
                , message.companyName()));

        mailSender.send(email);
    }

    private void sendCompanyEmail(ApplyJobEmailMessageDTO message) {
        var email = new SimpleMailMessage();
        email.setTo(message.companyEmail());
        email.setSubject("Nova candidatura recebida");
        email.setText("""
                Olá %s
                
                O candidato %s se candidatou para a vaga "%s".
                
                Email do candidato: %s
                """.formatted(
                message.candidateName(),
                message.companyName(),
                message.jobDescription(),
                message.candidateEmail()
        ));
        mailSender.send(email);
    }
}
