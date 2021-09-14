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
public class ClienteValor {

    private Cliente cliente;
    private BigDecimal valor;
    private Integer posicao;

    public ClienteValor(Cliente cliente, BigDecimal valor, Integer posicao) {
        this.valor = valor;
        this.cliente = cliente;
        this.posicao = posicao;
    }
    //https://endeavor.org.br/estrategia-e-gestao/curva-abc-gestao-estoque/

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliete) {
        this.cliente = cliete;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(Integer posicao) {
        this.posicao = posicao;
    }
    


}
