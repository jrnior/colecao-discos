package br.com.colecao.discos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Schema(description = "Disco da coleção")
public class Disco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    @Schema(description = "Título do disco", example = "Elis & Tom")
    private String titulo;

    @NotNull(message = "O ano de lançamento é obrigatório")
    @Min(value = 1900, message = "O ano deve ser maior ou igual a 1900")
    @Max(value = 2100, message = "O ano deve ser menor ou igual a 2100")
    @Schema(description = "Ano de lançamento", example = "1974")
    private Integer anoLancamento;

    @NotNull(message = "O formato é obrigatório")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Formato do disco", example = "VINIL")
    private FormatoDisco formato;

    // Muitos discos para um artista (Many-to-One)
    @NotNull(message = "O artista é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "artista_id")
    private Artista artista;

    // Muitos discos para muitos gêneros (Many-to-Many) - lado dono
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "disco_genero",
            joinColumns = @JoinColumn(name = "disco_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id"))
    private Set<Genero> generos = new HashSet<>();

    // Um disco possui uma capa (One-to-One) - lado dono
    @OneToOne
    @JoinColumn(name = "capa_id", unique = true)
    private Capa capa;

    // Um disco possui várias faixas (One-to-Many)
    @JsonIgnore
    @OneToMany(mappedBy = "disco", cascade = CascadeType.REMOVE)
    private List<Faixa> faixas = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Integer getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(Integer anoLancamento) { this.anoLancamento = anoLancamento; }
    public FormatoDisco getFormato() { return formato; }
    public void setFormato(FormatoDisco formato) { this.formato = formato; }
    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }
    public Set<Genero> getGeneros() { return generos; }
    public void setGeneros(Set<Genero> generos) { this.generos = generos; }
    public Capa getCapa() { return capa; }
    public void setCapa(Capa capa) { this.capa = capa; }
    public List<Faixa> getFaixas() { return faixas; }
    public void setFaixas(List<Faixa> faixas) { this.faixas = faixas; }
}
