package com.mercadolibre.bootcamp.projeto_integrador.util;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.InboundOrder;
import com.mercadolibre.bootcamp.projeto_integrador.model.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BatchGenerator {
    public static BatchRequestDto newBatchRequestDTO() {
        return BatchRequestDto.builder()
                .productId(1)
                .currentTemperature(15.0f)
                .minimumTemperature(5.0f)
                .initialQuantity(15)
                .manufacturingDate(LocalDate.now().minusDays(2))
                .manufacturingTime(LocalDateTime.now().minusDays(2))
                .dueDate(LocalDate.now().plusDays(30))
                .productPrice(new BigDecimal("5.9"))
                .build();
    }

    public static List<BatchRequestDto> newList2BatchRequestsDTO() {
        List<BatchRequestDto> batchRequests = new ArrayList<>();
        batchRequests.add(BatchRequestDto.builder()
                .productId(2)
                .currentTemperature(-5.0f)
                .minimumTemperature(-15.0f)
                .initialQuantity(50)
                .manufacturingDate(LocalDate.now().minusDays(2))
                .manufacturingTime(LocalDateTime.now().minusDays(2))
                .dueDate(LocalDate.now().plusWeeks(3))
                .productPrice(new BigDecimal("5.9"))
                .build()
        );
        batchRequests.add(BatchRequestDto.builder()
                .productId(3)
                .currentTemperature(-5.0f)
                .minimumTemperature(-15.0f)
                .initialQuantity(20)
                .manufacturingDate(LocalDate.now().minusDays(2))
                .manufacturingTime(LocalDateTime.now().minusDays(2))
                .dueDate(LocalDate.now().plusDays(10))
                .productPrice(new BigDecimal("2.49"))
                .build()
        );

        return batchRequests;
    }

    public static Batch newBatch(Product product, InboundOrder order) {
        Batch batch = new Batch();
        batch.setInitialQuantity(15);
        batch.setCurrentQuantity(batch.getInitialQuantity());
        batch.setCurrentTemperature(15.0f);
        batch.setMinimumTemperature(5.0f);
        batch.setManufacturingDate(LocalDate.now().minusDays(2));
        batch.setManufacturingTime(LocalDateTime.now().minusDays(2));
        batch.setDueDate(LocalDate.now().plusDays(10));
        batch.setProductPrice(new BigDecimal("2.49"));
        batch.setProduct(product);
        batch.setInboundOrder(order);
        return batch;
    }
}
