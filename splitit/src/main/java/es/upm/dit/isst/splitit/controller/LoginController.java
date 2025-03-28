package es.upm.dit.isst.splitit.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // Esta anotación es necesaria
public class LoginController {

    
    @GetMapping("/login")
    public String login() {
        return "login"; // Asegúrate de que existe login.html en src/main/resources/templates
    }
}

