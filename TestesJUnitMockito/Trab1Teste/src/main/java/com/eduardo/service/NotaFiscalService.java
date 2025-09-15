package com.eduardo.service;

import com.eduardo.model.Produto;
import java.util.List;

public interface NotaFiscalService {
    void emitirNota(double valor, List<Produto> produtos);
}
