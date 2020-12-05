package com.recsys.controller;

import com.recsys.model.DadosFilmes;
import com.recsys.model.Recomendar;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BuscaFilmesController implements Initializable{

    //tabela geral
    @FXML
    private TableView<DadosFilmes> tabelaFilmesGeral;
    @FXML
    private TableColumn<DadosFilmes,String> tcGeralTitulo;
    @FXML
    private TableColumn<DadosFilmes,String> tcGeralGenero;
    @FXML
    private TableColumn<DadosFilmes,String> tcGeralTotalVotos;
    @FXML
    private TableColumn<DadosFilmes,Float> tcGeralNotaMedia;
    //tabela usuario
    @FXML
    private TableView<DadosFilmes> tabelaFilmesUsuario;
    @FXML
    private TableColumn<DadosFilmes,String> tcUsuarioTitulo;
    @FXML
    private TableColumn<DadosFilmes,String> tcUsuarioGenero;
    @FXML
    private TableColumn<DadosFilmes,String> tcUsuarioTotalVotos;
    @FXML
    private TableColumn<DadosFilmes,Float> tcUsuarioNotaMedia;
    //tabela recomendados
    @FXML
    private TableView<DadosFilmes> tabelaFilmesRecomendados;
    @FXML
    private TableColumn<DadosFilmes,String> tcUsuarioRecTitulo;
    @FXML
    private TableColumn<DadosFilmes,String> tcUsuarioRecGenero;
    @FXML
    private TableColumn<DadosFilmes,String> tcUsuarioRecTotalVotos;
    @FXML
    private TableColumn<DadosFilmes,Float> tcUsuarioRecNotaMedia;
    @FXML
    private TextField inputK;
    @FXML
    private TextField filtroTitulo;

    //userId local
    private final int userId = 0;
    //Instancia da classe modelo Recomendar
    Recomendar recsys = new Recomendar();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            //Tabela Filmes Geral
            iniciaColunasDaTabela(tcGeralTitulo, tcGeralGenero, tcGeralTotalVotos, tcGeralNotaMedia);
            //Carrega com a lista de filmes
            tabelaFilmesGeral.setItems(this.recsys.listaDados());

            //Tabela Filmes Usuario
            iniciaColunasDaTabela(tcUsuarioTitulo, tcUsuarioGenero, tcUsuarioTotalVotos, tcUsuarioNotaMedia);

            //Tabela Filmes recomendados
            iniciaColunasDaTabela(tcUsuarioRecTitulo, tcUsuarioRecGenero, tcUsuarioRecTotalVotos, tcUsuarioRecNotaMedia);

            /* Implementação do filtro de busca
             * Inicia um objeto FilteredList com os DadosFilmes da tabela de filmes geral
             * adiciona um sentinela no TextField do campo de busca que caso seja alterado inicia
             * checando se o valor é vazio, caso for retorna toda a list. Caso não seja
             * transforma o campo para minusculo e busca o texto na lista de filmes.
             * Faz um bind com uma SortedList para exibir apenas os resultados filtrados.
             */
            FilteredList<DadosFilmes> filmesFiltrados = new FilteredList<>(recsys.getListGeral(), b->true);
            filtroTitulo.textProperty().addListener((observable, oldValue, newValue) -> filmesFiltrados.setPredicate(dadosfilme -> {
                if (newValue == null || newValue.isEmpty())
                    return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return dadosfilme.getTitulo().toLowerCase().contains(lowerCaseFilter);
            }));
            SortedList<DadosFilmes> filmesOrdenados = new SortedList<>(filmesFiltrados);
            filmesOrdenados.comparatorProperty().bind(tabelaFilmesGeral.comparatorProperty());
            tabelaFilmesGeral.setItems(filmesOrdenados);

            // Ao selecionar uma linha na tabela o usuario indica o filme que quer avaliar
            tabelaFilmesGeral.setOnMouseClicked((MouseEvent event) -> {
                if(event.getClickCount() == 2){
                    try{
                        //pega os dados do linha selecionada
                        DadosFilmes filmeAvaliado = new DadosFilmes(
                                tabelaFilmesGeral.getSelectionModel().getSelectedItem().getFilmeId(),
                                tabelaFilmesGeral.getSelectionModel().getSelectedItem().getTitulo(),
                                tabelaFilmesGeral.getSelectionModel().getSelectedItem().getGenero(),
                                tabelaFilmesGeral.getSelectionModel().getSelectedItem().getTotalVotos(),
                                tabelaFilmesGeral.getSelectionModel().getSelectedItem().getNotaMedia());
                        /* Faz o load da tela e carrega o controlador.
                        * Acessa o método preConstrutor do controlador para iniciar as legendas (labels)
                        * Inicia a tela e aguarda o seu término (clique no botão Avaliar)
                        * Como o recsys foi passado como associação de endereço a ação de atualizar a tabela de filmes assistidos
                        * retorna o filme avaliado pelo usuário.
                        */
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AvaliaFilmes.fxml"));
                            Parent root1 = fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.setTitle("Avalie o filme");
                            Image appIcon = new Image( "/claquete.png");
                            stage.getIcons().add(appIcon);
                            stage.setScene(new Scene(root1));
                            AvaliaFilmesController avaliaController = fxmlLoader.getController();
                            avaliaController.preConstrutor(filmeAvaliado, this.userId, this.recsys);
                            stage.showAndWait();
                            tabelaFilmesUsuario.setItems(this.recsys.listaDadosUsuario(this.userId));
                        } catch (Exception e) {
                            System.out.println("Erro ao abrir a janela de avaliar! ERROR: " + e.getMessage());
                            e.getStackTrace();
                        }
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }
            });
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private void iniciaColunasDaTabela(TableColumn<DadosFilmes,String> tcGeralTitulo, TableColumn<DadosFilmes,String> tcGeralGenero,
                                       TableColumn<DadosFilmes,String> tcGeralTotalVotos, TableColumn<DadosFilmes,Float> tcGeralNotaMedia){
        tcGeralTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tcGeralGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        tcGeralTotalVotos.setCellValueFactory(new PropertyValueFactory<>("totalVotos"));
        tcGeralNotaMedia.setCellValueFactory(new PropertyValueFactory<>("notaMedia"));
        //formatar a exibição para 2 casas decimais
        tcGeralNotaMedia.setCellFactory(tc -> new TableCell<>(){
            @Override
            protected void updateItem(Float s, boolean b) {
                super.updateItem(s, b);
                if (b)
                    setText(null);
                else
                    setText(String.format("%.2f", s.doubleValue()));
            }
        });
        tcGeralTitulo.setSortable(false);
        tcGeralGenero.setSortable(false);
        tcGeralTotalVotos.setSortable(false);
        tcGeralNotaMedia.setSortable(false);
    }

    @FXML
    private void abreSobre() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/claquete.png"));
        alert.setTitle("Informações");
        alert.setHeaderText("Sistema Desenvolvido por Fábio Almeida, Gabriel Arruda e Vitor Walter como projeto final da disciplina Programação Orientada a Objetos na UFCAT - 2020");
        alert.setContentText("Sistema de recomendação de filmes com uma abordagem Collaborative filtering que indica novos filmes baseado nos k usuários mais próximos de você.");
        alert.showAndWait();
    }

    @FXML
    private void abreAjuda(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/claquete.png"));
        alert.setTitle("Como funciona");
        alert.setHeaderText("Entenda como usar o programa");
        alert.setContentText("Na tabela da aba Lista de filmes escolha os filmes que você já viu e gostaria de avaliar. " +
                "Para escolhar basta clicar duas vezes na linha do filme e uma nova janela se abrirá. " +
                "\nDê uma nota entre 0 (odiei) a 5 (gostei muito) para filme avaliado.Perceba que o filme avaliado é listado na tabela da aba Filmes assistidos com seu voto e sua nota. " +
                "\nDepois de preencher com todos os filmes que você já assistiu insira um valor no campo abaixo da tabela para indicar a quantidade de usuários parecidos com você que o algoritmo irá buscar na base de dados. " +
                "\nClique no botão Recomendar e a tabela da aba Filmes recomendados irá se completar. A tabela de filmes recomendados é organizada com os filmes que você não viu mas que outros usuários com gostos parecidos do seu assistiu e avaliou. " +
                "\nOs primeiros listados são os que possuem a maior nota média dentre os k usuários indicados.");
        alert.showAndWait();
    }

    @FXML
    public void executarKNN(){
        ArrayList<DadosFilmes> recomendados;
        if (inputK.getText().equals("")) {
            recomendados = this.recsys.sugerePara(this.userId, 10);
        }else{
            recomendados = this.recsys.sugerePara(this.userId, Integer.parseInt(inputK.getText()));
            //JOptionPane.showMessageDialog(null, "Certifique-se de preencher um valor ao lado", "Atenção!", JOptionPane.WARNING_MESSAGE);
        }
        tabelaFilmesRecomendados.setItems(FXCollections.observableList(recomendados));
    }
}
