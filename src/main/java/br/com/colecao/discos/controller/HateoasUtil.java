package br.com.colecao.discos.controller;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.util.List;
import java.util.function.Function;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class HateoasUtil {

    // Coloca os links self, atualizar, excluir e listar em volta do objeto
    public static <T> EntityModel<T> modelo(T objeto, Class<?> controller, Long id) {
        Link self = linkTo(controller).slash(id).withSelfRel();
        return EntityModel.of(objeto,
                self,
                self.withRel("atualizar"),
                self.withRel("excluir"),
                linkTo(controller).withRel("listar"));
    }

    // Transforma uma Page em PagedModel (lista + informações de paginação)
    public static <T> PagedModel<EntityModel<T>> pagina(Page<T> pagina, Function<T, EntityModel<T>> conversor) {
        List<EntityModel<T>> itens = pagina.getContent().stream().map(conversor).toList();
        PagedModel.PageMetadata info = new PagedModel.PageMetadata(
                pagina.getSize(), pagina.getNumber(), pagina.getTotalElements());
        return PagedModel.of(itens, info);
    }
}
