package edu.pucmm.eict.controladores;

import edu.pucmm.eict.modelos.Estudiante;
import edu.pucmm.eict.modelos.NivelEscolar;
import edu.pucmm.eict.modelos.Registro;
import edu.pucmm.eict.modelos.Usuario;
import edu.pucmm.eict.modelos.Rol;
import edu.pucmm.eict.services.EstudianteService;
import edu.pucmm.eict.services.RegistroService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstudianteController {

    private final EstudianteService estudianteService = EstudianteService.getInstancia();
    private final RegistroService registroService = RegistroService.getInstancia();

    public void registerRoutes(Javalin app) {
        app.get("/estudiantes", this::listarEstudiantes);

        app.get("/estudiantes/crear", this::mostrarFormularioCrear);
        app.post("/estudiantes/crear", this::crearEstudiante);

        app.get("/estudiantes/editar/{id}", this::mostrarFormularioEdicion);
        app.post("/estudiantes/editar", this::procesarEdicion);

        app.get("/estudiantes/eliminar/{id}", this::eliminarEstudiante);
    }

    private void listarEstudiantes(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        if (usuario == null) {
            ctx.redirect("/login");
            return;
        }

        List<Estudiante> estudiantes = estudianteService.listarTodos();

        int currentPage = 1, pageSize = 5;
        String pageParam = ctx.queryParam("page");
        if (pageParam != null) {
            try { currentPage = Integer.parseInt(pageParam); } catch (NumberFormatException e) { currentPage = 1; }
        }
        String pageSizeParam = ctx.queryParam("pageSize");
        if (pageSizeParam != null) {
            try { pageSize = Integer.parseInt(pageSizeParam); } catch (NumberFormatException e) { pageSize = 5; }
        }

        int totalEstudiantes = estudiantes.size();
        int totalPages = (int) Math.ceil((double) totalEstudiantes / pageSize);
        if (totalPages > 0 && currentPage > totalPages) { currentPage = totalPages; }

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, totalEstudiantes);
        List<Estudiante> estudiantesPaginados = estudiantes.subList(start, end);

        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuario);
        model.put("estudiantes", estudiantesPaginados);
        model.put("currentPage", currentPage);
        model.put("totalPages", totalPages);
        model.put("pageSize", pageSize);

        ctx.render("estudiantes.html", model);
    }

    private void mostrarFormularioCrear(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        // Solo ENCUESTADOR y ADMINISTRADOR pueden crear estudiantes
        if (usuario.getRol() == Rol.USUARIO) {
            ctx.status(403).result("No tienes permisos para crear estudiantes.");
            return;
        }
        Map<String, Object> model = new HashMap<>();
        model.put("nivelesEscolares", NivelEscolar.values());
        ctx.render("crear-estudiante.html", model);
    }

    private void crearEstudiante(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        if (usuario.getRol() == Rol.USUARIO) {
            ctx.status(403).result("No tienes permisos para crear estudiantes.");
            return;
        }

        String nombre = ctx.formParam("nombre");
        String sector = ctx.formParam("sector");
        String nivelEscolarStr = ctx.formParam("nivelEscolar");

        String usuarioRegistro = usuario.getUsername();

        String latStr = ctx.formParam("latitud");
        String lonStr = ctx.formParam("longitud");
        double latitud = (latStr == null || latStr.isEmpty()) ? 0.0 : Double.parseDouble(latStr);
        double longitud = (lonStr == null || lonStr.isEmpty()) ? 0.0 : Double.parseDouble(lonStr);

        NivelEscolar nivelEscolar = NivelEscolar.valueOf(nivelEscolarStr);
        Estudiante estudiante = new Estudiante(nombre, sector, nivelEscolar, usuarioRegistro);
        estudianteService.crearEstudiante(estudiante);

        Registro registro = new Registro(usuarioRegistro, estudiante.getId(), latitud, longitud);
        registroService.crearRegistro(registro);

        ctx.redirect("/dashboard");
    }

    private void mostrarFormularioEdicion(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        // Solo ENCUESTADOR y ADMINISTRADOR pueden editar estudiantes
        if (usuario.getRol() == Rol.USUARIO) {
            ctx.status(403).result("No tienes permisos para editar estudiantes.");
            return;
        }

        String idStr = ctx.pathParam("id");
        Long id = Long.parseLong(idStr);
        Estudiante estudiante = estudianteService.buscarPorId(id);
        if (estudiante == null) {
            ctx.status(404).result("Estudiante no encontrado");
            return;
        }
        Map<String, Object> model = new HashMap<>();
        model.put("estudianteEdicion", estudiante);
        model.put("nivelesEscolares", NivelEscolar.values());
        ctx.render("crear-estudiante.html", model);
    }

    private void procesarEdicion(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        if (usuario.getRol() == Rol.USUARIO) {
            ctx.status(403).result("No tienes permisos para editar estudiantes.");
            return;
        }

        String idStr = ctx.formParam("id");
        if (idStr == null || idStr.isEmpty()) {
            ctx.status(400).result("El id del estudiante es obligatorio");
            return;
        }
        Long id = Long.parseLong(idStr);
        Estudiante estudiante = estudianteService.buscarPorId(id);
        if (estudiante == null) {
            ctx.status(404).result("Estudiante no encontrado");
            return;
        }
        estudiante.setNombre(ctx.formParam("nombre"));
        estudiante.setSector(ctx.formParam("sector"));
        String nivelEscolarStr = ctx.formParam("nivelEscolar");
        if (nivelEscolarStr != null && !nivelEscolarStr.isEmpty()) {
            estudiante.setNivelEscolar(NivelEscolar.valueOf(nivelEscolarStr));
        }
        estudianteService.actualizarEstudiante(estudiante);
        ctx.redirect("/dashboard");
    }

    private void eliminarEstudiante(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        // Solo ADMINISTRADOR puede eliminar estudiantes vía HTTP
        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            ctx.status(403).result("No tienes permisos para eliminar estudiantes.");
            return;
        }
        String idStr = ctx.pathParam("id");
        Long id = Long.parseLong(idStr);
        boolean eliminado = estudianteService.eliminarEstudiante(id);
        if (eliminado) {
            ctx.redirect("/dashboard");
        } else {
            ctx.status(400).result("No se pudo eliminar el estudiante.");
        }
    }
}
