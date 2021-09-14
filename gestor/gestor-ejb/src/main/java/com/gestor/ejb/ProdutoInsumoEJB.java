/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.Produto;
import com.gestor.entidades.ProdutoInsumo;
import com.gestor.entidades.Usuario;
import java.util.List;
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
public class ProdutoInsumoEJB extends AbstractEJB<ProdutoInsumo> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProdutoInsumoEJB() {
        super(ProdutoInsumo.class);
    }

    public List<Produto> getProdutoInsumos(Empresa empresa) {

        try {
            return (List<Produto>) em.createQuery("select distinct p.produto from ProdutoInsumo p where p.produto.empresa =:empresa order by p.produto.descricao")
                    .setParameter("empresa", empresa)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<ProdutoInsumo> getProdutoInsumos(Produto produto) {

        try {
            return (List<ProdutoInsumo>) em.createQuery("select p from ProdutoInsumo p where p.produto =:produto")
                    .setParameter("produto", produto)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
