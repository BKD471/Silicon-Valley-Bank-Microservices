package com.example.accountsservices.exception;

import com.example.accountsservices.dto.responseDtos.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AccountsException.class, BeneficiaryException.class,
            TransactionException.class, ResponseException.class,
            CustomerException.class})
    public ResponseEntity<ErrorDetails> handleAllUncheckedCustomException(Exception e, WebRequest web) {
        ErrorDetails error = new ErrorDetails(LocalTime.now(), e.getMessage(), web.getDescription(false));
        log.error(String.format("<==========================%s====================================================================" +
                "=======================================================================>",e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().stream().forEachOrdered((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

        });
        log.error(String.format("<============%s=====================================================" +
                "============================================================================>",ex.getMessage()));
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(Exception e, WebRequest web) {
        ErrorDetails error = new ErrorDetails(LocalTime.now(), e.getMessage(), web.getDescription(false));
        log.error(String.format("<============%s=========================================================================================" +
                "==================================================>",e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
