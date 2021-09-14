/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import java.text.SimpleDateFormat;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

/**
 *
 * @author laender
 * https://docs.oracle.com/javaee/7/tutorial/ejb-basicexamples004.htm
 */
@Singleton
public class TimerBean {

    final SystemUtils systemUtils = new SystemUtils();

//    @Schedule(hour = "12", persistent = false, dayOfWeek = "Mon, Tue, Wed, Thu, Fri, Sat, Sun")
//    public void avaliarPeriodoTestes() {
//        systemUtils.verificarPeriodoTestes();
//    }
//
//    @Schedule(hour = "12", persistent = false, dayOfWeek = "Mon, Tue, Wed, Thu, Fri, Sat, Sun")
//    public void avaliarMensalidade() {
//        systemUtils.verificarMensalidade();
//    }

//    @Schedule(hour = "*", minute = "*/10", persistent = false, dayOfWeek = "Mon, Tue, Wed, Thu, Fri, Sat")
//    public void verificaConexao() {
//        System.out.println("verificando conexao");
//        boolean onLine = systemUtils.conexaoOnLine();
//        if(!onLine){
//            MailBean.enviar("**ERRO na conexão: " + new SimpleDateFormat("dd/MM/yyyy HH:MM"), "o wifi ta zoado", null);
//        }
//    }
}
