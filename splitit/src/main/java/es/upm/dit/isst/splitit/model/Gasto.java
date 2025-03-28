package es.upm.dit.isst.splitit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idGasto;

    @Column(name = "concepto", nullable = false)
    private String concepto;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    private Grupo grupo;

    @ManyToOne
    @JoinColumn(name = "id_pagador", nullable = false)
    private Usuario pagador;

    // Getters y setters
    public Long getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(Long idGasto) {
        this.idGasto = idGasto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Usuario getPagador() {
        return pagador;
    }

    public void setPagador(Usuario pagador) {
        this.pagador = pagador;
    }
}
