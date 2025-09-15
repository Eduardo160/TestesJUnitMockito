package com.eduardo.service;

import com.eduardo.model.CarrinhoDeCompras;
import com.eduardo.model.Produto;
import java.util.ArrayList;
import java.util.Map;

public class VendaService {

    private final DescontoService descontoService;
    private final PagamentoService pagamentoService;
    private final NotaFiscalService notaFiscalService;

    public VendaService(DescontoService descontoService, 
            PagamentoService pagamentoService, 
            NotaFiscalService notaFiscalService) {
        this.descontoService = descontoService;
        this.pagamentoService = pagamentoService;
        this.notaFiscalService = notaFiscalService;
    }

    public boolean realizarVenda(CarrinhoDeCompras carrinho) {
        double totalBruto = carrinho.calcularTotal();
        if (totalBruto == 0) {
            return false;
        }

        double totalComDesconto = descontoService.aplicarDesconto(totalBruto);

        if (!verificarEstoqueDisponivel(carrinho)) {
            System.err.println("Venda não realizada: Produto sem estoque.");
            return false;
        }

        try {
            boolean pagamentoAprovado = pagamentoService.
                    processarPagamento(totalComDesconto);

            if (pagamentoAprovado) {
                System.out.println("Venda realizada com sucesso!");
                return true;
            } else {
                System.err.println("Venda não realizada: Pagamento recusado.");
                return false;
            }
        } catch (RuntimeException e) {
            System.err.println("ERRO CRÍTICO no serviço de pagamento: " + 
                    e.getMessage());
            return false;
        }
    }

    private boolean verificarEstoqueDisponivel(CarrinhoDeCompras carrinho) {
        for (Map.Entry<Produto, Integer> item : carrinho.getItens().entrySet()){
            Produto produto = item.getKey();
            Integer quantidadeDesejada = item.getValue();
            if (produto.getEstoque() < quantidadeDesejada) {
                return false;
            }
        }
        return true;
    }
}
