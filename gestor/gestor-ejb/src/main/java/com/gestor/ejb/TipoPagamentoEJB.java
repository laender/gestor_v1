/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.TipoPagamento;
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
public class TipoPagamentoEJB extends AbstractEJB<TipoPagamento> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private LancamentoFinanceiroEJB lancamentoFinanceiroEJB;
    
    @EJB
    private OrcamentoEJB orcamentoEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoPagamentoEJB() {
        super(TipoPagamento.class);
    }

    public void excluir(TipoPagamento tipoPagamento) throws GestorException {
        List<LancamentoFinanceiro> lctos = lancamentoFinanceiroEJB.getLancamentosPorTipoPagamento(tipoPagamento);
        if (!Utils.empty(lctos)) {
            throw new GestorException("O tipo de pagamento está sendo utilizado em lançamento(s) financeiro(s), exclusão não permitida.");
        }
        
        List<Orcamento> orcts = orcamentoEJB.getOrcamentoPortipoPagamento(tipoPagamento);
         if (!Utils.empty(orcts)) {
            throw new GestorException("O tipo de pagamento está sendo utilizado em orçamento ou venda");
        }
        super.remove(tipoPagamento);
    }

    @Override
    public List<TipoPagamento> findAll(Empresa empresa) {
        Query q = getEntityManager().createQuery("select c from TipoPagamento c where c.empresa =:empresa order by c.descricao ").
                setParameter("empresa", empresa);
        return q.getResultList();
    }

}
