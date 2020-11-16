package com.recsys.controller;

import com.recsys.model.DadosFilmes;
import com.recsys.model.DadosFilmesAvaliados;

import java.util.Scanner;

public class AvaliaFilmeController {
    public DadosFilmesAvaliados avaliarFilme(DadosFilmes filmeAvaliado){
        Scanner sc = new Scanner(System.in);
        System.out.print("Para o filme:\n" +
                "Título:" + filmeAvaliado.getTitulo() +
                "\nGenêro: " + filmeAvaliado.getGenero() +
                "\nDê uma nota de 0 a 5: ");
        float nota = sc.nextFloat();
        return new DadosFilmesAvaliados(filmeAvaliado, nota);
    }
}
