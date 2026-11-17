package br.com.colecao.discos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Capa de um disco")
public class Capa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "A URL da imagem é obrigatória")
    @Size(max = 255, message = "A URL deve ter no máximo 255 caracteres")
    @Schema(description = "Endereço da imagem da capa", example = "https://exemplo.com/capas/elis-e-tom.jpg")
    private String urlImagem;

    @Size(max = 100, message = "O nome do designer deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do designer da capa", example = "Estúdio Exemplo")
    private String designer;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    @Schema(description = "Descrição da arte da capa", example = "Foto em preto e branco dos artistas")
    private String descricao;

    // Uma capa pertence a um único disco (One-to-One) - lado inverso
    @JsonIgnore
    @OneToOne(mappedBy = "capa")
    private Disco disco;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUrlImagem() { return urlImagem; }
    public void setUrlImagem(String urlImagem) { this.urlImagem = urlImagem; }
    public String getDesigner() { return designer; }
    public void setDesigner(String designer) { this.designer = designer; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Disco getDisco() { return disco; }
    public void setDisco(Disco disco) { this.disco = disco; }
}
