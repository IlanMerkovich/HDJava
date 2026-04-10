package com.ilan.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @Email(message = "Email must be valid")
    @NotBlank (message = "Email is required")
    private String email;
    @NotBlank (message = "Password is required")
    private String password;

    public LoginRequest(){
    }
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password=password;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
