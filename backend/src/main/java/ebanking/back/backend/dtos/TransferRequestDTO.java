package ebanking.back.backend.dtos;

import lombok.Data;

@Data
public class TransferRequestDTO {
    private String accountId;
    private String accountDestination;
    private double amount;
    private String description;
}