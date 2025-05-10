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
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import es.upm.dit.isst.splitit.repository.ParticipacionRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import es.upm.dit.isst.splitit.repository.GrupoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

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

    @GetMapping("/balance/grupo/{id}")
    public ResponseEntity<?> getBalancePorGrupo(@PathVariable("id") Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        List<Gasto> gastosGrupo = gastoRepository.findByGrupo(grupo);

        Map<Long, Double> totalDebePorUsuario = new HashMap<>(); // Lo que debe cada uno
        Map<Long, Double> totalLeDebenAlUsuario = new HashMap<>(); // Lo que le deben a cada uno

        for (Gasto gasto : gastosGrupo) {
            Long idPagador = gasto.getPagador().getId();
            List<Participacion> participaciones = participacionRepository.findByGasto_Id(gasto.getId());

            for (Participacion p : participaciones) {
                Long idParticipante = p.getUsuario().getId();
                double cantidad = p.getCantidad();

                if (!idParticipante.equals(idPagador)) {
                    // Lo que debe el participante
                    totalDebePorUsuario.put(idParticipante,
                            totalDebePorUsuario.getOrDefault(idParticipante, 0.0) + cantidad);

                    // Lo que le deben al pagador
                    totalLeDebenAlUsuario.put(idPagador,
                            totalLeDebenAlUsuario.getOrDefault(idPagador, 0.0) + cantidad);
                }
            }
        }

        Set<Long> usuarios = new HashSet<>();
        usuarios.addAll(totalDebePorUsuario.keySet());
        usuarios.addAll(totalLeDebenAlUsuario.keySet());

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Long idUsuario : usuarios) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                double leDeben = totalLeDebenAlUsuario.getOrDefault(idUsuario, 0.0);
                double debe = totalDebePorUsuario.getOrDefault(idUsuario, 0.0);
                double balance = leDeben - debe;

                Map<String, Object> datoUsuario = new HashMap<>();
                datoUsuario.put("usuario", usuario.getNombre());
                datoUsuario.put("pagado", leDeben); // opcional: cambiar el nombre a "le deben"
                datoUsuario.put("debe", debe);
                datoUsuario.put("balance", balance);

                resultado.add(datoUsuario);
            }
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/deudas/grupo/{id}")
    public ResponseEntity<?> getDeudasEntreUsuarios(@PathVariable("id") Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        List<Gasto> gastosGrupo = gastoRepository.findByGrupo(grupo);
        Map<Long, Map<Long, Double>> deudas = new HashMap<>();

        for (Gasto gasto : gastosGrupo) {
            Long pagadorId = gasto.getPagador().getId();
            List<Participacion> participaciones = participacionRepository.findByGasto_Id(gasto.getId());
            for (Participacion p : participaciones) {
                Long participanteId = p.getUsuario().getId();
                double cantidad = p.getCantidad();

                if (!participanteId.equals(pagadorId)) {
                    deudas.computeIfAbsent(participanteId, k -> new HashMap<>())
                            .put(pagadorId, deudas.get(participanteId).getOrDefault(pagadorId, 0.0) + cantidad);
                }
            }
        }

        Map<String, Map<String, Double>> resultado = new HashMap<>();
        for (Long deudorId : deudas.keySet()) {
            String deudorNombre = usuarioRepository.findById(deudorId).map(Usuario::getNombre).orElse("?");
            resultado.putIfAbsent(deudorNombre, new HashMap<>());
            for (Long acreedorId : deudas.get(deudorId).keySet()) {
                String acreedorNombre = usuarioRepository.findById(acreedorId).map(Usuario::getNombre).orElse("?");
                resultado.get(deudorNombre).put(acreedorNombre, deudas.get(deudorId).get(acreedorId));
            }
        }

        return ResponseEntity.ok(resultado);
    }

}
