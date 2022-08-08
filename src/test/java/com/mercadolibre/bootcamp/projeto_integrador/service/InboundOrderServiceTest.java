package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.InboundOrder;
import com.mercadolibre.bootcamp.projeto_integrador.model.Section;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IInboundOrderRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IProductRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.ISectionRepository;
import com.mercadolibre.bootcamp.projeto_integrador.util.Generate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InboundOrderServiceTest {
    @InjectMocks
    private InboundOrderService inboundService;

    @Mock
    IProductRepository productRepository;

    @Mock
    ISectionRepository sectionRepository;

    @Mock
    IInboundOrderRepository inboundOrderRepository;

    @Mock
    IBatchRepository batchRepository;

    @Test
    void create_returnException_whenSectionNoExist() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = Generate.newInboundRequest();
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.empty());

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inboundService.create(inboundOrderRequest)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("There is no section with the specified id");
    }

    @Test
    void create_returnException_whenSectionHasNoSpace() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = Generate.newInboundRequest();
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(Generate.newCrowdedSection()));

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inboundService.create(inboundOrderRequest)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Section does not have enough space");
    }

    @Test
    void create_returnException_whenProductNoExist() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = Generate.newInboundRequest();
        inboundOrderRequest.getBatchStock().get(0).setProductId(99);
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(Generate.newSectionWith1SlotAvailable()));
        when(productRepository.findAllById(ArgumentMatchers.anyList()))
                .thenReturn(Generate.listProducts());

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inboundService.create(inboundOrderRequest)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("There is no product with the specified id");
    }

    @Test
    void create_returnBatch_whenSectionHasExactSpaceAvailable() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = Generate.newInboundRequest();
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(Generate.newSectionWith1SlotAvailable()));
        when(productRepository.findAllById(ArgumentMatchers.anyList()))
                .thenReturn(Generate.listProducts());
        when(sectionRepository.save(ArgumentMatchers.any(Section.class)))
                .thenReturn(null);
        when(inboundOrderRepository.save(ArgumentMatchers.any(InboundOrder.class)))
                .thenReturn(null);
        when(batchRepository.saveAll(ArgumentMatchers.anyList()))
                .thenReturn(null);

        // Act
        InboundOrderResponseDto inboundResponse = inboundService.create(inboundOrderRequest);

        // Assert
        assertThat(inboundResponse).isNotNull();
        Batch batchResponse = inboundResponse.getBatchStock().get(0);
        BatchRequestDto batchRequest = inboundOrderRequest.getBatchStock().get(0);
        assertThat(batchResponse.getBatchNumber()).isEqualTo(batchRequest.getBatchNumber());
        assertThat(batchResponse.getProduct().getProductId()).isEqualTo(batchRequest.getProductId());
        assertThat(batchResponse.getCurrentTemperature()).isEqualTo(batchRequest.getCurrentTemperature());
        assertThat(batchResponse.getMinimumTemperature()).isEqualTo(batchRequest.getMinimumTemperature());
        assertThat(batchResponse.getInitialQuantity()).isEqualTo(batchRequest.getInitialQuantity());
        assertThat(batchResponse.getCurrentQuantity()).isEqualTo(batchRequest.getInitialQuantity());
        assertThat(batchResponse.getManufacturingDate()).isEqualTo(batchRequest.getManufacturingDate());
        assertThat(batchResponse.getManufacturingTime()).isEqualTo(batchRequest.getManufacturingTime());
        assertThat(batchResponse.getDueDate()).isEqualTo(batchRequest.getDueDate());
        assertThat(batchResponse.getProductPrice()).isEqualTo(batchRequest.getProductPrice());
    }

    @Test
    void create_returnBatches_whenSectionHasMoreSpaceAvailable() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = Generate.newInboundRequest();
        inboundOrderRequest.setBatchStock(Generate.list2BatchRequests());
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(Generate.newSectionWith10SlotsAvailable()));
        when(productRepository.findAllById(ArgumentMatchers.anyList()))
                .thenReturn(Generate.listProducts());
        when(sectionRepository.save(ArgumentMatchers.any(Section.class)))
                .thenReturn(null);
        when(inboundOrderRepository.save(ArgumentMatchers.any(InboundOrder.class)))
                .thenReturn(null);
        when(batchRepository.saveAll(ArgumentMatchers.anyList()))
                .thenReturn(null);

        // Act
        InboundOrderResponseDto inboundResponse = inboundService.create(inboundOrderRequest);

        // Assert
        assertThat(inboundResponse).isNotNull();
        Batch batchResponse = inboundResponse.getBatchStock().get(0);
        BatchRequestDto batchRequest = inboundOrderRequest.getBatchStock().get(0);
        assertThat(batchResponse.getBatchNumber()).isEqualTo(batchRequest.getBatchNumber());
        assertThat(batchResponse.getProduct().getProductId()).isEqualTo(batchRequest.getProductId());
        assertThat(batchResponse.getCurrentTemperature()).isEqualTo(batchRequest.getCurrentTemperature());
        assertThat(batchResponse.getMinimumTemperature()).isEqualTo(batchRequest.getMinimumTemperature());
        assertThat(batchResponse.getInitialQuantity()).isEqualTo(batchRequest.getInitialQuantity());
        assertThat(batchResponse.getCurrentQuantity()).isEqualTo(batchRequest.getInitialQuantity());
        assertThat(batchResponse.getManufacturingDate()).isEqualTo(batchRequest.getManufacturingDate());
        assertThat(batchResponse.getManufacturingTime()).isEqualTo(batchRequest.getManufacturingTime());
        assertThat(batchResponse.getDueDate()).isEqualTo(batchRequest.getDueDate());
        assertThat(batchResponse.getProductPrice()).isEqualTo(batchRequest.getProductPrice());
        assertThat(inboundResponse.getBatchStock().get(1).getCurrentQuantity())
                .isEqualTo(inboundOrderRequest.getBatchStock().get(1).getInitialQuantity());
    }
}