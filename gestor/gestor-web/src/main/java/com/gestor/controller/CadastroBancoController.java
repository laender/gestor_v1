/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.BancoEJB;
import com.gestor.entidades.Banco;
import com.gestor.entidades.Usuario;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.util.List;
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
public class CadastroBancoController implements Serializable, ICadastros {

    private Banco banco = null;

    @EJB
    private BancoEJB bancoEJB;

    public CadastroBancoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
    }

    public void novo() {
        banco = new Banco();
        Usuario context = WebUtils.getUsuario();
        banco.setEmpresa(context.getEmpresa());
        WebUtils.update("form");

    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
        WebUtils.update("form");

    }

    public List<Banco> listarBancos() {
        return bancoEJB.findAll(WebUtils.getUsuario().getEmpresa());
    }


    public void salvar() {
        try {
            if (banco == null) {
                WebUtils.messageWarn("Nenhum banco informado");
                return;
            }
            if (Utils.empty(banco.getDescricao())) {
                WebUtils.messageWarn("Descrição não informada");
                return;
            }

            bancoEJB.merge(banco);
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
        banco = null;
        WebUtils.update("form");
    }

    @Override
    public void fechar() {
        cancelar();
        WebUtils.redirect("../menu");
        WebUtils.update("form");
    }

    public void excluir() {
        try {
            bancoEJB.excluir(banco);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

 

}
