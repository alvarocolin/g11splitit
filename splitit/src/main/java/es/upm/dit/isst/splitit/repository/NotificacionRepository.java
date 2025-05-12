package es.upm.dit.isst.splitit.repository;

import es.upm.dit.isst.splitit.model.Notificacion;
import es.upm.dit.isst.splitit.model.Pago;
import es.upm.dit.isst.splitit.model.Usuario;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacionRepository extends CrudRepository<Notificacion, Long> {
    List<Notificacion> findByUsuario(Usuario usuario);
    List<Notificacion> findByUsuarioAndLeidoFalse(Usuario usuario);
    Optional<Notificacion> findByUsuarioAndPago(Usuario usuario, Pago pago);
}
