package br.com.colecao.discos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/discos-versionados")
public class DiscoVersionadoController {

    @GetMapping(headers = "X-API-Version=1")
    public ResponseEntity<String> listarDiscosV1() {
        return ResponseEntity.ok("Listagem de Discos - Versão 1");
    }

    @GetMapping(headers = "X-API-Version=2")
    public ResponseEntity<List<String>> listarDiscosV2() {
        return ResponseEntity.ok(List.of("Disco 1 - V2", "Disco 2 - V2"));
    }
}