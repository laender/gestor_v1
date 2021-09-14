/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import com.gestor.entidades.Cliente;
import com.gestor.entidades.Produto;
import java.math.BigDecimal;

/**
 *
 * @author laend
 */
public class ProdutoQuantidade {

    private Produto produto;
    private BigDecimal quantidade;

    public ProdutoQuantidade(Produto produto, BigDecimal qtd) {
        this.quantidade = qtd;
        this.produto = produto;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }


}
