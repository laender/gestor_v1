/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.LancamentoFinanceiroEJB;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class EstornoLancamentoController implements Serializable, ICadastros {

    private LancamentoFinanceiro lancamentoFinanceiro = null;

    @EJB
    private LancamentoFinanceiroEJB lancamentoFinanceiroEJB;

    private Empresa empresa;

    public EstornoLancamentoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        empresa = WebUtils.getEmpresa();
    }

    public void novo() {

    }

    public LancamentoFinanceiro getLancamentoFinanceiro() {
        return lancamentoFinanceiro;
    }

    public void setLancamentoFinanceiro(LancamentoFinanceiro lancamentoFinanceiro) {
        this.lancamentoFinanceiro = lancamentoFinanceiro;
        lancamentoFinanceiro.setObservacao(null);
        WebUtils.update("form");
    }

    public List<LancamentoFinanceiro> getLancamentoFinanceiros() {
        return lancamentoFinanceiroEJB.getLancamentos(empresa, null);
    }

    public void salvar() {
        try {
            if (lancamentoFinanceiro == null) {
                WebUtils.messageWarn("Nenhum lançamento informado");
                return;
            }
            if (lancamentoFinanceiro.getValor() == null || lancamentoFinanceiro.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                WebUtils.messageWarn("Valor não infomado");
                return;
            }
            if (Utils.empty(lancamentoFinanceiro.getObservacao())) {
                WebUtils.messageWarn("Informe uma observação para o estorno");
                return;
            }

            lancamentoFinanceiroEJB.excluirLancamento(lancamentoFinanceiro);
            
            WebUtils.update("form");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtils.messageError("Erro ao salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
    }

    @Override
    public void cancelar() {
        lancamentoFinanceiro = null;
        WebUtils.update("form");
    }

    @Override
    public void fechar() {
        cancelar();
        WebUtils.redirect("../menu");
        WebUtils.update("form");
    }

}
