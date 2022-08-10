package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchBuyerResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import com.mercadolibre.bootcamp.projeto_integrador.util.BatchGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @InjectMocks
    private BatchService service;

    @Mock
    private IBatchRepository batchRepository;

    private List<Batch> batches;

    @BeforeEach
    private void setup() {
        batches = BatchGenerator.newBatchList();
    }

    @Test
    void findAll_returnBatches_whenBatchesExists() {
        // Arrange
        when(batchRepository.findByCurrentQuantityGreaterThanAndDueDateAfter(ArgumentMatchers.anyInt(), ArgumentMatchers.any()))
                .thenReturn(batches);
        // Act
        List<BatchBuyerResponseDto> foundBatches = service.findAll();

        // Assert
        assertThat(foundBatches).isNotEmpty();
        assertEquals(foundBatches.size(), batches.size());
        assertEquals(foundBatches.get(0).getBatchNumber(), batches.get(0).getBatchNumber());
        assertEquals(foundBatches.get(1).getBatchNumber(), batches.get(1).getBatchNumber());
        assertEquals(foundBatches.get(2).getBatchNumber(), batches.get(2).getBatchNumber());
    }

    @Test
    void findAll_returnNotFoundException_whenBatchesNotExists() {
        // Arrange
        batches.clear();
        when(batchRepository.findByCurrentQuantityGreaterThanAndDueDateAfter(ArgumentMatchers.anyInt(), ArgumentMatchers.any()))
                .thenReturn(batches);

        // Act
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findAll());

        // Assert
        assertThat(exception.getName()).contains("Products");
        assertEquals(exception.getMessage(), "There are no products in stock");
    }

    @Test
    void findBatchByCategory() {
        // Arrange

        // Act

        // Assert
    }
}