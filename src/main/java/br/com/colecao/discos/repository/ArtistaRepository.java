package br.com.colecao.discos.repository;

import br.com.colecao.discos.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    Page<Artista> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
