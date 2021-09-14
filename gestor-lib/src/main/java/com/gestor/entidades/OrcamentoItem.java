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
@Table(name = "orcamento_item")
public class OrcamentoItem extends Entidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "ORCT_ITEM_ID_GENERATOR", sequenceName = "GEN_ORCAMENTO_ITEM", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORCT_ITEM_ID_GENERATOR")
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

    @JoinColumn(name = "id_orcamento", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Orcamento orcamento;

    @JoinColumn(name = "id_produto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Produto produto;

    @JoinColumn(name = "id_tabela_preco_item", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TabelaPrecoItem tabelaPrecoItem;

    @JoinColumn(name = "id_unidade_medida", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UnidadeMedida unidadeMedida;
    
    @JoinColumn(name = "id_veiculo_cliente", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VeiculoCliente  veiculo;
    
    @Transient
    private String unidadeMedidaSelecionada;

    private boolean insumo = false;

    @Column(name = "observacao")
    private String observacao;

    @Transient
    private boolean selecionado;

    public OrcamentoItem() {
    }

    public OrcamentoItem(Integer id) {
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

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
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

    public boolean isInsumo() {
        return insumo;
    }

    public void setInsumo(boolean insumo) {
        this.insumo = insumo;
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

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getUnidadeMedidaSelecionada() {
        return unidadeMedidaSelecionada;
    }

    public void setUnidadeMedidaSelecionada(String unidadeMedidaSelecionada) {
        this.unidadeMedidaSelecionada = unidadeMedidaSelecionada;
    }

    public VeiculoCliente getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(VeiculoCliente veiculo) {
        this.veiculo = veiculo;
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
        if (!(object instanceof OrcamentoItem)) {
            return false;
        }
        OrcamentoItem other = (OrcamentoItem) object;
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
