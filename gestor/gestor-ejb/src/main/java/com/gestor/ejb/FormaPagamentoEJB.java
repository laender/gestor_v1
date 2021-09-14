/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.FormaPagamento;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.Orcamento;
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
public class FormaPagamentoEJB extends AbstractEJB<FormaPagamento> {

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

    public FormaPagamentoEJB() {
        super(FormaPagamento.class);
    }

    public void excluir(FormaPagamento frmPgto) throws GestorException {
        List<LancamentoFinanceiro> lctos = lancamentoFinanceiroEJB.getLancamentosPorFormaPagamento(frmPgto);
        if (!Utils.empty(lctos)) {
            throw new GestorException("A forma de pagamento está sendo utilizada em lançamento(s) financeiro(s), exclusão não permitida.");
        }
        List<Orcamento> orcamentos = orcamentoEJB.getOrcamentosPorFormaPagamento(frmPgto);
        if (!Utils.empty(orcamentos)) {
            throw new GestorException("A forma de pagamento está sendo utilizada em orçamento(s), exclusão nao permitida.");
        }
        super.remove(frmPgto);
    }

    @Override
    public List<FormaPagamento> findAll(Empresa empresa) {
        String sql = "select c from FormaPagamento c where c.empresa =:empresa order by c.descricao";
        Query q = getEntityManager().createQuery(sql).setParameter("empresa", empresa);
        return q.getResultList();
    }
    
    public FormaPagamento getFormaPgtoAVista(Empresa empresa){
        try {
            return (FormaPagamento)em.createQuery("select f from FormaPagamento f where f.empresa =:empresa and f.vendaAPrazo = false").setParameter("empresa", empresa).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
