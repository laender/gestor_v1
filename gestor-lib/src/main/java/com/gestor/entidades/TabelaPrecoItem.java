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
@Table(name = "tabela_preco_item")
public class TabelaPrecoItem extends Entidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "TBL_PRC_ITEM_ID_GENERATOR", sequenceName = "GEN_TABELA_PRECO_ITEM", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBL_PRC_ITEM_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation

    @Column(name = "valor_compra")
    private BigDecimal valorCompra;

    @Column(name = "valor_venda")
    private BigDecimal valorVenda;

    @Column(name = "perc_max_desc")
    private BigDecimal percMaxDesc;

    @Column(name = "valor_max_desc")
    private BigDecimal valorMaxDesc;

    @JoinColumn(name = "id_tabela_preco", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TabelaPreco tabelaPreco;

    @JoinColumn(name = "id_produto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Produto produto;

    public TabelaPrecoItem() {
    }

    public TabelaPrecoItem(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public BigDecimal getPercMaxDesc() {
        return percMaxDesc;
    }

    public void setPercMaxDesc(BigDecimal percMaxDesc) {
        this.percMaxDesc = percMaxDesc;
    }

    public TabelaPreco getTabelaPreco() {
        return tabelaPreco;
    }

    public void setTabelaPreco(TabelaPreco tabelaPreco) {
        this.tabelaPreco = tabelaPreco;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getValorMaxDesc() {
        return valorMaxDesc;
    }

    public void setValorMaxDesc(BigDecimal valorMaxDesc) {
        this.valorMaxDesc = valorMaxDesc;
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
        if (!(object instanceof TabelaPrecoItem)) {
            return false;
        }

        TabelaPrecoItem other = (TabelaPrecoItem) object;
        if (this.id == null && other.id == null) {
            return this.getProduto().equals(other.getProduto());
        }
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.TabelaPrecoItem[ id=" + id + " ]";
    }

}
