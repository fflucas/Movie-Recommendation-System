package com.recsys.model;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class DadosNotas {
    private final SimpleIntegerProperty userId;
    private final SimpleIntegerProperty filmeId;
    private final SimpleFloatProperty nota;

    public DadosNotas(int userId, int filmeId, float nota) {
        this.userId = new SimpleIntegerProperty(userId);
        this.filmeId = new SimpleIntegerProperty(filmeId);
        this.nota = new SimpleFloatProperty(nota);
    }

    public int getUserId() {
        return userId.get();
    }

    public SimpleIntegerProperty userIdProperty() {
        return userId;
    }

    public int getFilmeId() {
        return filmeId.get();
    }

    public SimpleIntegerProperty filmeIdProperty() {
        return filmeId;
    }

    public float getNota() {
        return nota.get();
    }

    public SimpleFloatProperty notaProperty() {
        return nota;
    }
}
