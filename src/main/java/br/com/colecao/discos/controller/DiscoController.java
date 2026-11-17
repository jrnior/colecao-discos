package br.com.colecao.discos.controller;

import br.com.colecao.discos.model.*;
import br.com.colecao.discos.repository.DiscoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/discos")
@Tag(name = "Discos", description = "Cadastro de discos da coleção")
public class DiscoController {

    private final DiscoRepository repositorio;

    public DiscoController(DiscoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    @Operation(summary = "Listar discos", description = "Lista paginada. Use ?page=0&size=10&sort=anoLancamento,desc")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    public PagedModel<EntityModel<Disco>> listar(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return HateoasUtil.pagina(repositorio.findAll(pageable), this::modelo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por id", description = "Retorna o disco com artista, gêneros e capa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    public EntityModel<Disco> buscarPorId(@PathVariable Long id) {
        return modelo(obter(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar disco",
            description = "O artista é obrigatório e deve existir. Gêneros e capa são opcionais. "
                    + "Exemplo: titulo, anoLancamento, formato (VINIL, CD, FITA_K7, DIGITAL), "
                    + "artista: { id: 1 }, generos: [ { id: 1 } ], capa: { id: 1 }")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (campo vazio ou formato inexistente)"),
            @ApiResponse(responseCode = "409", description = "Artista, gênero ou capa inexistente (ou capa já usada)")
    })
    public ResponseEntity<EntityModel<Disco>> criar(@Valid @RequestBody Disco novo) {
        novo.setId(null);
        Disco salvo = repositorio.save(novo);
        EntityModel<Disco> modelo = modelo(obter(salvo.getId()));
        return ResponseEntity.created(modelo.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(modelo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar disco")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "409", description = "Artista, gênero ou capa inexistente (ou capa já usada)")
    })
    public EntityModel<Disco> atualizar(@PathVariable Long id, @Valid @RequestBody Disco dados) {
        Disco existente = obter(id);
        existente.setTitulo(dados.getTitulo());
        existente.setAnoLancamento(dados.getAnoLancamento());
        existente.setFormato(dados.getFormato());
        existente.setArtista(dados.getArtista());
        existente.setGeneros(dados.getGeneros());
        existente.setCapa(dados.getCapa());
        repositorio.save(existente);
        return modelo(obter(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir disco", description = "Exclui o disco e também as suas faixas")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        repositorio.delete(obter(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar por título", description = "Consulta personalizada (contém o texto, sem diferenciar maiúsculas)")
    @ApiResponse(responseCode = "200", description = "Página com os resultados")
    public PagedModel<EntityModel<Disco>> buscarPorTitulo(@RequestParam String titulo,
                                                          @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return HateoasUtil.pagina(repositorio.findByTituloContainingIgnoreCase(titulo, pageable), this::modelo);
    }

    @GetMapping("/formato/{formato}")
    @Operation(summary = "Listar por formato", description = "Consulta personalizada: VINIL, CD, FITA_K7 ou DIGITAL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página com os resultados"),
            @ApiResponse(responseCode = "400", description = "Formato inexistente")
    })
    public PagedModel<EntityModel<Disco>> listarPorFormato(@PathVariable FormatoDisco formato,
                                                           @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return HateoasUtil.pagina(repositorio.findByFormato(formato, pageable), this::modelo);
    }

    private Disco obter(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disco com id " + id + " não encontrado"));
    }

    private EntityModel<Disco> modelo(Disco disco) {
        EntityModel<Disco> modelo = HateoasUtil.modelo(disco, DiscoController.class, disco.getId());
        modelo.add(linkTo(ArtistaController.class).slash(disco.getArtista().getId()).withRel("artista"));
        if (disco.getCapa() != null) {
            modelo.add(linkTo(CapaController.class).slash(disco.getCapa().getId()).withRel("capa"));
        }
        return modelo;
    }
}
