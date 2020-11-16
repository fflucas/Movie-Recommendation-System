package com.recsys.controller;

import com.recsys.dao.BackgroundAcesso;
import com.recsys.model.DadosFilmes;
import com.recsys.model.DadosFilmesAvaliados;
import com.recsys.model.DadosNotas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BuscaSeriesController implements Initializable{
    
    ObservableList<DadosFilmes> list;
    ArrayList<DadosFilmes> listaDadosFilmes;
    ArrayList<DadosNotas> listaDadosNotas;
    ArrayList<DadosFilmesAvaliados> listaDadosFilmesAvaliados;

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
    public String toString() {
        return "BuscaSeriesController{" +
                "listaDadosFilmesAvaliados=" + listaDadosFilmesAvaliados +
                '}';
    }

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

            tabela.setItems(listaDados());

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
                        if (listaDadosFilmesAvaliados == null){
                            listaDadosFilmesAvaliados = new ArrayList<>();
                        }
                        listaDadosFilmesAvaliados.add(afc.avaliarFilme(filmeAvaliado));
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }
            });
            
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    /* Solicita os dados pelos arquivos locais.
     * Salva os dados de notas na variável local.
     * Retorna os dados de filmes como uma coleção para a interface
     */
    private ObservableList<DadosFilmes> listaDados(){
        BackgroundAcesso ba = new BackgroundAcesso();
        ba.executaManipulacao(); //manipula os csv convertendo para json
        constroiNotas(ba.carregaNotas()); //constroi os dados de notas
        constroiFilmes(ba.carregaFilmes());
        contTotalVotos();
        contNotaMedia();
        return list = FXCollections.observableArrayList(listaDadosFilmes); //constroi os dados de filmes
    }

    /* Recebe os dados de filmes como string, converte para JSON
     * para manipular as chaves, constroi um ArrayList e ao final
     * salva os dados de filmes na variável local e retorna o ArrayList
     */
    private void constroiFilmes(String output){
        ArrayList<DadosFilmes> dataset = new ArrayList<>();
        if(output == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText("Ocorreu um erro na busca dos dados de filmes.");
            alert.showAndWait();
        }else{
            try{
                JSONArray jsonArray = new JSONArray(output);
                int count = 0;
                JSONObject jo;
                DadosFilmes ds;
                while(count < jsonArray.length()){
                    jo = jsonArray.getJSONObject(count);
                    ds = new DadosFilmes(jo.getInt("movieId"),
                            jo.getString("title"),
                            jo.getString("genres"),
                            0,0f);
                    dataset.add(ds);
                    count++;
                }
            }catch(JSONException err){
                System.out.println("Erro de JSON Filmes na atribuição dos dados ao ArrayList: "+err.getMessage());
            }catch(Exception err){
                System.out.println("Erro na atribuição dos dados JSON Filmes ao ArrayList: " + err.getMessage());
            }
        }
        listaDadosFilmes = dataset;
    }

    /* Recebe os dados de notas como string, converte para JSON
     * para manipular as chaves, constroi um ArrayList e ao final
     * salva os dados de notas na variável local
     */
    private void constroiNotas(String output){
        ArrayList<DadosNotas> dataset = new ArrayList<>();
        if(output == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText("Ocorreu um erro na busca dos dados de notas.");
            alert.showAndWait();
        }else{
            try{
                JSONArray jsonArray = new JSONArray(output);
                int count = 0;
                JSONObject jo;
                DadosNotas ds;
                while(count < jsonArray.length()){
                    jo = jsonArray.getJSONObject(count);
                    ds = new DadosNotas(jo.getInt("userId"),
                            jo.getInt("movieId"),
                            jo.getFloat("rating"));
                    dataset.add(ds);
                    count++;
                }
            }catch(JSONException err){
                System.out.println("Erro de JSON Notas na atribuição dos dados ao ArrayList: "+err.getMessage());
            }catch(Exception err){
                System.out.println("Erro na atribuição dos dados JSON Notas ao ArrayList: " + err.getMessage());
            }
        }
        listaDadosNotas = dataset;
    }

    private void contTotalVotos(){
        //monta uma lista somente com os filmeId da listaDadosNotas
        List<Integer> listaFilmesId = listaDadosNotas.stream().map(DadosNotas::getFilmeId).collect(Collectors.toList());
        //para cada filme em listaDadosFilmes faz a contagem da frequencia que filmeId ocorre na lista montada acima
        for (DadosFilmes df: listaDadosFilmes){
            df.setTotalVotos(Collections.frequency(listaFilmesId, df.getFilmeId()));
        }
    }

    private void contNotaMedia(){
        for (DadosFilmes df: listaDadosFilmes){
            //retorna todas notas caso a condição de filmeId seja igual em listaDadosNotas e listaDadosFilmes
            float media = (float)listaDadosNotas.stream().mapToDouble(notas -> (notas.getFilmeId()==df.getFilmeId()) ? notas.getNota() : 0).sum();
            //faz a media das notas com a quantidade de votos
            df.setNotaMedia(media/df.getTotalVotos());
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
