package es.upm.dit.isst.splitit.repository;

import es.upm.dit.isst.splitit.model.Participacion;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ParticipacionRepository extends CrudRepository<Participacion, Long> {
    List<Participacion> findByGasto_Id(Long id);
}
