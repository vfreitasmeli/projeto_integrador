package com.mercadolibre.bootcamp.projeto_integrador.repository;

import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IBatchRepository extends JpaRepository<Batch, Long> {
    Optional<List<Batch>> findByCurrentQuantityGreaterThanAndDueDateAfter(int minimumQuantity, LocalDate minimumExpirationDate);

    Optional<List<Batch>> findByCurrentQuantityGreaterThanAndDueDateAfterAndProduct_CategoryIs(
            int minimumQuantity, LocalDate minimumExpirationDate, Section.Category category);
}
