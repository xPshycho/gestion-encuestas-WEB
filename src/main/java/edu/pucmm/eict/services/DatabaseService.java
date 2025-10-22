package edu.pucmm.eict.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseService {

    private static DatabaseService instancia;
    // URL actualizada sin puerto (usa el 9092 por defecto)
    private String URL = "jdbc:h2:tcp://localhost/~/P2web;IFEXISTS=FALSE";

    private DatabaseService() {
        registrarDriver();
    }

    public static DatabaseService getInstancia() {
        if (instancia == null) {
            instancia = new DatabaseService();
        }
        return instancia;
    }

    private void registrarDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConexion() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, "sa", "");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }

    public void testConexion() {
        try {
            getConexion().close();
            System.out.println("Conexión realizada con éxito...");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
