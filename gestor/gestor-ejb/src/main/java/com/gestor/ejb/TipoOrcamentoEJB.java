/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Orcamento;
import com.gestor.entidades.TipoOrcamento;
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
public class TipoOrcamentoEJB extends AbstractEJB<TipoOrcamento>  {
    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private OrcamentoEJB orcamentoEJB;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoOrcamentoEJB() {
        super(TipoOrcamento.class);
    }
    
    public void excluir(TipoOrcamento tipoOrcamento ) throws GestorException{
        List<Orcamento> orcamentos = orcamentoEJB.getOrcamentosPorTipo(tipoOrcamento);
        if(!Utils.empty(orcamentos)){
            throw new GestorException("Exclusao nao permitida. Tipo de projeto ja vinculado à um orcamento");
        }else{
            this.remove(tipoOrcamento);
        }
    }
    
}
