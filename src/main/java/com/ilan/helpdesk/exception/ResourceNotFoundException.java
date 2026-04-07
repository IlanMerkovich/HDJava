package com.ilan.helpdesk.exception;


// that is A exception we created in order to handle not finding a resource //
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }

}
