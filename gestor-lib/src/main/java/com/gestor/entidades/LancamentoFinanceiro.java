/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import com.gestor.enums.EnumTipoLancamento;
import com.gestor.util.Utils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "lancamento_financeiro")
public class LancamentoFinanceiro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "LCTO_FIN_ID_GENERATOR", sequenceName = "GEN_LANCAMENTO_FINANCEIRO", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LCTO_FIN_ID_GENERATOR")
    @Column(name = "id")
    @Basic(optional = false)
    @NotNull
    private Integer id;

    @Column(name = "tipo_lancamento")
    @Enumerated(EnumType.STRING)
    private EnumTipoLancamento tipoLancamento;

    @Column(name = "valor")

    private BigDecimal valor;
    @Column(name = "numero_controle")

    private String numeroControle;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "data_lancamento")
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;

    @Column(name = "data_vencimento")
    @Temporal(TemporalType.DATE)
    private Date dataVencimento;

    @JoinColumn(name = "id_caixa", referencedColumnName = "id")
    @ManyToOne
    private Caixa caixa;

    @JoinColumn(name = "id_banco", referencedColumnName = "id")
    @ManyToOne
    private Banco banco;

    @JoinColumn(name = "id_cliente", referencedColumnName = "id")
    @ManyToOne
    private Cliente cliente;

    @JoinColumn(name = "id_orcamento", referencedColumnName = "id")
    @ManyToOne
    private Orcamento orcamento;

    @JoinColumn(name = "id_produto", referencedColumnName = "id")
    @ManyToOne
    private Produto produto;

    @JoinColumn(name = "id_producao", referencedColumnName = "id")
    @ManyToOne
    private Producao producao;

    @JoinColumn(name = "id_fornecedor", referencedColumnName = "id")
    @ManyToOne
    private Fornecedor fornecedor;

    @JoinColumn(name = "id_tipo_pagamento", referencedColumnName = "id")
    @ManyToOne
    private TipoPagamento tipoPagamento;

    @JoinColumn(name = "id_forma_pagamento", referencedColumnName = "id")
    @ManyToOne
    private FormaPagamento formaPagamento;

    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuario;

    @JoinColumn(name = "id_usuario_fornecedor", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioFornecedor;

    @JoinColumn(name = "id_lancamento_referencia", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private LancamentoFinanceiro lancamentoFinanceiroReferencia;

    @OneToMany(mappedBy = "lancamentoFinanceiro", cascade = CascadeType.REMOVE)
    private List<LancamentoParcela> parcelas;

    private boolean estornado = false;
    
    @Transient
    private String sacadoCedente;

    public LancamentoFinanceiro() {
    }

    public LancamentoFinanceiro(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EnumTipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(EnumTipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNumeroControle() {
        return numeroControle;
    }

    public void setNumeroControle(String numeroControle) {
        this.numeroControle = numeroControle;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public Usuario getUsuarioFornecedor() {
        return usuarioFornecedor;
    }

    public void setUsuarioFornecedor(Usuario usuarioFornecedor) {
        this.usuarioFornecedor = usuarioFornecedor;
    }

    public LancamentoFinanceiro getLancamentoFinanceiroReferencia() {
        return lancamentoFinanceiroReferencia;
    }

    public void setLancamentoFinanceiroReferencia(LancamentoFinanceiro lancamentoFinanceiroReferencia) {
        this.lancamentoFinanceiroReferencia = lancamentoFinanceiroReferencia;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<LancamentoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<LancamentoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public boolean isEstornado() {
        return estornado;
    }

    public void setEstornado(boolean estornado) {
        this.estornado = estornado;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Producao getProducao() {
        return producao;
    }

    public void setProducao(Producao producao) {
        this.producao = producao;
    }

    public String getSacadoCedente() {
        List<LancamentoParcela> parcelas = this.getParcelas();
        if(!Utils.empty(parcelas)){
            for (LancamentoParcela lanctPac : parcelas) {
                TituloParcela parcela = lanctPac.getParcela();
                Titulo titulo = parcela.getTitulo();;
                sacadoCedente = titulo.getSacadoCedente();
                return sacadoCedente;
            }
        }
        return sacadoCedente;
    }

    public void setSacadoCedente(String sacadoCedente) {
        this.sacadoCedente = sacadoCedente;
    }
    
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LancamentoFinanceiro)) {
            return false;
        }
        LancamentoFinanceiro other = (LancamentoFinanceiro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.LancamentoFinanceiro[ id=" + id + " ]";
    }

}
