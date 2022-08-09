package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.InboundOrder;
import com.mercadolibre.bootcamp.projeto_integrador.model.Product;
import com.mercadolibre.bootcamp.projeto_integrador.model.Section;
import com.mercadolibre.bootcamp.projeto_integrador.repository.*;
import com.mercadolibre.bootcamp.projeto_integrador.util.GeneratorInboundOrderAndBatch;
import com.mercadolibre.bootcamp.projeto_integrador.util.GeneratorProducts;
import com.mercadolibre.bootcamp.projeto_integrador.util.GeneratorSection;
import com.mercadolibre.bootcamp.projeto_integrador.util.GeneratorWarehouseAndManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    @Mock
    IManagerRepository managerRepository;

    private final long managerId = GeneratorWarehouseAndManager.getManagerWithId().getManagerId();

    @Test
    void create_returnException_whenSectionNoExist() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = GeneratorInboundOrderAndBatch.newInboundRequestDTO();
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.empty());

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inboundService.create(inboundOrderRequest, managerId)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("There is no section with the specified id");
        verify(inboundOrderRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void create_returnException_whenSectionHasNoSpace() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = GeneratorInboundOrderAndBatch.newInboundRequestDTO();
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(GeneratorSection.newCrowdedSection()));
        when(managerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(GeneratorWarehouseAndManager.getManagerWithId()));

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inboundService.create(inboundOrderRequest, managerId)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Section does not have enough space");
        verify(inboundOrderRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void create_returnException_whenProductNoExist() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = GeneratorInboundOrderAndBatch.newInboundRequestDTO();
        inboundOrderRequest.getBatchStock().get(0).setProductId(99);
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(GeneratorSection.getSectionWith1SlotAvailable()));
        when(productRepository.findAllById(ArgumentMatchers.anyList()))
                .thenReturn(new ArrayList<Product>());
        when(managerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(GeneratorWarehouseAndManager.getManagerWithId()));

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inboundService.create(inboundOrderRequest, managerId)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("There is no product with the specified id");
        verify(inboundOrderRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void create_returnException_whenManagerInvalid() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = GeneratorInboundOrderAndBatch.newInboundRequestDTO();
        long managerIdInvalid = 99l;
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(GeneratorSection.getSectionWith1SlotAvailable()));
        when(managerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inboundService.create(inboundOrderRequest, managerIdInvalid)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Manager with id " + managerIdInvalid + " not found");
        verify(inboundOrderRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void create_returnException_whenManagerNotHavePermission() {
        // TODO
    }

    @Test
    void create_returnException_whenProductCategoryNotCompatibleWithSection() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = GeneratorInboundOrderAndBatch.newInboundRequestDTO();
        inboundOrderRequest.getBatchStock().get(0).setProductId(2);
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(GeneratorSection.getSectionWith1SlotAvailable()));
        when(productRepository.findAllById(ArgumentMatchers.anyList()))
                .thenReturn(List.of(GeneratorProducts.newProductChilled()));
        when(managerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(GeneratorWarehouseAndManager.getManagerWithId()));

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inboundService.create(inboundOrderRequest, managerId)
        );

        // Assert
        assertThat(exception.getMessage()).contains("The following products have incompatible category with the section:");
        verify(inboundOrderRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void create_returnBatch_whenSectionHasExactSpaceAvailable() {
        // Arrange
        InboundOrderRequestDto inboundOrderRequest = GeneratorInboundOrderAndBatch.newInboundRequestDTO();
        Product productFresh = GeneratorProducts.newProductFresh();
        productFresh.setProductId(1);
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(GeneratorSection.getSectionWith1SlotAvailable()));
        when(managerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(GeneratorWarehouseAndManager.getManagerWithId()));
        when(productRepository.findAllById(ArgumentMatchers.anyList()))
                .thenReturn(List.of(productFresh));
        when(sectionRepository.save(ArgumentMatchers.any(Section.class)))
                .thenReturn(null);
        when(inboundOrderRepository.save(ArgumentMatchers.any(InboundOrder.class)))
                .thenReturn(null);
        when(batchRepository.saveAll(ArgumentMatchers.anyList()))
                .thenReturn(null);

        // Act
        InboundOrderResponseDto inboundResponse = inboundService.create(inboundOrderRequest, managerId);

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
        InboundOrderRequestDto inboundOrderRequest = GeneratorInboundOrderAndBatch.newInboundRequestDTO();
        inboundOrderRequest.setBatchStock(GeneratorInboundOrderAndBatch.newList2BatchRequestsDTO());
        List<Product> products = new ArrayList<>();
        products.add(GeneratorProducts.newProductFresh());
        products.add(GeneratorProducts.newProductFresh());
        products.get(0).setProductId(2);
        products.get(1).setProductId(3);
        when(sectionRepository.findById(inboundOrderRequest.getSectionCode()))
                .thenReturn(Optional.of(GeneratorSection.getSectionWith10SlotsAvailable()));
        when(managerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(GeneratorWarehouseAndManager.getManagerWithId()));
        when(productRepository.findAllById(ArgumentMatchers.anyList()))
                .thenReturn(products);
        when(sectionRepository.save(ArgumentMatchers.any(Section.class)))
                .thenReturn(null);
        when(inboundOrderRepository.save(ArgumentMatchers.any(InboundOrder.class)))
                .thenReturn(null);
        when(batchRepository.saveAll(ArgumentMatchers.anyList()))
                .thenReturn(null);

        // Act
        InboundOrderResponseDto inboundResponse = inboundService.create(inboundOrderRequest, managerId);

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