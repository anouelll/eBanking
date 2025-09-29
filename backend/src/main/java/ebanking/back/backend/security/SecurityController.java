package ebanking.back.backend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
// Removed incorrect import of CouchbaseProperties.Authentication
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ebanking.back.backend.dtos.LoginRequest;
import ebanking.back.backend.dtos.RegisterRequest;
import ebanking.back.backend.entities.Customer;
import ebanking.back.backend.repositories.CustomerRepository;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @GetMapping("/profile")
    public Authentication authentication(Authentication authentication) {
        return authentication;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.username();
        String password = loginRequest.password();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

                Customer customer = customerRepository.findByUsername(username);

        Instant instant = Instant.now();
        String scope = authentication.getAuthorities().stream().map(a -> a.getAuthority())
                .collect(Collectors.joining(" "));
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder().issuedAt(instant)
                .expiresAt(instant.plus(10, ChronoUnit.MINUTES)).claim("scope", scope).claim("userId", customer.getId()).subject(username).build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters
                .from(JwsHeader.with(MacAlgorithm.HS256).build(), jwtClaimsSet);
        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return Map.of("access_token", jwt);
    }

    @PostMapping("/register")
   public Map<String, String> register(@RequestBody RegisterRequest registerRequest) {

       Customer existingCustomer = customerRepository.findByUsername(registerRequest.username());
       if (existingCustomer != null) {
           return Map.of("status", "error", "message", "Username already exists");
       }
       Customer customer = new Customer();
       customer.setUsername(registerRequest.username());
       customer.setEmail(registerRequest.email());
       customer.setPassword(passwordEncoder.encode(registerRequest.password()));
    customer.setRole(registerRequest.role());
       customerRepository.save(customer);
       return Map.of("status", "success");
   }

}
