package es.upm.dit.isst.splitit.repository;

import es.upm.dit.isst.splitit.model.Usuario;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
