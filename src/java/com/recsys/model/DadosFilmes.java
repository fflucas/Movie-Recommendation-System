/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recsys.model;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DadosFilmes {
    /* SimpleIntegerProperty e SimpleStringProperty é uma implementação
    * que define um metodo comum para todas as propriedades independente
    * do tipo da variavel. Isso é feito para evitar erros de compatibilidade
    * ao usar os dados com o javafx
    */
    private final SimpleIntegerProperty filmeId;
    private final SimpleStringProperty titulo;
    private final SimpleStringProperty genero;
    private final SimpleIntegerProperty totalVotos;
    private final SimpleFloatProperty notaMedia;

    public DadosFilmes(int filmeId, String titulo, String genero, int totalVotos, float notaMedia){
        this.filmeId = new SimpleIntegerProperty(filmeId);
        this.titulo = new SimpleStringProperty(titulo);
        this.genero = new SimpleStringProperty(genero);
        this.totalVotos = new SimpleIntegerProperty(totalVotos);
        this.notaMedia = new SimpleFloatProperty(notaMedia);
    }

    //get dos atributos como tipos primitivos
    public int getFilmeId() {
        return filmeId.get();
    }
    public String getTitulo() {
        return titulo.get();
    }
    public String getGenero() { return genero.get(); }
    public int getTotalVotos() {
        return totalVotos.get();
    }
    public float getNotaMedia() {
        return notaMedia.get();
    }

    public void setTotalVotos(int totalVotos) {
        this.totalVotos.set(totalVotos);
    }

    public void setNotaMedia(float notaMedia) {
        this.notaMedia.set(notaMedia);
    }

    @Override
    public String toString() {
        return "DadosFilmes{" +
                "filmeId=" + filmeId +
                ", titulo=" + titulo +
                ", genero=" + genero +
                ", totalVotos=" + totalVotos +
                ", notaMedia=" + notaMedia +
                '}';
    }
}
