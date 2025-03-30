package es.upm.dit.isst.splitit.controller;

import java.security.Principal;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import es.upm.dit.isst.splitit.repository.GrupoRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;

@Controller
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @GetMapping
    @ResponseBody
    public List<Grupo> getAllGrupos() {
        List<Grupo> grupos = new ArrayList<>();
        grupoRepository.findAll().forEach(grupos::add);
        return grupos;
    }

    @GetMapping("/crear")
    public String crearGrupo(Model model) {
        model.addAttribute("grupo", new Grupo());
        return "creargrupo";
    }

    @PostMapping("/guardar")
    public String guardarGrupo(@ModelAttribute Grupo grupo, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName());
        if (grupo.getMiembros() == null) {
            grupo.setMiembros(new HashSet<>());
        }
        grupo.getMiembros().add(usuario);
        grupoRepository.save(grupo);
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String verGrupo(@PathVariable("id") Long id, Model model, Principal principal) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));

        List<?> gastos = gastoRepository.findByGrupo(grupo);
        Usuario usuario = usuarioRepository.findByEmail(principal.getName());

        List<Map<String, Object>> balanceMap = new ArrayList<>();
        try {
            String url = "http://localhost:8080/participaciones/balance/grupo/" + id;
            java.net.URL balanceURL = new java.net.URL(url);
            java.io.InputStream is = balanceURL.openStream();
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            String json = s.hasNext() ? s.next() : "[]";
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            balanceMap = mapper.readValue(json, List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("grupo", grupo);
        model.addAttribute("gastos", gastos);
        model.addAttribute("usuario", usuario);
        model.addAttribute("balanceMap", balanceMap);

        return "grupo";
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Grupo> updateGrupo(@PathVariable Long id, @RequestBody Grupo grupoDetails) {
        return grupoRepository.findById(id).map(grupo -> {
            grupo.setNombre(grupoDetails.getNombre());
            grupo.setMiembros(grupoDetails.getMiembros());
            Grupo updatedGrupo = grupoRepository.save(grupo);
            return ResponseEntity.ok(updatedGrupo);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteGrupo(@PathVariable Long id) {
        if (!grupoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        grupoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/participantes/nuevo")
    public String mostrarFormularioParticipantes(@PathVariable("id") Long grupoId, Model model) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        model.addAttribute("grupo", grupo);
        return "addparticipantes";
    }

    @PostMapping("/{grupoId}/add")
    public String addUsuarioToGrupoMVC(@PathVariable Long grupoId, @RequestParam Long usuarioId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (grupoOpt.isPresent() && usuarioOpt.isPresent()) {
            Grupo grupo = grupoOpt.get();
            grupo.getMiembros().add(usuarioOpt.get());
            grupoRepository.save(grupo);
            return "redirect:/grupos/" + grupoId;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo o Usuario no encontrado");
        }
    }

    @PostMapping("/{grupoId}/remove")
    public String removeUsuarioFromGrupoMVC(@PathVariable Long grupoId, @RequestParam Long usuarioId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (grupoOpt.isPresent() && usuarioOpt.isPresent()) {
            Grupo grupo = grupoOpt.get();
            grupo.getMiembros().remove(usuarioOpt.get());
            grupoRepository.save(grupo);
            return "redirect:/grupos/" + grupoId;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo o Usuario no encontrado");
        }
    }
}
