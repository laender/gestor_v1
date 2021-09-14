/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "produto_insumo")
public class ProdutoInsumo extends Entidade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "PRD_INSUMO_ID_GENERATOR", sequenceName = "GEN_PRODUTO_INSUMO", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRD_INSUMO_ID_GENERATOR")
    @Column(name = "id")

    private Integer id;
    @JoinColumn(name = "id_produto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Produto produto;

    @JoinColumn(name = "id_insumo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Produto insumo;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "quantidade")
    private BigDecimal quantidade;

    public ProdutoInsumo() {
    }

    public ProdutoInsumo(Integer id) {
        this.id = id;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Produto getInsumo() {
        return insumo;
    }

    public void setInsumo(Produto insumo) {
        this.insumo = insumo;
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
        if (!(object instanceof ProdutoInsumo)) {
            return false;
        }
        ProdutoInsumo other = (ProdutoInsumo) object;
        if(this.id == null && other.id == null){
            return this.getInsumo().equals(other.getInsumo()) && this.getProduto().equals(other.getProduto());
        }
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
          
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.ProdutoInsumo[ id=" + id + " ]";
    }

}
