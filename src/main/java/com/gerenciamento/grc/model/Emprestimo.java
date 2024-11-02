package com.gerenciamento.grc.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data de início é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;

    @NotNull(message = "Tipo de pagamento é obrigatório")
    private Integer tipoPagamento; // 0 = diário, 1 = semanal

    @NotNull(message = "Valor emprestado é obrigatório")
    @Min(value = 0, message = "O valor emprestado deve ser maior que 0")
    private Double valorEmprestado;

    @NotNull(message = "Número de parcelas é obrigatório")
    @Min(value = 1, message = "Número de parcelas deve ser no mínimo 1")
    private Integer numeroParcelas;

    @NotNull(message = "Valor da parcela é obrigatório")
    @Min(value = 0, message = "O valor da parcela deve ser maior que 0")
    private Double valorParcela;

    private Double total;
    private Double saldoDevedor;
    private boolean statusEmprestimo;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @NotNull(message = "Cliente é obrigatório")
    private Cliente cliente;

    @OneToMany(mappedBy = "emprestimo", cascade = CascadeType.ALL)
    private List<Parcela> parcelas;

    private LocalDate dataPagamento;
    private BigDecimal valorPago;
    private String observacao;
    private Long parcelasRestantes;



    public Double getTotal() {
        if (valorParcela != null && numeroParcelas != null) {
            return valorParcela * numeroParcelas; // Total é o valor da parcela vezes o número de parcelas
        }
        return 0.0; // Retorna 0 caso algum valor seja nulo
    }

    public void calcularSaldoDevedor() {
        double totalPago = 0.0;
        for (Parcela parcela : parcelas) {
            if (parcela.isPago()) {
                totalPago += parcela.getValorParcela();
            }
        }
        this.saldoDevedor = getTotal() - totalPago;
    }


    public Double getSaldoDevedor() {
        return saldoDevedor;
    }

    public void setSaldoDevedor(Double saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "Data de início é obrigatória") LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(@NotNull(message = "Data de início é obrigatória") LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public @NotNull(message = "Tipo de pagamento é obrigatório") Integer getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(@NotNull(message = "Tipo de pagamento é obrigatório") Integer tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public @NotNull(message = "Valor emprestado é obrigatório") @Min(value = 0, message = "O valor emprestado deve ser maior que 0") Double getValorEmprestado() {
        return valorEmprestado;
    }

    public void setValorEmprestado(@NotNull(message = "Valor emprestado é obrigatório") @Min(value = 0, message = "O valor emprestado deve ser maior que 0") Double valorEmprestado) {
        this.valorEmprestado = valorEmprestado;

    }

    public @NotNull(message = "Número de parcelas é obrigatório") @Min(value = 1, message = "Número de parcelas deve ser no mínimo 1") Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(@NotNull(message = "Número de parcelas é obrigatório") @Min(value = 1, message = "Número de parcelas deve ser no mínimo 1") Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public @NotNull(message = "Valor da parcela é obrigatório") @Min(value = 0, message = "O valor da parcela deve ser maior que 0") Double getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(@NotNull(message = "Valor da parcela é obrigatório") @Min(value = 0, message = "O valor da parcela deve ser maior que 0") Double valorParcela) {
        this.valorParcela = valorParcela;
    }

    public @NotNull(message = "Cliente é obrigatório") Cliente getCliente() {
        return cliente;
    }

    public void setCliente(@NotNull(message = "Cliente é obrigatório") Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Parcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<Parcela> parcelas) {
        this.parcelas = parcelas;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isStatusEmprestimo() {
        return statusEmprestimo;
    }

    public void setStatusEmprestimo(boolean statusEmprestimo) {
        this.statusEmprestimo = statusEmprestimo;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public long getParcelasRestantes() {
        return parcelasRestantes;
    }

    public void setParcelasRestantes(long parcelasRestantes) {
        this.parcelasRestantes = parcelasRestantes;
    }
}
