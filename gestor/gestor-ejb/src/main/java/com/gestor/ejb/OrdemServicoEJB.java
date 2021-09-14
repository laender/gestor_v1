/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.OrdemServico;
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
public class OrdemServicoEJB extends AbstractEJB<OrdemServico> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private EstoqueEJB estoqueEJB;

    @EJB
    private TituloEJB tituloEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrdemServicoEJB() {
        super(OrdemServico.class);
    }

    public List<OrdemServico> getOrdemServicosAbertas(Empresa empresa) {

        try {
            return (List<OrdemServico>) em.createQuery("select o from OrdemServico o where o.usuario.empresa =:empresa and o.concluida = FALSE order by o.id desc")
                    .setParameter("empresa", empresa)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public List<OrdemServico> getOrdemServicosConcluidas(Empresa empresa) {

        try {
            return (List<OrdemServico>) em.createQuery("select o from OrdemServico o where o.usuario.empresa =:empresa and o.concluida = TRUE order by o.id desc")
                    .setParameter("empresa", empresa)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
