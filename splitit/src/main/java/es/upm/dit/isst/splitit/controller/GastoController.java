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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

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

    @GetMapping("/nuevo-formulario")
    public String mostrarFormularioNuevoGasto(@RequestParam Long grupoId, @RequestParam Long usuarioId, Map<String, Object> model) {
        Grupo grupo = grupoRepository.findById(grupoId).orElseThrow();
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();

        model.put("grupo", grupo);
        model.put("usuario", usuario);
        return "addgasto";
    }

    @PostMapping("/nuevo")
    public String guardarGasto(
            @RequestParam String concepto,
            @RequestParam double cantidad,
            @RequestParam Long grupoId,
            @RequestParam Long pagadorId,
            @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {

        Grupo grupo = grupoRepository.findById(grupoId).orElseThrow();
        Usuario pagador = usuarioRepository.findById(pagadorId).orElseThrow();

        Gasto gasto = new Gasto();
        gasto.setConcepto(concepto);
        gasto.setCantidad(cantidad);
        gasto.setGrupo(grupo);
        gasto.setPagador(pagador);
        gastoRepository.save(gasto);

        // Procesar participaciones
        List<Participacion> participaciones = new ArrayList<>();

        for (String key : params.keySet()) {
            if (key.startsWith("participaciones[") && key.endsWith("].incluido")) {
                String userIdStr = key.substring("participaciones[".length(), key.indexOf("].incluido"));
                Long userId = Long.parseLong(userIdStr);

                String cantidadKey = "participaciones[" + userId + "].cantidad";
                if (params.containsKey(cantidadKey)) {
                    double cantidadUsuario = Double.parseDouble(params.get(cantidadKey));
                    Usuario usuario = usuarioRepository.findById(userId).orElseThrow();

                    Participacion p = new Participacion();
                    p.setUsuario(usuario);
                    p.setGasto(gasto);
                    p.setCantidad(cantidadUsuario);

                    participaciones.add(p);
                }
            }
        }

        participacionRepository.saveAll(participaciones);

        return "redirect:/grupos/" + grupoId;
    }
}
