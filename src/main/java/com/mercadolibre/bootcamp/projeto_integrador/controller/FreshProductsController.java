package com.mercadolibre.bootcamp.projeto_integrador.controller;

import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.service.IBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fresh-products")
public class FreshProductsController {
    @Autowired
    IBatchService batchService;

    @GetMapping
    public ResponseEntity<List<Batch>> findAllBatches() {
        return ResponseEntity.ok(batchService.findAll());
    }

    @GetMapping("list")
    public ResponseEntity<List<Batch>> findBatchByCategory(@RequestParam String querytype) {
        return ResponseEntity.ok(batchService.findBatchByCategory(querytype));
    }
}
