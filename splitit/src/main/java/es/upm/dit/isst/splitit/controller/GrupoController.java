package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import es.upm.dit.isst.splitit.repository.GrupoRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GastoRepository gastoRepository;

    // API: Obtener todos los grupos (retorna JSON)
    @GetMapping
    @ResponseBody
    public List<Grupo> getAllGrupos() {
        List<Grupo> grupos = new ArrayList<>();
        grupoRepository.findAll().forEach(grupos::add);
        return grupos;
    }

    // MVC: Muestra el formulario para crear un nuevo grupo (sin participantes)
    @GetMapping("/crear")
    public String crearGrupo(Model model) {
        model.addAttribute("grupo", new Grupo());
        return "creargrupo"; // Renderiza la plantilla creargrupo.html
    }

    //  Recibe los datos del formulario y guarda el grupo, luego redirige al
    // dashboard
    @PostMapping("/guardar")
    public String guardarGrupo(@ModelAttribute Grupo grupo, Principal principal) {
        // Obtener el usuario autenticado (su email se usa como username)
        Usuario usuario = usuarioRepository.findByEmail(principal.getName());

        // Inicializar la colección de miembros si es nula y añadir el usuario creador
        if (grupo.getMiembros() == null) {
            grupo.setMiembros(new HashSet<>());
        }
        grupo.getMiembros().add(usuario);

        grupoRepository.save(grupo);
        return "redirect:/dashboard";
    }

    // Método para mostrar los detalles de un grupo
    @GetMapping("/{id}")
    public String verGrupo(@PathVariable("id") Long id, Model model) {
        Grupo grupo = grupoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        
        // Se obtiene la lista de gastos asociados a este grupo
        List<?> gastos = gastoRepository.findByGrupo(grupo); // Asegúrate de tener este método en GastoRepository
        
        // Se calcula el balance de cada participante (puedes tener un servicio que lo haga)
        // Por simplicidad, aquí se usa un mapa simulado
        Map<?, ?> balanceMap = Map.of(); // Implementa tu lógica de cálculo de balances
        
        model.addAttribute("grupo", grupo);
        model.addAttribute("gastos", gastos);
        model.addAttribute("balanceMap", balanceMap);
        return "grupo"; // Thymeleaf buscará el template "grupo.html"
    }

    // API: Actualizar un grupo
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

    // API: Eliminar un grupo
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
    
    // MVC: Método para agregar un participante a un grupo (vía formulario)
    @PostMapping("/{grupoId}/add")
    public String addUsuarioToGrupoMVC(@PathVariable Long grupoId, @RequestParam Long usuarioId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (grupoOpt.isPresent() && usuarioOpt.isPresent()) {
            Grupo grupo = grupoOpt.get();
            grupo.getMiembros().add(usuarioOpt.get());
            grupoRepository.save(grupo);
            return "redirect:/grupos/" + grupoId; // Vuelve a ver el grupo
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo o Usuario no encontrado");
        }
    }

    // MVC: Método para quitar un participante de un grupo (vía formulario)
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
