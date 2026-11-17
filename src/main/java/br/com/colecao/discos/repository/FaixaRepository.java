package br.com.colecao.discos.repository;

import br.com.colecao.discos.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaixaRepository extends JpaRepository<Faixa, Long> {

    Page<Faixa> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
}
