package com.ilan.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank (message = "name must be provided")
    @Size (min = 2 , max = 100 ,message = "name must be between 2 and 100 chars")
    private String fullName;
    @NotBlank (message = "email must be provided")
    @Email (message = "email must be valid")
    private String email;
    @NotBlank (message = "password must be provided")
    @Size (min = 6,max = 100, message = "password must be between 6 and 100 chars")
    private String password;

    public RegisterRequest(){};
    public RegisterRequest(String fullName, String email, String password){
        this.fullName=fullName;
        this.email=email;
        this.password=password;
    }
    public String getEmail() {
        return email;
    }
    public String getFullName() {
        return fullName;
    }
    public String getPassword() {
        return password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
