package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomError {
    private String name;
    private String message;

    public CustomError(CustomException exception) {
        this.name = exception.getName();
        this.message = exception.getMessage();
    }
}
