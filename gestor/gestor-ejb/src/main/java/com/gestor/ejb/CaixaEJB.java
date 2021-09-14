/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Caixa;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Usuario;
import com.gestor.util.GestorException;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author laender
 */
@Stateless
@LocalBean
public class CaixaEJB extends AbstractEJB<Caixa> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CaixaEJB() {
        super(Caixa.class);
    }

    public Caixa getCaixaAberto(Empresa empresa) throws GestorException {
        try {
            Caixa caixa = (Caixa) em.createQuery("select c from Caixa c where c.aberto = TRUE and c.usuarioAbertura.empresa =:empresa")
                    .setParameter("empresa", empresa)
                    .getSingleResult();
            return caixa;
        } catch (NoResultException e) {
            throw new GestorException("Nenhum caixa aberto para lançamentos");
        }
    }

    public Caixa getUltimoCaixaFechado(Empresa empresa) {
        try {
            return (Caixa) em.createQuery("select c from Caixa c where c.usuarioAbertura.empresa =:empresa and c.aberto = FALSE order by c.id desc")
                    .setParameter("empresa", empresa)
                    .setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            //   e.printStackTrace();
        }
        return null;
    }

    public Caixa getCaixaDiaAberto(Empresa empresa, Date data) throws GestorException {
        try {
            Caixa caixa = (Caixa) em.createQuery("select c from Caixa c where c.usuarioAbertura.empresa =:empresa and c.dataFechamento =:dataFechamento")
                    .setParameter("empresa", empresa)
                    .setParameter("dataFechamento", data)
                    .getSingleResult();
            return caixa;
        } catch (NoResultException e) {
            throw new GestorException("Nenhum caixa aberto");
        }
    }

    public List<Caixa> getCaixas(Empresa empresa) {
        try {
            return (List<Caixa>) em.createQuery("select c from Caixa c where c.usuarioAbertura.empresa =:empresa order by c.id desc")
                    .setParameter("empresa", empresa)
                    .getResultList();
        } catch (NoResultException e) {
        }
        return null;
    }

    public List<Caixa> getCaixas(Empresa empresa, Date dataInicial, Date dataFinal, Usuario usuario) {
        String sql = "select o from Caixa o where o.usuarioAbertura.empresa =:empresa  ";
        if (dataInicial != null) {
            sql += " and o.dataAbertura >= :dataInicial ";
        }
        if (dataFinal != null) {
            sql += " and o.dataAbertura <= :dataFinal ";
        }
        if (usuario != null) {
            sql += " and o.usuarioFechamento =:usuario";
        }
        sql += " order by o.dataAbertura ";

        try {
            Query q = em.createQuery(sql);
            q.setParameter("empresa", empresa);
            if (dataInicial != null) {
                q.setParameter("dataInicial", dataInicial);
            }
            if (dataFinal != null) {
                q.setParameter("dataFinal", dataFinal);
            }
            if (usuario != null) {
                q.setParameter("usuario", usuario);
            }

            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void fecharCaixa(Caixa caixa, Usuario usuario) {
        caixa.setAberto(false);
        caixa.setUsuarioFechamento(usuario);
        caixa.setDataFechamento(new Date());
        caixa.setValorFinal(caixa.getSaldo());
        this.merge(caixa);
    }

    public void reabrirCaixa(Caixa caixa, Empresa empresa) throws GestorException {
        Caixa caixaAberto = null;
        try {
            caixaAberto = (Caixa) em.createQuery("select c from Caixa c where c.aberto = TRUE and c.usuarioAbertura.empresa =:empresa")
                    .setParameter("empresa", empresa)
                    .getSingleResult();

        } catch (NoResultException e) {
            //   e.printStackTrace();
        }
        if (caixaAberto != null) {
            throw new GestorException("Existe um caixa aberto, é necessário fechar para reabrir outro");
        }
        caixa.setAberto(true);
        caixa.setUsuarioFechamento(null);
        caixa.setSaldo(null);

        em.merge(caixa);

    }
    
    public List<Caixa> getCaixas(Empresa empresa, Date data) {
        String sql = "select o from Caixa o where o.usuarioAbertura.empresa =:empresa  ";
        if (data != null) {
            sql += " and o.dataAbertura >= :data ";
            sql += " and (o.dataFechamento <= :data or o.dataFechamento = NULL)";
        }
        sql += " order by o.dataAbertura ";

        try {
            Query q = em.createQuery(sql);
            q.setParameter("empresa", empresa);
            if (data != null) {
                q.setParameter("data", data);
            }

            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
