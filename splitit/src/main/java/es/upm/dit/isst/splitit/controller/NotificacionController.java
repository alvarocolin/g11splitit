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

import es.upm.dit.isst.splitit.model.Notificacion;
import es.upm.dit.isst.splitit.repository.NotificacionRepository;

@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Notificacion> update(@RequestBody Notificacion newNotificacion, @PathVariable("id") Long id) {
        return notificacionRepository.findById(id)
                .map(notificacion -> {
                    notificacion.setMensaje(newNotificacion.getMensaje());
                    notificacion.setLeido(newNotificacion.getLeido());
                    notificacion.setUsuario(newNotificacion.getUsuario());
                    notificacion.setPago(newNotificacion.getPago());
                    notificacion.setGrupo(newNotificacion.getGrupo());
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
                    if (newNotificacion.getLeido() != null) {
                        notificacion.setLeido(newNotificacion.getLeido());
                    }
                    if (newNotificacion.getUsuario() != null) {
                        notificacion.setUsuario(newNotificacion.getUsuario());
                    }
                    if (newNotificacion.getPago() != null) {
                        notificacion.setPago(newNotificacion.getPago());
                    }
                    if (newNotificacion.getGrupo() != null) {
                        notificacion.setGrupo(newNotificacion.getGrupo());
                    }
                    notificacionRepository.save(notificacion);
                    return ResponseEntity.ok(notificacion);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
}
