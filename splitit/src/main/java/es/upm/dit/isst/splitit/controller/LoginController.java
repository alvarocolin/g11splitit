/**
 * Split.it - LoginController.java
 * Controlador para gestionar la vista del login y la creación de cuentas.
 * 
 * @author Grupo 11
 * @version 2.0
 * @since 2023-10-01
 */

package es.upm.dit.isst.splitit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * Método para mostrar la página de inicio de sesión
     * 
     * @return Página de inicio de sesión
     */
    @GetMapping("/iniciar-sesion")
    public String login() {
        return "login";
    }

    /**
     * Método para mostrar la página de creación de cuenta
     * 
     * @return Página de creación de cuenta
     */
    @GetMapping("/crear-cuenta")
    public String signIn() {
        return "crear-cuenta";
    }
}
