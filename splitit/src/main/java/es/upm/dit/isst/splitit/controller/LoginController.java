/**
 * Split.it - LoginController.java
 * Controlador para gestionar la vista del login y la creación de cuentas.
 * También gestiona la lógica de registro de nuevos usuarios.
 * 
 * @author Grupo 11
 * @version 2.1
 * @since 2025-05-10
 */

 package es.upm.dit.isst.splitit.controller;

 import es.upm.dit.isst.splitit.model.Usuario;
 import es.upm.dit.isst.splitit.repository.UsuarioRepository;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.ModelAttribute;
 import org.springframework.web.bind.annotation.PostMapping;
 
 @Controller
 public class LoginController {
 
     @Autowired
     private UsuarioRepository usuarioRepository;
 
     @Autowired
     private PasswordEncoder passwordEncoder;
 
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
 
     /**
      * Método que procesa el formulario de registro de nuevo usuario
      * 
      * @param usuario Usuario recibido del formulario
      * @return Redirección a login si todo va bien, o a crear-cuenta si hay error
      */
     @PostMapping("/crear-cuenta/confirmar")
     public String createFromForm(@ModelAttribute Usuario usuario) {
         if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
             return "redirect:/crear-cuenta?error";
         }
         String encodedPassword = passwordEncoder.encode(usuario.getPassword());
         usuario.setPassword(encodedPassword);
         usuario.setAuth("ROLE_USER");
         usuarioRepository.save(usuario);
         return "redirect:/iniciar-sesion?signup";
     }
 }
 
