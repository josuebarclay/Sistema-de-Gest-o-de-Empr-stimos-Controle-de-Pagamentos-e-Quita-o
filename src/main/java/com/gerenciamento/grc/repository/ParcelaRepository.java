package com.gerenciamento.grc.repository;

import com.gerenciamento.grc.model.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
    List<Parcela> findByEmprestimoId(Long emprestimoId);

    List<Parcela> findByEmprestimoIdOrderByPagoAscNumeroParcelaAsc(Long emprestimoId);

    @Query("SELECT COUNT(p) FROM Parcela p WHERE p.emprestimo.id = :emprestimoId AND p.pago = false")
    Long countParcelasNaoPagas(@Param("emprestimoId") Long emprestimoId);
}