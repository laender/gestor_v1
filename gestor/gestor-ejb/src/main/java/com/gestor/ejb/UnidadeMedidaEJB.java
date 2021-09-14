/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.UnidadeMedida;
import com.gestor.enums.EnumUnidadeMedida;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author laender
 */
@Stateless
@LocalBean
public class UnidadeMedidaEJB extends AbstractEJB<UnidadeMedida> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadeMedidaEJB() {
        super(UnidadeMedida.class);
    }

    public UnidadeMedida getUnidadeMedidaPorChave(EnumUnidadeMedida sigla){
        try {
            return (UnidadeMedida) em.createQuery("select um from UnidadeMedida um where um.sigla =:sigla").setParameter("sigla", sigla).getSingleResult();
        } catch (Exception e) {
            
        }
        return null;
    }
}
