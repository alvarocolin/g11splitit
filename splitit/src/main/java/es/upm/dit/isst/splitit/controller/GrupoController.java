package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.GrupoRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los grupos (convertimos Iterable a List)
    @GetMapping
    public List<Grupo> getAllGrupos() {
        List<Grupo> grupos = new ArrayList<>();
        grupoRepository.findAll().forEach(grupos::add);
        return grupos;
    }

    // Crear un grupo
    @PostMapping
    public ResponseEntity<Grupo> createGrupo(@RequestBody Grupo grupo) {
        Grupo savedGrupo = grupoRepository.save(grupo);
        return ResponseEntity.ok(savedGrupo);
    }

    // Obtener un grupo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Grupo> getGrupoById(@PathVariable Long id) {
        Optional<Grupo> grupo = grupoRepository.findById(id);
        return grupo.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar un grupo
    @PutMapping("/{id}")
    public ResponseEntity<Grupo> updateGrupo(@PathVariable Long id, @RequestBody Grupo grupoDetails) {
        return grupoRepository.findById(id).map(grupo -> {
            grupo.setNombre(grupoDetails.getNombre());
            grupo.setMiembros(grupoDetails.getMiembros());
            Grupo updatedGrupo = grupoRepository.save(grupo);
            return ResponseEntity.ok(updatedGrupo);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un grupo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrupo(@PathVariable Long id) {
        if (!grupoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        grupoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Añadir un usuario a un grupo
    @PostMapping("/{grupoId}/add/{usuarioId}")
    public ResponseEntity<Grupo> addUsuarioToGrupo(@PathVariable Long grupoId, @PathVariable Long usuarioId) {
        Optional<Grupo> grupo = grupoRepository.findById(grupoId);
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (grupo.isPresent() && usuario.isPresent()) {
            Grupo grupoActual = grupo.get();
            grupoActual.getMiembros().add(usuario.get());
            grupoRepository.save(grupoActual);
            return ResponseEntity.ok(grupoActual);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Quitar un usuario de un grupo
    @PostMapping("/{grupoId}/remove/{usuarioId}")
    public ResponseEntity<Grupo> removeUsuarioFromGrupo(@PathVariable Long grupoId, @PathVariable Long usuarioId) {
        Optional<Grupo> grupo = grupoRepository.findById(grupoId);
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (grupo.isPresent() && usuario.isPresent()) {
            Grupo grupoActual = grupo.get();
            grupoActual.getMiembros().remove(usuario.get());
            grupoRepository.save(grupoActual);
            return ResponseEntity.ok(grupoActual);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
