package ebanking.back.backend.dtos.notification;

import java.util.Date;

import lombok.Data;

@Data
public class TransactionNotificationDTO {

    private double amount;
    private Date date;
    private String accountId;
    
}
