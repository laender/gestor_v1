/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "tipo_pagamento")
public class TipoPagamento extends Entidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "TP_PGTO_ID_GENERATOR", sequenceName = "GEN_TIPO_PAGAMENTO", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TP_PGTO_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "descricao")
    private String descricao;
    @OneToMany(mappedBy = "tipoPagamento")
    private List<LancamentoFinanceiro> lancamentoFinanceiroList;

    @JoinColumn(name = "id_empresa", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Empresa empresa;

    public TipoPagamento() {
    }

    public TipoPagamento(String descricao, Empresa empresa) {
        this.empresa = empresa;
        this.descricao = descricao;
    }

    public TipoPagamento(Integer id) {
        this.id = id;
    }

    public TipoPagamento(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<LancamentoFinanceiro> getLancamentoFinanceiroList() {
        return lancamentoFinanceiroList;
    }

    public void setLancamentoFinanceiroList(List<LancamentoFinanceiro> lancamentoFinanceiroList) {
        this.lancamentoFinanceiroList = lancamentoFinanceiroList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoPagamento)) {
            return false;
        }
        TipoPagamento other = (TipoPagamento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.TipoPagamento[ id=" + id + " ]";
    }

}
