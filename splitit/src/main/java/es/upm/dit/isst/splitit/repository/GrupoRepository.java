package es.upm.dit.isst.splitit.repository;

import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Usuario;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface GrupoRepository extends CrudRepository<Grupo, Long> {
    List<Grupo> findByMiembrosContaining(Usuario usuario);
}
