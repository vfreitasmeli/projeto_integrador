package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class IncompatibleCategoryException extends CustomException {
    /**
     * Lança uma CustomException com HTTP Status 400.
     */
    public IncompatibleCategoryException(List<String> productNames) {
        super("Categoria incompatível", "Os seguintes produtos possuem categoria incompatível com a seção: " + String.join(", ", productNames), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }
}
