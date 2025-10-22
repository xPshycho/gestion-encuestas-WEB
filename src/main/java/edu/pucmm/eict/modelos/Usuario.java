package edu.pucmm.eict.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    private String username;

    private String nombre;
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public Usuario() {
        // Constructor vacío requerido por JPA
    }

    public Usuario(String username, String nombre, String password, Rol rol) {
        this.username = username;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
