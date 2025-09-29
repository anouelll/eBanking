package ebanking.back.backend.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {

    private Long transactionId;
    private Long customerId;
    private String accountId;
    private String type;
    private double amount;
    private LocalDateTime timestamp;



}
