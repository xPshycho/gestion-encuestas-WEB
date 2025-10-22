package edu.pucmm.eict.controladores;

import edu.pucmm.eict.modelos.Rol;
import edu.pucmm.eict.modelos.Usuario;
import edu.pucmm.eict.services.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;
import edu.pucmm.eict.Main;

public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    public void registerRoutes(Javalin app) {
        // Se registra una única ruta GET para "/login" que inyecta la versión de la app
        app.get("/login", this::mostrarLogin);
        app.get("/register", ctx -> ctx.render("register.html"));
        app.post("/login", this::handleLogin);
        app.post("/register", this::handleRegister);
        app.get("/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });
    }

    private void handleLogin(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        Usuario usuario = userService.findUsuario(username);
        if (usuario != null && usuario.getPassword().equals(password)) {
            ctx.sessionAttribute("usuario", usuario);
            // Redirigir siempre a /dashboard, sin importar el rol.
            ctx.redirect("/dashboard");
        } else {
            ctx.attribute("error", "Credenciales incorrectas.");
            ctx.render("login.html");
        }
    }

    private void mostrarLogin(Context ctx) {
        // Inyectamos la versión actual para que el template la use
        Map<String, Object> model = new HashMap<>();
        model.put("appVersion", Main.APP_VERSION);
        ctx.render("login.html", model);
    }

    private void handleRegister(Context ctx) {
        String username = ctx.formParam("username");
        String nombre = ctx.formParam("nombre");
        String password = ctx.formParam("password");
        // Leer el parámetro "rol" enviado por el formulario
        String rolParam = ctx.formParam("rol");
        // Convertirlo al enum correspondiente
        Rol rol = Rol.fromString(rolParam);

        if (username == null || username.trim().isEmpty()) {
            ctx.status(400).result("Error: El nombre de usuario no puede estar vacío.");
            return;
        }

        if (userService.findUsuario(username) != null) {
            ctx.status(400).result("Error: El usuario ya existe.");
            return;
        }

        userService.registrarUsuario(username, nombre, password, rol);
        if (ctx.sessionAttribute("usuario") != null) {
            ctx.redirect("/dashboard");
        } else {
            ctx.redirect("/login");
        }
    }
}
