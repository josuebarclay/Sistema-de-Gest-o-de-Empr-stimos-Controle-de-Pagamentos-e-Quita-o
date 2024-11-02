package com.gerenciamento.grc.controller;

import com.gerenciamento.grc.model.Cliente;
import com.gerenciamento.grc.model.Emprestimo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("home/index");
        mv.addObject("cliente", new Cliente());
        mv.addObject("emprestimo", new Emprestimo());
        return mv;
    }

    @GetMapping("/valores")
    public ModelAndView valores() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("home/valores");
        return mv;
    }
}
