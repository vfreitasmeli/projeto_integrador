package com.mercadolibre.bootcamp.projeto_integrador.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BatchRequestDto {

    @NotBlank(message = "O Id do produto deve ser informado")
    @Positive(message = "O Id do produto deve ser um número positivo")
    private long productId;

    @NotBlank(message = "A temperatura atual deve ser informada")
    private float currentTemperature;

    @NotBlank(message = "A temperatura mínima dever ser informada")
    private float minimumTemperature;

    @NotBlank(message = "A quantidade inicial deve ser informada")
    private int initialQuantity;

    @NotBlank(message = "A quantidade atual deve ser informada")
    private int currentQuantity;

    @NotBlank(message = "A data de fabricação deve ser informada")
    @PastOrPresent(message = "A date de fabricação deve ser menor ou igual a data atual")
    private LocalDate manufacturingDate;

    @NotBlank(message = "A hora de fabricação deve ser informada")
    @PastOrPresent(message = "A hora de fabricação deve ser menor ou igual a data atual")
    private LocalDateTime manufacturingTime;

    @NotBlank(message = "A data de vencimento deve ser informada")
    @Future(message = "A data de vencimento deve ser posterior a data atual")
    private LocalDate dueDate;
}
