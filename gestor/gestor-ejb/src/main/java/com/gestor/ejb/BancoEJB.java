/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Banco;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.util.GestorException;
import com.gestor.util.Utils;
import java.util.List;
import javax.ejb.EJB;
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
public class BancoEJB extends AbstractEJB<Banco> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private LancamentoFinanceiroEJB lancamentoFinanceiroEJB;
    

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BancoEJB() {
        super(Banco.class);
    }

    public void excluir(Banco banco) throws GestorException {
        List<LancamentoFinanceiro> lctos = lancamentoFinanceiroEJB.getLancamentosPorBanco(banco);
        if (!Utils.empty(lctos)) {
            throw new GestorException("O banco está sendo utilizado em lançamento(s) financeiro(s), exclusão não permitida.");
        }
        
    }
    @Override
    public List<Banco> findAll(Empresa empresa) {
        Query q = getEntityManager().createQuery("select c from Banco c where c.empresa =:empresa order by c.descricao ").
                setParameter("empresa", empresa);
        return q.getResultList();
    }

}
