/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Fornecedor;
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
public class FornecedorEJB extends AbstractEJB<Fornecedor>  {
    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FornecedorEJB() {
        super(Fornecedor.class);
    }
    
}
