/**
 * Split.it - GrupoController.java
 * Controlador para gestionar los grupos de gasto.
 * 
 * @author Grupo 11
 * @version 2.0
 * @since 2025-03-30
 */

package es.upm.dit.isst.splitit.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import es.upm.dit.isst.splitit.model.Gasto;
import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Notificacion;
import es.upm.dit.isst.splitit.model.Pago;
import es.upm.dit.isst.splitit.model.Participacion;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import es.upm.dit.isst.splitit.repository.GrupoRepository;
import es.upm.dit.isst.splitit.repository.NotificacionRepository;
import es.upm.dit.isst.splitit.repository.PagoRepository;
import es.upm.dit.isst.splitit.repository.ParticipacionRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;

@Controller
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ParticipacionRepository participacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Método para obtener todos los grupos
     * 
     * @return Lista de todos los grupos
     */
    @GetMapping
    @ResponseBody
    public List<Grupo> getAll() {
        return (List<Grupo>) grupoRepository.findAll();
    }

    /**
     * Método para crear un nuevo grupo
     * 
     * @param newGrupo Grupo a crear
     * @return Grupo creado
     */
    @PostMapping("/crear")
    public String create(@ModelAttribute Grupo grupo, @AuthenticationPrincipal Object principal) {
        String email = null;
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (grupo.getMiembros() == null) {
            grupo.setMiembros(new HashSet<>());
        }
        grupo.setAdmin(usuario);
        grupo.setNumGastos(0L);
        grupo.setTotal(0.0);
        grupo.getMiembros().add(usuario);
        grupoRepository.save(grupo);
        return "redirect:/grupos/" + grupo.getId() + "/editar";
    }

    /**
     * Método para obtener un grupo con su ID
     * 
     * @param id        ID del grupo a obtener
     * @param model     Modelo para la vista
     * @param principal Usuario que realiza la acción
     * @return Grupo encontrado
     */
    @GetMapping("/{id}")
    @Transactional
    public String getOne(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal Object principal) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));

        String userEmail = null;
        if (principal instanceof OAuth2User) {
            userEmail = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        }
        final String email = userEmail;
        if (grupo.getMiembros().stream().noneMatch(usuario -> usuario.getEmail().equals(email))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else {
            List<?> gastos = gastoRepository.findByGrupo(grupo);
            Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            model.addAttribute("grupo", grupo);
            model.addAttribute("gastos", gastos);
            model.addAttribute("usuario", usuario);

            return "grupo";
        }
    }

    /**
     * Método para actualizar un grupo
     * 
     * @param newGrupo  Grupo a actualizar
     * @param id        ID del grupo a actualizar
     * @param principal Usuario que realiza la acción
     * @return Grupo actualizado
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Grupo> update(@RequestBody Grupo newGrupo, @PathVariable("id") Long id, @AuthenticationPrincipal Object principal) {
        return grupoRepository.findById(id)
                .map(grupo -> {
                    String email = null;
                    if (principal instanceof OAuth2User) {
                        email = ((OAuth2User) principal).getAttribute("email");
                    } else if (principal instanceof UserDetails) {
                        email = ((UserDetails) principal).getUsername();
                    }
                    if (grupo.getAdmin().getEmail().equals(email)) {
                        grupo.setNombre(newGrupo.getNombre());
                        grupo.setMiembros(newGrupo.getMiembros());
                        grupoRepository.save(grupo);
                        return ResponseEntity.ok(grupo);
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Método para actualizar parcialmente un grupo
     * 
     * @param newGrupo  Grupo a actualizar
     * @param id        ID del grupo a actualizar
     * @param principal Usuario que realiza la acción
     * @return Grupo actualizado
     */
    @PatchMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Grupo> partialUpdate(@RequestBody Grupo newGrupo, @PathVariable("id") Long id,
            @AuthenticationPrincipal Object principal) {
        return grupoRepository.findById(id)
                .map(grupo -> {
                    String email = null;
                    if (principal instanceof OAuth2User) {
                        email = ((OAuth2User) principal).getAttribute("email");
                    } else if (principal instanceof UserDetails) {
                        email = ((UserDetails) principal).getUsername();
                    }
                    if (grupo.getAdmin().getEmail().equals(email)) {
                        if (newGrupo.getNombre() != null) {
                            grupo.setNombre(newGrupo.getNombre());
                        }
                        if (newGrupo.getMiembros() != null) {
                            grupo.setMiembros(newGrupo.getMiembros());
                        }
                        if (newGrupo.isSaldado() != null) {
                            grupo.setSaldado(newGrupo.isSaldado());
                        }
                        grupoRepository.save(grupo);
                        return ResponseEntity.ok().body(grupo);
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Método para eliminar un grupo
     * 
     * @param id ID del grupo a eliminar
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteGrupo(@PathVariable Long id) {
        if (!grupoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        grupoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Método para saldar un grupo
     * 
     * @param id        ID del grupo a saldar
     * @param principal Usuario que realiza la acción
     * @return Grupo actualizado
     */
    @PutMapping("/{id}/saldar")
    @ResponseBody @Transactional
    public ResponseEntity<?> saldarGrupo(@PathVariable("id") Long id, @AuthenticationPrincipal Object principal) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        if (grupo.isSaldado()) {
            throw new IllegalStateException("El grupo ya está saldado");
        }
        String email = null;
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }
        if (grupo.getAdmin().getEmail().equals(email)) {
            List<Gasto> gastosGrupo = gastoRepository.findByGrupo(grupo);
            List<Pago> pagos = generatePagosGrupo(gastosGrupo);

            for (Pago pago : pagos) {
                Notificacion n = new Notificacion();
                n.setMensaje("Le debes <strong>" + String.format("%.2f", pago.getCantidad()).replace('.', ',') + "€</strong> a <strong>" + pago.getReceptor().getNombre() + "</strong>");
                n.setUsuario(pago.getEmisor());
                n.setPago(pago);
                n.setTipo(1);
                notificacionRepository.save(n);
                pago.setGrupo(grupo.getNombre());
            }

            pagoRepository.saveAll(pagos);

            grupo.setSaldado(true);
            grupoRepository.save(grupo);
            return ResponseEntity.ok().body(grupo);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Método para añadir un usuario a un grupo
     * 
     * @param user      Usuario a añadir
     * @param id        Grupo al que se añade el usuario
     * @param principal Usuario que realiza la acción
     * @return Grupo actualizado
     */
    @PostMapping("/{id}/add-user")
    public String addUsuarioToGrupo(@RequestParam String usuarioEmail, @PathVariable("id") Long id, @AuthenticationPrincipal Object principal) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        String email = null;
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }
        if (grupo.getAdmin().getEmail().equals(email) && !grupo.isSaldado()) {
            Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
            if (usuario == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            } else if (grupo.getMiembros().contains(usuario)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya es miembro del grupo");
            }
            grupo.getMiembros().add(usuario);
            grupoRepository.save(grupo);
            return "redirect:/grupos/" + id + "/editar";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Método para eliminar un usuario de un grupo
     * 
     * @param user      Participante a eliminar
     * @param id        Grupo del que se elimina el usuario
     * @param principal Usuario que realiza la acción
     * @return Grupo actualizado
     */
    @PostMapping("/{id}/remove-user")
    public String removeUsuarioFromGrupo(@RequestParam String usuarioEmail, @PathVariable("id") Long id, @AuthenticationPrincipal Object principal) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        String email = null;
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }
        if (grupo.getAdmin().getEmail().equals(email) && !grupo.isSaldado()) {
            Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
            if (usuario == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            } else if (!grupo.getMiembros().contains(usuario)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario no es miembro del grupo");
            } else if (grupo.getAdmin().equals(usuario)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar al administrador del grupo");
            }
            grupo.getMiembros().removeIf(u -> u.getEmail().equals(usuarioEmail));
            grupoRepository.save(grupo);
            return "redirect:/grupos/" + id + "/editar";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Método para ver los detalles de un grupo
     * 
     * @param user      Usuario a añadir
     * @param id        Grupo al que se añade el usuario
     * @param principal Usuario que realiza la acción
     * @return Página de detalles del grupo
     */
    @GetMapping("/{id}/editar")
    public String getDetalles(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal Object principal) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        String email = null;
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        model.addAttribute("grupo", grupo);
        model.addAttribute("usuario", usuario);
        return "editar-grupo";
    }

    /**
     * Método para obtener el balance de un usuario en un grupo
     * 
     * @param id ID del grupo
     * @param user ID del usuario
     * @return Balance del usuario en el grupo
     */
    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalanceUsuario(@PathVariable("id") Long id, @RequestParam("usuario") Long user) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));

        List<Gasto> gastosGrupo = gastoRepository.findByGrupo(grupo);

        double totalDebe = 0.0;
        double totalLeDeben = 0.0;

        for (Gasto gasto : gastosGrupo) {
            Long idPagador = gasto.getPagador().getId();
            List<Participacion> participaciones = participacionRepository.findByGasto_Id(gasto.getId());

            for (Participacion p : participaciones) {
                Long idParticipante = p.getUsuario().getId();
                if (idParticipante.equals(user)) {
                    // Lo que debe el usuario
                    if (!idParticipante.equals(idPagador)) {
                        totalDebe += p.getCantidad();
                    }
                // Lo que le deben al usuario
                } else if (idPagador.equals(user)) {
                    totalLeDeben += p.getCantidad();
                }
            }
        }
        return ResponseEntity.ok().body(totalLeDeben - totalDebe);
    }

    /**
     * Método para obtener el balance de los usuarios de un grupo
     * 
     * @param id ID del grupo
     * @return Balance del usuario en el grupo
     */
    @GetMapping("/{id}/balances")
    public ResponseEntity<?> getBalancesGrupo(@PathVariable("id") Long id) {
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

    /**
     * Método para obtener el balance de un usuario en un grupo
     * 
     * @param id ID del grupo
     * @param user ID del usuario
     * @return Balance del usuario en el grupo
     */
    @GetMapping("/{id}/deuda")
    public ResponseEntity<?> getDeudaUsuario(@PathVariable("id") Long id, @RequestParam("usuario") Long user) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        Pago pago = pagoRepository.findByGrupoAndEmisor_Id(grupo.getNombre(), user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));
        return ResponseEntity.ok().body(pago);
    }

    /**
     * Método para obtener los pagos a realizar en un grupo
     * 
     * @param gastosGrupo Lista de gastos del grupo
     * @return Lista de pagos a realizar
     */
    private List<Pago> generatePagosGrupo(List<Gasto> gastosGrupo) {
        Map<Long, Double> balances = new HashMap<>();

        // calcular balances de los participantes del grupo
        for (Gasto gasto : gastosGrupo) {
            Long idPagador = gasto.getPagador().getId();
            List<Participacion> participaciones = participacionRepository.findByGasto_Id(gasto.getId());

            for (Participacion p : participaciones) {
                Long idParticipante = p.getUsuario().getId();
                double cantidad = p.getCantidad();
                if (!idParticipante.equals(idPagador)) {
                    balances.put(idParticipante, balances.getOrDefault(idParticipante, 0.0) - cantidad);
                    balances.put(idPagador, balances.getOrDefault(idPagador, 0.0) + cantidad);
                }
            }
        }

        // separar entre deudores (balance negativo) y acreedores (balance positivo)
        List<Map.Entry<Long, Double>> acreedores = new ArrayList<>();
        List<Map.Entry<Long, Double>> deudores = new ArrayList<>();

        for (Map.Entry<Long, Double> entry : balances.entrySet()) {
            if (entry.getValue() > 0) {
                acreedores.add(entry);
            } else if (entry.getValue() < 0) {
                deudores.add(entry);
            }
            // los participantes con balance 0 no tienen que realizar ningún pago
        }

        acreedores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        deudores.sort((a, b) -> Double.compare(a.getValue(), b.getValue()));

        List<Pago> pagos = new ArrayList<>();

        int i = 0, j = 0;
        while (i < deudores.size() && j < acreedores.size()) {
            Map.Entry<Long, Double> deudor = deudores.get(i);
            Map.Entry<Long, Double> acreedor = acreedores.get(j);

            double deuda = Math.min(-deudor.getValue(), acreedor.getValue());

            if (deuda > 0) {
                Usuario usuarioDeudor = usuarioRepository.findById(deudor.getKey()).orElseThrow();
                Usuario usuarioReceptor = usuarioRepository.findById(acreedor.getKey()).orElseThrow();

                Pago pago = new Pago();
                pago.setEmisor(usuarioDeudor);
                pago.setReceptor(usuarioReceptor);
                pago.setCantidad(deuda);
                pagos.add(pago);

                deudor.setValue(deudor.getValue() + deuda);
                acreedor.setValue(acreedor.getValue() - deuda);
            }

            if (Math.abs(deudor.getValue()) < 0.01) { i++; }
            if (Math.abs(acreedor.getValue()) < 0.01) { j++; }
        }

        return pagos;
    }

}
