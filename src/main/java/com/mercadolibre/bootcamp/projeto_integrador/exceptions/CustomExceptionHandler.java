package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(SectionNotFoundException.class)
    public ResponseEntity<CustomError> sectionNotFoundHandler(SectionNotFoundException exception) {
        return ResponseEntity.status(exception.getStatus()).body(new CustomError(exception));
    }
}
