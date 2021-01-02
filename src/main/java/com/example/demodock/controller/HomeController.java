package com.example.demodock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("")
    public String home(Model m){
        m.addAttribute("content", "2021년 새해에는 좋은일이 가득하시길 !!");
        return "index";
    }
}
