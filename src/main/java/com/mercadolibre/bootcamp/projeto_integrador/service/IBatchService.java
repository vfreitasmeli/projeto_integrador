package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;

import java.util.List;

public interface IBatchService {
    Batch update(Batch batch);
    List<Batch> findAll();
    List<Batch> findBatchByCategory(String categoryCode);
}
