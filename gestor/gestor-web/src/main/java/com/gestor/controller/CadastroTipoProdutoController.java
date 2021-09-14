/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.TipoProdutoEJB;
import com.gestor.entidades.TipoProduto;
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
public class CadastroTipoProdutoController implements Serializable, ICadastros {

    private TipoProduto tipoProduto = null;

  //  private List<TipoProduto> tipoProdutos;

    @EJB
    private TipoProdutoEJB tipoProdutoEJB;

    public CadastroTipoProdutoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
      //  tipoProdutos = this.listarTipoProdutos();
    }

    public void novo() {
        tipoProduto = new TipoProduto();
        Usuario context = WebUtils.getUsuario();
        tipoProduto.setEmpresa(context.getEmpresa());
        WebUtils.update("form");

    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
        WebUtils.update("form");

    }

    public List<TipoProduto> listarTipoProdutos() {
        return tipoProdutoEJB.getTipoProdutos(WebUtils.getEmpresa());
    }

   // public void setTipoProdutos(List<TipoProduto> tipoProdutos) {
   //     this.tipoProdutos = tipoProdutos;
   // }

    public void salvar() {
        try {
            if (tipoProduto == null) {
                WebUtils.messageWarn("Nenhum tipo de Produto Informado");
                return;
            }
            if (Utils.empty(tipoProduto.getDescricao())) {
                WebUtils.messageWarn("Descricao nao informada");
                return;
            }
            if (tipoProduto.isPadrao()) {
                WebUtils.messageError("Tipo de produto padrao do sistema, alteracao nao permitida ");
                return;
            }

            tipoProdutoEJB.merge(tipoProduto);
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
        tipoProduto = null;
        WebUtils.update("form");
    }

    @Override
    public void fechar() {
        cancelar();
        WebUtils.redirect("../menu");
        WebUtils.update("form");
    }

    public void excluir() {
        if (tipoProduto.isPadrao()) {
            WebUtils.messageError("Tipo de produto padrao do sistema, exclusao nao permitida ");
            return;
        }
        try {
            tipoProdutoEJB.excluir(tipoProduto);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir.  " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }


}
