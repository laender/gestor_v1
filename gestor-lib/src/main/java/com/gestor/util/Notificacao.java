/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import com.gestor.entidades.Cliente;
import com.gestor.entidades.Produto;

/**
 *
 * @author laend
 */
public class Notificacao {
    
    private Cliente cliente;
    private Produto produto;
    private String texto;
    private boolean faturamentoAumentou = false;
    private boolean faturamentoIgual = false;

    public Notificacao(Cliente cliente, Produto produto, String texto) {
        this.cliente = cliente;
        this.produto = produto;
        this.texto = texto;
    }
    

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isFaturamentoAumentou() {
        return faturamentoAumentou;
    }

    public void setFaturamentoAumentou(boolean faturamentoAumentou) {
        this.faturamentoAumentou = faturamentoAumentou;
    }

    public boolean isFaturamentoIgual() {
        return faturamentoIgual;
    }

    public void setFaturamentoIgual(boolean faturamentoIgual) {
        this.faturamentoIgual = faturamentoIgual;
    }
    
    
}
