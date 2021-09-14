/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.SystemUtils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author laender
 */
@ManagedBean
@ViewScoped
public class BackupRestoreController implements Serializable {

    @EJB
    private SystemUtils systemUtilsEJB;

    public BackupRestoreController() {
    }

    @PostConstruct
    private void init() {

    }

    public void backup() {
        try {
         //   systemUtilsEJB.efetuarBackupPostgres();
            WebUtils.messageInfo("Backup realizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtils.messageError("Erro ao realizar o backup "+ e.toString());
        }
    }

    public void restore() {
        try {
           // systemUtilsEJB.realizaRestorePostgres();
            WebUtils.messageInfo("Restore realizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtils.messageError("Erro ao realizar o restore "+ e.toString());
        }
    }

    public void fechar() {
        WebUtils.redirect("../menuAdmin");
        WebUtils.update("form");
    }

}
