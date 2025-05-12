package es.upm.dit.isst.splitit.advice;

import es.upm.dit.isst.splitit.model.Notificacion;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.NotificacionRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class NotificacionAdvice {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @ModelAttribute
    public void addNotificaciones(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            List<Notificacion> notificaciones = notificacionRepository.findByUsuario(usuario);
            List<Notificacion> toasts = notificacionRepository.findByUsuarioAndLeidoFalse(usuario);

            model.addAttribute("notificaciones", notificaciones);
            model.addAttribute("toasts", toasts);
        }
    }

}
