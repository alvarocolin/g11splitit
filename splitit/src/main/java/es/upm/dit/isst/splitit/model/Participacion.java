package es.upm.dit.isst.splitit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "participaciones")
public class Participacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idParticipacion;

    @ManyToOne
    @JoinColumn(name = "id_gasto", nullable = false)
    private Gasto gasto;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

    // Getters y setters
    public Long getIdParticipacion() {
        return idParticipacion;
    }

    public void setIdParticipacion(Long idParticipacion) {
        this.idParticipacion = idParticipacion;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }
}
