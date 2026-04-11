package com.ilan.helpdesk.exception;

import com.ilan.helpdesk.dto.APIErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // this means, listen to all errors from controller and handle them here//
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) //each method handles other type of exception//
    public ResponseEntity<APIErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        APIErrorResponse errorResponse = new APIErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        APIErrorResponse errorResponse = new APIErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                LocalDateTime.now(),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorResponse> handleGeneralException(Exception ex) {
        APIErrorResponse errorResponse = new APIErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidTicketStateException.class)
    public ResponseEntity<APIErrorResponse> handleInvalidTicketState(InvalidTicketStateException ex) {
        APIErrorResponse errorResponse = new APIErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<APIErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex){
        APIErrorResponse errorResponse=new APIErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null);
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<APIErrorResponse> handleBadCredentials(InvalidCredentialsException ex){
        APIErrorResponse errorResponse=new APIErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        LocalDateTime.now(),
        null);
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        APIErrorResponse errorResponse = new APIErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "You do not have permission to perform this action",
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(InvalidUserRoleException.class)
    public ResponseEntity<APIErrorResponse> handleInvalidRole(InvalidUserRoleException ex){
        APIErrorResponse errorResponse=new APIErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null);
        return new ResponseEntity<>(errorResponse,HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(InvalidAttachmentException.class)
    public ResponseEntity<APIErrorResponse>handleInvalidAttachmentException(InvalidAttachmentException ex){
        APIErrorResponse errorResponse=new APIErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null);
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AttachmentPreviewNotAllowedException.class)
    public ResponseEntity<APIErrorResponse>handleAttachmentPreviewNotAllowed(AttachmentPreviewNotAllowedException ex){
        APIErrorResponse errorResponse=new APIErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null);
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<APIErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        APIErrorResponse errorResponse=new APIErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

}