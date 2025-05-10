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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
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
    public String dashboard(Model model, Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email);

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
    public String grupos(Model model, Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email);

        List<Grupo> grupos = grupoRepository.findByMiembrosContaining(usuario);
        grupos.sort(Comparator.comparingLong(Grupo::getId).reversed());

        model.addAttribute("usuario", usuario);
        model.addAttribute("grupos", grupos);
        return "grupos";
    }

    @GetMapping("/mi-cuenta")
    public String cuenta(Model model, Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email);

        model.addAttribute("usuario", usuario);
        return "cuenta";
    }

}
