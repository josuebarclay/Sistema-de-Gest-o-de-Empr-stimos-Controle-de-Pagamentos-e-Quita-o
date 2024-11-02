package com.gerenciamento.grc.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Número da parcela é obrigatório")
    private Integer numeroParcela;

    @NotNull(message = "Valor da parcela é obrigatório")
    @Min(value = 0, message = "O valor da parcela deve ser maior que 0")
    private Double valorParcela;

    private boolean pago;
    private LocalDate dataPagamento;

    @ManyToOne
    @JoinColumn(name = "emprestimo_id")
    @NotNull(message = "Empréstimo é obrigatório")
    private Emprestimo emprestimo;

    private LocalDate dataPagamentoParcela;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "Número da parcela é obrigatório") Integer getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(@NotNull(message = "Número da parcela é obrigatório") Integer numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public @NotNull(message = "Valor da parcela é obrigatório") @Min(value = 0, message = "O valor da parcela deve ser maior que 0") Double getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(@NotNull(message = "Valor da parcela é obrigatório") @Min(value = 0, message = "O valor da parcela deve ser maior que 0") Double valorParcela) {
        this.valorParcela = valorParcela;
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public @NotNull(message = "Empréstimo é obrigatório") Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public void setEmprestimo(@NotNull(message = "Empréstimo é obrigatório") Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    public String getDataPagamentoParcelaFormatada() {
        if (this.dataPagamentoParcela != null) {
            return this.dataPagamentoParcela.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }

    public LocalDate getDataPagamentoParcela() {
        return dataPagamentoParcela;
    }

    public void setDataPagamentoParcela(LocalDate dataPagamentoParcela) {
        this.dataPagamentoParcela = dataPagamentoParcela;
    }

    public void setDataPagamentoParcelaFormatada(String format) {
    }
}