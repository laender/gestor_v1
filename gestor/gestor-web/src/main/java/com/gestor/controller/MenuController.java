/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.ClienteEJB;
import com.gestor.ejb.EstoqueEJB;
import com.gestor.ejb.OrcamentoEJB;
import com.gestor.ejb.TituloEJB;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Estoque;
import com.gestor.entidades.TituloParcela;
import com.gestor.util.ClienteRetorno;
import com.gestor.util.Notificacao;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class MenuController implements Serializable {

    @EJB
    private TituloEJB tituloEJB;

    @EJB
    private EstoqueEJB estoqueEJB;

    @EJB
    private ClienteEJB clienteEJB;

    @EJB
    private OrcamentoEJB orcamentoEJB;

    private List<TituloParcela> parcelasVencidas;
    private List<Estoque> sugestoesCompra;
    private List<ClienteRetorno> retornos;
    private List<Notificacao> notificacoes;
    private String colorMsgAviso = "blue";
    private String msgAviso = null;

    @PostConstruct
    public void init() {
        Date dataInicial = null;
        Empresa empresa = WebUtils.getEmpresa();
        Date dataFinal = new Date();
        List<Notificacao> notificacoesEstoque = null;
        boolean empresaControlaEstoque = empresa.isControlaEstoque();
 
        Notificacao notFaturamento = orcamentoEJB.getNotificacaoFaturamentoPeriodoAnterior(empresa);
        List<Notificacao> notifCliente = clienteEJB.getNotificacoesCliente(empresa);

        parcelasVencidas = tituloEJB.listarParcelas(dataInicial, dataFinal, true, null, false, null, empresa, null, null, null);
        if (empresaControlaEstoque) {
            sugestoesCompra = estoqueEJB.getSugestaoCompra(empresa);
            notificacoesEstoque = estoqueEJB.getNotificacoesEstoque(empresa);
        }
        retornos = clienteEJB.listarRetornosPrevistos(empresa);

        List<Notificacao> notAniversario = clienteEJB.getNotificacaoClienteAniversario(empresa);
        notificacoes = new ArrayList<>();
        if (notFaturamento != null) {
            notificacoes.add(notFaturamento);
        }

        if (!Utils.empty(notificacoesEstoque)) {
            notificacoes.addAll(notificacoesEstoque);
        }
        if (!Utils.empty(notifCliente)) {
            notificacoes.addAll(notifCliente);
        }
        if (!Utils.empty(notAniversario)) {
            notificacoes.addAll(notAniversario);
        }
        this.atualizarMsgAviso();

    }

    public List<TituloParcela> getParcelasVencidas() {
        return parcelasVencidas;
    }

    public void setParcelasVencidas(List<TituloParcela> parcelasVencidas) {
        this.parcelasVencidas = parcelasVencidas;
    }

    public List<Estoque> getSugestoesCompra() {
        return sugestoesCompra;
    }

    public void setSugestoesCompra(List<Estoque> sugestoesCompra) {
        this.sugestoesCompra = sugestoesCompra;
    }

    public Date recuperaDataVcto(Empresa empresa) {
        Date date = empresa.getDataInicioEfetivo();
        Calendar cal = Calendar.getInstance();
        if (date == null) {
            date = empresa.getDataInicioTestes();
        }
        if (date == null) {
            return null;
        }
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public List<ClienteRetorno> getRetornos() {
        return retornos;
    }

    public void setRetornos(List<ClienteRetorno> retornos) {
        this.retornos = retornos;
    }

    public String getPrazo(Integer prazo) {
        if (prazo > 0) {
            return "em " + prazo + " dias";
        } else if (prazo == 0) {
            return "hoje";
        }
        return "";
    }

    public List<Notificacao> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<Notificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }

    public String getCorLabelNotFaturamento(Notificacao notificacao) {
        if (notificacao.getCliente() != null || notificacao.getProduto() != null) {
            return "blue";
        }
        if (notificacao.isFaturamentoAumentou()) {
            return "green";
        } else if (!notificacao.isFaturamentoIgual()) {
            return "red";
        }
        return "blue";
    }

    public String atualizarMsgAviso() {
        Empresa empresa = WebUtils.getEmpresa();
        Date dtVctTesteMens = this.recuperaDataVcto(WebUtils.getEmpresa());
        Date hoje = Calendar.getInstance().getTime();
        String dtVcto = new SimpleDateFormat("dd/MM/yyyy").format(dtVctTesteMens);
        msgAviso = "";
        if (empresa.getDataInicioEfetivo() == null) {
            if (dtVctTesteMens.before(Calendar.getInstance().getTime())) {
                colorMsgAviso = "red";
                msgAviso = "Período de testes expirou em " + new SimpleDateFormat("dd/MM/yyyy").format(dtVctTesteMens);
            } else {
                colorMsgAviso = "blue";
                msgAviso = "Período de testes expira em: " + dtVcto;

            }
        } else {
            colorMsgAviso = "blue";
            msgAviso = "Vencimento da mensalidade atual: " + dtVcto;
            if (dtVctTesteMens.before(hoje)) {
                colorMsgAviso = "red";
                int dif = Utils.diffDate(dtVctTesteMens, hoje, Calendar.DAY_OF_YEAR);
                msgAviso += " ** Vencido há " + dif + " dias **";
            }
        }
        return msgAviso;
    }

    public String colorMsgAviso() {
        return colorMsgAviso;
    }

    public String getMsgAviso() {
        return msgAviso;
    }

    public void setMsgAviso(String msgAviso) {
        this.msgAviso = msgAviso;
    }
    

}
