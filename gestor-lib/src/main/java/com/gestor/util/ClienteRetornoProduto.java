/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import com.gestor.entidades.Produto;

/**
 *
 * @author laend
 */
public class ClienteRetornoProduto {

    private Produto produto;
    private Integer qtdDiasFaltantes;

    public ClienteRetornoProduto(Produto produto, Integer qtdDiasFaltantes) {
        this.produto = produto;
        this.qtdDiasFaltantes = qtdDiasFaltantes;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQtdDiasFaltantes() {
        return qtdDiasFaltantes;
    }

    public void setQtdDiasFaltantes(Integer qtdDiasFaltantes) {
        this.qtdDiasFaltantes = qtdDiasFaltantes;
    }
    
    
}
