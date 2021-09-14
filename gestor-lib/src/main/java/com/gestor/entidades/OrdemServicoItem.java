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
import javax.persistence.Transient;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "ordem_servico_item")
public class OrdemServicoItem extends Entidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "OS_ITEM_ID_GENERATOR", sequenceName = "GEN_ORDEM_SERVICO_ITEM", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OS_ITEM_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation

    @Column(name = "quantidade")
    private BigDecimal quantidade = BigDecimal.ONE;

    @Column(name = "valor_unitario", precision = 2)
    private BigDecimal valorUnitario = BigDecimal.ZERO;

    @Column(name = "valor_total", precision = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "valor_desconto", precision = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "valor_acrescimo", precision = 2)
    private BigDecimal valorAcrescimo = BigDecimal.ZERO;

    @Column(name = "valor_custo", precision = 2)
    private BigDecimal valorCusto = BigDecimal.ZERO;

    @Column(name = "perc_desconto", precision = 2)
    private BigDecimal percDesconto = BigDecimal.ZERO;

    @Column(name = "perc_acrescimo", precision = 2)
    private BigDecimal percAcrescimo = BigDecimal.ZERO;

    @Column(name = "valor_embutido", precision = 2)
    private BigDecimal valorEmbutido = BigDecimal.ZERO;

    @JoinColumn(name = "id_ordem_servico", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private OrdemServico ordemServico;

    @JoinColumn(name = "id_produto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Produto produto;

    @JoinColumn(name = "id_tabela_preco_item", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TabelaPrecoItem tabelaPrecoItem;


    private String observacao;

    @Transient
    private boolean selecionado;

    public OrdemServicoItem() {
    }

    public OrdemServicoItem(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
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

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorAcrescimo() {
        return valorAcrescimo;
    }

    public void setValorAcrescimo(BigDecimal valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public BigDecimal getPercDesconto() {
        return percDesconto;
    }

    public void setPercDesconto(BigDecimal percDesconto) {
        this.percDesconto = percDesconto;
    }

    public BigDecimal getPercAcrescimo() {
        return percAcrescimo;
    }

    public void setPercAcrescimo(BigDecimal percAcrescimo) {
        this.percAcrescimo = percAcrescimo;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }


    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getValorCusto() {
        return valorCusto;
    }

    public void setValorCusto(BigDecimal valorCusto) {
        this.valorCusto = valorCusto;
    }

    public TabelaPrecoItem getTabelaPrecoItem() {
        return tabelaPrecoItem;
    }

    public void setTabelaPrecoItem(TabelaPrecoItem tabelaPrecoItem) {
        this.tabelaPrecoItem = tabelaPrecoItem;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public BigDecimal getValorEmbutido() {
        return valorEmbutido;
    }

    public void setValorEmbutido(BigDecimal valorEmbutido) {
        this.valorEmbutido = valorEmbutido;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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
        if (!(object instanceof OrdemServicoItem)) {
            return false;
        }
        OrdemServicoItem other = (OrdemServicoItem) object;
        if (!this.produto.equals(other.produto)) {
            return false;
        }
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.OrcamentoItem[ id=" + id + " ]";
    }

}
