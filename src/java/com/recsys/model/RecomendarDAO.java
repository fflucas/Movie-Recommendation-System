/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recsys.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class RecomendarDAO {
    public void executaManipulacao(){
        try{
            //checa se o .json existe, senão converte o csv para json
            String currentWorkingPath = System.getProperty("user.dir");
            File jsonRatings = new File(currentWorkingPath + "\\src\\assets\\ratings.json");
            File jsonMovies = new File(currentWorkingPath + "\\src\\assets\\movies.json");
            if (!jsonRatings.exists() || !jsonMovies.exists()){
                csv2json(currentWorkingPath + "\\src\\assets\\ratings.csv", currentWorkingPath + "\\src\\assets\\ratings.json");
                csv2json(currentWorkingPath + "\\src\\assets\\movies.csv", currentWorkingPath + "\\src\\assets\\movies.json");
            }
        } catch (IOException e) {
            System.out.println("Alguns dos arquivos não puderam ser lidos"+e.getMessage());
        }
    }

    public String carregaFilmes(){
        //abre o arquivo movies.json como string
        String currentWorkingPath = System.getProperty("user.dir");
        String filmes = null;
        try {
            filmes = new String(Files.readAllBytes(Paths.get(currentWorkingPath + "\\src\\assets\\movies.json")));
        } catch (IOException e) {
            System.out.println("O arquivo movies.json não pode ser lido: "+e.getMessage());
        }
        return filmes;
    }

    public String carregaNotas(){
        //abre o arquivo movies.json como string
        String currentWorkingPath = System.getProperty("user.dir");
        String notas = null;
        try {
            notas = new String(Files.readAllBytes(Paths.get(currentWorkingPath + "\\src\\assets\\ratings.json")));
        } catch (IOException e) {
            System.out.println("O arquivo ratings.json não pode ser lido: "+e.getMessage());
        }
        return notas;
    }

    public void csv2json(String input, String output) throws IOException {
        File fInput = new File(input);
        File fOutput = new File(output);

        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
        CsvMapper csvMapper = new CsvMapper();

        // Read data from CSV file
        List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(fInput).readAll();

        ObjectMapper mapper = new ObjectMapper();

        // Write JSON formatted data to output.json file
        mapper.writerWithDefaultPrettyPrinter().writeValue(fOutput, readAll);

        // Write JSON formatted data to stdout
        //System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll));
    }
}