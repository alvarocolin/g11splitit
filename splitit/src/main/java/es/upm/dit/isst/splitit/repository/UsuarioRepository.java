package es.upm.dit.isst.splitit.repository;

import org.springframework.data.repository.CrudRepository;

import es.upm.dit.isst.splitit.model.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}
