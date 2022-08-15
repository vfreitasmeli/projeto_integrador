package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.WarehouseResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.ManagerNotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IManagerRepository;
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

    @Autowired
    private IManagerRepository managerRepository;

    /**
     * Metodo que retorna todos armazens que contenham um determinado item com as quantidades totais.
     * @param productId long representando o id do produto
     * @return ProductResponseDto contendo o id do produto com uma lista de códigos de armazens com quantidades do produto.
     */
    @Override
    public ProductResponseDto getWarehouses(long productId, long managerId) {
        managerRepository.findById(managerId).orElseThrow(() -> new ManagerNotFoundException(managerId));
        List<Batch> batchList = batchRepository.findAllByProduct(productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product")));
        List<WarehouseResponseDto> warehouses = new ArrayList<>();
        
        batchList.stream()
                .collect(Collectors.groupingBy(b -> b.getInboundOrder().getSection().getWarehouse().getWarehouseCode(),
                        Collectors.summingInt(b->b.getCurrentQuantity())))
                .forEach((warehouseCode,totalQuantity)->warehouses.add(new WarehouseResponseDto(warehouseCode, totalQuantity)));
        return new ProductResponseDto(productId, warehouses);
    }
}
