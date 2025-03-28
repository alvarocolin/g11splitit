package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<Iterable<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // Crear un usuario
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(savedUsuario);
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar un usuario
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(usuarioDetails.getNombre());
            usuario.setEmail(usuarioDetails.getEmail());
            Usuario updatedUsuario = usuarioRepository.save(usuario);
            return ResponseEntity.ok(updatedUsuario);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuarioRepository.delete(usuario);
            return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
