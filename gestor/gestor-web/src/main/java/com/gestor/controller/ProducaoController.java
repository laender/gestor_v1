/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.ClienteEJB;
import com.gestor.ejb.ProducaoEJB;
import com.gestor.entidades.Cliente;
import com.gestor.entidades.Producao;
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
public class ProducaoController implements Serializable, ICadastros {

    private Producao producao = null;

    @EJB
    private ProducaoEJB producaoEJB;
    
    @EJB
    private ClienteEJB clienteEJB;

    public ProducaoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
    }

    public void novo() {
        producao = new Producao();
        WebUtils.update("form");

    }

    public Producao getProducao() {
        return producao;
    }

    public void setProducao(Producao producao) {
        this.producao = producao;
        WebUtils.update("form");
    }

    public ProducaoEJB getProducaoEJB() {
        return producaoEJB;
    }

    public void setProducaoEJB(ProducaoEJB producaoEJB) {
        this.producaoEJB = producaoEJB;
    }

    public List<Producao> listarProducoes() {
        return producaoEJB.findAll();
    }
    
     public List<Cliente> listarClientes() {
        return clienteEJB.findAll(WebUtils.getEmpresa());
    }

    public void salvar() {
        try {
            if (producao == null) {
                WebUtils.messageWarn("Nenhuma produção informada");
                return;
            }
            if(Utils.empty(producao.getObservacao())){
                WebUtils.messageWarn("Informe a descrição da produçao");
                return;
            }
            producaoEJB.merge(producao);
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
        producao = null;
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
            producaoEJB.remove(producao);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

}
