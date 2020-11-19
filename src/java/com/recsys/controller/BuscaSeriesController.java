package com.recsys.controller;

import com.recsys.model.DadosFilmes;
import com.recsys.model.Recomendar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BuscaSeriesController implements Initializable{

    @FXML
    private TableView<DadosFilmes> tabela;
    @FXML
    private TableColumn<DadosFilmes,String> tcTitulo;
    @FXML
    private TableColumn<DadosFilmes,String> tcGenero;
    @FXML
    private TableColumn<DadosFilmes,String> tcTotalVotos;
    @FXML
    private TableColumn<DadosFilmes,String> tcNotaMedia;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            tcTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
            tcGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
            tcTotalVotos.setCellValueFactory(new PropertyValueFactory<>("totalVotos"));
            tcNotaMedia.setCellValueFactory(new PropertyValueFactory<>("notaMedia"));

            tcTitulo.setSortable(false);
            tcGenero.setSortable(false);
            tcTotalVotos.setSortable(false);
            tcNotaMedia.setSortable(false);

            //Carrega com a lista de filmes
            Recomendar recsys = new Recomendar();
            tabela.setItems(recsys.listaDados());

            //teste improvisado
            ArrayList<DadosFilmes> recomendados =  recsys.sugerePara(250, 10);
            for (DadosFilmes filme: recomendados)
                System.out.println(filme.toString());

            // Ao selecionar uma linha na tabela o usuario indica o filme que quer avaliar
            tabela.setOnMouseClicked((MouseEvent event) -> {
                if(event.getClickCount() == 2){
                    try{
                        //pega os dados do linha selecionada
                        DadosFilmes filmeAvaliado = new DadosFilmes(
                                tabela.getSelectionModel().getSelectedItem().getFilmeId(),
                                tabela.getSelectionModel().getSelectedItem().getTitulo(),
                                tabela.getSelectionModel().getSelectedItem().getGenero(),
                                tabela.getSelectionModel().getSelectedItem().getTotalVotos(),
                                tabela.getSelectionModel().getSelectedItem().getNotaMedia());
                        //inicia o objeto para avaliar o filme e guarda o resultado na listaDadosFilmesAvaliados
                        AvaliaFilmeController afc = new AvaliaFilmeController();
                        recsys.setListaDadosFilmesAvaliados(afc.avaliarFilme(filmeAvaliado));
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }
            });
            
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void abreSobre() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informações");
        alert.setHeaderText("Sistema Desenvolvido por XX como projeto final da disciplina Programação Orientada a Objetos na UFCAT - 2020");
        alert.setContentText("Sistema de recomendação de filmes com uma abordagem Collaborative filtering.");
        alert.showAndWait();
    }
}
