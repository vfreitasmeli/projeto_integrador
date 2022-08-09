package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductResponseDto;

public interface IProductService {

    public ProductResponseDto getWarehouses(long productId);
}
