package br.com.colecao.discos;

import br.com.colecao.discos.model.*;
import br.com.colecao.discos.repository.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DiscosApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscosApplication.class, args);
    }

    // Cabeçalho do Swagger: nome, versão, descrição e contato
    // (as seções detalhadas ficam em static/docs.html)
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("API de Gerenciamento de Coleção de Discos")
                .version("1.0.0")
                .description("API para gerenciar uma coleção de discos, organizando artistas, discos, "
                        + "gêneros, faixas e capas.")
                .contact(new Contact().name("Junior Souza").email("jr.gsouza@icloud.com")));
    }

    // A página padrão do Springdoc (/swagger-ui/index.html) é redirecionada para a nossa
    // (static/docs.html), que tem as seções, o rodapé e o botão de modo escuro
    @Bean
    public WebMvcConfigurer redirecionarSwagger() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addRedirectViewController("/swagger-ui/index.html", "/docs.html");
            }
        };
    }

    // Dados de exemplo (fictícios) carregados ao iniciar
    @Bean
    public CommandLineRunner carga(ArtistaRepository artistas, GeneroRepository generos,
                                   CapaRepository capas, DiscoRepository discos, FaixaRepository faixas) {
        return args -> {
            Artista elis = new Artista();
            elis.setNome("Elis Regina");
            elis.setPaisOrigem("Brasil");
            artistas.save(elis);

            Genero mpb = new Genero();
            mpb.setNome("MPB");
            mpb.setDescricao("Música Popular Brasileira");
            generos.save(mpb);

            Capa capa = new Capa();
            capa.setUrlImagem("https://exemplo.com/capas/elis-e-tom.jpg");
            capa.setDesigner("Estúdio Exemplo");
            capas.save(capa);

            Disco disco = new Disco();
            disco.setTitulo("Elis & Tom");
            disco.setAnoLancamento(1974);
            disco.setFormato(FormatoDisco.VINIL);
            disco.setArtista(elis);
            disco.getGeneros().add(mpb);
            disco.setCapa(capa);
            discos.save(disco);

            Faixa faixa = new Faixa();
            faixa.setTitulo("Águas de Março");
            faixa.setNumero(1);
            faixa.setDuracaoSegundos(215);
            faixa.setDisco(disco);
            faixas.save(faixa);
        };
    }
}
