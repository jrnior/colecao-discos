package br.com.colecao.discos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Faixa (música) de um disco")
public class Faixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O título da faixa é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    @Schema(description = "Título da faixa", example = "Águas de Março")
    private String titulo;

    @NotNull(message = "O número da faixa é obrigatório")
    @Min(value = 1, message = "O número da faixa deve ser maior que zero")
    @Schema(description = "Posição da faixa no disco", example = "1")
    private Integer numero;

    @NotNull(message = "A duração é obrigatória")
    @Min(value = 1, message = "A duração deve ser maior que zero")
    @Schema(description = "Duração em segundos", example = "215")
    private Integer duracaoSegundos;

    // Muitas faixas para um disco (Many-to-One)
    @NotNull(message = "O disco é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "disco_id")
    private Disco disco;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getDuracaoSegundos() { return duracaoSegundos; }
    public void setDuracaoSegundos(Integer duracaoSegundos) { this.duracaoSegundos = duracaoSegundos; }
    public Disco getDisco() { return disco; }
    public void setDisco(Disco disco) { this.disco = disco; }
}
