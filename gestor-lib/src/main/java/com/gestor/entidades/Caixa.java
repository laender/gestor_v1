/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "caixa")
public class Caixa extends Entidade {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "CAIXA_ID_GENERATOR", sequenceName = "GEN_CAIXA", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAIXA_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "data_abertura")
    @Temporal(TemporalType.DATE)
    private Date dataAbertura;
    
    @Column(name = "data_fechamento")
    @Temporal(TemporalType.DATE)
    private Date dataFechamento;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    
    @Column(name = "valor_inicial")
    private BigDecimal valorInicial;
    
    @Column(name = "valor_final")
    private BigDecimal valorFinal;
    
    @Column(name = "aberto")
    private Boolean aberto;
    
    @OneToMany(mappedBy = "caixa")
    private List<LancamentoFinanceiro> lancamentosFinanceiros;
    
    @JoinColumn(name = "id_usuario_abertura", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioAbertura;
    @JoinColumn(name = "id_usuario_fechamento", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioFechamento;
    
    @Transient
    private BigDecimal totalEntradas;
    
    @Transient
    private BigDecimal totalSaidas;
    
    @Transient
    private BigDecimal saldo;

    public Caixa() {
    }

    public Caixa(Integer id) {
        this.id = id;
    }

    public Caixa(Integer id, Date dataAbertura) {
        this.id = id;
        this.dataAbertura = dataAbertura;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Date getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Boolean getAberto() {
        return aberto;
    }

    public void setAberto(Boolean aberto) {
        this.aberto = aberto;
    }

    public List<LancamentoFinanceiro> getLancamentosFinanceiros() {
        return lancamentosFinanceiros;
    }

    public void setLancamentosFinanceiros(List<LancamentoFinanceiro> lancamentosFinanceiros) {
        this.lancamentosFinanceiros = lancamentosFinanceiros;
    }

    public BigDecimal getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(BigDecimal totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public BigDecimal getTotalSaidas() {
        return totalSaidas;
    }

    public void setTotalSaidas(BigDecimal totalSaidas) {
        this.totalSaidas = totalSaidas;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Usuario getUsuarioAbertura() {
        return usuarioAbertura;
    }

    public void setUsuarioAbertura(Usuario usuarioAbertura) {
        this.usuarioAbertura = usuarioAbertura;
    }

    public Usuario getUsuarioFechamento() {
        return usuarioFechamento;
    }

    public void setUsuarioFechamento(Usuario usuarioFechamento) {
        this.usuarioFechamento = usuarioFechamento;
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
        if (!(object instanceof Caixa)) {
            return false;
        }
        Caixa other = (Caixa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.Caixa[ id=" + id + " ]";
    }

}
