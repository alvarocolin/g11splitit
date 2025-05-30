/**
 * Split.it - GastoController.java
 * Controlador para gestionar los gastos.
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequestMapping("/gastos")
public class GastoController {

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipacionRepository participacionRepository;

    @PostMapping("/crear")
    public String create(@RequestParam String concepto, @RequestParam double cantidad, @RequestParam Long grupoId,
            @RequestParam Long pagadorId, @RequestParam Map<String, String> params, @RequestParam("recibo") MultipartFile recibo)
            throws IOException {

        validateInputs(concepto, cantidad, grupoId, pagadorId);
        validateImg(recibo);

        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        Usuario pagador = usuarioRepository.findById(pagadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Gasto gasto = new Gasto();
        gasto.setConcepto(concepto);
        gasto.setCantidad(cantidad);
        gasto.setFecha(new Date());
        gasto.setGrupo(grupo);
        gasto.setPagador(pagador);
        gastoRepository.save(gasto);

        grupo.addGasto(cantidad);
        grupoRepository.save(grupo);

        if (recibo != null && !recibo.isEmpty()) {
            String original = recibo.getOriginalFilename();
            String extension = original.substring(original.lastIndexOf("."));
            String nombre = "recibo_" + System.currentTimeMillis() + "_" + gasto.getId() + extension;
            Path ruta = Paths.get("uploads/" + nombre);
            Files.write(ruta, recibo.getBytes());

            gasto.setRecibo(nombre);
            gastoRepository.save(gasto);
        }

        List<Participacion> participaciones = new ArrayList<>();
        double sumaParticipaciones = 0.0;

        for (String key : params.keySet()) {
            if (key.startsWith("participaciones[") && key.endsWith("].incluido")) {
                String userIdStr = key.substring("participaciones[".length(), key.indexOf("].incluido"));
                Long userId = Long.parseLong(userIdStr);

                String cantidadKey = "participaciones[" + userId + "].cantidad";
                if (params.containsKey(cantidadKey)) {
                    double cantidadUsuario = Double.parseDouble(params.get(cantidadKey));
                    Usuario usuario = usuarioRepository.findById(userId).orElseThrow();

                    if (!usuario.getId().equals(pagadorId)) {
                        Participacion p = new Participacion();
                        p.setUsuario(usuario);
                        p.setGasto(gasto);
                        p.setCantidad(cantidadUsuario);
                        participaciones.add(p);
                        sumaParticipaciones += cantidadUsuario;
                    }
                }
            }
        }

        double diferencia = cantidad - sumaParticipaciones;
        if (diferencia > 0.001) {
            Participacion propia = new Participacion();
            propia.setUsuario(pagador);
            propia.setGasto(gasto);
            propia.setCantidad(diferencia);
            participaciones.add(propia);
        }

        participacionRepository.saveAll(participaciones);

        return "redirect:/grupos/" + grupoId;
    }

    /**
     * Procesa el formulario de edición de un gasto.
     * 
     * @param id       ID del gasto a editar
     * @param concepto Nuevo concepto del gasto
     * @param cantidad Nueva cantidad del gasto
     * @param fecha    Nueva fecha del gasto
     * @param recibo   Recibo asociado (opcional)
     * @return Redirección al listado de gastos del grupo
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Gasto> update(@PathVariable("id") Long id, @RequestParam String concepto, @RequestParam double cantidad,
            @RequestParam Long grupoId, @RequestParam Long pagadorId, @RequestParam(value = "recibo", required = false) MultipartFile recibo,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha) throws IOException {
        validateInputs(concepto, cantidad, grupoId, pagadorId);
        validateImg(recibo);
        
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gasto no encontrado"));

        gasto.setConcepto(concepto);
        gasto.setFecha(fecha);

        if (recibo != null && !recibo.isEmpty()) {
            String original = recibo.getOriginalFilename();
            String extension = original.substring(original.lastIndexOf("."));
            String nombre = "recibo_" + System.currentTimeMillis() + "_" + gasto.getId() + extension;
            Path ruta = Paths.get("uploads/" + nombre);
            Files.write(ruta, recibo.getBytes());

            gasto.setRecibo(nombre);
        }

        if (gasto.getCantidad() != cantidad) {
            gasto.setCantidad(cantidad);

            List<Participacion> participaciones = participacionRepository.findByGasto_Id(id);
            double newParticipacion = Math.round((cantidad / participaciones.size()) * 100.0) / 100.0;
            for (Participacion p : participaciones) {
                p.setCantidad(newParticipacion);
            }
            participacionRepository.saveAll(participaciones);
        }

        gastoRepository.save(gasto);
        return ResponseEntity.ok(gasto);
    }

    /**
     * Método para añadir participaciones a un gasto
     * 
     * @param id ID del gasto
     * @param participaciones Lista de participaciones a añadir
     * @return
     */
    @PostMapping("/{id}")
    public ResponseEntity<?> addParticipaciones(@PathVariable("id") Long id,
            @RequestBody List<Participacion> participaciones) {
        Gasto gasto = gastoRepository.findById(id)
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
        return ResponseEntity.ok().body("Participaciones añadidas correctamente");
    }

    /**
     * Método para obtener los participantes de un gasto
     * 
     * @param id ID del gasto
     * @return Lista de usuarios participantes en el gasto
     */
    @GetMapping("/{id}/participantes")
    public ResponseEntity<List<Usuario>> getParticipantesGasto(@PathVariable("id") Long id) {
        return ResponseEntity.ok()
                .body(participacionRepository.findByGasto_Id(id)
                        .stream()
                        .map(Participacion::getUsuario)
                        .toList()
                );
    }

    /**
     * Método para obtener la ruta del recibo de un gasto
     * 
     * @param id ID del gasto
     * @return Ruta de la imagen del recibo
     */
    @GetMapping("/{id}/recibo")
    public ResponseEntity<String> getRecibo(@PathVariable("id") Long id) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gasto no encontrado"));
        return ResponseEntity.ok().body(gasto.getRecibo());
    }

    /**
     * Muestra el formulario para editar un gasto existente.
     * 
     * @param id    ID del gasto a editar
     * @param model Modelo para pasar datos a la vista
     * @return Página de edición del gasto
     */
    @GetMapping("/{id}/editar")
    public String getEditForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal Object principal) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gasto no encontrado"));
        String email = null;
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("gasto", gasto);
        return "editar-gasto";
    }

    /**
     * Método para validar los parámetros de entrada
     * 
     * @param concepto  Concepto del gasto
     * @param cantidad  Coste del gasto
     * @param grupoId   Grupo al que pertenece el gasto
     * @param pagadorId Usuario que paga el gasto
     */
    private void validateInputs(String concepto, double cantidad, Long grupoId, Long pagadorId) {
        if (concepto == null || concepto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El concepto no puede estar vacío");
        }
        if (cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que cero");
        }
        if (grupoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del grupo no puede ser nulo");
        }
        if (pagadorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del pagador no puede ser nulo");
        }
    }

    /**
     * Método para validar la imagen del recibo
     * 
     * @param img Imagen del recibo
     */
    private void validateImg(MultipartFile img) {
        if (img != null && !img.isEmpty()) {
            long maxSize = 5 * 1024 * 1024;
            if (img.getSize() > maxSize) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo es demasiado grande");
            }
            List<String> tiposPermitidos = List.of("image/jpeg", "image/jpg", "image/png", "image/tiff");
            if (!tiposPermitidos.contains(img.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no es una imagen");
            }
        }
    }
}
