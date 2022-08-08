package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler {

    /**
     * Throw unmapped exceptions with HTTP Status 500, printing these exceptions to verify.
     * @throw Exception
     * @param exception
     */
    @ExceptionHandler(Exception.class)
    public Object unmappedExceptionHandler(Exception exception) {
        System.out.println(exception.toString());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomError("Internal Server Error",
                        "An internal server error has occurred.", LocalDateTime.now()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomError> sectionNotFoundHandler(NotFoundException exception) {
        return ResponseEntity.status(exception.getStatus()).body(new CustomError(exception));
    }

    @ExceptionHandler(MaxSizeException.class)
    public ResponseEntity<CustomError> maxSizeBatchHandler(MaxSizeException exception) {
        return ResponseEntity.status(exception.getStatus()).body(new CustomError(exception));
    }

    @ExceptionHandler(InitialQuantityException.class)
    public ResponseEntity<CustomError> batchInitialQuantityExceptionHandler(InitialQuantityException exception) {
        return ResponseEntity.status(exception.getStatus()).body(new CustomError(exception));
    }

    // Exceptions for @Valid annotation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> handle(MethodArgumentNotValidException exception) {
        // Create list with all errors on exception
        List<FieldError> errors = exception.getBindingResult().getFieldErrors();

        // New CustomError instance, transforming each FieldError to String
        CustomError error = new CustomError("Invalid fields", errors.stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .collect(Collectors.joining(" | ")), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
