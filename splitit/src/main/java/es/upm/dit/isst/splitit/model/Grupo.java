package es.upm.dit.isst.splitit.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idGrupo;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToMany
    @JoinTable(
        name = "grupo_usuarios",
        joinColumns = @JoinColumn(name = "id_grupo"),
        inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private Set<Usuario> miembros;

    // Getters y setters
    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Usuario> getMiembros() {
        return miembros;
    }

    public void setMiembros(Set<Usuario> miembros) {
        this.miembros = miembros;
    }
}
