/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import com.gestor.enums.EnumRamoAtividade;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "ramo_atividade")
public class RamoAtividade extends Entidade {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "RAMO_ATIV_ID_GENERATOR", sequenceName = "GEN_RAMO_ATIVIDADE", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RAMO_ATIV_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "chave")
    @Enumerated(EnumType.STRING)
    private EnumRamoAtividade chave;

    public RamoAtividade() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EnumRamoAtividade getChave() {
        return chave;
    }

    public void setChave(EnumRamoAtividade chave) {
        this.chave = chave;
    }


    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RamoAtividade)) {
            return false;
        }
        RamoAtividade other = (RamoAtividade) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.Empresa[ id=" + id + " ]";
    }

}
