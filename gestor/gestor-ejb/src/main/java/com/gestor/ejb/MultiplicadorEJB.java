/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.Multiplicador;
import com.gestor.entidades.Orcamento;
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
public class MultiplicadorEJB extends AbstractEJB<Multiplicador> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private OrcamentoEJB orcamentoEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MultiplicadorEJB() {
        super(Multiplicador.class);
    }

    public Multiplicador getMultiplicadorPadrao(Empresa empresa) {
        try {
            return (Multiplicador) em.createQuery("select m from Multiplicador m where m.empresa =:empresa and m.padrao = true").setParameter("empresa", empresa).getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public Multiplicador merge(Multiplicador multiplicador) {
        Empresa empresa = multiplicador.getEmpresa();
        Multiplicador multDb = this.getMultiplicadorPadrao(empresa);
        if (multiplicador.isPadrao()) {
            if (multDb != null) {
                if ((multiplicador.getId() != null && !multiplicador.getId().equals(multDb.getId()) || multiplicador.getId() == null)) {
                    multDb.setPadrao(false);
                    super.merge(multDb);
                }
            }
        }else if(multDb == null){
            multiplicador.setPadrao(true);
        }
        return super.merge(multiplicador);
    }

    public void excluir(Multiplicador multiplicador) throws GestorException {
        List<Orcamento> orcamentos = orcamentoEJB.getOrcamentoPorMultiplicador(multiplicador);
        if (!Utils.empty(orcamentos)) {
            throw new GestorException("Multiplicador vinculado à alguma venda ou orçamento, exclusao não permitida");
        }
        super.remove(multiplicador);
    }

}
