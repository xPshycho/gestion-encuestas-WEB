package edu.pucmm.eict.modelos;

public enum Rol {
    USUARIO,
    ENCUESTADOR,
    ADMINISTRADOR;

    /**
     * Convierte un String a un valor del enum.
     * Si el parámetro es nulo o no coincide, retorna USUARIO por defecto.
     */
    public static Rol fromString(String rol) {
        if (rol == null) {
            return USUARIO;
        }
        switch (rol.toLowerCase()) {
            case "encuestador":
                return ENCUESTADOR;
            case "admin":
            case "administrador":
                return ADMINISTRADOR;
            case "usuario":
            default:
                return USUARIO;
        }
    }
}
