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
@Table(name = "lancamento_parcela")
public class LancamentoParcela implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "LANCTO_PARCELA_ID_GENERATOR", sequenceName = "GEN_LANCAMENTO_PARCELA", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LANCTO_PARCELA_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "id_titulo_parcela", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TituloParcela parcela;
    
    @JoinColumn(name = "id_lancamento_financeiro", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private LancamentoFinanceiro lancamentoFinanceiro;
    
    @Column(name = "valor_liquidacao")
    private BigDecimal valorLiquidacao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TituloParcela getParcela() {
        return parcela;
    }

    public void setParcela(TituloParcela parcela) {
        this.parcela = parcela;
    }

    public LancamentoFinanceiro getLancamentoFinanceiro() {
        return lancamentoFinanceiro;
    }

    public void setLancamentoFinanceiro(LancamentoFinanceiro lancamentoFinanceiro) {
        this.lancamentoFinanceiro = lancamentoFinanceiro;
    }

    public BigDecimal getValorLiquidacao() {
        return valorLiquidacao;
    }

    public void setValorLiquidacao(BigDecimal valorLiquidacao) {
        this.valorLiquidacao = valorLiquidacao;
    }
    

    
    
}
