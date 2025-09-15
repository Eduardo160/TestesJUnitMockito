package com.eduardo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CarrinhoDeComprasTest {

    private CarrinhoDeCompras carrinho;
    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    public void setUp() {
        carrinho = new CarrinhoDeCompras();
        produto1 = new Produto(1, "Laptop Gamer", 7500.0, 10);
        produto2 = new Produto(2, "Teclado Mecânico", 450.0, 30);
    }

    @Test
    public void testAdicionarProdutosNoCarrinho() {
        // Ação
        carrinho.adicionarProduto(produto1, 1);
        carrinho.adicionarProduto(produto2, 2);
        carrinho.adicionarProduto(produto1, 1);

        // Verificação
        assertEquals(2, carrinho.getItens().get(produto1), 
                "A quantidade do produto 1 deveria ser 2.");
        assertEquals(2, carrinho.getItens().get(produto2), 
                "A quantidade do produto 2 deveria ser 2.");
    }

    @Test
    public void testRemoverProdutoDoCarrinho() {
        // Preparação
        carrinho.adicionarProduto(produto1, 1);
        carrinho.adicionarProduto(produto2, 2);

        // Ação
        carrinho.removerProduto(produto1.getId());

        // Verificação
        assertFalse(carrinho.getItens().containsKey(produto1), 
                "O produto 1 não deveria mais estar no carrinho.");
        assertTrue(carrinho.getItens().containsKey(produto2), 
                "O produto 2 deveria permanecer no carrinho.");
        assertEquals(1, carrinho.getItens().size());
    }

    @Test
    public void testCalcularValorTotal() {
        // Preparação
        carrinho.adicionarProduto(produto1, 1);
        carrinho.adicionarProduto(produto2, 2);

        // Ação
        double total = carrinho.calcularTotal();

        // Verificação
        assertEquals(8400.0, total, 
                "O valor total do carrinho está incorreto.");
    }
}
