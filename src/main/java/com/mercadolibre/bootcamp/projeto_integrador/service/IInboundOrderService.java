package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderResponseDto;

public interface IInboundOrderService {
    InboundOrderResponseDto create(InboundOrderRequestDto inboundOrderRequestDto);
    InboundOrderResponseDto update(InboundOrderRequestDto inboundOrderRequestDto);
}
