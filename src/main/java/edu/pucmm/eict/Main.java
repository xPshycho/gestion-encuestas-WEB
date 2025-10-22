package edu.pucmm.eict;

import edu.pucmm.eict.controladores.AuthController;
import edu.pucmm.eict.controladores.DashboardController;
import edu.pucmm.eict.controladores.EstudianteController;
import edu.pucmm.eict.controladores.RegistrosController;
import edu.pucmm.eict.controladores.UsuarioController;
import edu.pucmm.eict.modelos.Rol;
import edu.pucmm.eict.modelos.Usuario;
import edu.pucmm.eict.services.BootStrapServices;
import edu.pucmm.eict.services.UserService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class Main {

    public static final String APP_VERSION = String.valueOf(System.currentTimeMillis());

    public static void main(String[] args) {
        System.out.println("Iniciando aplicación...");

        BootStrapServices.getInstancia().init();

        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(templateResolver);

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
            config.fileRenderer(new JavalinThymeleaf(templateEngine));
        }).start(getHerokuAssignedPort());

        // Middleware global: redirigir a login si no hay sesión en rutas protegidas
        app.before(ctx -> {
            String path = ctx.path();
            if (!path.equals("/login") && !path.equals("/register") && !path.startsWith("/public") && ctx.sessionAttribute("usuario") == null) {
                ctx.redirect("/login");
            }
        });

        // Middleware para asignar el usuario a la petición
        app.before(ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuario");
            ctx.attribute("usuario", usuario);
        });

        // Permitir acceso a /dashboard a cualquier usuario autenticado
        app.before("/dashboard", ctx -> {
            if (ctx.sessionAttribute("usuario") == null) {
                ctx.redirect("/login");
            }
        });

        // Proteger rutas de administración: solo administradores pueden acceder a /usuarios/*
        app.before("/usuarios/*", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null || usuario.getRol() != Rol.ADMINISTRADOR) {
                ctx.status(403).result("Acceso denegado");
            }
        });

        app.get("/", ctx -> ctx.redirect("/login"));

        // WebSocket para sincronización de datos (código omitido para brevedad)
        app.ws("/sync", ws -> {
            ws.onMessage(ctx -> {
                // Código de sincronización
            });
        });

        UserService userService = new UserService();
        userService.initializeDefaultUser();

        new AuthController(userService).registerRoutes(app);
        new DashboardController().registerRoutes(app);
        new EstudianteController().registerRoutes(app);
        new RegistrosController().registerRoutes(app);
        new UsuarioController(userService).registerRoutes(app);

        System.out.println("Aplicación corriendo en: http://localhost:" + getHerokuAssignedPort());
    }

    private static int getHerokuAssignedPort() {
        String port = System.getenv("PORT");
        return port != null ? Integer.parseInt(port) : 7000;
    }
}
