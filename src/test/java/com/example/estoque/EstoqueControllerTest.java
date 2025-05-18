package com.example.estoque;

import com.example.estoque.controller.EstoqueController;
import com.example.estoque.domain.Pedido;
import com.example.estoque.domain.Produto;
import com.example.estoque.exception.ForaDeEstoqueException;
import com.example.estoque.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EstoqueControllerStandaloneTest {

    private MockMvc mockMvc;

    @Mock
    private ProdutoService service;

    @InjectMocks
    private EstoqueController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void cadastraProduto_deveRetornar200() throws Exception {
        Produto produto = new Produto("Mouse", "Mouse gamer", 99.99, 5);

        mockMvc.perform(post("/estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cadastrado com Sucesso"));

        verify(service).cadastrarProduto(any(Produto.class));
    }


    @Test
    void atualizarEstoque_quandoForaDeEstoque_deveRetornar400() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setItens(List.of());
        doThrow(new ForaDeEstoqueException("Fora de estoque"))
                .when(service).atualizarEstoque(any(Pedido.class));

        mockMvc.perform(post("/estoque/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Fora de estoque"));
    }
}
