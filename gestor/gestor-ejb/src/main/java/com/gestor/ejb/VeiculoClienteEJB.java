/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.VeiculoCliente;
import java.util.List;
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
public class VeiculoClienteEJB extends AbstractEJB<VeiculoCliente> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VeiculoClienteEJB() {
        super(VeiculoCliente.class);
    }

    public List<VeiculoCliente> listarVeiculos(Empresa empresa, Cliente cliente) {
        String sql = "select v from VeiculoCliente v where v.cliente.empresa =:empresa and v.ativo = TRUE ";
        if (cliente != null) {
            sql += " and v.cliente =:cliente";
        }
        sql += " order by v.veiculo ";

        Query q = em.createQuery(sql);
        q.setParameter("empresa", empresa);
        if (cliente != null) {
            q.setParameter("cliente", cliente);
        }
        try {
            return (List<VeiculoCliente>) q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
