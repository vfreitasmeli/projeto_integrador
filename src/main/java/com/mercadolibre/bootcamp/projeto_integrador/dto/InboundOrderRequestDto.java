package com.mercadolibre.bootcamp.projeto_integrador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class InboundOrderRequestDto {
    @NotNull(message = "O 'sectionCode' não pode estar vazio")
    private long sectionCode;

    @NotEmpty(message = "A lista de 'batchStock' é obrigatória")
    private List<@Valid BatchRequestDto> batchStock;
}
