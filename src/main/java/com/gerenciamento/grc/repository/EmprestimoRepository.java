package com.gerenciamento.grc.repository;

import com.gerenciamento.grc.model.Emprestimo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // Método para obter todos os empréstimos em ordem decrescente
    @Query("SELECT e FROM Emprestimo e ORDER BY e.id DESC")
    List<Emprestimo> findAllOrderByIdDesc();

    @Query("select j from Emprestimo j where j.statusEmprestimo = false")
    public List<Emprestimo> findByStatusPendente();

    @Query("select j from Emprestimo j where j.statusEmprestimo = true")
    public List<Emprestimo> findByStatusPago();

    // Busca todos os empréstimos junto com o cliente
    @EntityGraph(attributePaths = {"cliente"})
    List<Emprestimo> findAll();

    @Query("SELECT e FROM Emprestimo e JOIN e.cliente c WHERE c.nome LIKE %:nome%")
    @EntityGraph(attributePaths = {"cliente"})
    List<Emprestimo> findByClienteNomeContainingIgnoreCase(@Param("nome") String nome);

}

