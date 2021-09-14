/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import com.gestor.entidades.Cliente;
import java.math.BigDecimal;

/**
 *
 * @author laend
 */
public class CurvaAbcCliente {

    private Cliente cliente;
    private BigDecimal qtdCompras;
    private BigDecimal valorTotal;
    private BigDecimal percentualFat;
    private BigDecimal percentualAcumulado;
    private String classificacao;

    public CurvaAbcCliente(Cliente cliente, BigDecimal qtdCompras, 
            BigDecimal valorUnitario,  
            BigDecimal valorTotal, BigDecimal percentualFat,
            BigDecimal percentualAcumulado, String classificacao) {
        this.cliente = cliente;
        this.qtdCompras = qtdCompras;
        this.valorTotal = valorTotal;
        this.percentualFat = percentualFat;
        this.percentualAcumulado = percentualAcumulado;
        this.classificacao = classificacao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getQtdCompras() {
        return qtdCompras;
    }

    public void setQtdCompras(BigDecimal qtdCompras) {
        this.qtdCompras = qtdCompras;
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

}
