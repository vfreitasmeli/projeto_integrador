package com.mercadolibre.bootcamp.projeto_integrador.dto;

import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class InboundOrderResponseDto {
    private List<Batch> batchStock;
}
