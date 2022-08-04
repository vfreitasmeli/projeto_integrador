package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InboundOrderService implements IInboundOrderService{

    @Autowired
    private IInboundOrderService service;

    @Override
    public InboundOrderResponseDto create(InboundOrderRequestDto inboundOrderRequestDto) {
        return null;
    }

    @Override
    public InboundOrderResponseDto update(InboundOrderRequestDto inboundOrderRequestDto) {
        return null;
    }
}
