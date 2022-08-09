package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.exceptions.InitialQuantityException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.InboundOrder;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BatchService implements IBatchService {

    @Autowired
    IBatchRepository batchRepository;

    @Override
    public Batch update(InboundOrder order, Batch batch) {
        Optional<Batch> b = batchRepository.findById(batch.getBatchNumber());
        batch.setInboundOrder(order);
        if (b.isEmpty()) {
            batch.setCurrentQuantity(batch.getInitialQuantity());
            batchRepository.save(batch);
            return batch;
        }
        int selledProducts = b.get().getInitialQuantity() - b.get().getCurrentQuantity();
        batch.setCurrentQuantity(batch.getInitialQuantity() - selledProducts);
        if (batch.getCurrentQuantity() < 0) {
            throw new InitialQuantityException(batch.getInitialQuantity(), selledProducts);
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
