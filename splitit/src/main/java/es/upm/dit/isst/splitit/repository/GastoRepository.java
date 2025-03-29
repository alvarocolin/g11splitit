package es.upm.dit.isst.splitit.repository;

import es.upm.dit.isst.splitit.model.Gasto;
import es.upm.dit.isst.splitit.model.Grupo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface GastoRepository extends CrudRepository<Gasto, Long> {
    List<Gasto> findByGrupo(Grupo grupo);
    
}
