package ebanking.back.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ebanking.back.backend.entities.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {

    List<BankAccount> findByCustomerId(Long customerId);
}
