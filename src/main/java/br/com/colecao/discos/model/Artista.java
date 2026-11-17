package br.com.colecao.discos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Schema(description = "Artista ou banda responsável por um ou mais discos")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O nome do artista é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do artista ou banda", example = "Elis Regina")
    private String nome;

    @NotBlank(message = "O país de origem é obrigatório")
    @Size(max = 60, message = "O país deve ter no máximo 60 caracteres")
    @Schema(description = "País de origem", example = "Brasil")
    private String paisOrigem;

    // Um artista possui vários discos (One-to-Many)
    @JsonIgnore
    @OneToMany(mappedBy = "artista")
    private List<Disco> discos = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPaisOrigem() { return paisOrigem; }
    public void setPaisOrigem(String paisOrigem) { this.paisOrigem = paisOrigem; }
    public List<Disco> getDiscos() { return discos; }
    public void setDiscos(List<Disco> discos) { this.discos = discos; }
}
