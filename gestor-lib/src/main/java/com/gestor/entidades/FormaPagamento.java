/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "forma_pagamento")
public class FormaPagamento implements Serializable{
    
    
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "FRM_PGTO_ID_GENERATOR", sequenceName = "GEN_FORMA_PAGAMENTO", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FRM_PGTO_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "numero_parcelas")
    private Integer numeroParcelas;
    
    @Column(name = "dias_vcto_primeira_parcela")
    private Integer diasVctoPrimeiraParcela;
    
    @Column(name = "intervalo_parcelas")
    private Integer intervaloParcelas;
    
    private String descricao;
    
    @Column(name = "venda_prazo")
    private boolean vendaAPrazo = false;
    
     @JoinColumn(name = "id_empresa", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Empresa empresa;
    
     public FormaPagamento(String descricao, boolean vendaAPrazo, 
             Integer intervaloParcelas, 
             Integer diasVctoPrimeiraParcela, 
             Integer numeroParcelas,
             Empresa empresa){
         this.descricao = descricao;
         this.vendaAPrazo = vendaAPrazo;
         this.intervaloParcelas = intervaloParcelas;
         this.diasVctoPrimeiraParcela = diasVctoPrimeiraParcela;
         this.empresa = empresa;
         this.numeroParcelas = numeroParcelas;
     }
     
     public FormaPagamento(){
         
     }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public boolean isVendaAPrazo() {
        return vendaAPrazo;
    }

    public void setVendaAPrazo(boolean vendaAPrazo) {
        this.vendaAPrazo = vendaAPrazo;
    }

    public Integer getDiasVctoPrimeiraParcela() {
        return diasVctoPrimeiraParcela;
    }

    public void setDiasVctoPrimeiraParcela(Integer diasVctoPrimeiraParcela) {
        this.diasVctoPrimeiraParcela = diasVctoPrimeiraParcela;
    }

    public Integer getIntervaloParcelas() {
        return intervaloParcelas;
    }

    public void setIntervaloParcelas(Integer intervaloParcelas) {
        this.intervaloParcelas = intervaloParcelas;
    }
    
    
    
    
}
