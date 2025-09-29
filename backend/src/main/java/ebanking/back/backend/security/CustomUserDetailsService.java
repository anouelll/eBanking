package ebanking.back.backend.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ebanking.back.backend.entities.Customer;
import ebanking.back.backend.repositories.CustomerRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

        private CustomerRepository customerRepository;

        public CustomUserDetailsService(CustomerRepository customerRepository) {
            this.customerRepository = customerRepository;
        }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return User.builder()
                .username(customer.getUsername())
                .password(customer.getPassword())
                .authorities(customer.getRole()) // Convert role to String
                .build();

	}

}
