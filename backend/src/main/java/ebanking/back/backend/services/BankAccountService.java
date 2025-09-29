package ebanking.back.backend.services;

import java.util.List;

import ebanking.back.backend.dtos.AccountHistoryDTO;
import ebanking.back.backend.dtos.AccountOperationDTO;
import ebanking.back.backend.dtos.BankAccountDTO;
import ebanking.back.backend.dtos.CurrentAccountDTO;
import ebanking.back.backend.dtos.CustomerDTO;
import ebanking.back.backend.dtos.SavingAccountDTO;
import ebanking.back.backend.exceptions.BalanceNotSufficientException;
import ebanking.back.backend.exceptions.BankAccountNotFoundException;
import ebanking.back.backend.exceptions.CustomerNotFoundException;


public interface BankAccountService {

    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentAccountDTO saveCurrentAccountDTO(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccountDTO saveSavingAccountDTO(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomers(String keyword);

    List<BankAccountDTO> getAccountDTOs(Long customerId) throws BankAccountNotFoundException, CustomerNotFoundException;


}
