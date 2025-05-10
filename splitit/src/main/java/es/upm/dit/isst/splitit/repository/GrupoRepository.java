package es.upm.dit.isst.splitit.repository;

import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface GrupoRepository extends CrudRepository<Grupo, Long> {
    List<Grupo> findByMiembrosContaining(Usuario usuario);
}
