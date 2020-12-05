package com.recsys.controller;

import com.recsys.model.DadosFilmes;
import com.recsys.model.DadosNotas;
import com.recsys.model.Recomendar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;
import java.net.URL;
import java.util.ResourceBundle;

public class AvaliaFilmesController implements Initializable {
    @FXML
    private Button avaliar;
    @FXML
    private Label notaEscrita;
    @FXML
    private Label titulo;
    @FXML
    private Label genero;
    @FXML
    private Rating rating;

    private int userId;
    private int filmeId;
    private Recomendar recsys;

    /* Adiciona um sentinela que aguarda a interação do usuário com as estrelas
    *  Ao mudar a estrela o label é alterado
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            rating.ratingProperty().addListener((ov, t, t1) -> notaEscrita.setText("Nota: " + t1.toString()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /* Pre construtor da classe que inicia os valores do label com o filme clicado na tabela
    * O construtor não pode ser usado devido a classe implementar o Initializable
    * O método é chamado após o FXML ser carregado
    */
    public void preConstrutor(DadosFilmes filmeAvaliado, int userId, Recomendar recsys) {
        titulo.setText(filmeAvaliado.getTitulo());
        genero.setText(filmeAvaliado.getGenero());
        this.userId = userId;
        this.filmeId = filmeAvaliado.getFilmeId();
        rating.setRating(1);
        rating.setPartialRating(false);
        notaEscrita.setText("Nota: 1");
        this.recsys = recsys;
    }

    /* Ação do botão Avaliar
    *  Recebe como associação de endereço o parâmetro recsys que contêm as informações de filmes avaliados
    *  Adiciona um novo dado ao recsys com a avaliação do usuário
    */
    @FXML
    public void avaliarFilme() {
        recsys.setListaDadosFilmesAvaliados(new DadosNotas(userId, filmeId, (float) rating.getRating()));
        Stage stage = (Stage) avaliar.getScene().getWindow();
        stage.close();
    }
}
