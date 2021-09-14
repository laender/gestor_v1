/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.EmpresaEJB;
import com.gestor.ejb.UsuarioEJB;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Usuario;
import com.gestor.util.GestorException;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author laender
 */
@ManagedBean
@SessionScoped
public class UsuarioController implements Serializable {

    /**
     * Creates a new instance of LoginController
     */
    @EJB
    private UsuarioEJB usuarioEJB;

    private Usuario usuario = null;

    private String login, senha, loginKey;

    private Empresa empresa;

    @EJB
    private EmpresaEJB empresaEJB;

    private boolean isMobile = false;

    public void logar() {

        try {

            empresa = empresaEJB.getEmpresaPorLogin(loginKey);
            if (empresa == null) {
                WebUtils.messageWarn("Empresa não cadastrada");
                return;
            }
            if (Utils.empty(login)) {
                WebUtils.messageWarn("Informe o login");
                return;
            }
            if (Utils.empty(senha)) {
                WebUtils.messageWarn("Informe a senha");
                return;
            }
            usuario = usuarioEJB.getUsuario(login, senha, empresa);
            if (usuario == null) {
                WebUtils.messageWarn("Usuário não Cadastrado");
                return;
            }
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.sendRedirect("menu.xhtml");
        } catch (IOException ex) {
            WebUtils.messageWarn("Erro ao logar: " + ex.toString());
        } catch (GestorException ex) {
            WebUtils.messageError("Erro ao Logar: " + ex.getMessage());
        }

    }

    public void logarAdmin() {

        try {

            usuario = usuarioEJB.getUsuarioPainelAdmin(login, senha);
            if (usuario == null) {
                WebUtils.messageWarn("Usuário não cadastrado ou nao é usuario administrador do sistema");
                return;
            }
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.sendRedirect("menuAdmin.xhtml");
        } catch (IOException ex) {
            WebUtils.messageWarn("Erro ao logar: " + ex.toString());
        } catch (GestorException ex) {
            WebUtils.messageError("Erro ao Logar: " + ex.getMessage());
        }

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public String getLoginKey() {
        return loginKey;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

    public void fecharAdmin() {
        WebUtils.redirect("index.xhtml");
    }

    public boolean isIsMobile() {
        return isMobile;
    }

    public void setIsMobile(boolean isMobile) {
        this.isMobile = isMobile;
    }

   

}
