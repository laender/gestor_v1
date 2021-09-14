/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.FornecedorEJB;
import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.TabelaPrecoEJB;
import com.gestor.ejb.TipoProdutoEJB;
import com.gestor.entidades.Fornecedor;
import com.gestor.entidades.Produto;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TabelaPrecoItem;
import com.gestor.entidades.TipoProduto;
import com.gestor.enums.EnumTipoPeriodo;
import com.gestor.enums.EnumTipoProduto;
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
public class CadastroProdutoController implements Serializable, ICadastros {

    private Produto produto = null;
    private List<Produto> produtos;

    @EJB
    private ProdutoEJB produtoEJB;

    @EJB
    private FornecedorEJB fornecedorEJB;

    @EJB
    private TipoProdutoEJB tipoProdutoEJB;

    private String tipoPeriodoSelecionado;
    
    @EJB
    private TabelaPrecoEJB tabelaPrecoEJB;
    
    private List<TabelaPreco> precos;

    public CadastroProdutoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        produtos = this.listarProdutos();
    }

    public void novo() {
        produto = new Produto();
        produto.setEmpresa(WebUtils.getEmpresa());
        List<TipoProduto> tps = this.listarTipoProdutos();
        if (!Utils.empty(tps)) {
            for (TipoProduto tp : tps) {
                EnumTipoProduto enumTipoProduto = tp.getEnumTiipoProduto();
                if (enumTipoProduto != null && enumTipoProduto.equals(EnumTipoProduto.PRODUTO)) {
                    produto.setTipoProduto(tp);
                    break;
                }
            }
        }
        WebUtils.update("form");

    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        tipoPeriodoSelecionado = produto.getTipoPeriodo() != null  ? produto.getTipoPeriodo().getValue() : null;
        precos = this.listarPrecosProduto();
        WebUtils.update("form");

    }

    public List<Produto> listarProdutos() {
        return produtoEJB.findAll(WebUtils.getEmpresa());
    }

    public List<Fornecedor> listarFornecedores() {
        return fornecedorEJB.findAll(WebUtils.getEmpresa());
    }

    public List<TipoProduto> listarTipoProdutos() {
        return tipoProdutoEJB.getTipoProdutos(WebUtils.getEmpresa());
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public void salvar() {
        try {
            if (!isValido()) {
                return;
            }

            produtoEJB.merge(produto);
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
        produto = null;
        tipoPeriodoSelecionado = null;
        precos = null;
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
            produtoEJB.excluir(produto);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir o produto. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

    private boolean isValido() {
        if (produto == null) {
            WebUtils.messageWarn("Nenhum produto Informado");
            return false;
        }
        if (Utils.empty(produto.getDescricao())) {
            WebUtils.messageWarn("Descrição não informada");
            return false;
        }
        if (produto.getTipoProduto() == null) {
            WebUtils.messageWarn("Tipo de produto não informado");
            return false;
        }
        if (produto.getPeriodoRetorno() != null && produto.getTipoPeriodo() == null) {
            WebUtils.messageWarn("Deve ser informado um tipo de período.");
            return false;
        }
        if (produto.getTipoPeriodo()!= null && produto.getPeriodoRetorno() == null) {
            WebUtils.messageWarn("Deve ser informado um período.");
            return false;
        }
        if (!Utils.empty(produto.getCodigoBarras())) {
            Produto p = produtoEJB.getProdutoPorCodigoBarras(WebUtils.getEmpresa(), produto.getCodigoBarras());
            if (p != null && produto.getId() == null) {
                WebUtils.messageWarn("Ja existe um produto com o mesmo codigo de barras (" + p.getDescricao() + ")");
                return false;
            }
            if (p != null && produto.getId() != null && !produto.getId().equals(p.getId())) {
                WebUtils.messageWarn("Ja existe um produto com o mesmo codigo de barras (" + p.getDescricao() + ")");
                return false;
            }
        }
        Produto p = produtoEJB.getProdutoPorDescricao(WebUtils.getEmpresa(), produto.getDescricao());
        if (p != null && produto.getId() == null) {
            WebUtils.messageWarn("Ja existe um produto com a mesma descrição");
            return false;
        }
        if (p != null && produto.getId() != null && !produto.getId().equals(p.getId())) {
            WebUtils.messageWarn("Ja existe um produto com a mesma descrição");
            return false;
        }
        return true;
    }

    public String getTipoPeriodoSelecionado() {
        return tipoPeriodoSelecionado;
    }

    public void setTipoPeriodoSelecionado(String tipoPeriodoSelecionado) {
        this.tipoPeriodoSelecionado = tipoPeriodoSelecionado;
    }

    public List<String> listarTipoPeriodos() {
        List<String> tipos = new ArrayList<>();
        for (EnumTipoPeriodo value : EnumTipoPeriodo.values()) {
            tipos.add(value.getValue());
        }
        return tipos;
    }
    
    public void atualizarTipoPeriodo(){
        if(tipoPeriodoSelecionado != null){
            for (EnumTipoPeriodo tipoPeriodo : EnumTipoPeriodo.values()) {
                if(tipoPeriodo.getValue().equals(tipoPeriodoSelecionado)){
                    produto.setTipoPeriodo(tipoPeriodo);
                }
            }
            
        }
    }
    
    private List<TabelaPreco> listarPrecosProduto(){
        if(produto != null){
            List<TabelaPrecoItem> itens = tabelaPrecoEJB.getTabelaPrecoPorProduto(produto);
            if(itens != null && !itens.isEmpty()){
                List<TabelaPreco> tabelas = new ArrayList<>();
                for (TabelaPrecoItem item : itens) {
                    tabelas.add(item.getTabelaPreco());
                }
                return tabelas;
            }
        }
        return null;
    }

    public List<TabelaPreco> getPrecos() {
        return precos;
    }

    public void setPrecos(List<TabelaPreco> precos) {
        this.precos = precos;
    }
    
}
