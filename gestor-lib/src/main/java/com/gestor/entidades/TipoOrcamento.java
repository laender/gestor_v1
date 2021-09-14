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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "tipo_orcamento")
public class TipoOrcamento extends Entidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "TP_ORC_ID_GENERATOR", sequenceName = "GEN_TIPO_ORCAMENTO", allocationSize = 1 ,initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TP_ORC_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "descricao")
    private String descricao;
    
  //  @ManyToMany(mappedBy = "tipoOrcamentos")
    //private List<Produto> produtos;
    
    @OneToMany(mappedBy = "tipoOrcamento")
    private List<Orcamento> orcamentoList;
    
    @JoinColumn(name = "id_empresa", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Empresa empresa;
    
    @JoinTable(name = "tipo_orcamento_produto", joinColumns = {
        @JoinColumn(name = "id_tipo_orcamento", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "id_produto", referencedColumnName = "id")})
    @ManyToMany
    private List<Produto> produtos;

    public TipoOrcamento() {
    }

    public TipoOrcamento(Integer id) {
        this.id = id;
    }

    public TipoOrcamento(Integer id, String descricao) {
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

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }


    public List<Orcamento> getOrcamentoList() {
        return orcamentoList;
    }

    public void setOrcamentoList(List<Orcamento> orcamentoList) {
        this.orcamentoList = orcamentoList;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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
        if (!(object instanceof TipoOrcamento)) {
            return false;
        }
        TipoOrcamento other = (TipoOrcamento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.TipoOrcamento[ id=" + id + " ]";
    }

}
