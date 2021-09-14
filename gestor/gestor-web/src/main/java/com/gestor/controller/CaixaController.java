/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.CaixaEJB;
import com.gestor.ejb.LancamentoFinanceiroEJB;
import com.gestor.entidades.Caixa;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.util.GestorException;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class CaixaController implements Serializable, ICadastros {

    private Caixa caixa = null;

    @EJB
    private CaixaEJB caixaEJB;

    private Empresa empresa;

    @EJB
    private LancamentoFinanceiroEJB lancamentoFinanceiroEJB;

    private List<LancamentoFinanceiro> lancamentosFinanceiros;

    public CaixaController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        empresa = WebUtils.getEmpresa();
    }

    public void novo() {
        try {
            Caixa caixa = caixaEJB.getCaixaAberto(empresa);
            if (caixa != null) {
                WebUtils.messageWarn("Existe um caixa aberto, é necessário fechar para abrir outro");
                return;
            }
        } catch (Exception e) {
            // WebUtils.messageError(e.toString());
            // return;
        }
        caixa = new Caixa();
        caixa.setUsuarioAbertura(WebUtils.getUsuario());
        caixa.setDataAbertura(new Date());
        caixa.setAberto(Boolean.TRUE);

        Caixa ultimoCaixa = caixaEJB.getUltimoCaixaFechado(empresa);
        if (ultimoCaixa != null) {
            caixa.setValorInicial(ultimoCaixa.getValorFinal());
        }

        WebUtils.update("form");

    }

    public List<Caixa> getCaixas() {
        List<Caixa> caixas = caixaEJB.getCaixas(empresa);
        if (!Utils.empty(caixas)) {
            for (Caixa caixa : caixas) {
                Double totalEntradas = 0d;
                Double totalSaidas = 0d;
                List<LancamentoFinanceiro> lancamentos = caixa.getLancamentosFinanceiros();
                if (!Utils.empty(lancamentos)) {
                    for (LancamentoFinanceiro lancamento : lancamentos) {
                        if (lancamento.getTipoLancamento().equals(EnumTipoLancamento.ENTRADA)) {
                            totalEntradas += lancamento.getValor().doubleValue();
                        } else {
                            totalSaidas += lancamento.getValor().doubleValue();
                        }
                    }
                }
                caixa.setTotalEntradas(BigDecimal.valueOf(totalEntradas));
                caixa.setTotalSaidas(BigDecimal.valueOf(totalSaidas));

                Double saldo = totalEntradas - totalSaidas;
                Double valorInicial = caixa.getValorInicial() != null ? caixa.getValorInicial().doubleValue() : 0d;
                Double totalSaldo = saldo + valorInicial;
                caixa.setSaldo(BigDecimal.valueOf(totalSaldo));
            }
        }
        return caixas;
    }

    public void salvar() {
        try {
            if (caixa == null) {
                WebUtils.messageWarn("Nenhum caixa informado");
                return;
            }
//            List<Caixa> caixas = caixaEJB.getCaixas(empresa, caixa.getDataAbertura());
//            if(!Utils.empty(caixas)){
//                WebUtils.messageWarn("Existe um caixa para a data informada, reabra o caixa para realizar lançamentos");
//                return;
//            }
            caixaEJB.merge(caixa);

        } catch (Exception e) {
            e.printStackTrace();
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
    }

    @Override
    public void cancelar() {
        caixa = null;
        lancamentosFinanceiros = null;
        WebUtils.update("form");
    }

    @Override
    public void fechar() {
        cancelar();
        WebUtils.redirect("../menu");
        WebUtils.update("form");
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
        carregarLancamentos();
    }

    public void fecharCaixa(Caixa caixa) {
        caixaEJB.fecharCaixa(caixa, WebUtils.getUsuario());
        Date data = caixa.getDataAbertura();

        WebUtils.messageInfo("Caixa de " + new SimpleDateFormat("dd/MM/yyyy").format(data) + " Fechado");

    }

    public void carregarLancamentos() {
        lancamentosFinanceiros = lancamentoFinanceiroEJB.getLancamentosPorCaixa(caixa);
    }

    public List<LancamentoFinanceiro> getLancamentosFinanceiros() {
        return lancamentosFinanceiros;
    }

    public String getColor(BigDecimal value) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            return "red";
        } else {
            return "blue";
        }
    }

    public void reabrir(Caixa caixa) {
        try {

            caixaEJB.reabrirCaixa(caixa, empresa);
            Date data = caixa.getDataAbertura();

            WebUtils.messageInfo("Caixa de " + new SimpleDateFormat("dd/MM/yyyy").format(data) + " Reaberto");
            WebUtils.update("form");

        } catch (GestorException ex) {
            WebUtils.messageWarn(ex.getMessage());
        }

    }

}
