package com.mercadolibre.bootcamp.projeto_integrador.integration;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.integration.listeners.ResetDatabase;
import com.mercadolibre.bootcamp.projeto_integrador.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

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
        long batchNumber = batchOfFreshSaved.getBatchNumber();
        float newTemperature = batchOfFreshRequestDto.getCurrentTemperature() + 1;
        batchOfFreshRequestDto.setBatchNumber(batchNumber);
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
                .andExpect(status().isOk()) // TODO no requisito o status era pra ser created, rever status ou nome do teste
                .andExpect(jsonPath("$.batchStock[0].currentTemperature").value(newTemperature))
                .andExpect(jsonPath("$.batchStock[0].batchNumber").value(batchNumber));
    }

    @Test
    void updateInboundOrder_returnCreated_whenBatchNotExists() throws Exception {
        // Generate BatchRequestDto object
        BatchRequestDto batchRequest = getValidBatchRequest(freshProduct);

        // Create InboundOrderRequestDto to send in PUT body
        InboundOrderRequestDto requestDto = new InboundOrderRequestDto();
        requestDto.setSectionCode(freshSection.getSectionCode());
        requestDto.setBatchStock(List.of(batchRequest));

        mockMvc.perform(put("/api/v1/fresh-products/inboundorder")
                        .param("orderNumber", "" + savedFreshInboundOrder.getOrderNumber())
                        .content(asJsonString(requestDto))
                        .header("Manager-Id", manager.getManagerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchStock[0].batchNumber").value(batchOfFreshSaved.getBatchNumber() + 1))
                .andExpect(jsonPath("$.batchStock[0].productPrice").value(batchRequest.getProductPrice()));
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
