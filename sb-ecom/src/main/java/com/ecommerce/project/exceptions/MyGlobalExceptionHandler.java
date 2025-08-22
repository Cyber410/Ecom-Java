package com.ecommerce.project.exceptions;

import com.ecommerce.project.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String,String> map= new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error->{
                    String fieldName=((FieldError)error).getField();
                    String message=error.getDefaultMessage();
                    map.put(fieldName,message);
                }
        );
     return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)

    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException ex) {
        String message=ex.getMessage();
        APIResponse apiResponse=new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)

    public ResponseEntity<APIResponse> myAPIException(APIException ex) {
        String message=ex.getMessage();
        APIResponse apiResponse=new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

}
