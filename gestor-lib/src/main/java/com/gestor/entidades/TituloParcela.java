/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
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

/**
 *
 * @author laender
 */
@Entity
@Table(name = "titulo_parcela")
public class TituloParcela implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "TITULO_PARCELA_ID_GENERATOR", sequenceName = "GEN_TITULO_PARCELA", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TITULO_PARCELA_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "id_titulo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Titulo titulo;
    
    @Column(name = "data_vencimento")
    @Temporal(TemporalType.DATE)
    private Date dataVencimento;
    
    @Column(name = "valor_parcela")
    private BigDecimal valorParcela;
    
    @Column(name = "valor_saldo")
    private BigDecimal valorSaldo;
    
    @Column(name = "numero_parcela")
    private Integer numeroParcela;
    
     
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "parcela")
    private List<LancamentoParcela> lancamentoParcelas;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getValorSaldo() {
        return valorSaldo;
    }

    public void setValorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
    }

    public Titulo getTitulo() {
        return titulo;
    }

    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(BigDecimal valorParcela) {
        this.valorParcela = valorParcela;
    }

    public Integer getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(Integer numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public List<LancamentoParcela> getLancamentoParcelas() {
        return lancamentoParcelas;
    }

    public void setLancamentoParcelas(List<LancamentoParcela> lancamentoParcelas) {
        this.lancamentoParcelas = lancamentoParcelas;
    }
    
    
    
    
}
