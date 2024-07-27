package com.backend.backend.global.controller;

import com.backend.backend.global.exception.GlobalException;
import com.backend.backend.global.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class ExceptionController {
    @ResponseBody
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> mySuperEx(GlobalException e) {
        int statusCode = e.getStatusCode();

        ErrorResponse response = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

        return ResponseEntity.status(statusCode).body(response);
    }
}
