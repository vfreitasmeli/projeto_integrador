package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class BatchService implements IBatchService {

    @Autowired
    IBatchRepository batchRepository;

    @Override
    public Batch update(Batch batch) {
        //TODO: BatchNotFound
        Batch b = batchRepository.findById(batch.getBatchNumber())
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        batch.setCurrentQuantity(batch.getInitialQuantity() - (b.getInitialQuantity() - b.getCurrentQuantity()));
        if (batch.getCurrentQuantity() < 0) {
            //TODO: InvalidMaxQuantity
            throw new RuntimeException("Current quantity invalid");
        }
        batchRepository.save(batch);
        return batch;
    }

    @Override
    public List<Batch> findAll() {
        LocalDate minimumExpirationDate = LocalDate.now().plusDays(21);
        List<Batch> batches = batchRepository.findByCurrentQuantityGreaterThanAndDueDateAfter(0, minimumExpirationDate)
                .orElseThrow(() -> new RuntimeException("Nenhum produto encontrado"));
        if (batches.isEmpty()) {
            // TODO BatchNotFound
            throw new RuntimeException("Nenhum produto encontrado");
        }
        return batches;
    }

    @Override
    public List<Batch> findBatchByCategory(String categoryCode) {
        LocalDate minimumExpirationDate = LocalDate.now().plusDays(21);
        List<Batch> batches = batchRepository.findByCategory(getCategory(categoryCode), minimumExpirationDate);
        if (batches.isEmpty()) {
            // TODO BatchNotFound
            throw new RuntimeException("Nenhum produto encontrado");
        }
        return batches;
    }

    private String getCategory(String categoryCode) {
        categoryCode = categoryCode.toUpperCase();
        switch (categoryCode) {
            case "FS":
                return "FRESH";
            case "RF":
                return "CHILLED";
            case "FF":
                return "FROZEN";
            default:
                throw new RuntimeException("Categoria inválida");
        }
    }
}
