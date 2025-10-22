package edu.pucmm.eict.controladores;

import edu.pucmm.eict.modelos.Usuario;
import edu.pucmm.eict.modelos.Rol;
import edu.pucmm.eict.services.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioController {

    private final UserService userService;

    public UsuarioController(UserService userService) {
        this.userService = userService;
    }

    public void registerRoutes(Javalin app) {
        app.get("/usuarios", this::listarUsuarios);
        app.post("/usuarios/crear", this::registrarUsuario);
        app.post("/usuarios/sync", ctx -> {
            try {
                List<Map<String, Object>> usuariosPendientes = ctx.bodyAsClass(List.class);
                for (Map<String, Object> userData : usuariosPendientes) {
                    String username = (String) userData.get("username");
                    String nombre = (String) userData.get("nombre");
                    String password = (String) userData.get("password");
                    String rolParam = (String) userData.get("rol");
                    Rol rol = Rol.fromString(rolParam);

                    Usuario usuario = new Usuario(username, nombre, password, rol);

                    if (userService.findUsuario(username) == null) {
                        userService.crearUsuario(usuario);
                    }
                }
                ctx.status(200).result("Usuarios sincronizados correctamente.");
            } catch (Exception e) {
                ctx.status(500).result("Error al sincronizar usuarios: " + e.getMessage());
            }
        });
        app.get("/usuarios/editar/{id}", this::mostrarFormularioEdicion);
        app.post("/usuarios/editar", this::procesarEdicion);
        app.get("/usuarios/eliminar/{id}", this::eliminarUsuario);
    }

    private void registrarUsuario(Context ctx) {
        Usuario usuarioActual = ctx.sessionAttribute("usuario");
        if (usuarioActual == null || usuarioActual.getRol() != Rol.ADMINISTRADOR) {
            ctx.status(403).result("No tienes permisos para crear usuarios.");
            return;
        }
        try {
            String username = ctx.formParam("username");
            String nombre = ctx.formParam("nombre");
            String password = ctx.formParam("password");
            String rolParam = ctx.formParam("rol");
            Rol rol = Rol.fromString(rolParam);

            Usuario nuevoUsuario = new Usuario(username, nombre, password, rol);

            if (userService.findUsuario(username) != null) {
                ctx.status(409).result("El usuario ya existe.");
                return;
            }

            userService.crearUsuario(nuevoUsuario);
            ctx.status(201).result("Usuario registrado exitosamente.");
        } catch (Exception e) {
            ctx.status(500).result("Error al registrar usuario: " + e.getMessage());
        }
    }

    private void listarUsuarios(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        if (usuario == null) {
            ctx.redirect("/login");
            return;
        }

        List<Usuario> usuarios = userService.findAllUsuarios();

        int currentPage = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int pageSize = ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(5);
        int totalUsuarios = usuarios.size();
        int totalPages = (int) Math.ceil((double) totalUsuarios / pageSize);
        if (totalPages > 0 && currentPage > totalPages) { currentPage = totalPages; }

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, totalUsuarios);
        List<Usuario> usuariosPaginados = (start < end) ? usuarios.subList(start, end) : List.of();

        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuario);
        model.put("usuarios", usuariosPaginados);
        model.put("currentPage", currentPage);
        model.put("totalPages", totalPages);
        model.put("pageSize", pageSize);

        ctx.render("usuarios.html", model);
    }

    private void mostrarFormularioEdicion(Context ctx) {
        String username = ctx.pathParam("id");
        Usuario usuario = userService.findUsuario(username);
        if (usuario == null) {
            ctx.status(404).result("Usuario no encontrado");
            return;
        }
        ctx.attribute("usuarioEdicion", usuario);
        ctx.render("crear-usuario.html");
    }

    private void procesarEdicion(Context ctx) {
        String username = ctx.formParam("username");
        String nombre = ctx.formParam("nombre");
        String password = ctx.formParam("password");
        String rolParam = ctx.formParam("rol");
        Rol rol = Rol.fromString(rolParam);

        if (username == null || username.trim().isEmpty()) {
            ctx.status(400).result("El nombre de usuario es obligatorio.");
            return;
        }

        Usuario existente = userService.findUsuario(username);
        if (existente == null) {
            ctx.status(404).result("Usuario no encontrado.");
            return;
        }

        existente.setNombre(nombre);
        existente.setPassword(password);
        existente.setRol(rol);

        userService.editarUsuario(existente);
        ctx.redirect("/usuarios");
    }

    private void eliminarUsuario(Context ctx) {
        String username = ctx.pathParam("id");
        boolean eliminado = userService.eliminarUsuario(username);
        if (eliminado) {
            ctx.redirect("/usuarios");
        } else {
            ctx.status(400).result("No se pudo eliminar el usuario.");
        }
    }
}
