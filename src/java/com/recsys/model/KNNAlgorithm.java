package com.recsys.model;

import java.util.ArrayList;

import com.recsys.model.DadosFilmes;
import javafx.beans.property.SimpleStringProperty;

public class KNNAlgorithm {
    public ArrayList<DadosFilmes> findKNNs(ArrayList<DadosFilmes> listDadosSeries, DadosFilmes serieAssistida){
        
        ArrayList<DadosFilmes> listaSeries = new ArrayList<>();
        DadosFilmes ds;
        
        for (int i = 0; i < listDadosSeries.size(); i++) {
            double somatorio = 0, dEuclidiana;
            somatorio += Math.pow(Double.parseDouble(serieAssistida.getGenero()) - Double.parseDouble(listDadosSeries.get(i).getGenero()), 2);

            
            dEuclidiana = Math.sqrt(somatorio);
            SimpleStringProperty dEuclidianaProperty = new SimpleStringProperty(String.valueOf(dEuclidiana));
            ds = new DadosFilmes(listDadosSeries.get(i).getFilmeId(), listDadosSeries.get(i).getTitulo(),
                    listDadosSeries.get(i).getGenero(), listDadosSeries.get(i).getTotalVotos(), listDadosSeries.get(i).getNotaMedia());
            
            listaSeries.add(ds);
        }
        return listaSeries;
    }
}
