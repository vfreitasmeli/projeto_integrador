package com.mercadolibre.bootcamp.projeto_integrador.repository;

import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IBatchRepository extends JpaRepository<Batch, Long> {
    Optional<List<Batch>> findByCurrentQuantityGreaterThanAndDueDateAfter(int minimumQuantity, LocalDate minimumExpirationDate);

    @Query(value = "select * from batch " +
            "inner join inbound_order io on batch.order_number = io.order_number " +
            "inner join section on io.section_code = section.section_code " +
            "where section.category = ?1 and batch.current_quantity > 0 and batch.due_date > \"2022-08-08\"", nativeQuery = true)
    List<Batch> findByCategory(String category, LocalDate minimumExpirationDate);
}
