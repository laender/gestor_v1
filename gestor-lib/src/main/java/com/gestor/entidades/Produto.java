/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import com.gestor.enums.EnumTipoPeriodo;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "produto")
public class Produto extends Entidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "PRODUTO_ID_GENERATOR", sequenceName = "GEN_PRODUTO", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUTO_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "descricao")
    private String descricao;
    @Column(name = "codigo")
    private String codigo;
    
    @Column(name = "codigo_barras")
    private String codigoBarras;

    @JoinColumn(name = "id_empresa", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Empresa empresa;

    @JoinColumn(name = "id_fornecedor", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Fornecedor fornecedor;

    @JoinColumn(name = "id_tipo_produto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TipoProduto tipoProduto;

    @JoinTable(name = "tipo_orcamento_produto", joinColumns = {
        @JoinColumn(name = "id_produto", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "id_tipo_orcamento", referencedColumnName = "id")})
    @ManyToMany
    private List<TipoOrcamento> tipoOrcamentos;

    @Transient
    private List<ProdutoInsumo> produtoInsumos;

    @OneToMany(mappedBy = "produto")
    private List<TabelaPrecoItem> tabelaPrecoItens;
    
    @OneToOne(mappedBy = "produto")
    private Estoque estoque;
    
    @Column (name = "periodo_retorno")
    private Integer periodoRetorno;

    @Column(name = "tipo_periodo")
    @Enumerated(EnumType.STRING)
    private EnumTipoPeriodo tipoPeriodo;
    
    private boolean ativo = true;
    
    public Produto() {
    }
    public Produto(Empresa empresa){
        this.empresa = empresa;
    }

    public Produto(Integer id) {
        this.id = id;
    }

    public Produto(Integer id, String descricao) {
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public List<TabelaPrecoItem> getTabelaPrecoItens() {
        return tabelaPrecoItens;
    }

    public void setTabelaPrecoItens(List<TabelaPrecoItem> tabelaPrecoItens) {
        this.tabelaPrecoItens = tabelaPrecoItens;
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
        if (!(object instanceof Produto)) {
            return false;
        }
        Produto other = (Produto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.Produto[ id=" + id + " ]";
    }

    public List<TipoOrcamento> getTipoOrcamentos() {
        return tipoOrcamentos;
    }

    public void setTipoOrcamentos(List<TipoOrcamento> tipoOrcamentos) {
        this.tipoOrcamentos = tipoOrcamentos;
    }

    public List<ProdutoInsumo> getProdutoInsumos() {
        return produtoInsumos;
    }

    public void setProdutoInsumos(List<ProdutoInsumo> produtoInsumos) {
        this.produtoInsumos = produtoInsumos;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Integer getPeriodoRetorno() {
        return periodoRetorno;
    }

    public void setPeriodoRetorno(Integer periodoRetorno) {
        this.periodoRetorno = periodoRetorno;
    }

    public EnumTipoPeriodo getTipoPeriodo() {
        return tipoPeriodo;
    }

    public void setTipoPeriodo(EnumTipoPeriodo tipoPeriodo) {
        this.tipoPeriodo = tipoPeriodo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    

}
