package com.mercadolibre.bootcamp.projeto_integrador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class InboundOrderRequestDto {
    private long sectionCode;
    private List<BatchRequestDto> batchStock;
}
