package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.Role;

public class userResponse {
    private String fullName;
    private String email;
    private Role role;
    private long id;

    public userResponse(){};
    public userResponse(String fullName,String email,Role role,long id){
        this.email=email;
        this.id=id;
        this.fullName=fullName;
        this.role=role;
    }
    public String getFullName() {
        return fullName;
    }
    public String getEmail() {
        return email;
    }
    public long getId() {
        return id;
    }
    public Role getRole() {
        return role;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void setId(long id) {
        this.id = id;
    }
}
