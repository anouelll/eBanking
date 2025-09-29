package ebanking.back.backend.services;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ebanking.back.backend.dtos.notification.TransactionNotificationDTO;
import ebanking.back.backend.model.TransactionEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {
    
    private final SimpMessagingTemplate template;

    public void sendTransactionNotification(TransactionEvent transactionEvent){
        
        String destination = "/topic/transactions." + transactionEvent.getCustomerId();
        log.info("Sending notification to {}", destination);
        template.convertAndSend(destination, transactionEvent);
    }
}
