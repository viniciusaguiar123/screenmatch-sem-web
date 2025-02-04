package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=7e8944d6";
    private ConsumoApi consumo = new ConsumoApi();
    ConverteDados conversor = new ConverteDados();


    public void exibeMenu(){
        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = leitura.nextLine();
        String json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i < dados.totalTemporada(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

        temporadas.forEach(temporada -> temporada.episodios()
                .forEach(episodio -> System.out.println(episodio.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(temporada -> temporada.episodios().stream())
                .collect(Collectors.toList());

        dadosEpisodios.add(new DadosEpisodio("teste", 3, "10", "2020-01-01"));

        System.out.println("\nTop 10 episódios");
        dadosEpisodios.stream()
                .filter(episodio -> !episodio.avaliacao().equalsIgnoreCase("N/A"))
                .peek(episodio -> System.out.println("Primeiro filtro(N/A) " + episodio))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .peek(episodio -> System.out.println("Ordenação " + episodio))
                .limit(10)
                .peek(episodio -> System.out.println("Limite " + episodio))
                .map(episodio -> episodio.titulo().toUpperCase())
                .peek(episodio -> System.out.println("Mapeamento " + episodio))
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(temporada -> temporada.episodios().stream()
                        .map(dadosEpisodio -> new Episodio(temporada.numero(), dadosEpisodio))).collect(Collectors.toList());
        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do titulo do episodio: ");
        var trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodioFiltrado = episodios.stream()
                .filter(episodio -> episodio.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();
        if(episodioFiltrado.isPresent()){
            System.out.println("Episodio encotrado!");
            System.out.println("Temporada: " + episodioFiltrado.get().getTemporada());
        }else {
            System.out.println("Episodio não encontrado!");
        }

//        System.out.println("A partir de que ano você deseja ver os episódios? ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(episodio -> episodio.getDataLancamento() != null && episodio.getDataLancamento().isAfter(dataBusca))
//                .forEach(episodio -> System.out.println(
//                        "Temporada: " + episodio.getTemporada() +
//                                ", Episódio: " + episodio.getTitulo() +
//                                ", Data lançamento: " + episodio.getDataLancamento().format(formatador)
//                ));
    }
}
