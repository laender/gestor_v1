/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.OrcamentoItem;
import com.gestor.entidades.Produto;
import com.gestor.entidades.TipoProduto;
import com.gestor.enums.EnumTipoProduto;
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
public class ProdutoEJB extends AbstractEJB<Produto> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private OrcamentoEJB orcamentoEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProdutoEJB() {
        super(Produto.class);
    }

    public List<Produto> getProdutosSemInsumo(Empresa empresa) {

        try {
            List<Produto> lista = (List<Produto>) em.createQuery("select p from Produto p where p.empresa =:empresa and not exists(select pi FROM ProdutoInsumo pi where pi.produto = p)")
                    .setParameter("empresa", empresa)
                    .getResultList();
            return lista;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Produto> getProdutosDiferentes(Produto produtoPrincipal, Empresa empresa) {
        try {
            List<Produto> lista = (List<Produto>) em.createQuery("select p from Produto p where p.empresa =:empresa and p.id <> :id")
                    .setParameter("empresa", empresa)
                    .setParameter("id", produtoPrincipal.getId())
                    .getResultList();
            return lista;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Produto> getProdutosPorTipo(TipoProduto tipoProduto) {
        try {
            List<Produto> lista = (List<Produto>) em.createQuery("select p from Produto p where  p.tipoProduto =:tipo")
                    .setParameter("tipo", tipoProduto)
                    .getResultList();
            return lista;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Produto> findAll(Empresa empresa) {
        Query q = getEntityManager().createQuery("select c from Produto c where c.empresa =:empresa order by c.descricao ").
                setParameter("empresa", empresa);
        return q.getResultList();
    }

    private List<OrcamentoItem> getOrcamentoItem(Produto produto) {
        List<OrcamentoItem> itens = orcamentoEJB.getItensPorProduto(produto);
        return itens;
    }

    public void excluir(Produto produto) throws GestorException {
        if (!Utils.empty(this.getOrcamentoItem(produto))) {
            throw new GestorException("Produto vinculado a uma venda");
        }
        this.remove(this.find(produto.getId()));
    }

    public Produto getProdutoPorCodigoBarras(Empresa empresa, String codigoBarras) {
        Query q = getEntityManager().createQuery("select c from Produto c "
                + "where c.empresa =:empresa "
                + " and c.codigoBarras =:codBarras "
                + " order by c.descricao ");
        q.setParameter("empresa", empresa);
        q.setParameter("codBarras", codigoBarras);
        try {
            return (Produto) q.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

    public Produto getProdutoPorDescricao(Empresa empresa, String descricao) {
        Query q = getEntityManager().createQuery("select c from Produto c "
                + "where c.empresa =:empresa "
                + " and c.descricao =:descricao "
                + " order by c.descricao ");
        q.setParameter("empresa", empresa);
        q.setParameter("descricao", descricao);
        try {
            return (Produto) q.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }
    
    public List<Produto> getProdutoDiferenteDeInsumos(final Empresa empresa) {
        Query q = getEntityManager().createQuery("select c from Produto c "
                + "where c.empresa =:empresa "
                + " and c.tipoProduto.enumTiipoProduto != :insumo "
                + " order by c.descricao ");
        q.setParameter("empresa", empresa);
        q.setParameter("insumo", EnumTipoProduto.INSUMO);
        try {
            return (List<Produto>) q.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

}
