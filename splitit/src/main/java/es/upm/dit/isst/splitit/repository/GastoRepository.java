package es.upm.dit.isst.splitit.repository;

import es.upm.dit.isst.splitit.model.Gasto;
import es.upm.dit.isst.splitit.model.Grupo;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface GastoRepository extends CrudRepository<Gasto, Long> {
    List<Gasto> findByGrupo(Grupo grupo);   
}
