/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
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
public class EmpresaEJB extends AbstractEJB<Empresa>  {
    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmpresaEJB() {
        super(Empresa.class);
    }
    
    public Empresa getEmpresaPorLogin(String loginKey){
        try {
            return (Empresa) em.createQuery("select e from Empresa e where e.loginKey =:loginKey").setParameter("loginKey", loginKey).getSingleResult();
        } catch (Exception e) {
          //  e.printStackTrace();
        }
        return null;
    }
    
}
