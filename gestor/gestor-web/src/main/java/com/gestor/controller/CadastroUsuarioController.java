/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.CidadeEJB;
import com.gestor.ejb.UsuarioEJB;
import com.gestor.entidades.Cidade;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Usuario;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class CadastroUsuarioController implements Serializable, ICadastros {

    private Usuario usuario = null;

    private List<Usuario> usuarios;

    @EJB
    private UsuarioEJB usuarioEJB;

    @EJB
    private CidadeEJB cidadeEJB;

    private Empresa empresa = null;

    public CadastroUsuarioController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        empresa = WebUtils.getUsuario().getEmpresa();
        usuarios = this.getUsuarios();
    }

    public void novo() {
        usuario = new Usuario();
        usuario.setAtivo(true);
        usuario.setAdministrador(false);
        Usuario context = WebUtils.getUsuario();
        usuario.setEmpresa(context.getEmpresa());
        WebUtils.update("form");

    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if(usuario != null && !Utils.empty(usuario.getSenha())){
            String senhaDec = new String(Utils.decrypt(usuario.getSenha().getBytes()));
            this.usuario.setSenha(senhaDec);
        } 
            
        WebUtils.update("form");

    }

    public List<Usuario> getUsuarios() {
        return usuarioEJB.getUsuarios(empresa);
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Cidade> listarCidades() {
        return cidadeEJB.findAll();
    }

    public void salvar() {
        try {
            if (usuario == null) {
                WebUtils.messageWarn("Nenhum usuario Informado");
                return;
            }

            if (Utils.empty(usuario.getNome())) {
                WebUtils.messageWarn("Nome não informado");
                return;
            }
            if (!Utils.empty(usuario.getLogin()) && Utils.empty(usuario.getSenha())) {
                WebUtils.messageWarn("A senha deve ser informada quando informado um login de acesso ao sistema");
                return;
            }

            if (usuario.getLogin() != null) {
                if (usuario.getLogin().equalsIgnoreCase("system")) {
                    WebUtils.messageWarn("Informe outro login, pois o login informado é reservado ao sistema");
                    return;
                }
                Usuario usu = usuarioEJB.getUsuario(usuario.getLogin(), empresa);
                if (usu != null && usuario.getId() == null) {
                    WebUtils.messageWarn("Usuario já Cadastrado");
                    return;
                }
                if (usu != null && usuario.getId() != null && !usu.getId().equals(usuario.getId())) {
                    WebUtils.messageWarn("Login já cadastrado para essa empresa para o usuario " + usu.getNome());
                    return;
                }

            }
            String email = usuario.getEmail();
            if (!Utils.empty(email) && !Utils.emailValido(usuario.getEmail())) {
                WebUtils.messageWarn("Email Inválido");
                return;
            }

            usuarioEJB.salvar(usuario);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
    }

    @Override
    public void cancelar() {
        usuario = null;
        WebUtils.update("form");
    }

    @Override
    public void fechar() {
        cancelar();
        WebUtils.redirect("../menu");
        WebUtils.update("form");
    }

    public void excluir() {

    }

}
