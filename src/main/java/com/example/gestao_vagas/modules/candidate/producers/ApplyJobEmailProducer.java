package com.example.gestao_vagas.modules.candidate.producers;

import com.example.gestao_vagas.modules.candidate.dto.ApplyJobEmailMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplyJobEmailProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.email-queue}")
    private String emailQueue;

    public ApplyJobEmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(ApplyJobEmailMessageDTO message) {
        rabbitTemplate.convertAndSend("application-email-exchange", "email", message);
    }
}
