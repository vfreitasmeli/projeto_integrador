package com.mercadolibre.bootcamp.projeto_integrador.dto;

import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class InboundOrderRequestDto {
    private int orderNumber;
    private LocalDate orderDate;
    private long sectionCode;
    private List<Batch> batchStock;
}
