package com.mercadolibre.bootcamp.projeto_integrador.util;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.model.Product;
import com.mercadolibre.bootcamp.projeto_integrador.model.Section;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Generate {
    public static InboundOrderRequestDto newInboundRequest() {
        return InboundOrderRequestDto.builder()
                .sectionCode(1)
                .batchStock(List.of(newBatchRequest()))
                .build();
    }

    private static BatchRequestDto newBatchRequest() {
        return BatchRequestDto.builder()
                .productId(1)
                .currentTemperature(-5)
                .minimumTemperature(-25)
                .initialQuantity(15)
                .manufacturingDate(LocalDate.now().minusDays(2))
                .manufacturingTime(LocalDateTime.now().minusDays(2))
                .dueDate(LocalDate.now().plusDays(30))
                .productPrice(new BigDecimal("29.9"))
                .build();
    }

    public static List<BatchRequestDto> list2BatchRequests() {
        List<BatchRequestDto> batchRequests = new ArrayList<>();
        batchRequests.add(BatchRequestDto.builder()
                .productId(2)
                .currentTemperature(15)
                .minimumTemperature(5)
                .initialQuantity(50)
                .manufacturingDate(LocalDate.now().minusDays(2))
                .manufacturingTime(LocalDateTime.now().minusDays(2))
                .dueDate(LocalDate.now().plusDays(20))
                .productPrice(new BigDecimal("5.9"))
                .build()
        );
        batchRequests.add(BatchRequestDto.builder()
                .productId(3)
                .currentTemperature(15)
                .minimumTemperature(5)
                .initialQuantity(20)
                .manufacturingDate(LocalDate.now().minusDays(2))
                .manufacturingTime(LocalDateTime.now().minusDays(2))
                .dueDate(LocalDate.now().plusDays(10))
                .productPrice(new BigDecimal("2.49"))
                .build()
        );

        return batchRequests;
    }

    public static Section newSectionWith1SlotAvailable() {
        return Section.builder()
                .sectionCode(1)
                .category(Section.Category.FRESH)
                .maxBatches(8)
                .currentBatches(7)
                .build();
    }

    public static Section newSectionWith10SlotsAvailable() {
        return Section.builder()
                .sectionCode(1)
                .category(Section.Category.FRESH)
                .maxBatches(10)
                .currentBatches(0)
                .build();
    }

    public static Section newCrowdedSection() {
        return Section.builder()
                .sectionCode(1)
                .category(Section.Category.FRESH)
                .maxBatches(20)
                .currentBatches(20)
                .build();
    }

    public static List<Product> listProducts() {
        List<Product> products = new  ArrayList<>();
        products.add(Product.builder()
                .productId(1)
                .productName("Açaí")
                .brand("Frooty")
                .build());
        products.add(Product.builder()
                .productId(2)
                .productName("Maça")
                .brand("Nacional")
                .category("Frutas")
                .build());
        products.add(Product.builder()
                .productId(3)
                .productName("Alface")
                .brand("Qualitá")
                .category("Vegetais")
                .build());
        products.add(Product.builder()
                .productId(4)
                .productName("Iogurte")
                .brand("Holandês")
                .category("Laticínios")
                .build());
        return products;
    }
}
