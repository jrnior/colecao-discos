package br.com.colecao.discos.controller;

import br.com.colecao.discos.model.*;
import br.com.colecao.discos.repository.CapaRepository;
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
@RequestMapping("/capas")
@Tag(name = "Capas", description = "Cadastro de capas de discos")
public class CapaController {

    private final CapaRepository repositorio;

    public CapaController(CapaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    @Operation(summary = "Listar capas", description = "Lista paginada. Use ?page=0&size=10&sort=id,asc")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    public PagedModel<EntityModel<Capa>> listar(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return HateoasUtil.pagina(repositorio.findAll(pageable), this::modelo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    public EntityModel<Capa> buscarPorId(@PathVariable Long id) {
        return modelo(obter(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar", description = "Cria uma capa. Depois ela pode ser ligada a um disco.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Id informado não existe ou valor duplicado")
    })
    public ResponseEntity<EntityModel<Capa>> criar(@Valid @RequestBody Capa novo) {
        novo.setId(null);
        Capa salvo = repositorio.save(novo);
        EntityModel<Capa> modelo = modelo(obter(salvo.getId()));
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
    public EntityModel<Capa> atualizar(@PathVariable Long id, @Valid @RequestBody Capa dados) {
        Capa existente = obter(id);
        existente.setUrlImagem(dados.getUrlImagem());
        existente.setDesigner(dados.getDesigner());
        existente.setDescricao(dados.getDescricao());
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
    @Operation(summary = "Buscar por designer", description = "Consulta personalizada (contém o texto, sem diferenciar maiúsculas)")
    @ApiResponse(responseCode = "200", description = "Página com os resultados")
    public PagedModel<EntityModel<Capa>> buscar(@RequestParam String designer,
                                              @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return HateoasUtil.pagina(repositorio.findByDesignerContainingIgnoreCase(designer, pageable), this::modelo);
    }

    private Capa obter(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Capa com id " + id + " não encontrado(a)"));
    }

    private EntityModel<Capa> modelo(Capa objeto) {
        return HateoasUtil.modelo(objeto, CapaController.class, objeto.getId());
    }
}
