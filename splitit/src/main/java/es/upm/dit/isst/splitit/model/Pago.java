/**
 * Split.it - Pago.java
 * Modelo de la entidad Pago.
 * 
 * @author Grupo 11
 * @version 2.0
 * @since 2025-03-30
 */

package es.upm.dit.isst.splitit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emisor", nullable = false)
    private Usuario emisor;

    @ManyToOne
    @JoinColumn(name = "receptor", nullable = false)
    private Usuario receptor;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

    @Column(name = "estado", nullable = false)
    private Boolean estado = false;

    @Column(name = "grupo", nullable = false)
    private String grupo;

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getEmisor() {
        return emisor;
    }

    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }

    public Usuario getReceptor() {
        return receptor;
    }

    public void setReceptor(Usuario receptor) {
        this.receptor = receptor;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

}
