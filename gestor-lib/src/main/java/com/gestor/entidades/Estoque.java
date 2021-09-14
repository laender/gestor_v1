/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "estoque")
public class Estoque extends Entidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "ESTOQUE_ID_GENERATOR", sequenceName = "GEN_ESTOQUE", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ESTOQUE_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_produto", referencedColumnName = "id")
    @OneToOne(optional = false)
    private Produto produto;

    private BigDecimal quantidade;
    
    @Column(name = "quantidade_minima")
    private BigDecimal quantidadeMinima;
    
    @Column(name = "quantidade_maxima")
    private BigDecimal quantidadeMaxima;
    
    @Transient
    private BigDecimal valorUnitCompra;
    
    @Transient
    private BigDecimal valorUnitVenda;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estoque")
    private List<MovimentoEstoque> movimentos;

    @JoinColumn(name = "id_unidade_medida", referencedColumnName = "id")
    @OneToOne(optional = false)
    private UnidadeMedida unidadeMedida;
    
    @Transient
    private boolean selecionado;

    public Estoque() {
    }

    public Estoque(Integer id) {
        this.id = id;
    }

    public Estoque(Integer id, String descricao) {
        this.id = id;
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

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public List<MovimentoEstoque> getMovimentos() {
        return movimentos  == null ?  new ArrayList<MovimentoEstoque>(): movimentos;
    }

    public void setMovimentos(List<MovimentoEstoque> movimentos) {
        this.movimentos = movimentos;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public BigDecimal getValorUnitCompra() {
        return valorUnitCompra;
    }

    public void setValorUnitCompra(BigDecimal valorUnitCompra) {
        this.valorUnitCompra = valorUnitCompra;
    }

    public BigDecimal getValorUnitVenda() {
        return valorUnitVenda;
    }

    public void setValorUnitVenda(BigDecimal valorUnitVenda) {
        this.valorUnitVenda = valorUnitVenda;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public BigDecimal getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(BigDecimal quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public BigDecimal getQuantidadeMaxima() {
        return quantidadeMaxima;
    }

    public void setQuantidadeMaxima(BigDecimal quantidadeMaxima) {
        this.quantidadeMaxima = quantidadeMaxima;
    }
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Estoque)) {
            return false;
        }
        Estoque other = (Estoque) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.Estoque[ id=" + id + " ]";
    }

}
