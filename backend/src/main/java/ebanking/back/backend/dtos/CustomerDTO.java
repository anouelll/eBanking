package ebanking.back.backend.dtos;

import lombok.Data;

@Data
public class CustomerDTO {
  private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
}
