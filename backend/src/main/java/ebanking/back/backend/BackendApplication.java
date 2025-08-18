package ebanking.back.backend;

import java.util.stream.Stream;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ebanking.back.backend.dtos.BankAccountDTO;
import ebanking.back.backend.dtos.CurrentAccountDTO;
import ebanking.back.backend.dtos.CustomerDTO;
import ebanking.back.backend.dtos.SavingAccountDTO;
import ebanking.back.backend.exceptions.CustomerNotFoundException;
import ebanking.back.backend.services.BankAccountService;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		System.out.println("Backend application started successfully.");
	}
 @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
           Stream.of("Hassan","Imane","Mohamed").forEach(name->{
               CustomerDTO customer=new CustomerDTO();
               customer.setName(name);
               customer.setEmail(name+"@gmail.com");
               bankAccountService.saveCustomer(customer);
           });
           bankAccountService.listCustomers().forEach(customer->{
               try {
                   bankAccountService.saveCurrentAccountDTO(Math.random()*90000,9000,customer.getId());
                   bankAccountService.saveSavingAccountDTO(Math.random()*120000,5.5,customer.getId());

               } catch (CustomerNotFoundException e) {
                   e.printStackTrace();
               }
           });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i = 0; i <10 ; i++) {
                    String accountId;
                    if(bankAccount instanceof SavingAccountDTO){
                        accountId=((SavingAccountDTO) bankAccount).getId();
                    } else{
                        accountId=((CurrentAccountDTO) bankAccount).getId();
                    }
                    bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
                    bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");
                }
            }
        };
    }
}
