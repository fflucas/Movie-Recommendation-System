package com.recsys.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Recomendar {

    ObservableList<DadosFilmes> listGeral;
    ArrayList<DadosFilmes> listaDadosFilmes;
    ArrayList<DadosNotas> listaDadosNotas = new ArrayList<>();

    public ObservableList<DadosFilmes> getListGeral() {
        return listGeral;
    }

    //Adiciona o filme avaliado pelo usuario 0 (usuario local) a lista de notas
    public void setListaDadosFilmesAvaliados(DadosNotas DadosFilmeAvaliado) {
        this.listaDadosNotas.add(DadosFilmeAvaliado);
    }

    /*Metodo para calcular a distancia entre usuarios.
    * Calcula a distancia euclidiana pela formula da hipotenusa
    * Recebe como parametro as notas dos filmes em comum dos usuarios
     */
    public double distanciaEuclidiana(double[] notaUsuario, double[] notaUsuarioAvaliado){
        try{
            assert notaUsuario.length == notaUsuarioAvaliado.length;
            double distanciaQuadrada = 0.d;
            for(int i=0; i<notaUsuario.length; i++)
                distanciaQuadrada += Math.pow(notaUsuario[i]-notaUsuarioAvaliado[i], 2);
            return Math.sqrt(distanciaQuadrada);
        }catch (Exception e){
            System.out.println("Distância euclidiana entre vetores de tamanhos diferentes! Error:" + e);
        }
        return 0;
    }

    /*Retorna apenas os DadosFilmes em que o usuarioId avaliou*/
    public ArrayList<DadosNotas> notasDoUsuario(int usuarioId){
        ArrayList<DadosNotas> DadosNotasUsuario;
        //checa se o userId é o mesmo. Transforma em List e faz o cast para ArrayList ao final
        DadosNotasUsuario = (ArrayList<DadosNotas>) listaDadosNotas.stream().map(usuarios -> (usuarios.getUserId()==usuarioId) ? usuarios : null).collect(Collectors.toList());
        //dropa as ocorrencias falsas
        DadosNotasUsuario.removeIf(Objects::isNull);
        return DadosNotasUsuario;
    }

    /*Com os usuarios passados como parametro é buscado os filmes que cada usuario avaliou
    * Apenas os filmes avaliados pelos dois usuarios é mantido
    * O calculo da distancia é feito com base na diferença (ou não) das notas para o mesmo filme
    * Por fim é retornado um valor que indica:
    * * quanto maior o valor mais distante são os gostos entre os dois usuarios
    * * quanto menor o valor mais proximo são os gostos entre os dois usuarios
    */
    public double distanciaEntreUsuarios(int usuario1, int usuario2){
        ArrayList<DadosNotas> user1 = notasDoUsuario(usuario1);
        ArrayList<DadosNotas> user2 = notasDoUsuario(usuario2);
        List<Double> notasEmComumUser1 = new ArrayList<>();
        List<Double> notasEmComumUser2 = new ArrayList<>();
        //manter somente os filmes em comum (user1.filmeId==user2.filmeId)
        for (DadosNotas user1Avaliacao : user1){
            for(DadosNotas user2Avaliacao : user2){
                if (user1Avaliacao.getFilmeId()==user2Avaliacao.getFilmeId()){
                    notasEmComumUser1.add((double) user1Avaliacao.getNota());
                    notasEmComumUser2.add((double) user2Avaliacao.getNota());
                }
            }
        }
        //calcular a distancia se no minimo 5 filmes estão em comum
        if((long) notasEmComumUser1.size() >=5)
            return distanciaEuclidiana(notasEmComumUser1.stream().mapToDouble(x->x).toArray(), notasEmComumUser2.stream().mapToDouble(x->x).toArray());
        else
            return 9999.0;
    }

    /*Calcula a distancia do usuario para todos os demais
    * Organiza em uma tupla (userId, distancia) com os resultados
    * ordenados pela distancia em ordem crescente
    * Retorna somente os k mais proximos passado pelo usuario
    */
    public ArrayList<Pair<Integer, Double>> distanciaDeTodos(int usuario, int kMaisProximos){
        //pega todos os valores unicos de usersId
        Set<Integer> usuarios = listaDadosNotas.stream().map(DadosNotas::getUserId).collect(Collectors.toSet());
        //remove o usuario da lista
        usuarios.removeIf(userId->(userId==usuario));
        ArrayList<Pair<Integer, Double>> distancias = new ArrayList<>();
        Pair<Integer, Double> dist;
        //calcula a distancia para todos
        for (Integer user : usuarios){
            dist = new Pair<>(user, distanciaEntreUsuarios(usuario, user));
            distancias.add(dist);
        }
        distancias.sort(Comparator.comparing(Pair::getValue)); //ordena em ordem crescente
        return (ArrayList<Pair<Integer, Double>>) distancias.stream().limit(kMaisProximos).collect(Collectors.toList()); //retorna somente os k primeiros
    }

    /* Recebe o userId e um valor de k mais proximos para busca dos usuarios similares
    *  Retorna a lista de filmes recomendados com base na similaridade
    */
    public ArrayList<DadosFilmes> sugerePara(int usuario, int kMaisProximos){
        //pega as notas que o usuario deu para os filmes para coletar os filmes assitidos
        ArrayList<DadosNotas> notasUsuario = notasDoUsuario(usuario);
        ArrayList<Integer> filmesVistosPeloUsuario = (ArrayList<Integer>) notasUsuario.stream().map(DadosNotas::getFilmeId).collect(Collectors.toList());

        //dentre todos pega somente os kMaisProximos de voce
        ArrayList<Pair<Integer, Double>> kSimilares = distanciaDeTodos(usuario, kMaisProximos);

        //pega o userId dos kMaisProximos a voce
        ArrayList<Integer> usuariosSimilares = (ArrayList<Integer>) kSimilares.stream().map(Pair::getKey).collect(Collectors.toList());

        //dentre todas as notas pega somente as notas dos usuariosSimilares
        ArrayList<DadosNotas> notasUsuariosSimilares = new ArrayList<>();
        for (int i : usuariosSimilares){
            notasUsuariosSimilares.addAll(notasDoUsuario(i));
        }
        //para cada filme assistido pelos usuarios similares eu faço a media das notas
        Set<Integer> idFilmesUsuariosSimilares = notasUsuariosSimilares.stream().map(DadosNotas::getFilmeId).collect(Collectors.toSet());
        ArrayList<DadosFilmes> filmesRecomendados = new ArrayList<>();
        DadosFilmes tempDadosFilmes;
        float somaNotasFilme;
        int qtdVezesQueOFilmeFoiAvaliado;
        for (int filmeId : idFilmesUsuariosSimilares) { //enquanto houver ocorrencias
            somaNotasFilme = (float) notasUsuariosSimilares.stream().mapToDouble(notas -> (notas.getFilmeId() == filmeId) ? notas.getNota() : 0).sum();
            qtdVezesQueOFilmeFoiAvaliado = notasUsuariosSimilares.stream().mapToInt(notas -> (notas.getFilmeId() == filmeId) ? 1 : 0).sum();
            // Buscando o elemento DadosFilmes com o filmeId. Mudando a nota media para a calculada.
            // Mudando o total votos para a calculada. Adicionando ao vetor de filmes recomendados
            for (DadosFilmes filme : listaDadosFilmes){
                if (filme.getFilmeId()==filmeId){
                    tempDadosFilmes = new DadosFilmes(filme.getFilmeId(), filme.getTitulo(), filme.getGenero(),
                            qtdVezesQueOFilmeFoiAvaliado, (somaNotasFilme / qtdVezesQueOFilmeFoiAvaliado));
                    filmesRecomendados.add(tempDadosFilmes);
                }
            }
        }

        //ordena em ordem decrescente as notas e remove os filmes ja vistos pelo usuario
        filmesRecomendados.sort(Comparator.comparing(DadosFilmes::getNotaMedia));
        Collections.reverse(filmesRecomendados);
        for (Integer filmeJaVisto : filmesVistosPeloUsuario){
            filmesRecomendados.removeIf(filmes -> (filmes.getFilmeId()==filmeJaVisto));
        }
        return filmesRecomendados;
    }

    /* Solicita os dados pelos arquivos locais.
     * Salva os dados de notas na variável local.
     * Retorna os dados de filmes como uma coleção para a interface
     */
    public ObservableList<DadosFilmes> listaDados(){
        RecomendarDAO ba = new RecomendarDAO();
        ba.executaManipulacao(); //manipula os csv convertendo para json
        constroiNotas(ba.carregaNotas()); //constroi os dados de notas
        constroiFilmes(ba.carregaFilmes());
        contTotalVotos();
        contNotaMedia();
        return listGeral = FXCollections.observableArrayList(listaDadosFilmes); //constroi os dados de filmes
    }

    //retorna a lista de filmes avaliados do usuario local (userId=0) como ObservableList para adicionar ao tableView
    public ObservableList<DadosFilmes> listaDadosUsuario(int userId){
        //busca as avaliações do usuario userId
        ArrayList<DadosNotas> notasUsuario = notasDoUsuario(userId);
        DadosFilmes tempDadosFilmes;
        ArrayList<DadosFilmes> filmesDoUsuario = new ArrayList<>();
        //percorro cada nota procurando o filme avaliado,
        // criando um novo objeto com a nota dada pelo usuario e adicionando a um novo ArrayList
        for (DadosNotas notaUsuario : notasUsuario){
            for (DadosFilmes filme : listaDadosFilmes){
                if (filme.getFilmeId()==notaUsuario.getFilmeId()){
                    tempDadosFilmes = new DadosFilmes(filme.getFilmeId(), filme.getTitulo(), filme.getGenero(),
                            1, notaUsuario.getNota());
                    filmesDoUsuario.add(tempDadosFilmes);
                }
            }
        }
        return FXCollections.observableList(filmesDoUsuario);
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
}
