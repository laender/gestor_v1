/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.TipoOrcamentoEJB;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Produto;
import com.gestor.entidades.ProdutoInsumo;
import com.gestor.entidades.TipoOrcamento;
import com.gestor.entidades.Usuario;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.util.ArrayList;
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
public class CadastroTipoOrcamentoController implements Serializable, ICadastros {

    private TipoOrcamento tipoOrcamento = null;

    @EJB
    private ProdutoEJB produtoEJB;

    @EJB
    private TipoOrcamentoEJB tipoOrcamentoEJB;

    private Empresa empresa;

    public CadastroTipoOrcamentoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        Usuario context = WebUtils.getUsuario();
        empresa = context.getEmpresa();
    }

    public void novo() {
        tipoOrcamento = new TipoOrcamento();
        tipoOrcamento.setEmpresa(empresa);
        WebUtils.update("form");

    }

    public TipoOrcamento getTipoOrcamento() {
        return tipoOrcamento;
    }

    public void setTipoOrcamento(TipoOrcamento tipoOrcamento) {
        this.tipoOrcamento = tipoOrcamento;
        WebUtils.update("form");

    }

    public List<TipoOrcamento> getTipoOrcamentos() {
        return tipoOrcamentoEJB.findAll(empresa);
    }

    public void addProduto(Produto produto) {
        List<Produto> produtosVinculados = tipoOrcamento.getProdutos();

        if (produtosVinculados != null && !produtosVinculados.isEmpty()) {
            if (produtosVinculados.contains(produto)) {
                return;
            }
        }else{
            produtosVinculados = new ArrayList<>();
            produtosVinculados.add(produto);
            tipoOrcamento.setProdutos(produtosVinculados);
            return;
        }
        tipoOrcamento.getProdutos().add(produto);
    }
    
    public void removeProduto(Produto produto) {
        tipoOrcamento.getProdutos().remove(produto);
        WebUtils.update("form");
    }

    public List<Produto> getProdutos() {
        return produtoEJB.findAll(empresa);
    }

    public void salvar() {
        try {
            if (tipoOrcamento == null) {
                WebUtils.messageWarn("Nenhum tipo de Projeto Informado");
                return;
            }
            if (Utils.empty(tipoOrcamento.getDescricao())) {
                WebUtils.messageWarn("Descrição não informada");
                return;
            }

            tipoOrcamentoEJB.merge(tipoOrcamento);
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
        tipoOrcamento = null;
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
            tipoOrcamentoEJB.excluir(tipoOrcamento);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir o tipo de Projeto. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

}
