/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import com.gestor.entidades.Produto;
import com.gestor.entidades.UnidadeMedida;
import java.math.BigDecimal;

/**
 *
 * @author laend
 */
public class CurvaAbcEstoque {

    private Produto produto;
    private UnidadeMedida unidadeMedida;
    private BigDecimal qtdVendida;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal percentualFat;
    private BigDecimal percentualAcumulado;
    private String classificacao;

    public CurvaAbcEstoque(Produto produto, UnidadeMedida unidadeMedida, BigDecimal qtdVendida, 
            BigDecimal valorUnitario,  
            BigDecimal valorTotal, BigDecimal percentualFat,
            BigDecimal percentualAcumulado, String classificacao) {
        this.produto = produto;
        this.qtdVendida = qtdVendida;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
        this.percentualFat = percentualFat;
        this.percentualAcumulado = percentualAcumulado;
        this.classificacao = classificacao;
        this.unidadeMedida = unidadeMedida;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getQtdVendida() {
        return qtdVendida;
    }

    public void setQtdVendida(BigDecimal qtdVendida) {
        this.qtdVendida = qtdVendida;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getPercentualFat() {
        return percentualFat;
    }

    public void setPercentualFat(BigDecimal percentualFat) {
        this.percentualFat = percentualFat;
    }

    public BigDecimal getPercentualAcumulado() {
        return percentualAcumulado;
    }

    public void setPercentualAcumulado(BigDecimal percentualAcumulado) {
        this.percentualAcumulado = percentualAcumulado;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
    
    
    
    
    
    

}
