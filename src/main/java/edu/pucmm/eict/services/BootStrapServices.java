package edu.pucmm.eict.services;

import org.h2.tools.Server;

import java.sql.SQLException;

public class BootStrapServices {

    private static BootStrapServices instancia;

    private BootStrapServices() {
    }

    public static BootStrapServices getInstancia() {
        if (instancia == null) {
            instancia = new BootStrapServices();
        }
        return instancia;
    }

    // Inicia el servidor H2 usando el puerto por defecto (9092)
    public void startDb() {
        try {
            // Al no especificar -tcpPort se usa el puerto 9092
            Server.createTcpServer("-tcpAllowOthers", "-tcpDaemon", "-ifNotExists").start();
            System.out.println("Base de datos H2 en modo servidor iniciada (puerto por defecto: 9092).");
        } catch (SQLException ex) {
            System.out.println("Problema con la base de datos: " + ex.getMessage());
        }
    }

    // Metodo de inicialización general
    public void init() {
        startDb();
    }
}
