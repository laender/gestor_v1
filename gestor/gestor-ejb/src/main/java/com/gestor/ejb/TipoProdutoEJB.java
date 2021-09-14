/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.Produto;
import com.gestor.entidades.TipoProduto;
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
public class TipoProdutoEJB extends AbstractEJB<TipoProduto>  {
    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;
    
    @EJB
    private ProdutoEJB produtoEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoProdutoEJB() {
        super(TipoProduto.class);
    }
    
    public List<TipoProduto> getTipoProdutos(Empresa empresa) {
        try {
            return (List<TipoProduto>) em.createQuery("select distinct u from TipoProduto u where u.empresa is null OR u.empresa =:empresa  ").
                    setParameter("empresa",empresa).
                    getResultList();
        } catch (Exception ex) {
            //     ex.printStackTrace();
            return null;
        }
    }
    public void excluir(TipoProduto tipoProduto) throws GestorException{
        List<Produto> produtos = produtoEJB.getProdutosPorTipo(tipoProduto);
        if(Utils.empty(produtos)){
            this.remove(tipoProduto);
        }else{
            throw new GestorException("Exclusão nao permitida, o tipo do produto está vinculado com um ou mais produtos");
        }
    }
    
}
