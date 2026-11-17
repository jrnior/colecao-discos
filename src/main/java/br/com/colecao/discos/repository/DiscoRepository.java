package br.com.colecao.discos.repository;

import br.com.colecao.discos.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscoRepository extends JpaRepository<Disco, Long> {

    Page<Disco> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    Page<Disco> findByFormato(FormatoDisco formato, Pageable pageable);
}
