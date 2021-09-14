/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.entidades.Usuario;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author laender
 */
@ManagedBean
@ViewScoped
public class CadastrosController implements Serializable {

    /**
     * Creates a new instance of CadastrosController
     */
    public CadastrosController() {
    }

    public void init() {
        Usuario usuario = WebUtils.getUsuario();

        if (usuario == null) {
            WebUtils.messageError("Usuário do Contexto não definido");
            WebUtils.redirect("menu");
            return;
        }
        if (!usuario.isAdministrador()) {
            WebUtils.messageError("Usuário não é administrador do sistema. Acesso aos cadastros não autorizado");
            WebUtils.redirect("menu");

        }

    }

    public void fechar() {
        WebUtils.redirect("../menu");
    }


}
