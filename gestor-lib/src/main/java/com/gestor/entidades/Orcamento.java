/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import com.gestor.enums.EnumStatusOrcamento;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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

/**
 *
 * @author laender
 */
@Entity
@Table(name = "orcamento")
public class Orcamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "ORCAMENTO_ID_GENERATOR", sequenceName = "GEN_ORCAMENTO", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORCAMENTO_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @Column(name = "data_orcamento")
    @Temporal(TemporalType.DATE)
    private Date dataOrcamento;

    @Column(name = "data_venda")
    @Temporal(TemporalType.DATE)
    private Date dataVenda;

    @Column(name = "data_vencimento")
    @Temporal(TemporalType.DATE)
    private Date dataVencimento;

    @Column(name = "data_entrega_prevista")
    @Temporal(TemporalType.DATE)
    private Date dataEntregaPrevista;

    @Column(name = "observacao")
    private String observacao;

// @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "valor_desconto")
    private BigDecimal valorDesconto;

    @Column(name = "valor_acrescimo")
    private BigDecimal valorAcrescimo;

    @Column(name = "valor_saldo")
    private BigDecimal valorSaldo;

    @Column(name = "valor_comissao")
    private BigDecimal valorComissao;

    @JoinColumn(name = "id_cliente", referencedColumnName = "id")
    @ManyToOne
    private Cliente cliente;

    @JoinColumn(name = "id_tipo_orcamento", referencedColumnName = "id")
    @ManyToOne
    private TipoOrcamento tipoOrcamento;

    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuario;

    @JoinColumn(name = "id_multiplicador", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Multiplicador multiplicador;

    @JoinColumn(name = "id_usuario_executante", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioExecutante;

    @JoinColumn(name = "id_forma_pagamento", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FormaPagamento formaPagamento;

    @JoinColumn(name = "id_tipo_pagamento", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TipoPagamento tipoPagamento;

    @OneToMany(mappedBy = "orcamento")
    private List<LancamentoFinanceiro> lancamentoFinanceiroList;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "orcamento")
    private List<OrcamentoItem> orcamentoItemList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orcamento")
    private List<OrcamentoPagamento> orcamentoPagamentoList;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnumStatusOrcamento status;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orcamento")
//    private List<OrcamentoVeiculo> veiculos;

    @Transient
    private BigDecimal totalCustos;

    @Transient
    private BigDecimal totalLucro;

    @Transient
    private BigDecimal totalMaoObra;

    @Column(name = "endereco_entrega")
    private String enderecoEntrega;

    @Column(name = "placa_veiculo")
    private String placaVeiculo;

    @Column(name = "quilometragem")
    private BigDecimal quilometragem;

    public Orcamento() {
    }

    public Orcamento(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataOrcamento() {
        return dataOrcamento;
    }

    public void setDataOrcamento(Date dataOrcamento) {
        this.dataOrcamento = dataOrcamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorSaldo() {
        return valorSaldo;
    }

    public void setValorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
    }

    public BigDecimal getValorComissao() {
        return valorComissao;
    }

    public void setValorComissao(BigDecimal valorComissao) {
        this.valorComissao = valorComissao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente clliente) {
        this.cliente = clliente;
    }

    public TipoOrcamento getTipoOrcamento() {
        return tipoOrcamento;
    }

    public void setTipoOrcamento(TipoOrcamento tipoOrcamento) {
        this.tipoOrcamento = tipoOrcamento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<LancamentoFinanceiro> getLancamentoFinanceiroList() {
        return lancamentoFinanceiroList;
    }

    public void setLancamentoFinanceiroList(List<LancamentoFinanceiro> lancamentoFinanceiroList) {
        this.lancamentoFinanceiroList = lancamentoFinanceiroList;
    }

    public List<OrcamentoItem> getOrcamentoItemList() {
        return orcamentoItemList;
    }

    public void setOrcamentoItemList(List<OrcamentoItem> orcamentoItemList) {
        this.orcamentoItemList = orcamentoItemList;
    }

    public List<OrcamentoPagamento> getOrcamentoPagamentoList() {
        return orcamentoPagamentoList;
    }

    public void setOrcamentoPagamentoList(List<OrcamentoPagamento> orcamentoPagamentoList) {
        this.orcamentoPagamentoList = orcamentoPagamentoList;
    }

    public EnumStatusOrcamento getStatus() {
        return status;
    }

    public void setStatus(EnumStatusOrcamento status) {
        this.status = status;
    }

    public Date getDataEntregaPrevista() {
        return dataEntregaPrevista;
    }

    public void setDataEntregaPrevista(Date dataEntregaPrevista) {
        this.dataEntregaPrevista = dataEntregaPrevista;
    }

    public BigDecimal getTotalCustos() {
        return totalCustos;
    }

    public void setTotalCustos(BigDecimal totalCustos) {
        this.totalCustos = totalCustos;
    }

    public BigDecimal getTotalLucro() {
        return totalLucro;
    }

    public void setTotalLucro(BigDecimal totalLucro) {
        this.totalLucro = totalLucro;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public String getPlacaVeiculo() {
        if (this.getOrcamentoItemList() != null) {
            placaVeiculo = "";
            for (OrcamentoItem item : this.getOrcamentoItemList()) {
                VeiculoCliente vc = item.getVeiculo();
                if (vc != null) {
                    placaVeiculo +=  vc.getVeiculo()+" - "+ vc.getPlacaVeiculo() + ", ";
                }
            }
        }
        if (placaVeiculo != null && !placaVeiculo.equals("")) {
            char ultimo = placaVeiculo.charAt(placaVeiculo.length() - 2);
            if (ultimo == ',') {
                return placaVeiculo.substring(0, placaVeiculo.length() - 2);
            }
        }
        return placaVeiculo;

    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public BigDecimal getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(BigDecimal quilometragem) {
        this.quilometragem = quilometragem;
    }

    public Usuario getUsuarioExecutante() {
        return usuarioExecutante;
    }

    public void setUsuarioExecutante(Usuario usuarioExecutante) {
        this.usuarioExecutante = usuarioExecutante;
    }

    public BigDecimal getTotalMaoObra() {
        return totalMaoObra;
    }

    public void setTotalMaoObra(BigDecimal totalMaoObra) {
        this.totalMaoObra = totalMaoObra;
    }

    public Date getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
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

    public Multiplicador getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(Multiplicador multiplicador) {
        this.multiplicador = multiplicador;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

//    public List<OrcamentoVeiculo> getVeiculos() {
//        return veiculos;
//    }
//
//    public void setVeiculos(List<OrcamentoVeiculo> veiculos) {
//        this.veiculos = veiculos;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Orcamento)) {
            return false;
        }
        Orcamento other = (Orcamento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.Orcamento[ id=" + id + " ]";
    }

}
