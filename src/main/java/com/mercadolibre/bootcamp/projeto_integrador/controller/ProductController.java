package com.mercadolibre.bootcamp.projeto_integrador.controller;

import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Autowired
    private IProductService service;

    @GetMapping("/fresh-products/warehouse")
    public ResponseEntity<ProductResponseDto> getWarehouses(@RequestParam long productId) {
        return ResponseEntity.ok(service.getWarehouses(productId));
    }
}
