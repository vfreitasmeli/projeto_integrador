package com.mercadolibre.bootcamp.projeto_integrador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
public class InboundOrderRequestDto {
    @NotBlank(message = "O 'sectionCode' não pode estar vazio")
    private long sectionCode;

    @NotEmpty(message = "A lista de 'batchStock' é obrigatória")
    private List<@Valid BatchRequestDto> batchStock;
}
