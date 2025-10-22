package edu.pucmm.eict.services;

import edu.pucmm.eict.modelos.Estudiante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class EstudianteService extends GestionDb<Estudiante> {

    private static EstudianteService instancia;

    private EstudianteService() {
        super(Estudiante.class);
    }

    public static EstudianteService getInstancia() {
        if (instancia == null) {
            instancia = new EstudianteService();
        }
        return instancia;
    }

    // Metodo para obtener todos los estudiantes
    public List<Estudiante> listarTodos() {
        return findAll();
    }

    // Metodo para buscar un estudiante por ID
    public Estudiante buscarPorId(Long id) {
        return find(id);
    }

    // Metodo para crear un estudiante
    public Estudiante crearEstudiante(Estudiante estudiante) {
        return crear(estudiante);
    }

    // Metodo para actualizar un estudiante
    public Estudiante actualizarEstudiante(Estudiante estudiante) {
        return editar(estudiante);
    }

    // Metodo para eliminar un estudiante
    public boolean eliminarEstudiante(Long id) {
        return eliminar(id);
    }

    // Buscar estudiantes por nombre con patrón LIKE
    public List<Estudiante> buscarPorNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("SELECT e FROM Estudiante e WHERE e.nombre LIKE :nombre");
            query.setParameter("nombre", nombre + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Consulta nativa de todos los estudiantes
    public List<Estudiante> consultaNativa() {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT * FROM estudiante", Estudiante.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
