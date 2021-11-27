package com.egs.atmservice.web.controller;

import com.egs.atmservice.web.error.BadRequestAlertException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestAlertException exception) {
        return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception exception) {
        return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.CONFLICT);
    }
}
