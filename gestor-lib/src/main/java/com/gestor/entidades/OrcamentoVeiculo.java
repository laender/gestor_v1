/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "orcamento_veiculo")
public class OrcamentoVeiculo extends Entidade {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "OV_ID_GENERATOR", sequenceName = "GEN_ORCAMENTO_VEICULO", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OV_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "id_veiculo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VeiculoCliente veiculoCliente;
    
     @JoinColumn(name = "id_orcamento", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Orcamento orcamento;
    

    public OrcamentoVeiculo() {
    }

    public OrcamentoVeiculo(Integer id) {
        this.id = id;
    }

    public OrcamentoVeiculo(Integer id, Orcamento orcamento, VeiculoCliente veiculo) {
        this.id = id;
        this.orcamento = orcamento;
        this.veiculoCliente = veiculo;
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public VeiculoCliente getVeiculoCliente() {
        return veiculoCliente;
    }

    public void setVeiculoCliente(VeiculoCliente veiculoCliente) {
        this.veiculoCliente = veiculoCliente;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }


    @XmlTransient
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @XmlTransient
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrcamentoVeiculo)) {
            return false;
        }
        OrcamentoVeiculo other = (OrcamentoVeiculo) object;
        if((this.getVeiculoCliente() != null && other.getVeiculoCliente() != null) && 
                this.getVeiculoCliente().equals(other.getVeiculoCliente())){
            return true;
        }
        
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @XmlTransient
    @Override
    public String toString() {
        return "com.gestor.entidades.OrcamentoVeiculo[ id=" + id + " ]";
    }


}
