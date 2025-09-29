package ebanking.back.backend.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ebanking.back.backend.model.TransactionEvent;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionConsumer {

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveTransactionMessage(TransactionEvent transactionEvent) {

        try {
             log.info("✅ Transaction received: {}", transactionEvent);

            notificationService.sendTransactionNotification(transactionEvent);
           
        } catch (Exception e) {
            log.error("Failed to save transaction to database", e);
        }
    }
}
