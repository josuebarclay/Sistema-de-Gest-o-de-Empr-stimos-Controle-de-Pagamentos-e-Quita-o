package com.gerenciamento.grc.repository;

import com.gerenciamento.grc.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    public List<Cliente> findByNomeContainingIgnoreCase(String nome);

}

