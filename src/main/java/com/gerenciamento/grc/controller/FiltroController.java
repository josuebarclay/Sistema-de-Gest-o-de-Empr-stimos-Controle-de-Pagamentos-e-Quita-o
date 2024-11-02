package com.gerenciamento.grc.controller;

import com.gerenciamento.grc.model.Emprestimo;
import com.gerenciamento.grc.model.RecursoNaoEncontradoException;
import com.gerenciamento.grc.repository.EmprestimoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/Filtro")
public class FiltroController {

    @Autowired
    EmprestimoRepository emprestimoRepository;


    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("home/index");
        return mv;
    }

    @GetMapping("/{emprstimoId}/filtro")
    public ModelAndView filtroEmprestmo(@PathVariable Long emprestimoId) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("receita/list-resultado");

        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Parcela não encontrada com o ID: " + emprestimoId));

        return mv;
    }
}
