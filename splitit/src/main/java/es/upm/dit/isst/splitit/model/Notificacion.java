package es.upm.dit.isst.splitit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "leido", nullable = false)
    private Boolean leido = false;

    @Column(name = "confirmando", nullable = false)
    private Boolean confirmando = false;

    @Column(name = "tipo", nullable = false)
    private Integer tipo;

    @ManyToOne
    @JoinColumn(name = "usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "pago")
    private Pago pago;

    // CONSTRUCTOR
    public Notificacion() {
    }

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return true si la notificacion se ha leido y no se muestra como toast, false en caso contrario
     */
    public Boolean isLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }

    /**
     * @return true si el usuario ha marcado una deuda como pagada para ser confirmado, false en caso contrario
     */
    public Boolean isConfirmando() {
        return confirmando;
    }

    public void setConfirmando(Boolean confirmando) {
        this.confirmando = confirmando;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

}
