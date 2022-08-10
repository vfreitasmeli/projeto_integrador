package com.mercadolibre.bootcamp.projeto_integrador.integration;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.integration.listeners.ResetDatabase;
import com.mercadolibre.bootcamp.projeto_integrador.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ResetDatabase
public class UpdateInboundOrderTest extends BaseControllerTest {
    private Warehouse warehouse;
    private Manager manager;
    private Manager forbiddenManager;
    private Section freshSection;
    private Product freshProduct;

    private InboundOrder savedFreshInboundOrder;
    private BatchRequestDto batchOfFreshRequestDto;
    private Batch batchOfFreshSaved;

    @BeforeEach
    private void setup() {
        warehouse = getSavedWarehouse();

        manager = getSavedManager();
        forbiddenManager = getSavedManager();

        freshSection = getSavedFreshSection(warehouse, manager);
        freshProduct = getSavedFreshProduct();

        savedFreshInboundOrder = getSavedInboundOrder(freshSection);
        batchOfFreshRequestDto = getValidBatchRequest(freshProduct);
        batchOfFreshSaved = getSavedBatch(batchOfFreshRequestDto, savedFreshInboundOrder);
    }

    @Test
    void updateInboundOrder_returnCreated_whenBatchExists() throws Exception {
        // Get the current temperature from batch to make a PUT to update it (specify BatchNumber).
        float newTemperature = batchOfFreshRequestDto.getCurrentTemperature() + 1;
        batchOfFreshRequestDto.setBatchNumber(batchOfFreshSaved.getBatchNumber());
        batchOfFreshRequestDto.setCurrentTemperature(newTemperature);

        // Create InboundOrderRequestDto to send in PUT body
        InboundOrderRequestDto requestDto = new InboundOrderRequestDto();
        requestDto.setSectionCode(freshSection.getSectionCode());
        requestDto.setBatchStock(List.of(batchOfFreshRequestDto));

        mockMvc.perform(put("/api/v1/fresh-products/inboundorder")
                        .param("orderNumber", "" + savedFreshInboundOrder.getOrderNumber())
                        .content(asJsonString(requestDto))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // TODO no requisito o status era pra ser created, rever status ou nome do teste

        Batch batch = batchRepository.findById(batchOfFreshSaved.getBatchNumber()).get(); // TODO devemos consultar no BD mesmo?
        assertThat(batch.getCurrentTemperature()).isEqualTo(newTemperature);
    }

    @Test
    void updateInboundOrder_returnCreated_whenBatchNotExists() throws Exception {
        // Generate BatchRequestDto object
        BatchRequestDto batchRequest = getValidBatchRequest(freshProduct);

        // Create InboundOrderRequestDto to send in PUT body
        InboundOrderRequestDto requestDto = new InboundOrderRequestDto();
        requestDto.setSectionCode(freshSection.getSectionCode());
        requestDto.setBatchStock(List.of(batchRequest));

        MvcResult response = mockMvc.perform(put("/api/v1/fresh-products/inboundorder")
                        .param("orderNumber", "" + savedFreshInboundOrder.getOrderNumber())
                        .content(asJsonString(requestDto))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = response.getResponse().getContentAsString();
        InboundOrderResponseDto responseDto = objectMapper.readValue(json, InboundOrderResponseDto.class);

        Batch batch = batchRepository.findById(responseDto.getBatchStock().get(0).getBatchNumber()).get();
        assertThat(batch).isNotNull();
        assertThat(batch.getInboundOrder().getOrderNumber()).isEqualTo(savedFreshInboundOrder.getOrderNumber());
    }

    @Test
    void updateInboundOrder_returnNotFound_whenInboundOrderIdNotExists() throws Exception {
        // Create InboundOrderRequestDto to send in PUT body
        InboundOrderRequestDto requestDto = new InboundOrderRequestDto(); // TODO refac
        requestDto.setSectionCode(freshSection.getSectionCode());
        requestDto.setBatchStock(List.of(batchOfFreshRequestDto));

        mockMvc.perform(put("/api/v1/fresh-products/inboundorder")
                        .param("orderNumber", "" + savedFreshInboundOrder.getOrderNumber() + 1)
                        .content(asJsonString(requestDto))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.name").value("Inbound Order not found."));
    }

    @Test
    void updateInboundOrder_returnBadRequest_whenSectionDoesNotHaveEnoughSpace() throws Exception {
        int availableSpace = freshSection.getMaxBatches() - freshSection.getCurrentBatches();

        List<BatchRequestDto> batches = new ArrayList<>();
        for (int i = 0; i < availableSpace+1; i++) {
            batches.add(batchOfFreshRequestDto);
        }
        // Create InboundOrderRequestDto to send in PUT body
        InboundOrderRequestDto requestDto = new InboundOrderRequestDto();
        requestDto.setSectionCode(freshSection.getSectionCode());
        requestDto.setBatchStock(batches);

        mockMvc.perform(put("/api/v1/fresh-products/inboundorder")
                        .param("orderNumber", "" + savedFreshInboundOrder.getOrderNumber())
                        .content(asJsonString(requestDto))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Section"))
                .andExpect(jsonPath("$.message", containsString("enough space")));
    }

    @Test
    void updateInboundOrder_returnBadRequest_whenInvalidInitialQuantity() throws Exception {
        // Change initial quantity to less than what have been sold
        batchOfFreshRequestDto.setBatchNumber(batchOfFreshSaved.getBatchNumber());
        batchOfFreshRequestDto.setInitialQuantity(batchOfFreshSaved.getInitialQuantity()
                - (batchOfFreshSaved.getCurrentQuantity() + 1));

        // Create InboundOrderRequestDto to send in PUT body
        InboundOrderRequestDto requestDto = new InboundOrderRequestDto();
        requestDto.setSectionCode(freshSection.getSectionCode());
        requestDto.setBatchStock(List.of(batchOfFreshRequestDto));

        mockMvc.perform(put("/api/v1/fresh-products/inboundorder")
                        .param("orderNumber", "" + savedFreshInboundOrder.getOrderNumber())
                        .content(asJsonString(requestDto))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Invalid batch quantity"))
                .andExpect(jsonPath("$.message", containsString("update batch initial quantity")));
    }

    @Test
    void updateInboundOrder_returnForbidden_whenIsGivenAManagerThatDoesNotHavePermission() throws Exception {
        BatchRequestDto batchRequest = getValidBatchRequest(freshProduct);
        InboundOrderRequestDto requestDto = getValidInboundOrderRequestDto(freshSection, batchRequest);

        mockMvc.perform(put("/api/v1/fresh-products/inboundorder")
                        .param("orderNumber", "" + savedFreshInboundOrder.getOrderNumber())
                        .content(asJsonString(requestDto))
                        .header("Manager-Id", forbiddenManager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateInboundOrder_returnBadRequest_whenIsNotGivenManagerIdHeader() throws Exception {
        BatchRequestDto batchRequest = getValidBatchRequest(freshProduct);
        InboundOrderRequestDto requestDto = getValidInboundOrderRequestDto(freshSection, batchRequest);

        mockMvc.perform(put("/api/v1/fresh-products/inboundorder")
                        .param("orderNumber", "" + savedFreshInboundOrder.getOrderNumber())
                        .content(asJsonString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Header Manager-Id is required"));
    }

    @Test
    void updateInboundOrder_returnBadRequest_whenIsGivenIncompatibleProducts() throws Exception {
        Product frozenProduct = getSavedProduct(Section.Category.FROZEN);

        BatchRequestDto batchOfFrozenRequest = getValidBatchRequest(frozenProduct);
        InboundOrderRequestDto incompatibleRequestDto = getValidInboundOrderRequestDto(freshSection, batchOfFrozenRequest);

        mockMvc.perform(put("/api/v1/fresh-products/inboundorder")
                        .param("orderNumber", "" + savedFreshInboundOrder.getOrderNumber())
                        .content(asJsonString(incompatibleRequestDto))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Incompatible category"));
    }
}
