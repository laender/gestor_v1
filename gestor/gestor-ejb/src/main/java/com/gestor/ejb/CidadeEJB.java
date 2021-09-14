/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Cidade;
import com.gestor.entidades.Orcamento;
import com.gestor.filtros.FiltroPaginacao;
import com.gestor.util.Utils;
import java.util.List;
import java.util.Map;
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
public class CidadeEJB extends AbstractEJB<Cidade>  {
    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CidadeEJB() {
        super(Cidade.class);
    }
    
    
    public List<Cidade> getCidadesFiltradas(FiltroPaginacao filtro) {

        return (List<Cidade>) this.criarQueryFiltro(filtro).
                setFirstResult(filtro.getPrimeiroRegistro())
                .setMaxResults(filtro.getQuantidadeRegistros())
                .getResultList();
    }

    private Query criarQueryFiltro(FiltroPaginacao filtro) {
        Map<String, Object> filters = filtro.getFiltros();
        String filtroId = (String) filters.get("id");
        String filtroNome = (String) filters.get("nome");
        String filtroEstado = (String) filters.get("estado.sigla");

        String sortField = filtro.getPropriedadeOrdenacao();
        boolean asc = filtro.isAscendente();

        String sql = "select c from Cidade c where 1 = 1   ";
        if (!Utils.empty(filtroNome)) {
            sql += " and c.nome like :nomeCidade ";
        }
        if (!Utils.empty(filtroId)) {
            sql += " and CAST(c.id as varchar) like :id ";
        }
        if (!Utils.empty(filtroEstado)) {
            sql += " and c.estado.sigla like :filtroEstado ";
        }
        if (!Utils.empty(sortField)) {
            sql += " order by c."+ sortField; 
            if (!asc) {
                sql += " desc";
            }
        } else {
            sql += "order by c.id desc";
        }
        Query q = em.createQuery(sql);
       
        if (!Utils.empty(filtroNome)) {
            q.setParameter("nomeCidade", "%" + filtroNome + "%");
        }
        if (!Utils.empty(filtroId)) {
            q.setParameter("id", "%" + filtroId + "%");
        }
        if (!Utils.empty(filtroEstado)) {
            q.setParameter("filtroEstado", "%" + filtroEstado + "%");
        }
        return q;
    }

    public int quantidadeFiltrados(FiltroPaginacao filtro) {
        Query q = this.criarQueryFiltro(filtro);
        try {
            if (!Utils.empty(q.getResultList())) {
                return q.getResultList().size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
}
