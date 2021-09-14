/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.FormaPagamentoEJB;
import com.gestor.entidades.FormaPagamento;
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
public class CadastroFormaPagamentoController implements Serializable, ICadastros {

    private FormaPagamento formaPagamento = null;

    private List<FormaPagamento> formaPagamentos;

    @EJB
    private FormaPagamentoEJB formaPagamentoEJB;

    public CadastroFormaPagamentoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        formaPagamentos = this.getFormaPagamentos();
    }

    public void novo() {
        formaPagamento = new FormaPagamento();
        Usuario context = WebUtils.getUsuario();
        formaPagamento.setEmpresa(context.getEmpresa());
        formaPagamento.setNumeroParcelas(0);
        formaPagamento.setDiasVctoPrimeiraParcela(30);
        formaPagamento.setIntervaloParcelas(30);
        WebUtils.update("form");

    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
        WebUtils.update("form");

    }

    public List<FormaPagamento> getFormaPagamentos() {
        return formaPagamentoEJB.findAll(WebUtils.getUsuario().getEmpresa());
    }

    public void setFormaPagamentos(List<FormaPagamento> formaPagamentos) {
        this.formaPagamentos = formaPagamentos;
    }

    public void salvar() {
        try {
            if (!isValido()) {
                return;
            }
            formaPagamentoEJB.merge(formaPagamento);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
    }

    private boolean isValido() {
        if (formaPagamento == null) {
            WebUtils.messageWarn("Nenhuma forma de Pagamento Informada");
            return false;
        }
        if (Utils.empty(formaPagamento.getDescricao())) {
            WebUtils.messageWarn("Descrição não informada");
            return false;
        }
        if (formaPagamento.isVendaAPrazo() && formaPagamento.getNumeroParcelas() != null && formaPagamento.getNumeroParcelas() <= 0) {
            WebUtils.messageWarn("Deve ser informado um numero de parcelas para venda a vista");
            return false;
        }
        if (!formaPagamento.isVendaAPrazo() && formaPagamento.getNumeroParcelas() != null && formaPagamento.getNumeroParcelas() > 0) {
            WebUtils.messageWarn("Para venda a vista nao deve ser informado um numero de parcelas");
            return false;
        }
        if (formaPagamento.isVendaAPrazo()) {
            if (formaPagamento.getNumeroParcelas() == null || formaPagamento.getNumeroParcelas() <= 0) {
                WebUtils.messageWarn("Informe um numero de parcelas para vende a prazo");
                return false;
            }
            if (formaPagamento.getDiasVctoPrimeiraParcela() <= 0) {
                WebUtils.messageWarn("Informe um numero de dias a partir de hoje para o vencimento da primeira parcela (sugerido 30) ou altere para venda a vista");
                return false;
            }
        }
        if (formaPagamento.getNumeroParcelas() != null && formaPagamento.getNumeroParcelas() > 99) {
            WebUtils.messageWarn("Numero de parcelas acima de 99 não permitido, verifique o numero informado");
            return false;
        }
        if (!formaPagamento.isVendaAPrazo()) {
            if (formaPagamento.getNumeroParcelas() == null) {
                formaPagamento.setNumeroParcelas(0);
            }
            if (formaPagamento.getIntervaloParcelas() == null) {
                formaPagamento.setIntervaloParcelas(0);
            }
            if (formaPagamento.getIntervaloParcelas() == null) {
                formaPagamento.setIntervaloParcelas(0);
            }
        }
        return true;
    }

    @Override
    public void cancelar() {
        formaPagamento = null;
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
            formaPagamentoEJB.excluir(formaPagamento);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir o tipo de Pagamento. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

}
