package es.upm.dit.isst.splitit.repository;

import es.upm.dit.isst.splitit.model.Pago;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface PagoRepository extends CrudRepository<Pago, Long> {
    Optional<Pago> findByGrupoAndEmisor_Id(String grupo, Long idUsuario);
}