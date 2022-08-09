package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.exceptions.BadRequestException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.InitialQuantityException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
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

    /**
     * Método que busca a lista de Batches com estoque positovo e data de validade superior a 20 dias.
     *
     * @return List<Batch>
     */
    @Override
    public List<Batch> findAll() {
        LocalDate minimumExpirationDate = LocalDate.now().plusDays(20);
        List<Batch> batches = batchRepository.findByCurrentQuantityGreaterThanAndDueDateAfter(0, minimumExpirationDate)
                .orElseThrow(() -> new RuntimeException("Something went wrong"));
        if (batches.isEmpty()) {
            throw new NotFoundException("Products", "There are no products in stock");
        }
        return batches;
    }

    /**
     * Método que busca a lista de Batches com estoque positovo e data de validade superior a 20 dias, filtrado por categoria.
     *
     * @param categoryCode
     * @return List<Batch>
     */
    @Override
    public List<Batch> findBatchByCategory(String categoryCode) {
        String category = getCategory(categoryCode);
        LocalDate minimumExpirationDate = LocalDate.now().plusDays(21);
        List<Batch> batches = batchRepository.findByCategory(category, minimumExpirationDate);
        if (batches.isEmpty()) {
            throw new NotFoundException("Products", "There are no products in stock in the requested category");
        }
        return batches;
    }

    /**
     * Método que retorna a categoria do produto dado o código da cateogria.
     *
     * @param categoryCode
     * @return String category
     */
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
                throw new BadRequestException("Invalid category, try again with one of the options: " +
                        "'FS', 'RF' or 'FF' for fresh, chilled or frozen products respectively.");
        }
    }
}
