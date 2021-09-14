/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;


import com.gestor.entidades.Cidade;
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
public class CidadeEstadoPaisEJB {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private EmpresaEJB empresaEJB;

    


    public List<Cidade> getCidades() {
        try {
            return (List<Cidade>) em.createQuery("select c from Cidade c ").getResultList();
        } catch (Exception ee) {
            return null;
        }
    }

 
    public Cidade getCidade(Integer id) {
        return (Cidade) em.find(Cidade.class, id);
    }


    public Cidade salvarCidade(Cidade cidade) {
        return em.merge(cidade);
    }

   
}
