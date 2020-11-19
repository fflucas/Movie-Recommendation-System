package com.recsys.model;

public class DadosFilmesAvaliados {
    private final DadosFilmes filmeAssistido;
    private final float nota;

    public DadosFilmesAvaliados(DadosFilmes filmeAssistido, float nota) {
        this.filmeAssistido = filmeAssistido;
        this.nota = nota;
    }
}
