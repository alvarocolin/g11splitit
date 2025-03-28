package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Gasto;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gastos")
public class GastoController {

    @Autowired
    private GastoRepository gastoRepository;

    // Obtener todos los gastos
    @GetMapping
    public List<Gasto> getAllGastos() {
        List<Gasto> gastos = new ArrayList<>();
        gastoRepository.findAll().forEach(gastos::add);
        return gastos;
    }

    // Crear un nuevo gasto
    @PostMapping
    public ResponseEntity<Gasto> createGasto(@RequestBody Gasto gasto) {
        Gasto savedGasto = gastoRepository.save(gasto);
        return new ResponseEntity<>(savedGasto, HttpStatus.CREATED);
    }

    // Obtener un gasto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Gasto> getGastoById(@PathVariable Long id) {
        Optional<Gasto> gasto = gastoRepository.findById(id);
        return gasto.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar un gasto existente
    @PutMapping("/{id}")
    public ResponseEntity<Gasto> updateGasto(@PathVariable Long id, @RequestBody Gasto gastoDetails) {
        return gastoRepository.findById(id).map(gasto -> {
            gasto.setConcepto(gastoDetails.getConcepto());
            gasto.setCantidad(gastoDetails.getCantidad());
            gasto.setGrupo(gastoDetails.getGrupo());
            gasto.setPagador(gastoDetails.getPagador());
            Gasto updatedGasto = gastoRepository.save(gasto);
            return ResponseEntity.ok(updatedGasto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un gasto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGasto(@PathVariable Long id) {
        if (!gastoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        gastoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
