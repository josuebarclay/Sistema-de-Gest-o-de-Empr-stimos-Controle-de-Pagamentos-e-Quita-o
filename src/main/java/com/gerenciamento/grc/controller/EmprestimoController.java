package com.gerenciamento.grc.controller;

import com.gerenciamento.grc.model.*;
import com.gerenciamento.grc.repository.ClienteRepository;
import com.gerenciamento.grc.repository.EmprestimoRepository;
import com.gerenciamento.grc.repository.ParcelaRepository;
import com.gerenciamento.grc.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/receita")
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private ParcelaRepository parcelaRepository;

    @GetMapping("/cadastroReceita")
    public ModelAndView emprestimo() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("receita/cadastro");

        // Obtenha a lista de clientes e ordene-a em ordem alfabética
        List<Cliente> clientes = clienteRepository.findAll();
        clientes.sort(Comparator.comparing(Cliente::getNome)); // Ordena por nome

        mv.addObject("clientes", clientes);
        mv.addObject("emprestimo", new Emprestimo());
        return mv;
    }

    @PostMapping("/salvar")
    public ModelAndView salvarReceita(@ModelAttribute Emprestimo emprestimo) {
        // Associar cliente ao empréstimo
        Cliente cliente = clienteRepository.findById(emprestimo.getCliente().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
        emprestimo.setCliente(cliente);

        // Salvar o empréstimo no banco de dados
        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);

        // Gerar e associar as parcelas
        List<Parcela> parcelas = emprestimoService.gerarParcelas(emprestimoSalvo);

        // Associar as parcelas ao empréstimo e salvar as parcelas
        for (Parcela parcela : parcelas) {
            parcela.setEmprestimo(emprestimoSalvo); // Relaciona com o mesmo ID de empréstimo
        }

        parcelaRepository.saveAll(parcelas); // Salvar todas as parcelas no banco de dados

        // Redirecionar para a lista de receitas
        return new ModelAndView("redirect:/receita/listaReceita");
    }


    @GetMapping("/listaReceita")
    public ModelAndView listaReceita() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("receita/list-emprestimo");

        List<Emprestimo> emprestimos = emprestimoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        mv.addObject("emprestimos", emprestimos);

        // Adicionar a quantidade de parcelas restantes a cada empréstimo
        for (Emprestimo emprestimo : emprestimos) {
            Long parcelasRestantes = emprestimoService.getParcelasRestantes(emprestimo);
            emprestimo.setParcelasRestantes(parcelasRestantes); // Você pode adicionar um getter/setter para isso
        }

        return mv;
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("receita/editar");

        // Buscar o empréstimo pelo ID
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        // Passar o empréstimo para o formulário de edição
        mv.addObject("emprestimo", emprestimo);
        return mv;
    }

    @PostMapping("/editar-emprestimo")
    public ModelAndView editarReceita(@ModelAttribute("emprestimo") Emprestimo emprestimoEditado) {
        // Buscar o empréstimo existente pelo ID
        Emprestimo emprestimoExistente = emprestimoRepository.findById(emprestimoEditado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado para o ID: " + emprestimoEditado.getId()));

        // Verificar se o empréstimo possui parcelas pagas
        boolean temParcelasPagas = emprestimoExistente.getParcelas().stream()
                .anyMatch(parcela -> parcela.isPago());

        if (temParcelasPagas) {
            // Se houver parcelas pagas, não permitir a edição e redirecionar com uma mensagem de erro
            ModelAndView mv = new ModelAndView("redirect:/receita/editar/" + emprestimoEditado.getId());
            mv.addObject("error", "Não é possível editar o empréstimo. Existem parcelas pagas.");
            return mv;
        }

        // Atualizar os dados do empréstimo existente com os valores editados
        emprestimoExistente.setTipoPagamento(emprestimoEditado.getTipoPagamento());
        emprestimoExistente.setDataInicio(emprestimoEditado.getDataInicio());
        emprestimoExistente.setValorEmprestado(emprestimoEditado.getValorEmprestado());
        emprestimoExistente.setNumeroParcelas(emprestimoEditado.getNumeroParcelas());
        emprestimoExistente.setValorParcela(emprestimoEditado.getValorParcela());


        // Salvar o empréstimo editado no banco de dados
        emprestimoRepository.save(emprestimoExistente);

        // Redirecionar para a lista de receitas após a edição
        return new ModelAndView("redirect:/receita/listaReceita");
    }


    @GetMapping("/ex_emprestimo")
    public ModelAndView listaExcluirReceita() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("receita/list-emprestimo-editar");

        // Use o novo método para obter os empréstimos em ordem decrescente
        List<Emprestimo> emprestimos = emprestimoRepository.findAllOrderByIdDesc();
        mv.addObject("emprestimos", emprestimos);

        return mv;
    }


    @GetMapping("/excluir/{id}")
    public ModelAndView excluirEmprestimo(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // Buscar o empréstimo pelo ID
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado para o ID: " + id));

        // Verificar se o empréstimo possui parcelas pagas
        boolean temParcelasPagas = emprestimo.getParcelas().stream()
                .anyMatch(parcela -> parcela.isPago());

        if (temParcelasPagas) {
            // Se houver parcelas pagas, adicionar a mensagem de erro e redirecionar para a página de mensagem
            redirectAttributes.addFlashAttribute("error", "Não é possível excluir o empréstimo. Existem parcelas pagas.");
            return new ModelAndView("redirect:/receita/mensagem");
        }

        // Se não houver parcelas pagas, excluir o empréstimo e redirecionar para a página de mensagem
        emprestimoRepository.delete(emprestimo);
        redirectAttributes.addFlashAttribute("success", "Empréstimo excluído com sucesso.");
        return new ModelAndView("redirect:/receita/listaReceita");
    }

    @GetMapping("/mensagem")
    public ModelAndView mensagem() {
        return new ModelAndView("/receita/mensagem"); // Retorna o nome do template mensagem.html
    }

    @GetMapping("/{emprestimoId}/parcelas")
    public ModelAndView listarParcelas(@PathVariable Long emprestimoId) {
        ModelAndView mv = new ModelAndView("receita/list-parcelas");

        // Use o método ordenado
        List<Parcela> parcelas = parcelaRepository.findByEmprestimoIdOrderByPagoAscNumeroParcelaAsc(emprestimoId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        parcelas.forEach(parcela -> {
            if (parcela.getDataPagamentoParcela() != null) {
                parcela.setDataPagamentoParcelaFormatada(parcela.getDataPagamentoParcela().format(formatter));
            }
        });

        mv.addObject("parcelas", parcelas);
        return mv;
    }

    @GetMapping("/{parcelaId}/pagar")
    @ResponseBody
    public ModelAndView marcarComoPago(@PathVariable Long parcelaId) {
        Parcela parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Parcela não encontrada com o ID: " + parcelaId));
        parcela.setPago(true);
        parcela.setDataPagamento(LocalDate.now());
        parcelaRepository.save(parcela);
        return new ModelAndView("redirect:/receita/listaReceita");
    }

    @GetMapping("/{parcelaId}/recibo")
    public ModelAndView gerarRecibo(@PathVariable Long parcelaId) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("receita/recibo");

        Parcela parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Parcela não encontrada com o ID: " + parcelaId));

        // Formatar a data e hora atuais
        String dataPagamento = parcela.getDataPagamento() != null ?
                parcela.getDataPagamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        mv.addObject("parcela", parcela);
        mv.addObject("dataPagamento", dataPagamento); // Passando a data formatada
        mv.addObject("titulo", "Estratégias de Finanças Pessoais");

        return mv;
    }


    @GetMapping("/listaReceitaQuitar")
    public ModelAndView listaPQuitar() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("receita/list-emprestimo-quitar");

        // Obter todos os empréstimos
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();

        // Filtrar apenas os empréstimos com status PENDENTE
        List<Emprestimo> emprestimosPendentes = emprestimos.stream()
                .filter(emprestimo -> !emprestimo.isStatusEmprestimo()) // Supondo que 'false' indica PENDENTE
                .collect(Collectors.toList());

        // Calcular saldo devedor para cada empréstimo pendente
        for (Emprestimo emprestimo : emprestimosPendentes) {
            emprestimo.calcularSaldoDevedor();
        }

        mv.addObject("emprestimos", emprestimosPendentes);
        return mv;
    }

    @PostMapping("/{emprestimoId}/quitar")
    public ModelAndView quitarEmprestimo(@PathVariable Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo não encontrado com o ID: " + emprestimoId));

        // Recalcula o saldo devedor
        emprestimo.calcularSaldoDevedor();
        emprestimoRepository.save(emprestimo);

        return new ModelAndView("redirect:/receita/{emprestimoId}/formularioRecibo");
    }


    @PostMapping("/{emprestimoId}/salvarRecibo")
    public ModelAndView salvarRecibo(@PathVariable Long emprestimoId,
                                     @RequestParam("valorPago") BigDecimal valorPago,
                                     @RequestParam("observacao") String observacao) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo não encontrado com o ID: " + emprestimoId));

        // Verifica se o valor pago é menor que o saldo devedor
        if (valorPago.compareTo(BigDecimal.valueOf(emprestimo.getSaldoDevedor())) < 0) {
            // Retorna uma mensagem de erro, se o valor pago for menor
            ModelAndView mv = new ModelAndView("receita/formularioRecibo");
            mv.addObject("emprestimo", emprestimo);
            mv.addObject("valorPago", valorPago);
            mv.addObject("observacao", observacao);
            mv.addObject("errorMessage", "O valor pago deve ser maior ou igual ao saldo devedor."); // Mensagem de erro
            return mv;
        }

        List<Parcela> parcelas = parcelaRepository.findByEmprestimoId(emprestimoId);

        for (Parcela parcela : parcelas) {
            if (!parcela.isPago()) {
                parcela.setPago(true);
                parcela.setDataPagamento(LocalDate.now());
            }
        }

        parcelaRepository.saveAll(parcelas);

        // Recalcula o saldo devedor
        emprestimo.calcularSaldoDevedor();
        emprestimoRepository.save(emprestimo);

        // Verifica se todas as parcelas estão pagas
        boolean todasPagas = parcelas.stream().allMatch(Parcela::isPago);
        if (todasPagas) {
            emprestimo.setStatusEmprestimo(true); // Marca o empréstimo como quitado
            emprestimoRepository.save(emprestimo);
        }

        // Atualiza os campos
        emprestimo.setValorPago(valorPago);
        emprestimo.setObservacao(observacao);
        emprestimo.setDataPagamento(LocalDate.now()); // Atualiza a data de pagamento

        // Salva o empréstimo
        emprestimoRepository.save(emprestimo);

        // Redireciona para a página de recibo
        return new ModelAndView("redirect:/receita/{emprestimoId}/reciboQuitado");
    }

    @GetMapping("/{emprestimoId}/formularioRecibo")
    public ModelAndView mostrarFormularioRecibo(@PathVariable Long emprestimoId) {
        ModelAndView mv = new ModelAndView("receita/formularioRecibo");
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo não encontrado com o ID: " + emprestimoId));

        // Formatando a data de início para ser exibida corretamente
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataInicioFormatada = emprestimo.getDataInicio().format(formatter);

        mv.addObject("emprestimo", emprestimo);
        mv.addObject("valorPago", new BigDecimal(0));
        mv.addObject("observacao", "");
        mv.addObject("dataInicioFormatada", dataInicioFormatada); // Adiciona a data formatada ao Model

        return mv;
    }


    @GetMapping("/{emprestimoId}/reciboQuitado")
    public ModelAndView gerarReciboQuitado(@PathVariable Long emprestimoId) {
        ModelAndView mv = new ModelAndView("receita/recibo-quitado");

        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo não encontrado com o ID: " + emprestimoId));

        // Formata a data de pagamento no padrão "dd/MM/yyyy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataPagamentoFormatada = emprestimo.getDataPagamento() != null
                ? emprestimo.getDataPagamento().format(formatter)
                : "N/A"; // Caso não tenha sido definida

        // Adiciona o empréstimo e a data formatada ao Model
        mv.addObject("emprestimo", emprestimo);
        mv.addObject("dataPagamentoFormatada", dataPagamentoFormatada); // Data formatada

        return mv;
    }

    @GetMapping("/listaPendentes")
    public ModelAndView listaEmprestimoPendentes() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("filtro/list-pendentes");
        mv.addObject("emprestimosPendentes", emprestimoRepository.findByStatusPendente());
        return mv;
    }

    @GetMapping("/listaPagos")
    public ModelAndView listaEmprestimoPagos() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("filtro/list-pagos");
        mv.addObject("emprestimosPagos", emprestimoRepository.findByStatusPago());
        return mv;
    }

    @PostMapping("/pesquisa-cliente")
    public ModelAndView pesquisaCliente(@RequestParam(required = false) String nome) {
        ModelAndView mv = new ModelAndView();
        List<Emprestimo> listaEmprestimos;

        if (nome == null || nome.trim().isEmpty()) {
            listaEmprestimos = emprestimoRepository.findAll(); // Busca todos os empréstimos e seus clientes
        } else {
            listaEmprestimos = emprestimoRepository.findByClienteNomeContainingIgnoreCase(nome); // Busca por nome do cliente
        }

        // Adiciona a lista de empréstimos ao ModelAndView
        mv.addObject("ListaDeEmprestimos", listaEmprestimos);

        mv.setViewName("filtro/list-resultado");

        return mv;
    }

    @GetMapping("/listaPendentesPorData")
    public ModelAndView listaPendentesPorData(@RequestParam("data") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("filtro/list-pendentes-data");

        // Buscando todos os empréstimos com parcelas pendentes
        List<Emprestimo> emprestimosPendentes = emprestimoRepository.findByStatusPendente();

        // Lista para armazenar as parcelas a serem exibidas
        List<Parcela> parcelasPendentes = new ArrayList<>();
        double total = 0;

        for (Emprestimo emprestimo : emprestimosPendentes) {
            List<Parcela> parcelas = parcelaRepository.findByEmprestimoId(emprestimo.getId());

            for (Parcela parcela : parcelas) {
                // Verifica se a parcela é pendente e se a data de vencimento é menor ou igual à data fornecida
                if (!parcela.isPago() && (parcela.getDataPagamentoParcela().isEqual(data) || parcela.getDataPagamentoParcela().isBefore(data))) {
                    parcelasPendentes.add(parcela);
                    total += parcela.getValorParcela();
                }
            }
        }

        mv.addObject("parcelasPendentes", parcelasPendentes);
        mv.addObject("dataEscolhida", data);
        mv.addObject("total", total);
        return mv;
    }
}