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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "estado")
public class Estado extends Entidade {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "ESTADO_ID_GENERATOR", sequenceName = "GEN_ESTADO", allocationSize = 1 ,initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ESTADO_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "nome")
    private String nome;
    
    @Basic(optional = false)
    @Column(name = "sigla")
    private String sigla;
    
    public Estado() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
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
        if (!(object instanceof Estado)) {
            return false;
        }
        Estado other = (Estado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @XmlTransient
    @Override
    public String toString() {
        return "com.gestor.entidades.Estado[ id=" + id + " ]";
    }

}
