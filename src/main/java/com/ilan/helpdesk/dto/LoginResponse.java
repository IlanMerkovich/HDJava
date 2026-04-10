package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.Role;

public class LoginResponse {
    private String fullName;
    private String email;
    private long id;
    private String password;
    private Role role;
    private String message;

    public LoginResponse(){}
    public LoginResponse(String fullName, String email, String password, long id, Role role){
        this.email=email;
        this.id=id;
        this.fullName=fullName;
        this.role=role;
        this.password=password;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getFullName() {
        return fullName;
    }
    public Role getRole() {
        return role;
    }
    public long getId() {
        return id;
    }
    public String getMessage() {
        return message;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
