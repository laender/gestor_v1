/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.OrcamentoItem;
import com.gestor.entidades.Produto;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TabelaPrecoItem;
import com.gestor.util.GestorException;
import com.gestor.util.Utils;
import java.util.List;
import javax.ejb.EJB;
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
public class TabelaPrecoEJB extends AbstractEJB<TabelaPreco> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private OrcamentoEJB orcamentoEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TabelaPrecoEJB() {
        super(TabelaPreco.class);
    }

    public List<TabelaPrecoItem> getTabelaPrecoPorProduto(Produto produto) {
        try {
            return (List<TabelaPrecoItem>) em.createQuery("select tpi from TabelaPrecoItem tpi JOIN tpi.tabelaPreco tp where tpi.produto =:produto").
                    setParameter("produto", produto).getResultList();
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    public void atualizarItem(TabelaPrecoItem tabelaPrecoItem) {
        em.merge(tabelaPrecoItem);
    }

    public TabelaPreco getTabelaPrecoPadrao(Empresa empresa) {
        try {
            return (TabelaPreco) em.createQuery("select tp from TabelaPreco tp where tp.padrao = true and tp.empresa =:empresa").setParameter("empresa", empresa).
                    getSingleResult();
        } catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }
    }

    public TabelaPreco getTabelaPReco(Empresa empresa) {
        try {
            return (TabelaPreco) em.createQuery("select tp from TabelaPreco tp where tp.padrao = true and tp.empresa =:empresa").setParameter("empresa", empresa).
                    getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeTabelaPreco(TabelaPreco tabelaPreco) throws GestorException {

        List<OrcamentoItem> itens = (List<OrcamentoItem>) em.createQuery("select item from OrcamentoItem item where item.tabelaPrecoItem.tabelaPreco =:tabelaPreco").
                setParameter("tabelaPreco", tabelaPreco).
                getResultList();
        if (!Utils.empty(itens)) {
            throw new GestorException("Tabela de preço nao pode ser excluida pois existe referencia com orcamento");
        }
        TabelaPreco p = em.find(TabelaPreco.class, tabelaPreco.getId());
        em.remove(p);

    }

    public void removeItem(TabelaPrecoItem item) {

        try {
            List<OrcamentoItem> orcamentoItems = em.createQuery("select i from OrcamentoItem i where i.tabelaPrecoItem =:item").
                    setParameter("item", item).getResultList();
            if (!Utils.empty(orcamentoItems)) {
                // removendo o vinculo da tabela de preco do orcamento pq nao tem muita importancia
                for (OrcamentoItem orcamentoItem : orcamentoItems) {
                    orcamentoItem.setTabelaPrecoItem(null);
                    em.merge(orcamentoItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        em.remove(em.find(TabelaPrecoItem.class, item.getId()));

    }

}
