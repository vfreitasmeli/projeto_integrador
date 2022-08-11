package com.mercadolibre.bootcamp.projeto_integrador.integration;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.integration.listeners.ResetDatabase;
import com.mercadolibre.bootcamp.projeto_integrador.model.*;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IInboundOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ResetDatabase
public class CreateInboundOrderTest extends BaseControllerTest {
    @Autowired
    IInboundOrderRepository inboundOrderRepository;

    @Autowired
    IBatchRepository batchRepository;

    private Manager manager;
    private Manager forbiddenManager;
    private Section sectionWithChilled;
    private Product frozenProduct;

    private InboundOrderRequestDto validInboundOrderRequest;
    private InboundOrderRequestDto invalidInboundOrderRequest;

    @BeforeEach
    void setup() {
        Warehouse warehouse = getSavedWarehouse();

        manager = getSavedManager();
        forbiddenManager = getSavedManager();

        Section section = getSavedFreshSection(warehouse, manager);
        sectionWithChilled = getSavedSection(warehouse, manager, Section.Category.CHILLED);

        Product product = getSavedFreshProduct();
        frozenProduct = getSavedProduct(Section.Category.FROZEN);

        validInboundOrderRequest = getValidInboundOrderRequestDto(section, getValidBatchRequest(product));
        invalidInboundOrderRequest = getValidInboundOrderRequestDto(section, getInvalidBatchRequestDto(product));
    }

    @Test
    void createInboundOrder_returnsOk_whenIsGivenAValidInput() throws Exception {
        int quantityInboundOrder = inboundOrderRepository.findAll().size();
        int quantityBatch = batchRepository.findAll().size();

        mockMvc.perform(post("/api/v1/fresh-products/inboundorder")
                        .content(asJsonString(validInboundOrderRequest))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertThat(inboundOrderRepository.findAll().size()).isEqualTo(quantityInboundOrder + 1);
        assertThat(batchRepository.findAll().size()).isEqualTo(quantityBatch + 1);
    }

    @Test // TODO cobrir todos os validations
    void createInboundOrder_returnsError_whenIsGivenAnInvalidInput() throws Exception {
        int quantityInboundOrder = inboundOrderRepository.findAll().size();
        int quantityBatch = batchRepository.findAll().size();

        mockMvc.perform(post("/api/v1/fresh-products/inboundorder")
                        .content(asJsonString(invalidInboundOrderRequest))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        assertThat(inboundOrderRepository.findAll().size()).isEqualTo(quantityInboundOrder);
        assertThat(batchRepository.findAll().size()).isEqualTo(quantityBatch);
    }

    @Test
    void createInboundOrder_returnsError_whenIsGivenAManagerThatDoesNotHavePermission() throws Exception {
        int quantityInboundOrder = inboundOrderRepository.findAll().size();
        int quantityBatch = batchRepository.findAll().size();

        mockMvc.perform(post("/api/v1/fresh-products/inboundorder")
                        .content(asJsonString(validInboundOrderRequest))
                        .header("Manager-Id", forbiddenManager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertThat(inboundOrderRepository.findAll().size()).isEqualTo(quantityInboundOrder);
        assertThat(batchRepository.findAll().size()).isEqualTo(quantityBatch);
    }

    @Test
    void createInboundOrder_returnsError_whenIsNotGivenManagerIdHeader() throws Exception {
        int quantityInboundOrder = inboundOrderRepository.findAll().size();
        int quantityBatch = batchRepository.findAll().size();

        mockMvc.perform(post("/api/v1/fresh-products/inboundorder")
                        .content(asJsonString(validInboundOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Header Manager-Id is required"));

        assertThat(inboundOrderRepository.findAll().size()).isEqualTo(quantityInboundOrder);
        assertThat(batchRepository.findAll().size()).isEqualTo(quantityBatch);
    }

    @Test
    void createInboundOrder_returnsError_whenIsGivenIncompatibleProducts() throws Exception {
        int quantityInboundOrder = inboundOrderRepository.findAll().size();
        int quantityBatch = batchRepository.findAll().size();
        BatchRequestDto batchRequest = getValidBatchRequest(frozenProduct);
        InboundOrderRequestDto requestDto = getValidInboundOrderRequestDto(sectionWithChilled, batchRequest);

        mockMvc.perform(post("/api/v1/fresh-products/inboundorder")
                        .content(asJsonString(requestDto))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Incompatible category"));

        assertThat(inboundOrderRepository.findAll().size()).isEqualTo(quantityInboundOrder);
        assertThat(batchRepository.findAll().size()).isEqualTo(quantityBatch);
    }

    @Test
    void createInboundOrder_returnsBadRequest_whenSectionNoExist() throws Exception {
        // TODO
    }

    @Test
    void createInboundOrder_returnsBadRequest_whenSectionHasNoSpace() throws Exception {
        // TODO
    }
}
