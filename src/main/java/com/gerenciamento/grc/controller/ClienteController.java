package com.gerenciamento.grc.controller;

import com.gerenciamento.grc.model.Cliente;
import com.gerenciamento.grc.model.RecursoNaoEncontradoException;
import com.gerenciamento.grc.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/cadastroCliente")
    public ModelAndView cliente() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("cliente/cadastro");
        mv.addObject("cliente", new Cliente());
        return mv;
    }

    @PostMapping("/salvar")
    public ModelAndView salvarCliente(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result) {
        // Verifica se há erros de validação
        if (result.hasErrors()) {
            // Se houver erros, retorna o formulário com os erros exibidos
            ModelAndView modelAndView = new ModelAndView("cliente/cadastro");
            return modelAndView;
        }

        cliente.setNome(cliente.getNome().toUpperCase());

        // Salvando o cliente no banco de dados
        clienteRepository.save(cliente);

        // Redirecionando para a lista de clientes
        return new ModelAndView("redirect:/clientes/listaClientes");
    }

    @GetMapping("/listaClientes")
    public ModelAndView listaClientes (){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("cliente/list-cliente");

        // Buscar todos os clientes ordenados por ID de forma crescente
        List<Cliente> clientes = clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        mv.addObject("clientes", clientes);
        return mv;
    }

    @GetMapping("/excluir/{id}")
    public ModelAndView excluirCliente(@PathVariable("id") Long id) {
        // Busca o cliente pelo ID
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);

        // Se o cliente não existir, redireciona com uma mensagem de erro (ou um tratamento apropriado)
        if (!clienteOptional.isPresent()) {
            ModelAndView mv = new ModelAndView("redirect:/clientes/listaClientes");
            mv.addObject("error", "Cliente não encontrado.");
            return mv;
        }

        Cliente cliente = clienteOptional.get();

        // Verifica se o cliente possui empréstimos vinculados
        if (!cliente.getEmprestimos().isEmpty()) {
            // Caso tenha empréstimos vinculados, redireciona para a lista de clientes com uma mensagem de erro
            ModelAndView mv = new ModelAndView("redirect:/clientes/listaClientes");
            mv.addObject("error", "Não é possível excluir o cliente. Existem empréstimos vinculados.");
            return mv;
        }

        // Caso contrário, exclui o cliente
        clienteRepository.deleteById(id);

        // Redireciona para a lista de clientes após exclusão bem-sucedida
        return new ModelAndView("redirect:/clientes/listaClientes");
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("cliente/editar");

        // Buscar o cliente pelo ID, ou lançar exceção se não for encontrado
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        // Passar o cliente para o formulário de edição
        mv.addObject("cliente", cliente);
        return mv;
    }

    @PostMapping("/editar-cliente")
    public ModelAndView editarCliente(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("cliente/editar");
            return modelAndView;
        }

        // Verifica se o cliente já tem um ID (existe no banco de dados)
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("ID inválido para atualização");
        }

        cliente.setNome(cliente.getNome().toUpperCase());
        clienteRepository.save(cliente);  // Salvar o cliente atualizado

        return new ModelAndView("redirect:/clientes/listaClientes");
    }


}
