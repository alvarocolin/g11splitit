package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Gasto;
import es.upm.dit.isst.splitit.model.Participacion;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import es.upm.dit.isst.splitit.repository.ParticipacionRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/participaciones")
public class ParticipacionController {

    @Autowired
    private ParticipacionRepository participacionRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crea participaciones para un gasto dado.
     * La petición recibe una lista de objetos Participacion.
     * - Si todas las cantidades vienen en null, se calcula una división equitativa.
     * - Si alguna cantidad es no nula se asume modo manual y se valida que:
     * • Todas las participaciones tengan un valor
     * • La suma de las cantidades coincida con el total del gasto.
     */
    @PostMapping("/gastos/{gastoId}")
    public ResponseEntity<?> addParticipacionesToGasto(@PathVariable Long gastoId,
            @RequestBody List<Participacion> participaciones) {
        Optional<Gasto> gastoOpt = gastoRepository.findById(gastoId);
        if (!gastoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Gasto no encontrado");
        }
        Gasto gasto = gastoOpt.get();

        // Determinar si es modo manual (si al menos una participación tiene cantidad no
        // nula)
        boolean manual = participaciones.stream().anyMatch(p -> p.getCantidad() != null);

        if (manual) {
            double suma = 0.0;
            for (Participacion p : participaciones) {
                if (p.getCantidad() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("En modo manual, todas las participaciones deben tener cantidad definida.");
                }
                suma += p.getCantidad();
            }
            if (Math.abs(suma - gasto.getCantidad()) > 0.01) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La suma de las cantidades (" + suma + ") no coincide con el total del gasto ("
                                + gasto.getCantidad() + ").");
            }
        } else {
            // Si todas las participaciones vienen con cantidad null, se calcula la cuota
            // equitativa.
            if (participaciones.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Debe haber al menos un participante.");
            }
            double cuota = gasto.getCantidad() / participaciones.size();
            for (Participacion p : participaciones) {
                p.setCantidad(cuota);
            }
        }

        // Asignar el gasto a cada participación y, opcionalmente, cargar el usuario
        // completo si solo se envía el ID.
        List<Participacion> participacionesGuardadas = new ArrayList<>();
        for (Participacion p : participaciones) {
            // Si el objeto Usuario viene incompleto (por ejemplo, solo con el ID), se puede
            // cargar el objeto completo:
            if (p.getUsuario() != null && p.getUsuario().getIdUsuario() != null) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(p.getUsuario().getIdUsuario());
                if (!usuarioOpt.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Usuario con ID " + p.getUsuario().getIdUsuario() + " no encontrado.");
                }
                p.setUsuario(usuarioOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cada participación debe incluir el usuario con su ID.");
            }
            p.setGasto(gasto);
            participacionesGuardadas.add(p);
        }

        participacionRepository.saveAll(participacionesGuardadas);
        return ResponseEntity.status(HttpStatus.CREATED).body(participacionesGuardadas);
    }

    /**
     * Obtiene las participaciones asociadas a un gasto.
     */
    @GetMapping("/gastos/{gastoId}")
    public ResponseEntity<List<Participacion>> getParticipacionesByGasto(@PathVariable Long idGasto) {
        List<Participacion> participaciones = participacionRepository.findByGasto_IdGasto(idGasto);
        return ResponseEntity.ok(participaciones);
    }
}
