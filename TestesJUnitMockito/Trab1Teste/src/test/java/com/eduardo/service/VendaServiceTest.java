package com.eduardo.service;

import com.eduardo.model.CarrinhoDeCompras;
import com.eduardo.model.Produto;
import com.eduardo.service.DescontoService;
import com.eduardo.service.NotaFiscalService;
import com.eduardo.service.PagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class VendaServiceTest {

    private DescontoService descontoService;
    private PagamentoService pagamentoService;
    private NotaFiscalService notaFiscalService;

    private VendaService vendaService;

    private Produto produtoComEstoque;
    private CarrinhoDeCompras carrinho;

    @BeforeEach
    public void setUp() {
        descontoService = Mockito.mock(DescontoService.class);
        pagamentoService = Mockito.mock(PagamentoService.class);
        notaFiscalService = Mockito.mock(NotaFiscalService.class);

        vendaService = new VendaService(descontoService, pagamentoService,
                notaFiscalService);

        produtoComEstoque = new Produto(1, "Monitor 4K", 2000.0, 5);
        carrinho = new CarrinhoDeCompras();
    }


    @Test
    public void testDeveAplicarDescontoDe10Porcento() {
        carrinho.adicionarProduto(produtoComEstoque, 1);
        double valorComDesconto = 1800.0;

        when(descontoService.aplicarDesconto(2000.0)).
                thenReturn(valorComDesconto);
        when(pagamentoService.processarPagamento(valorComDesconto)).
                thenReturn(true);

        vendaService.realizarVenda(carrinho);

        verify(pagamentoService).processarPagamento(valorComDesconto);
    }

    @Test
    public void testDeveManterValorSeDescontoForZero() {
        carrinho.adicionarProduto(produtoComEstoque, 1); 

        when(descontoService.aplicarDesconto(2000.0)).thenReturn(2000.0);
        when(pagamentoService.processarPagamento(2000.0)).thenReturn(true);

        vendaService.realizarVenda(carrinho);

        verify(pagamentoService).processarPagamento(2000.0);
    }


    @Test
    public void testDeveFalharVendaPorEstoqueInsuficiente() {
        carrinho.adicionarProduto(produtoComEstoque, 10);

        boolean resultado = vendaService.realizarVenda(carrinho);

        assertFalse(resultado);
        verify(pagamentoService, never()).processarPagamento(anyDouble());
    }


    @Test
    public void testDeveConcluirVendaSePagamentoAprovado() {
        carrinho.adicionarProduto(produtoComEstoque, 2);
        when(descontoService.aplicarDesconto(anyDouble())).thenAnswer(inv ->
                inv.getArgument(0));
        when(pagamentoService.processarPagamento(anyDouble())).thenReturn(true);

        boolean resultado = vendaService.realizarVenda(carrinho);

        assertTrue(resultado);
        assertEquals(3, produtoComEstoque.getEstoque(), "O estoque "
                + "deveria ser reduzido.");
        verify(notaFiscalService, times(1)).emitirNota(anyDouble(), anyList());
    }

    @Test
    public void testDeveFalharVendaSePagamentoRecusado() {
        carrinho.adicionarProduto(produtoComEstoque, 2);
        when(descontoService.aplicarDesconto(anyDouble())).thenAnswer(inv ->
                inv.getArgument(0));
        when(pagamentoService.processarPagamento(anyDouble())).thenReturn(false);

        boolean resultado = vendaService.realizarVenda(carrinho);

        assertFalse(resultado);
        assertEquals(5, produtoComEstoque.getEstoque(), "O estoque não "
                + "deveria ser alterado.");
        verify(notaFiscalService, never()).emitirNota(anyDouble(), anyList());
    }

    @Test
    public void testDeveFalharVendaSePagamentoLancarExcecao() {
        carrinho.adicionarProduto(produtoComEstoque, 1);
        when(descontoService.aplicarDesconto(anyDouble())).thenAnswer(inv ->
                inv.getArgument(0));
        when(pagamentoService.processarPagamento(anyDouble()))
            .thenThrow(new RuntimeException("Gateway de pagamento offline"));

        boolean resultado = vendaService.realizarVenda(carrinho);

        assertFalse(resultado);
        assertEquals(5, produtoComEstoque.getEstoque(), "O estoque deve"
                + " permanecer intacto após exceção.");
        verify(notaFiscalService, never()).emitirNota(anyDouble(), anyList());
    }
}