package ebanking.back.backend.dtos;


public record RegisterRequest(String username, String email, String password, String role) {
}
