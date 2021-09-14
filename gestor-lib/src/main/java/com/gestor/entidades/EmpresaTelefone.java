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

/**
 *
 * @author laender
 */
@Entity
@Table(name = "empresa_telefone")
public class EmpresaTelefone extends Entidade {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "EMPRESA_TEL_ID_GENERATOR", sequenceName = "GEN_EMPRESA_TELEFONE", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMPRESA_TEL_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @Column(name = "telefone")
    private String telefone;

    private boolean principal = false;

    @JoinColumn(name = "id_empresa", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Empresa empresa;

    public EmpresaTelefone() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean getPrincipal() {
        return principal;
    }

    public void isPrincipal(boolean principal) {
        this.principal = principal;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
    
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmpresaTelefone)) {
            return false;
        }
        EmpresaTelefone other = (EmpresaTelefone) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.EmpresaTelefone[ id=" + id + " ]";
    }

}
