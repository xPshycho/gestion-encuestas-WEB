package edu.pucmm.eict.controladores;

import edu.pucmm.eict.modelos.Estudiante;
import edu.pucmm.eict.modelos.Registro;
import edu.pucmm.eict.services.EstudianteService;
import edu.pucmm.eict.services.RegistroService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrosController {
    private final RegistroService registroService;

    public RegistrosController() {
        this.registroService = RegistroService.getInstancia();
    }

    public void registerRoutes(Javalin app) {
        app.get("/registros", this::listarRegistros);
    }

    private void listarRegistros(Context ctx) {
        // Obtener todos los registros y paginar 5 en 5
        List<Registro> allRegistros = registroService.listarRegistros();
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int pageSize = 5;
        int totalRecords = allRegistros.size();
        int totalPages = (totalRecords + pageSize - 1) / pageSize;

        // Validar que el índice esté en el rango
        int fromIndex = Math.max(0, (page - 1) * pageSize);
        int toIndex = Math.min(fromIndex + pageSize, totalRecords);
        List<Registro> registrosPage = allRegistros.subList(fromIndex, toIndex);

        // Obtener la información de estudiantes
        List<Estudiante> estudiantes = EstudianteService.getInstancia().listarTodos();
        Map<Long, Map<String, Object>> estudianteData = new HashMap<>();
        for (Estudiante estudiante : estudiantes) {
            Map<String, Object> data = new HashMap<>();
            data.put("nombre", estudiante.getNombre());
            data.put("nivelEscolar", estudiante.getNivelEscolar().name());
            estudianteData.put(estudiante.getId(), data);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("registros", registrosPage);
        model.put("estudianteData", estudianteData);
        model.put("currentPage", page);
        model.put("totalPages", totalPages);

        // Obtener la clave de API de Google Maps desde las variables de entorno
        String apiKey = System.getenv("GOOGLE_MAPS_API_KEY");
        model.put("googleMapsApiKey", apiKey);

        ctx.render("registros.html", model);
    }


}
