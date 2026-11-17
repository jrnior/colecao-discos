package br.com.colecao.discos.controller;

import br.com.colecao.discos.model.*;
import br.com.colecao.discos.repository.ArtistaRepository;
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
@RequestMapping("/artistas")
@Tag(name = "Artistas", description = "Cadastro de artistas e bandas")
public class ArtistaController {

    private final ArtistaRepository repositorio;

    public ArtistaController(ArtistaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    @Operation(summary = "Listar artistas", description = "Lista paginada. Use ?page=0&size=10&sort=id,asc")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    public PagedModel<EntityModel<Artista>> listar(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return HateoasUtil.pagina(repositorio.findAll(pageable), this::modelo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    public EntityModel<Artista> buscarPorId(@PathVariable Long id) {
        return modelo(obter(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar", description = "Cria um artista (nome e país de origem).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Id informado não existe ou valor duplicado")
    })
    public ResponseEntity<EntityModel<Artista>> criar(@Valid @RequestBody Artista novo) {
        novo.setId(null);
        Artista salvo = repositorio.save(novo);
        EntityModel<Artista> modelo = modelo(obter(salvo.getId()));
        return ResponseEntity.created(modelo.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(modelo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "409", description = "Id informado não existe ou valor duplicado")
    })
    public EntityModel<Artista> atualizar(@PathVariable Long id, @Valid @RequestBody Artista dados) {
        Artista existente = obter(id);
        existente.setNome(dados.getNome());
        existente.setPaisOrigem(dados.getPaisOrigem());
        repositorio.save(existente);
        return modelo(obter(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "409", description = "Registro está em uso")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        repositorio.delete(obter(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar por nome", description = "Consulta personalizada (contém o texto, sem diferenciar maiúsculas)")
    @ApiResponse(responseCode = "200", description = "Página com os resultados")
    public PagedModel<EntityModel<Artista>> buscar(@RequestParam String nome,
                                              @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return HateoasUtil.pagina(repositorio.findByNomeContainingIgnoreCase(nome, pageable), this::modelo);
    }

    private Artista obter(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artista com id " + id + " não encontrado(a)"));
    }

    private EntityModel<Artista> modelo(Artista objeto) {
        return HateoasUtil.modelo(objeto, ArtistaController.class, objeto.getId());
    }
}
