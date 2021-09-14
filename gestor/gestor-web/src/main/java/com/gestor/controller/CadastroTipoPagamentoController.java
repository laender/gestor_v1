/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.TipoPagamentoEJB;
import com.gestor.entidades.TipoPagamento;
import com.gestor.entidades.Usuario;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author laender
 */
@ManagedBean
@ViewScoped
public class CadastroTipoPagamentoController implements Serializable, ICadastros {

    private TipoPagamento tipoPagamento = null;

    private List<TipoPagamento> tipoPagamentos;

    @EJB
    private TipoPagamentoEJB tipoPagamentoEJB;

    public CadastroTipoPagamentoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        tipoPagamentos = this.getTipoPagamentos();
    }

    public void novo() {
        tipoPagamento = new TipoPagamento();
        Usuario context = WebUtils.getUsuario();
        tipoPagamento.setEmpresa(context.getEmpresa());
        WebUtils.update("form");

    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
        WebUtils.update("form");

    }

    public List<TipoPagamento> getTipoPagamentos() {
        return tipoPagamentoEJB.findAll(WebUtils.getUsuario().getEmpresa());
    }

    public void setTipoPagamentos(List<TipoPagamento> tipoPagamentos) {
        this.tipoPagamentos = tipoPagamentos;
    }

    public void salvar() {
        try {
            if (tipoPagamento == null) {
                WebUtils.messageWarn("Nenhum tipo de Pagamento Informado");
                return;
            }
            if (Utils.empty(tipoPagamento.getDescricao())) {
                WebUtils.messageWarn("Descrição não informada");
                return;
            }

            tipoPagamentoEJB.merge(tipoPagamento);
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
        tipoPagamento = null;
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
            tipoPagamentoEJB.excluir(tipoPagamento);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir o tipo de Pagamento. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

 

}
