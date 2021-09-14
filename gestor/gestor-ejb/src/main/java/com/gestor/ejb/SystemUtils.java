/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.util.Utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class SystemUtils {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;


    public void verificarPeriodoTestes() {

        List<Empresa> empresas = em.createQuery("select e from Empresa e where e.dataInicioEfetivo = NULL and e.ativo = true ").getResultList();
        if (!Utils.empty(empresas)) {
            for (Empresa empresa : empresas) {
                Date dtInicioTestes = empresa.getDataInicioTestes();
                Calendar cal = Calendar.getInstance();
                cal.setTime(dtInicioTestes);
                cal.add(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date vctoTestes = cal.getTime();
                if(vctoTestes.before(Calendar.getInstance().getTime())){
                    MailBean.enviar("Período de testes expirou: "+ empresa.getNome(),
                            "Expirou em "+ new SimpleDateFormat("dd/MM/yyyy").format(vctoTestes), null);
                }
            }
        }
    }
    
    public void verificarMensalidade() {

        List<Empresa> empresas = em.createQuery("select e from Empresa e where e.dataInicioEfetivo != NULL and e.ativo = true ").getResultList();
        if (!Utils.empty(empresas)) {
            for (Empresa empresa : empresas) {
                Date dtInicioEfetivo = empresa.getDataInicioTestes();
                Calendar cal = Calendar.getInstance();
                cal.setTime(dtInicioEfetivo);
                cal.add(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date vctoMens = cal.getTime();
                if(vctoMens.after(Calendar.getInstance().getTime())){
                    MailBean.enviar("Mensalidade venceu: "+ empresa.getNome(),
                            "Venceu em "+ new SimpleDateFormat("dd/MM/yyyy").format(vctoMens), null);
                }
            }
        }
    }

}
