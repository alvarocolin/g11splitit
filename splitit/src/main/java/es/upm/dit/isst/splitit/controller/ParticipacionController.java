/**
 * Split.it - ParticipacionController.java
 * Controlador para gestionar las participaciones de gasto y los balances de usuario en un grupo.
 * 
 * @author Grupo 11
 * @version 2.0
 * @since 2025-03-30
 */

package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Gasto;
import es.upm.dit.isst.splitit.model.Participacion;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import es.upm.dit.isst.splitit.repository.ParticipacionRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/participaciones")
public class ParticipacionController {

    @Autowired
    private ParticipacionRepository participacionRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @PostMapping("/gastos/{id}")
    public ResponseEntity<?> addParticipacionesToGasto(@PathVariable("id") Long gastoId,
            @RequestBody List<Participacion> participaciones) {
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gasto no encontrado"));

        boolean manual = participaciones.stream().anyMatch(p -> p.getCantidad() != null);

        if (manual) {
            double suma = participaciones.stream()
                    .mapToDouble(p -> Optional.ofNullable(p.getCantidad()).orElse(0.0))
                    .sum();

            if (participaciones.stream().anyMatch(p -> p.getCantidad() == null)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Todos los participantes deben tener una cantidad asignada");
            }

            if (Math.abs(suma - gasto.getCantidad()) > 0.01) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La suma de las cantidades no coincide con el gasto total");
            }

        } else {
            double cantidadEquitativa = gasto.getCantidad() / participaciones.size();
            participaciones.forEach(p -> p.setCantidad(cantidadEquitativa));
        }

        participaciones.forEach(p -> p.setGasto(gasto));
        participacionRepository.saveAll(participaciones);
        return ResponseEntity.ok("Participaciones añadidas");
    }

}
