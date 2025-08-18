package ebanking.back.backend.services;

import java.util.Date;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ebanking.back.backend.dtos.notification.TransactionNotificationDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {
    
    private final SimpMessagingTemplate template;

    public void sendTransactionNotification(Long customerId, String accountId, double amount){
        TransactionNotificationDTO transactionNotificationDTO = new TransactionNotificationDTO();
        transactionNotificationDTO.setAccountId(accountId);
        transactionNotificationDTO.setAmount(amount);
        transactionNotificationDTO.setDate(new Date());

        String destination = "/topic/transactions." + customerId;
        log.info("Sending notification to {}", destination);
        template.convertAndSend(destination, transactionNotificationDTO);
    }
}
