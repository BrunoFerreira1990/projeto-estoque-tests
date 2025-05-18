package com.example.estoque.service;

import com.example.estoque.domain.ItemPedido;
import com.example.estoque.domain.Pedido;
import com.example.estoque.domain.Produto;
import com.example.estoque.entity.ProdutoEntity;
import com.example.estoque.exception.ForaDeEstoqueException;
import com.example.estoque.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cadastrarProduto_quandoProdutoNaoExiste_deveSalvarNovo() {
        Produto produto = new Produto("Cabo HDMI", "Cabo 2m", 25.0, 5);

        when(repository.findByNome("Cabo HDMI")).thenReturn(null);

        service.cadastrarProduto(produto);

        verify(repository).save(any(ProdutoEntity.class));
    }

    @Test
    void cadastrarProduto_quandoProdutoExiste_deveAtualizarEstoque() {
        Produto produto = new Produto("Mouse", "Mouse sem fio", 100.0, 10);
        ProdutoEntity existente = new ProdutoEntity(produto);
        existente.setId(1L);
        existente.setQtd(2);

        when(repository.findByNome("Mouse")).thenReturn(existente);

        service.cadastrarProduto(produto);

        assertEquals(10, existente.getQtd());
        verify(repository).save(existente);
    }


    @Test
    void encontrarTodos_deveRetornarListaDeProdutos() {
        ProdutoEntity p1 = new ProdutoEntity();
        p1.setId(1L);
        p1.setNome("Produto 1");
        ProdutoEntity p2 = new ProdutoEntity();
        p2.setId(2L);
        p2.setNome("Produto 2");

        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<Produto> produtos = service.encontrarTodos();

        assertEquals(2, produtos.size());
        assertEquals("Produto 1", produtos.get(0).getNome());
    }

    @Test
    void atualizarEstoque_quandoQtdInsuficiente_deveLancarExcecao() {
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);
        produtoEntity.setNome("Notebook");
        produtoEntity.setQtd(3);

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setQtd(5);

        Pedido pedido = new Pedido();
        pedido.setItens(List.of(item));

        when(repository.findById(1L)).thenReturn(Optional.of(produtoEntity));

        assertThrows(ForaDeEstoqueException.class, () -> service.atualizarEstoque(pedido));
    }

    @Test
    void atualizarEstoque_quandoQtdSuficiente_deveAtualizar() {
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);
        produtoEntity.setNome("Notebook");
        produtoEntity.setQtd(10);

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setQtd(3);

        Pedido pedido = new Pedido();
        pedido.setItens(List.of(item));

        when(repository.findById(1L)).thenReturn(Optional.of(produtoEntity));

        service.atualizarEstoque(pedido);

        assertEquals(7, produtoEntity.getQtd());
        verify(repository).save(produtoEntity);
    }

    @Test
    void encontrarPorNome_deveRetornarProduto() {
        ProdutoEntity entity = new ProdutoEntity();
        entity.setId(1L);
        entity.setNome("Monitor");

        when(repository.findByNome("Monitor")).thenReturn(entity);

        Produto produto = service.encontrarPorNome("Monitor");

        assertEquals("Monitor", produto.getNome());
    }
}



