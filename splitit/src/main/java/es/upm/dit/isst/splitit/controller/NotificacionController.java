package es.upm.dit.isst.splitit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import es.upm.dit.isst.splitit.model.Notificacion;
import es.upm.dit.isst.splitit.repository.NotificacionRepository;
import es.upm.dit.isst.splitit.repository.PagoRepository;

@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Notificacion> update(@RequestBody Notificacion newNotificacion, @PathVariable("id") Long id) {
        return notificacionRepository.findById(id)
                .map(notificacion -> {
                    notificacion.setMensaje(newNotificacion.getMensaje());
                    notificacion.setLeido(newNotificacion.isLeido());
                    notificacion.setConfirmando(newNotificacion.isConfirmando());
                    notificacion.setUsuario(newNotificacion.getUsuario());
                    notificacion.setPago(newNotificacion.getPago());
                    notificacionRepository.save(notificacion);
                    return ResponseEntity.ok(notificacion);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Notificacion> patch(@RequestBody Notificacion newNotificacion, @PathVariable("id") Long id) {
        return notificacionRepository.findById(id)
                .map(notificacion -> {
                    if (newNotificacion.getMensaje() != null) {
                        notificacion.setMensaje(newNotificacion.getMensaje());
                    }
                    if (newNotificacion.isLeido() != null) {
                        notificacion.setLeido(newNotificacion.isLeido());
                    }
                    if (newNotificacion.isConfirmando() != null) {
                        notificacion.setConfirmando(newNotificacion.isConfirmando());
                    }
                    if (newNotificacion.getUsuario() != null) {
                        notificacion.setUsuario(newNotificacion.getUsuario());
                    }
                    if (newNotificacion.getPago() != null) {
                        notificacion.setPago(newNotificacion.getPago());
                    }
                    notificacionRepository.save(notificacion);
                    return ResponseEntity.ok(notificacion);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/confirmar")
    @ResponseBody
    public ResponseEntity<Notificacion> setConfirmando(@PathVariable("id") Long id) {
        return notificacionRepository.findById(id)
                .map(notificacion -> {
                    Notificacion n = new Notificacion();
                    n.setUsuario(notificacion.getPago().getReceptor());
                    n.setMensaje("¿" + notificacion.getUsuario().getNombre() + " te ha pagado " + notificacion.getPago().getCantidad() + "€?");
                    n.setTipo(2);
                    n.setPago(notificacion.getPago());
                    notificacionRepository.save(n);
                    notificacion.setConfirmando(true);
                    notificacionRepository.save(notificacion);
                    return ResponseEntity.ok(notificacion);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/confirmar-pago")
    @ResponseBody
    public ResponseEntity<?> setConfirmado(@PathVariable("id") Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificación no encontrada"));
        Notificacion n = notificacionRepository.findByUsuarioAndPago(notificacion.getPago().getEmisor(), notificacion.getPago())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificación no encontrada"));
        notificacion.getPago().setPagado(true);
        pagoRepository.save(notificacion.getPago());
        notificacionRepository.delete(notificacion);
        notificacionRepository.delete(n);
        return ResponseEntity.ok("Pago confirmado");
    }
    
}
