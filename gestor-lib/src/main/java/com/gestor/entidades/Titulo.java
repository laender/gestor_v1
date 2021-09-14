/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import com.gestor.enums.EnumTipoTitulo;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author laender
 */
@Entity
public class Titulo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "TITULO_ID_GENERATOR", sequenceName = "GEN_TITULO", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TITULO_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "id_lancamento_financeiro", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private LancamentoFinanceiro lancamentoFinanceiro;
    
    @JoinColumn(name = "id_orcamento", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Orcamento orcamento;
    
    @JoinColumn(name = "id_tipo_pagamento", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TipoPagamento tipoPagamento;
    
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuario;
    
    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private EnumTipoTitulo tipo;

    @Column(name = "data_emissao")
    @Temporal(TemporalType.DATE)
    private Date dataEmissao;
    
    @Column(name = "valor_titulo")
    private BigDecimal valorTitulo;
    
    @Column(name = "valor_saldo")
    private BigDecimal valorSaldo;
    
    @Column(name = "numero_controle")
    private String numeroControle;
    
    @Column(name = "sacado_cedente")
    private String sacadoCedente;
    
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "titulo")
    private List<TituloParcela> parcelas;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LancamentoFinanceiro getLancamentoFinanceiro() {
        return lancamentoFinanceiro;
    }

    public void setLancamentoFinanceiro(LancamentoFinanceiro lancamentoFinanceiro) {
        this.lancamentoFinanceiro = lancamentoFinanceiro;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public EnumTipoTitulo getTipo() {
        return tipo;
    }

    public void setTipo(EnumTipoTitulo tipo) {
        this.tipo = tipo;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public BigDecimal getValorTitulo() {
        return valorTitulo;
    }

    public void setValorTitulo(BigDecimal valorTitulo) {
        this.valorTitulo = valorTitulo;
    }

    public BigDecimal getValorSaldo() {
        return valorSaldo;
    }

    public void setValorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
    }

    public List<TituloParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<TituloParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public String getNumeroControle() {
        return numeroControle;
    }

    public void setNumeroControle(String numeroControle) {
        this.numeroControle = numeroControle;
    }

    public String getSacadoCedente() {
        return sacadoCedente;
    }

    public void setSacadoCedente(String sacadoCedente) {
        this.sacadoCedente = sacadoCedente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }
    

}
