package edu.pucmm.eict.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String sector;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelEscolar nivelEscolar;

    @Column(nullable = false)
    private String usuarioRegistro;

    public Estudiante() {
    }

    public Estudiante(String nombre, String sector, NivelEscolar nivelEscolar, String usuarioRegistro) {
        this.nombre = nombre;
        this.sector = sector;
        this.nivelEscolar = nivelEscolar;
        this.usuarioRegistro = usuarioRegistro;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public NivelEscolar getNivelEscolar() {
        return nivelEscolar;
    }

    public void setNivelEscolar(NivelEscolar nivelEscolar) {
        this.nivelEscolar = nivelEscolar;
    }

    public String getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }
}
