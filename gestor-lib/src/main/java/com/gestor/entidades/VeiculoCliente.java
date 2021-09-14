/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import javax.persistence.Basic;
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
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "veiculo_cliente")
public class VeiculoCliente extends Entidade {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "VEICULO_CLI_ID_GENERATOR", sequenceName = "GEN_VEICULO_CLIENTE", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VEICULO_CLI_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "veiculo")
    private String veiculo;
    
    @Basic(optional = false)
    @Column(name = "placa_veiculo")
    private String placaVeiculo;

    @XmlTransient
    @JoinColumn(name = "id_cliente", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Cliente cliente;
    
    @Transient
    private boolean selecionado = false;
    
    private boolean ativo = true;


    public VeiculoCliente() {
    }

    public VeiculoCliente(Integer id) {
        this.id = id;
    }

    public VeiculoCliente(Integer id, String nome) {
        this.id = id;
        this.veiculo = veiculo;
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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
        if (!(object instanceof VeiculoCliente)) {
            return false;
        }
        VeiculoCliente other = (VeiculoCliente) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @XmlTransient
    @Override
    public String toString() {
        return "com.gestor.entidades.VeiculoCliente[ id=" + id + " ]";
    }


}
