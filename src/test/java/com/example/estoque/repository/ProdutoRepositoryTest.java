package com.example.estoque.repository;

import com.example.estoque.entity.ProdutoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository repository;

    @Test
    @DisplayName("Deve salvar e recuperar um produto pelo nome")
    void testFindByNome() {
        ProdutoEntity entity = new ProdutoEntity();
        entity.setNome("Teclado");
        entity.setDescricao("Teclado mecânico");
        entity.setPreco(250.0);
        entity.setQtd(15);

        repository.save(entity);

        ProdutoEntity resultado = repository.findByNome("Teclado");

        assertNotNull(resultado);
        assertEquals("Teclado", resultado.getNome());
        assertEquals(250.0, resultado.getPreco());
    }

    @Test
    @DisplayName("Deve retornar lista com todos os produtos salvos")
    void testFindAll() {
        ProdutoEntity p1 = new ProdutoEntity();
        p1.setNome("Produto A");
        p1.setDescricao("Descrição A");
        p1.setPreco(10.0);
        p1.setQtd(5);

        ProdutoEntity p2 = new ProdutoEntity();
        p2.setNome("Produto B");
        p2.setDescricao("Descrição B");
        p2.setPreco(20.0);
        p2.setQtd(10);

        repository.save(p1);
        repository.save(p2);

        List<ProdutoEntity> lista = repository.findAll();

        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("Deve retornar vazio se nome não existir")
    void testFindByNome_notFound() {
        ProdutoEntity resultado = repository.findByNome("Inexistente");
        assertNull(resultado);
    }
}
