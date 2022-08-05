package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class CustomException extends Exception {
    private String name;
    private String message;
    private HttpStatus status;
}
