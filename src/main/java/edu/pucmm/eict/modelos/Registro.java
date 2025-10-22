package edu.pucmm.eict.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "registros")
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del usuario que realizó el registro (por ejemplo, su username)
    @Column(nullable = false)
    private String usuarioId;

    // ID del estudiante registrado
    @Column(nullable = false)
    private Long estudianteId;

    // Ubicación (latitud y longitud) del usuario que realizó el registro
    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    public Registro() {
    }

    public Registro(String usuarioId, Long estudianteId, Double latitud, Double longitud) {
        this.usuarioId = usuarioId;
        this.estudianteId = estudianteId;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
