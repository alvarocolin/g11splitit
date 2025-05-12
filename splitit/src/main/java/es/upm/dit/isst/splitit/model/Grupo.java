/**
 * Split.it - Grupo.java
 * Modelo de la entidad Grupo.
 * 
 * @author Grupo 11
 * @version 2.0
 * @since 2025-03-30
 */

package es.upm.dit.isst.splitit.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "grupos")
public class Grupo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "numGastos", nullable = false)
    private Long numGastos;

    @Column(name = "total", nullable = false)
    private Double total;

    @ManyToOne
    @JoinColumn(name = "admin", nullable = false)
    private Usuario admin;

    @Column(name = "saldado", nullable = false)
    private Boolean saldado = false;

    @ManyToMany
    @JoinTable(name = "grupo_usuarios", joinColumns = @JoinColumn(name = "grupo"), inverseJoinColumns = @JoinColumn(name = "usuario"))
    private Set<Usuario> miembros;

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getNumGastos() {
        return numGastos;
    }

    public void setNumGastos(Long numGastos) {
        this.numGastos = numGastos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setAdmin(Usuario admin) {
        this.admin = admin;
    }

    public Usuario getAdmin() {
        return admin;
    }

    public void setSaldado(Boolean saldado) {
        this.saldado = saldado;
    }

    /**
     * @return true si el grupo está saldado, false en caso contrario
     */
    public Boolean isSaldado() {
        return saldado;
    }

    public Set<Usuario> getMiembros() {
        return miembros;
    }

    public void setMiembros(Set<Usuario> miembros) {
        this.miembros = miembros;
    }

    public void addGasto(Double gasto) {
        this.numGastos++;
        this.total += gasto;
    }

    public void removeGasto(Double gasto) {
        this.numGastos--;
        this.total -= gasto;
    }
}
