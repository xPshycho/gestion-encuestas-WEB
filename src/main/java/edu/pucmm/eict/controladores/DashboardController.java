package edu.pucmm.eict.controladores;

import edu.pucmm.eict.modelos.Usuario;
import edu.pucmm.eict.modelos.Rol;
import edu.pucmm.eict.modelos.Estudiante;
import edu.pucmm.eict.services.UserService;
import edu.pucmm.eict.services.EstudianteService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.*;

public class DashboardController {

    private final UserService userService = new UserService();
    private final EstudianteService estudianteService = EstudianteService.getInstancia();

    public void registerRoutes(Javalin app) {
        app.get("/dashboard", this::handleDashboard);
        app.get("/crear-usuario", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null || usuario.getRol() != Rol.ADMINISTRADOR) {
                ctx.status(403).result("Acceso denegado");
                return;
            }
            ctx.render("crear-usuario.html");
        });
    }

    private void handleDashboard(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        if (usuario == null) {
            ctx.redirect("/login");
            return;
        }

        if (usuario.getRol() == null) {
            usuario.setRol(Rol.USUARIO);
        }

        List<Usuario> usuarios = userService.findAllUsuarios();
        List<Estudiante> estudiantes = estudianteService.listarTodos();

        int totalUsuarios = usuarios.size();
        List<Usuario> ultimosUsuarios = (totalUsuarios > 5)
                ? new ArrayList<>(usuarios.subList(totalUsuarios - 5, totalUsuarios))
                : new ArrayList<>(usuarios);
        Collections.reverse(ultimosUsuarios);

        int totalEstudiantes = estudiantes.size();
        List<Estudiante> ultimosEstudiantes = (totalEstudiantes > 5)
                ? new ArrayList<>(estudiantes.subList(totalEstudiantes - 5, totalEstudiantes))
                : new ArrayList<>(estudiantes);
        Collections.reverse(ultimosEstudiantes);

        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuario);
        if(usuario.getRol() == Rol.ADMINISTRADOR){
            model.put("usuarios", ultimosUsuarios);
        }
        model.put("estudiantes", ultimosEstudiantes);

        ctx.render("dashboard.html", model);
    }
}
