package ebanking.back.backend.services;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ebanking.back.backend.dtos.AccountHistoryDTO;
import ebanking.back.backend.dtos.AccountOperationDTO;
import ebanking.back.backend.dtos.BankAccountDTO;
import ebanking.back.backend.dtos.CurrentAccountDTO;
import ebanking.back.backend.dtos.CustomerDTO;
import ebanking.back.backend.dtos.SavingAccountDTO;
import ebanking.back.backend.entities.AccountOperation;
import ebanking.back.backend.entities.BankAccount;
import ebanking.back.backend.entities.CurrentAccount;
import ebanking.back.backend.entities.Customer;
import ebanking.back.backend.entities.SavingAccount;
import ebanking.back.backend.enums.OperationType;
import ebanking.back.backend.exceptions.BalanceNotSufficientException;
import ebanking.back.backend.exceptions.BankAccountNotFoundException;
import ebanking.back.backend.exceptions.CustomerNotFoundException;
import ebanking.back.backend.mappers.BankAccountMapperImpl;
import ebanking.back.backend.model.TransactionEvent;
import ebanking.back.backend.repositories.AccountOperationRepository;
import ebanking.back.backend.repositories.BankAccountRepository;
import ebanking.back.backend.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;
    private TransactionPublisher transactionPublisher;
    // private final NotificationService notificationService;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);

    }

    @Override
    public CurrentAccountDTO saveCurrentAccountDTO(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setCustomer(customer);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);

    }

    @Override
    public List<CustomerDTO> listCustomers() {

        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOs = customers.stream().map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        return customerDTOs;
    }

    @Override
    public SavingAccountDTO saveSavingAccountDTO(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        SavingAccount savingAccount = new SavingAccount();

        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreatedAt(new Date());
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);

        SavingAccount savedAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingAccount(savedAccount);
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));

        if (bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not sufficient");

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(amount);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setDescription(description);

        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);

        // Publish transaction event to RabbitMQ
        TransactionEvent transactionEvent = new TransactionEvent();
        transactionEvent.setCustomerId(bankAccount.getCustomer().getId());
        transactionEvent.setTransactionId(accountOperation.getId());
        transactionEvent.setAccountId(bankAccount.getId());
        transactionEvent.setAmount(amount);
        transactionEvent.setType(OperationType.DEBIT.name());
        transactionEvent.setTimestamp(LocalDateTime.now());

        transactionPublisher.sendTransactionMessage(transactionEvent);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(amount);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setDescription(description);

        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);

        
        // Publish transaction event to RabbitMQ
        TransactionEvent transactionEvent = new TransactionEvent();
          transactionEvent.setCustomerId(bankAccount.getCustomer().getId());
        transactionEvent.setTransactionId(accountOperation.getId());
        transactionEvent.setAccountId(bankAccount.getId());
        transactionEvent.setAmount(amount);
        transactionEvent.setType(OperationType.CREDIT.name());
        transactionEvent.setTimestamp(LocalDateTime.now());

        transactionPublisher.sendTransactionMessage(transactionEvent);
    }

    @Override
    public void transfer(String accountId, String accountIdDestination, double amount)
            throws BankAccountNotFoundException, BalanceNotSufficientException {

        credit(accountIdDestination, amount, "Transfer from" + accountId);
        debit(accountId, amount, "Transfer to" + accountIdDestination);

    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size)
            throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null)
            throw new BankAccountNotFoundException("Account not Found");
        Page<AccountOperation> accountOperations = accountOperationRepository
                .findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream()
                .map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.findByUsernameContainingIgnoreCase(keyword);
        List<CustomerDTO> customerDTOS = customers.stream().map(cust -> dtoMapper.fromCustomer(cust))
                .collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public List<BankAccountDTO> getAccountDTOs(Long customerId) throws BankAccountNotFoundException, CustomerNotFoundException {
      Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
      List<BankAccount> bankAccounts = bankAccountRepository.findByCustomerId(customer.getId());
      if(bankAccounts.isEmpty()){
        throw new BankAccountNotFoundException("No bank accounts found for the customer");
      }
      List<BankAccountDTO> bankAccountDTOs = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOs;

      
	  
    }

}
