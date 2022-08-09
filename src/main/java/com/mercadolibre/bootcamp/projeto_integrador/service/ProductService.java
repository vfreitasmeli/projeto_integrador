package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.WarehouseResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IBatchRepository batchRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    public ProductResponseDto getWarehouses(long productId) {
        List<Batch> batchList = batchRepository.findAllByProduct(productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product")));
        List<WarehouseResponseDto> warehouses = new ArrayList<>();
        
        batchList.stream()
                .collect(Collectors.groupingBy(b -> b.getInboundOrder().getSection().getWarehouse().getWarehouseCode(),
                        Collectors.summingInt(b->b.getCurrentQuantity())))
                .forEach((warehouseCode,totalQuantity)->warehouses.add(new WarehouseResponseDto(warehouseCode, totalQuantity)));
        return new ProductResponseDto(productId, warehouses);
    }
}
