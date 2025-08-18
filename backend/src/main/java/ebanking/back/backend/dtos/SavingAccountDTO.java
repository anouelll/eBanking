package ebanking.back.backend.dtos;

import lombok.Data;
import ebanking.back.backend.enums.AccountStatus;
import java.util.Date;
@Data
public class SavingAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double interestRate;
}