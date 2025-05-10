/**
 * Split.it - UsuarioController.java
 * Controlador para gestionar los usuarios.
 * 
 * @author Alvaro Colin
 * @version 1.0
 * @since 2025-03-30
 */

package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Método para obtener todos los usuarios
     * 
     * @return Lista de todos los usuarios
     */
    @GetMapping
    public List<Usuario> getAll() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    /**
     * Método para crear un nuevo usuario
     * 
     * @param newUsuario Usuario a crear
     * @return Usuario creado
     */
    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario newUsuario) {
        if (usuarioRepository.findById(newUsuario.getId()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        String encodedPassword = passwordEncoder.encode(newUsuario.getPassword());
        newUsuario.setPassword(encodedPassword);
        newUsuario.setAuth("ROLE_USER");
        Usuario result = usuarioRepository.save(newUsuario);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Método para obtener un usuario por su ID
     * 
     * @param id ID del usuario a obtener
     * @return Usuario encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getOne(@PathVariable("id") String id) {
        Usuario usuario = usuarioRepository.findByEmail(id);
        return ResponseEntity.ok().body(usuario);
    }

    /**
     * Método para actualizar un usuario
     * 
     * @param newUsuario Usuario a actualizar
     * @param id         ID del usuario a actualizar
     * @return Usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@RequestBody Usuario newUsuario, @PathVariable("id") Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(newUsuario.getNombre());
                    usuario.setEmail(newUsuario.getEmail());
                    String encodedPassword = passwordEncoder.encode(newUsuario.getPassword());
                    usuario.setPassword(encodedPassword);
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Método para actualizar parcialmente un usuario
     * 
     * @param newUsuario Usuario a actualizar
     * @param id         ID del usuario a actualizar
     * @return Usuario actualizado
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Usuario> partialUpdate(@ModelAttribute Usuario newUsuario, @PathVariable("id") Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    if (newUsuario.getNombre() != null) {
                        usuario.setNombre(newUsuario.getNombre());
                    }
                    if (newUsuario.getEmail() != null) {
                        usuario.setEmail(newUsuario.getEmail());
                    }
                    if (newUsuario.getPassword() != null) {
                        String encodedPassword = passwordEncoder.encode(newUsuario.getPassword());
                        usuario.setPassword(encodedPassword);
                    }
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Método para eliminar un usuario
     * 
     * @param id ID del usuario a eliminar
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuarioRepository.delete(usuario);
            return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
