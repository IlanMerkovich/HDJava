package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.Role;

public class CurrentUserResponse {
    private long id;
    private String fullName;
    private String email;
    private Role role;
    public CurrentUserResponse(){}

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
