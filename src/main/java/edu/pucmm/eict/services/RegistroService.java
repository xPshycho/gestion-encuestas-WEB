package edu.pucmm.eict.services;

import edu.pucmm.eict.modelos.Registro;

import java.util.List;

public class RegistroService {

    private static RegistroService instancia;
    private final GestionDb<Registro> gestionDb;

    private RegistroService() {
        this.gestionDb = new GestionDb<>(Registro.class);
    }

    public static RegistroService getInstancia() {
        if (instancia == null) {
            instancia = new RegistroService();
        }
        return instancia;
    }

    public Registro crearRegistro(Registro registro) {
        return gestionDb.crear(registro);
    }

    public List<Registro> listarRegistros() {
        return gestionDb.findAll();
    }
}
