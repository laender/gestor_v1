/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import com.gestor.enums.EnumTipoLancamento;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "movimento_estoque")
public class MovimentoEstoque extends Entidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "MOV_EST_ID_GENERATOR", sequenceName = "GEN_MOVIMENTO_ESTOQUE", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MOV_EST_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_orcamento")
    private Integer idOrcamento;
    
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuario;

    @JoinColumn(name = "id_estoque", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Estoque estoque;

    private BigDecimal quantidade;

    @Column(name = "nota_fiscal")
    private String notaFiscal;

    @Column(name = "data_movimento")
    @Temporal(TemporalType.DATE)
    private Date dataMovimento;

    @Column(name = "tipo_movimento")
    @Enumerated(EnumType.STRING)
    private EnumTipoLancamento tipoMovimento;
    
    private String observacao;
    
    @Transient
    private BigDecimal valorMovimento = BigDecimal.ZERO;
    
    public MovimentoEstoque() {
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

    public Integer getIdOrcamento() {
        return idOrcamento;
    }

    public void setIdOrcamento(Integer idOrcamento) {
        this.idOrcamento = idOrcamento;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    public String getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(String notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public EnumTipoLancamento getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(EnumTipoLancamento tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getValorMovimento() {
        return valorMovimento;
    }

    public void setValorMovimento(BigDecimal valorMovimento) {
        this.valorMovimento = valorMovimento;
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
        if (!(object instanceof Estoque)) {
            return false;
        }
        MovimentoEstoque other = (MovimentoEstoque) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.MovimentoEstoque[ id=" + id + " ]";
    }

}
