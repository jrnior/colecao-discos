# API Coleção de Discos

API REST em Spring Boot (projeto final). Java 17 · Spring Boot 3.5 · Spring Web · Spring Data JPA · Validation · HATEOAS · Springdoc (Swagger) · H2.

## Como rodar

```bash
mvn spring-boot:run
```

- Página inicial: http://localhost:8080/
- Swagger (com botão de modo escuro): http://localhost:8080/docs.html  (`/swagger-ui.html` e `/swagger-ui/index.html` redirecionam para ela)
- Console H2: http://localhost:8080/h2-console (URL `jdbc:h2:mem:discosdb`, usuário `sa`, senha vazia)

## Estrutura (3 pastas)

| Pasta | O que tem |
|---|---|
| `model` | As 5 entidades (`Artista`, `Disco`, `Genero`, `Capa`, `Faixa`) e o enum `FormatoDisco` |
| `repository` | Interfaces do Spring Data JPA (CRUD pronto + consultas personalizadas) |
| `controller` | Os endpoints REST, com Swagger e HATEOAS (`HateoasUtil` monta os links) |

Em `resources/static`: `index.html` (página inicial), `docs.html` (Swagger com botão de modo escuro) e `css/` (estilos).

Na pasta principal: `DiscosApplication` (inicia o app, define título/descrição do Swagger e carrega dados de exemplo) e `TratadorDeErros` (devolve 409 quando há conflito no banco).

## Relacionamentos

- One-to-One: `Disco` ↔ `Capa`
- One-to-Many: `Artista` → `Disco`, `Disco` → `Faixa`
- Many-to-Many: `Disco` ↔ `Genero`

## Endpoints

Cada recurso (`/artistas`, `/discos`, `/generos`, `/faixas`, `/capas`) tem:
`GET` (lista paginada) · `GET /{id}` · `POST` · `PUT /{id}` · `DELETE /{id}` · `GET /buscar?...` (consulta personalizada).
Em `/discos` também: `GET /formato/{formato}`.

## Exemplo: criar um disco

```json
POST /discos
{
  "titulo": "Elis & Tom",
  "anoLancamento": 1974,
  "formato": "VINIL",
  "artista": { "id": 1 },
  "generos": [ { "id": 1 } ],
  "capa": { "id": 1 }
}
```

Para criar uma faixa: `{ "titulo": "...", "numero": 1, "duracaoSegundos": 200, "disco": { "id": 1 } }`.

Códigos: `200`/`201`/`204` sucesso · `400` dados inválidos · `404` não encontrado · `409` conflito (id inexistente, registro em uso, valor duplicado).
