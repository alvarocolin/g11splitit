package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard")
    @Transactional  // Permite inicializar colecciones lazy, como usuario.getGrupos()
    public String dashboard(Model model, Principal principal) {
        // Se asume que el username del usuario autenticado es su email
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setNombre("Desconocido");
        }
        model.addAttribute("usuario", usuario);
        // Añade al modelo los grupos asociados al usuario
        model.addAttribute("grupos", usuario.getGrupos());
        return "dashboard";
    }
}
