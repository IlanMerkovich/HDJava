package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.Role;

public class currentUserResponse {
    private String email;
    private Role role;
    public currentUserResponse(){}
    public currentUserResponse(String email,Role role){
        this.email=email;
        this.role=role;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
