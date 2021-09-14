/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Banco;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.Producao;
import com.gestor.util.GestorException;
import com.gestor.util.Utils;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author laender
 */
@Stateless
@LocalBean
public class ProducaoEJB extends AbstractEJB<Producao> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProducaoEJB() {
        super(Producao.class);
    }

   

}
