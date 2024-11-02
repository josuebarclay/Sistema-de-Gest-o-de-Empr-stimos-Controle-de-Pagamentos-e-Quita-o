package com.gerenciamento.grc.service;

import com.gerenciamento.grc.model.Emprestimo;
import com.gerenciamento.grc.model.Parcela;
import com.gerenciamento.grc.model.RecursoNaoEncontradoException;
import com.gerenciamento.grc.repository.EmprestimoRepository;
import com.gerenciamento.grc.repository.ParcelaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmprestimoService {


    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private ParcelaRepository parcelaRepository;

    public double calcularTotal(double valorEmprestado, int tipoPagamento,double valorParcela,int numeroParcelas) {
        if (tipoPagamento == 0) { // Pagamento diário
            return valorParcela * numeroParcelas;
        } else { // Pagamento semanal
            return valorParcela * numeroParcelas; //
        }
    }

    // Método para gerar as parcelas com as datas
    public List<Parcela> gerarParcelas(Emprestimo emprestimo) {
        List<Parcela> parcelas = new ArrayList<>();

        LocalDate dataInicial = emprestimo.getDataInicio();  // Data de início do empréstimo
        int numeroParcelas = emprestimo.getNumeroParcelas();
        int tipoPagamento = emprestimo.getTipoPagamento();  // 0 = diário, 1 = semanal

        for (int i = 1; i <= numeroParcelas; i++) {
            Parcela parcela = new Parcela();
            parcela.setNumeroParcela(i);
            parcela.setValorParcela(emprestimo.getValorParcela());
            parcela.setPago(false);

            // Calcular a data de pagamento da parcela
            if (tipoPagamento == 0) { // Pagamento diário
                parcela.setDataPagamentoParcela(dataInicial.plusDays(i - 1));  // A cada 1 dia
            } else if (tipoPagamento == 1) { // Pagamento semanal
                parcela.setDataPagamentoParcela(dataInicial.plusWeeks(i - 1));  // A cada 7 dias
            }

            parcelas.add(parcela);
        }

        return parcelas;
    }

    public Long getParcelasRestantes(Emprestimo emprestimo) {
        return parcelaRepository.countParcelasNaoPagas(emprestimo.getId());
    }

}




