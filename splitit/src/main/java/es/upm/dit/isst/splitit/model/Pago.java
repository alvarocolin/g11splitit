package es.upm.dit.isst.splitit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idPago;

    @ManyToOne
    @JoinColumn(name = "id_emisor", nullable = false)
    private Usuario emisor;

    @ManyToOne
    @JoinColumn(name = "id_receptor", nullable = false)
    private Usuario receptor;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

    // Getters y setters
    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
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
}
