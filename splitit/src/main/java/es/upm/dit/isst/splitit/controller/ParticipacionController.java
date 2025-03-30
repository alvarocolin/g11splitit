package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Gasto;
import es.upm.dit.isst.splitit.model.Participacion;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import es.upm.dit.isst.splitit.repository.ParticipacionRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import es.upm.dit.isst.splitit.repository.GrupoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @PostMapping("/gastos/{gastoId}")
    public ResponseEntity<?> addParticipacionesToGasto(@PathVariable Long gastoId,
            @RequestBody List<Participacion> participaciones) {
        Optional<Gasto> gastoOpt = gastoRepository.findById(gastoId);
        if (!gastoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Gasto no encontrado");
        }
        Gasto gasto = gastoOpt.get();

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

    @GetMapping("/balance/grupo/{grupoId}")
    public ResponseEntity<?> getBalancePorGrupo(@PathVariable Long grupoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        if (!grupoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grupo no encontrado");
        }
        Grupo grupo = grupoOpt.get();

        List<Gasto> gastosGrupo = gastoRepository.findByGrupo(grupo);

        Map<Long, Double> totalPagadoPorUsuario = new HashMap<>();
        Map<Long, Double> totalDebePorUsuario = new HashMap<>();
        Map<Long, Map<Long, Double>> deudasEntreUsuarios = new HashMap<>();

        for (Gasto gasto : gastosGrupo) {
            Usuario pagador = gasto.getPagador();
            Long idPagador = pagador.getIdUsuario();
            totalPagadoPorUsuario.put(idPagador,
                    totalPagadoPorUsuario.getOrDefault(idPagador, 0.0) + gasto.getCantidad());

            List<Participacion> participaciones = participacionRepository.findByGasto_IdGasto(gasto.getIdGasto());

            for (Participacion p : participaciones) {
                Long idParticipante = p.getUsuario().getIdUsuario();
                double cantidad = p.getCantidad();

                if (!idParticipante.equals(idPagador)) {
                    totalDebePorUsuario.put(idParticipante,
                            totalDebePorUsuario.getOrDefault(idParticipante, 0.0) + cantidad);

                    deudasEntreUsuarios
                            .computeIfAbsent(idParticipante, k -> new HashMap<>())
                            .put(idPagador,
                                    deudasEntreUsuarios.get(idParticipante).getOrDefault(idPagador, 0.0) + cantidad);
                }
            }
        }

        Set<Long> usuarios = new HashSet<>();
        usuarios.addAll(totalPagadoPorUsuario.keySet());
        usuarios.addAll(totalDebePorUsuario.keySet());

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Long idUsuario : usuarios) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                double pagado = totalPagadoPorUsuario.getOrDefault(idUsuario, 0.0);
                double debe = totalDebePorUsuario.getOrDefault(idUsuario, 0.0);
                double balance = pagado - debe;

                Map<String, Object> datoUsuario = new HashMap<>();
                datoUsuario.put("usuario", usuario.getNombre());
                datoUsuario.put("pagado", pagado);
                datoUsuario.put("debe", debe);
                datoUsuario.put("balance", balance);

                resultado.add(datoUsuario);
            }
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/deudas/grupo/{grupoId}")
    public ResponseEntity<?> getDeudasEntreUsuarios(@PathVariable Long grupoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        if (!grupoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grupo no encontrado");
        }
        Grupo grupo = grupoOpt.get();
        List<Gasto> gastosGrupo = gastoRepository.findByGrupo(grupo);
        Map<Long, Map<Long, Double>> deudas = new HashMap<>();

        for (Gasto gasto : gastosGrupo) {
            Long pagadorId = gasto.getPagador().getIdUsuario();
            List<Participacion> participaciones = participacionRepository.findByGasto_IdGasto(gasto.getIdGasto());
            for (Participacion p : participaciones) {
                Long participanteId = p.getUsuario().getIdUsuario();
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
