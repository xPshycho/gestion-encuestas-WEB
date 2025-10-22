package edu.pucmm.eict.modelos;

import java.util.List;

public class SyncMessage {
    private String type;
    private List<Estudiante> data;

    public SyncMessage() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Estudiante> getData() {
        return data;
    }

    public void setData(List<Estudiante> data) {
        this.data = data;
    }
}
