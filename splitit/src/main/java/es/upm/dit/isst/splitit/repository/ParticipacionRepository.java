package es.upm.dit.isst.splitit.repository;

import es.upm.dit.isst.splitit.model.Participacion;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ParticipacionRepository extends CrudRepository<Participacion, Long> {
    List<Participacion> findByGasto_IdGasto(Long idGasto);
}
