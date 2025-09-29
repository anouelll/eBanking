package ebanking.back.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ebanking.back.backend.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
  List<Customer> findByUsernameContainingIgnoreCase(String keyword);

  Customer findByUsername(String username);
}
