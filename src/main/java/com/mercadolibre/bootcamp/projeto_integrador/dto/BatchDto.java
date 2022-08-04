package com.mercadolibre.bootcamp.projeto_integrador.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BatchDto {
    private long batchNumber;

    private long productId;

    private float currentTemperature;

    private float minimumTemperature;

    private int initialQuantity;

    private int currentQuantity;

    private LocalDate manufacturingDate;

    private LocalDateTime manufacturingTime;

    private LocalDate dueDate;
}
