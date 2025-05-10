/**
 * Split.it - PagoController.java
 * Controlador para gestionar los pagos.
 * 
 * @author Grupo 11
 * @version 2.0
 * @since 2023-10-01
 */

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

    /**
     * Método para obtener todos los pagos
     * 
     * @return Lista de todos los pagos
     */
    @GetMapping
    public List<Pago> getAll() {
        List<Pago> pagos = new ArrayList<>();
        pagoRepository.findAll().forEach(pagos::add);
        return pagos;
    }

    /**
     * Método para crear un nuevo pago
     * 
     * @param newPago Pago a crear
     * @return Pago creado
     */
    @PostMapping("/crear")
    public ResponseEntity<Pago> create(@RequestBody Pago newPago) {
        Pago result = pagoRepository.save(newPago);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Método para obtener un pago con su ID
     * 
     * @param id ID del pago a obtener
     * @return Pago encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pago> getOne(@PathVariable("id") Long id) {
        Optional<Pago> pago = pagoRepository.findById(id);
        return pago.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Método para actualizar un pago
     * 
     * @param newPago Pago a actualizar
     * @param id      ID del pago a actualizar
     * @return Pago actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Pago> update(@RequestBody Pago newPago, @PathVariable Long id) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    pago.setEmisor(newPago.getEmisor());
                    pago.setReceptor(newPago.getReceptor());
                    pago.setCantidad(newPago.getCantidad());
                    pagoRepository.save(pago);
                    return ResponseEntity.ok(pago);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Método para actualizar un pago
     * 
     * @param newPago Pago a actualizar
     * @param id      ID del pago a actualizar
     * @return Pago actualizado
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Pago> partialUpdate(@RequestBody Pago newPago, @PathVariable Long id) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    if (newPago.getEmisor() != null) {
                        pago.setEmisor(newPago.getEmisor());
                    }
                    if (newPago.getReceptor() != null) {
                        pago.setReceptor(newPago.getReceptor());
                    }
                    if (newPago.getCantidad() != null) {
                        pago.setCantidad(newPago.getCantidad());
                    }
                    if (newPago.getEstado() != null) {
                        pago.setEstado(newPago.getEstado());
                    }
                    if (newPago.getGrupo() != null) {
                        pago.setGrupo(newPago.getGrupo());
                    }
                    pagoRepository.save(pago);
                    return ResponseEntity.ok(pago);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Método para eliminar un pago
     * 
     * @param id ID del pago a eliminar
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!pagoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pagoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
