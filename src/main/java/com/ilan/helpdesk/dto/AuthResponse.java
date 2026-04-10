package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.Role;

public class AuthResponse {
    private long id;
    private String email;
    private String fullName;
    private Role role;
    private String token;
    public AuthResponse(){}
    public AuthResponse(long id, String fullName, String email, Role role, String token){
        this.email=email;
        this.id=id;
        this.fullName=fullName;
        this.role=role;
        this.token=token;
    }
    public String getFullName() {
        return fullName;
    }
    public long getId() {
        return id;
    }
    public Role getRole() {
        return role;
    }
    public String getEmail() {
        return email;
    }
    public String getToken() {
        return token;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
