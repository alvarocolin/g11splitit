/**
 * Split.it - DashboardController.java
 * Controlador para gestionar la vista del dashboard y los grupos del usuario.
 * 
 * @author Grupo 11
 * @version 2.0
 * @since 2023-10-01
 */

package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Gasto;
import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import es.upm.dit.isst.splitit.repository.GrupoRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private GrupoRepository grupoRepository;
    
    @Autowired
    private GastoRepository gastoRepository;

    @GetMapping("/mi-espacio")
    @Transactional
    public String dashboard(@AuthenticationPrincipal Object principal, Model model) {
        String email = null;
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<Grupo> grupos = grupoRepository.findByMiembrosContaining(usuario);
        grupos.sort(Comparator.comparingLong(Grupo::getId).reversed());
        List<Grupo> grupos3 = grupos.stream().limit(3).toList();

        List<Gasto> gastos = new ArrayList<>();
        
        for (Grupo grupo : grupos) {
            gastos.addAll(gastoRepository.findByGrupo(grupo));
        }

        gastos.sort(Comparator.comparing(Gasto::getFecha).reversed());
        List<Gasto> gastos6 = gastos.stream().limit(6).toList();

        model.addAttribute("usuario", usuario);
        model.addAttribute("grupos", grupos3);
        model.addAttribute("gastos", gastos6);
        return "dashboard";
    }

    @GetMapping("/mis-grupos")
    @Transactional
    public String grupos(@AuthenticationPrincipal Object principal, Model model) {
        String email = null;
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<Grupo> grupos = grupoRepository.findByMiembrosContaining(usuario);
        grupos.sort(Comparator.comparingLong(Grupo::getId).reversed());

        model.addAttribute("usuario", usuario);
        model.addAttribute("grupos", grupos);
        return "grupos";
    }

    @GetMapping("/mi-cuenta")
    public String cuenta(@AuthenticationPrincipal Object principal, Model model) {
        String email = null;
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        return "cuenta";
    }

}
