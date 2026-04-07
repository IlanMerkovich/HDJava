package com.ilan.helpdesk.exception;

public class InvalidUserRoleException extends RuntimeException{
    public InvalidUserRoleException(String message){
        super(message);
    }
}
