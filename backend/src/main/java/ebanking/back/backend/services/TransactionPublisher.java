package ebanking.back.backend.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ebanking.back.backend.model.TransactionEvent;

@Service
public class TransactionPublisher {

 private static final Logger logger = LoggerFactory.getLogger(TransactionPublisher.class);
    @Value("${rabbitmq.exchange.name}")
    private String transactionExchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendTransactionMessage(TransactionEvent transactionEvent){
        try {
            rabbitTemplate.convertAndSend(transactionExchange, routingKey, transactionEvent);
            logger.info("✅ Message sent to RabbitMQ: {}", transactionEvent);
        } catch (Exception e) {
            logger.error("Failed to send message to RabbitMQ", e);
        }
    }

}
