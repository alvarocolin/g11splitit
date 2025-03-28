package es.upm.dit.isst.splitit.controller;

import es.upm.dit.isst.splitit.model.Pago;
import es.upm.dit.isst.splitit.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoRepository pagoRepository;

    // Obtener todos los pagos (convertimos Iterable a List)
    @GetMapping
    public List<Pago> getAllPagos() {
        List<Pago> pagos = new ArrayList<>();
        pagoRepository.findAll().forEach(pagos::add);
        return pagos;
    }

    // Crear un nuevo pago
    @PostMapping
    public ResponseEntity<Pago> createPago(@RequestBody Pago pago) {
        Pago savedPago = pagoRepository.save(pago);
        return new ResponseEntity<>(savedPago, HttpStatus.CREATED);
    }

    // Obtener un pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pago> getPagoById(@PathVariable Long id) {
        Optional<Pago> pago = pagoRepository.findById(id);
        return pago.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar un pago existente
    @PutMapping("/{id}")
    public ResponseEntity<Pago> updatePago(@PathVariable Long id, @RequestBody Pago pagoDetails) {
        return pagoRepository.findById(id).map(pago -> {
            pago.setEmisor(pagoDetails.getEmisor());
            pago.setReceptor(pagoDetails.getReceptor());
            pago.setCantidad(pagoDetails.getCantidad());
            Pago updatedPago = pagoRepository.save(pago);
            return ResponseEntity.ok(updatedPago);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un pago
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePago(@PathVariable Long id) {
        if (!pagoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pagoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
