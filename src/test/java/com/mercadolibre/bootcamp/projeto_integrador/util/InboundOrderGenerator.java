package com.mercadolibre.bootcamp.projeto_integrador.util;

import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.model.InboundOrder;

import java.time.LocalDate;
import java.util.List;

public class InboundOrderGenerator {
    public static InboundOrderRequestDto newInboundRequestDTO() {
        return InboundOrderRequestDto.builder()
                .sectionCode(1)
                .batchStock(List.of(BatchGenerator.newBatchRequestDTO()))
                .build();
    }

    public static InboundOrder newFreshInboundOrder() {
        InboundOrder inboundOrder = new InboundOrder();
        inboundOrder.setOrderNumber(1l);
        inboundOrder.setSection(SectionGenerator.getFreshSectionWith1SlotAvailable());
        inboundOrder.setOrderDate(LocalDate.now());
        return inboundOrder;
    }
}
