package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

public class SectionNotFoundException extends CustomException {
    /**
     * Throw CustomException with HTTP Status 404.
     * @throws CustomException
     * @param sectionId
     */
    public SectionNotFoundException(Long sectionId) {
        super("Section not found.", "There is no section with id " + sectionId, HttpStatus.NOT_FOUND);
    }
}
