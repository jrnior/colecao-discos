package br.com.colecao.discos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Schema(description = "Gênero musical")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O nome do gênero é obrigatório")
    @Size(max = 60, message = "O nome deve ter no máximo 60 caracteres")
    @Column(unique = true)
    @Schema(description = "Nome do gênero (único)", example = "MPB")
    private String nome;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    @Schema(description = "Descrição do gênero", example = "Música Popular Brasileira")
    private String descricao;

    // Muitos gêneros para muitos discos (Many-to-Many) - lado inverso
    @JsonIgnore
    @ManyToMany(mappedBy = "generos")
    private Set<Disco> discos = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Set<Disco> getDiscos() { return discos; }
    public void setDiscos(Set<Disco> discos) { this.discos = discos; }
}
