package com.eduardo.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CarrinhoDeCompras {

    private final Map<Produto, Integer> itens = new HashMap<>();

    public void adicionarProduto(Produto produto, int quantidade) {
        if (quantidade <= 0) {
            return;
        }
        this.itens.merge(produto, quantidade, Integer::sum);
    }

    public void removerProduto(int produtoId) {
        this.itens.keySet().removeIf(produto -> produto.getId() == produtoId);
    }

    public double calcularTotal() {
        return itens.entrySet().stream()
                .mapToDouble(item -> item.getKey().getPreco() * item.getValue())
                .sum();
    }
    
    public Map<Produto, Integer> getItens() {
        return Collections.unmodifiableMap(itens);
    }
}
