package edu.pucmm.eict.services;

import edu.pucmm.eict.modelos.Usuario;
import edu.pucmm.eict.modelos.Rol;
import java.util.List;

public class UserService {

    private GestionDb<Usuario> gestion = new GestionDb<>(Usuario.class);

    public Usuario crearUsuario(Usuario usuario) {
        return gestion.crear(usuario);
    }

    public Usuario editarUsuario(Usuario usuario) {
        return gestion.editar(usuario);
    }

    public boolean eliminarUsuario(String username) {
        return gestion.eliminar(username);
    }

    public Usuario findUsuario(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null; // Evita hacer la consulta si el username es inválido
        }
        return gestion.find(username);
    }

    public List<Usuario> findAllUsuarios() {
        return gestion.findAll();
    }

    public void registrarUsuario(String username, String nombre, String password, Rol rol) {
        Usuario usuario = new Usuario(username, nombre, password, rol);
        crearUsuario(usuario);
    }

    /**
     * Crea el usuario por defecto "admin" si no existe.
     */
    public void initializeDefaultUser() {
        if (findUsuario("admin") == null) {
            crearUsuario(new Usuario("admin", "Administrador", "admin", Rol.ADMINISTRADOR));
            System.out.println("Usuario administrador creado.");
        }
    }
}
