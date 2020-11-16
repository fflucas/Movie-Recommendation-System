package com.recsys.model;

import com.recsys.model.DadosFilmes;

public class DadosFilmesAvaliados {
    private DadosFilmes filmeAssistido;
    private float nota;

    public DadosFilmesAvaliados(DadosFilmes filmeAssistido, float nota) {
        this.filmeAssistido = filmeAssistido;
        this.nota = nota;
    }

    public DadosFilmes getFilmeAssistido() {
        return filmeAssistido;
    }

    public void setFilmeAssistido(DadosFilmes filmeAssistido) {
        this.filmeAssistido = filmeAssistido;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }
}
