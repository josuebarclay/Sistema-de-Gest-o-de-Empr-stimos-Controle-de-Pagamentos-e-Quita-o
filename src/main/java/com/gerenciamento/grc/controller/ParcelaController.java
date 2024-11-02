package com.gerenciamento.grc.controller;

import com.gerenciamento.grc.model.Emprestimo;
import com.gerenciamento.grc.model.Parcela;
import com.gerenciamento.grc.model.RecursoNaoEncontradoException;
import com.gerenciamento.grc.repository.EmprestimoRepository;
import com.gerenciamento.grc.repository.ParcelaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/parcelas")
public class ParcelaController {

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @PatchMapping("/{parcelaId}/pagar")
    public ResponseEntity<?> marcarComoPago(@PathVariable Long parcelaId) {
        Parcela parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Parcela não encontrada com o ID: " + parcelaId));

        // Marca a parcela como paga
        parcela.setPago(true);
        parcela.setDataPagamento(LocalDate.now());
        parcelaRepository.save(parcela);

        // Verifica se todas as parcelas do empréstimo estão pagas
        Emprestimo emprestimo = parcela.getEmprestimo();
        List<Parcela> parcelasDoEmprestimo = parcelaRepository.findByEmprestimoId(emprestimo.getId());

        boolean todasPagas = parcelasDoEmprestimo.stream().allMatch(Parcela::isPago);

        // Se todas as parcelas estiverem pagas, marca o empréstimo como quitado
        if (todasPagas) {
            emprestimo.setStatusEmprestimo(true); // Atualiza o campo pago no empréstimo
            emprestimoRepository.save(emprestimo);
        }

        return ResponseEntity.ok().build();
    }




}