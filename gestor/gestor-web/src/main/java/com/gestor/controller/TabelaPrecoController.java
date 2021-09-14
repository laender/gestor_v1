/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.TabelaPrecoEJB;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Produto;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TabelaPrecoItem;
import com.gestor.entidades.Usuario;
import com.gestor.util.GestorException;
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
public class TabelaPrecoController implements Serializable, ICadastros {

    private TabelaPreco tabelaPreco = null;
    private Produto produto;

    @EJB
    private TabelaPrecoEJB tabelaPrecoEJB;
    @EJB
    private ProdutoEJB produtoEJB;

    private Empresa empresa;

    public TabelaPrecoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();

        Usuario context = WebUtils.getUsuario();
        empresa = context.getEmpresa();
    }

    public void novo() {
        tabelaPreco = new TabelaPreco();
        tabelaPreco.setEmpresa(empresa);
        WebUtils.update("form");

    }

    public TabelaPreco getTabelaPreco() {
        return tabelaPreco;
    }

    public void setTabelaPreco(TabelaPreco tabelaPreco) {
        this.tabelaPreco = tabelaPreco;
        WebUtils.update("form");

    }

    public List<TabelaPreco> getTabelaPrecos() {
        return tabelaPrecoEJB.findAll(empresa);
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void addProduto(Produto produto) {
        List<TabelaPrecoItem> itens = tabelaPreco.getTabelaPrecoItemList();
        if (itens != null && !itens.isEmpty()) {
            for (TabelaPrecoItem tpi : itens) {
                Produto i = tpi.getProduto();
                if (i.equals(produto)) {
                    return;
                }
            }
        }
        TabelaPrecoItem itemNovo = new TabelaPrecoItem();
        itemNovo.setProduto(produto);
        itemNovo.setTabelaPreco(tabelaPreco);
        tabelaPreco.getTabelaPrecoItemList().add(itemNovo);
        // WebUtils.update("form");
    }

    public void removeItem(TabelaPrecoItem item) {
        List<TabelaPrecoItem> itens = tabelaPreco.getTabelaPrecoItemList();
        for (TabelaPrecoItem tabelaPrecoItem : itens) {
            if (tabelaPrecoItem.getProduto().equals(item.getProduto())) {
                itens.remove(tabelaPrecoItem);
                break;
            }
        }

        try {
            tabelaPrecoEJB.removeItem(item);
            WebUtils.messageInfo("Item removido");
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir " + e.getMessage());
        }

    }

    public List<Produto> getProdutos() {
        return produtoEJB.findAll(empresa);
    }

    public void salvar() {
        try {
            if (tabelaPreco == null) {
                WebUtils.messageWarn("Nenhuma tabela de preço informada");
                return;
            }
            if (Utils.empty(tabelaPreco.getDescricao())) {
                WebUtils.messageWarn("Descrição não informada");
                return;
            }
            if (Utils.empty(tabelaPreco.getTabelaPrecoItemList())) {
                WebUtils.messageWarn("Nenhum item informado");
                return;
            }

            for (TabelaPrecoItem tabelaPrecoItem : tabelaPreco.getTabelaPrecoItemList()) {
                if (!Utils.empty(tabelaPrecoItem.getPercMaxDesc()) && tabelaPrecoItem.getPercMaxDesc().compareTo(BigDecimal.valueOf(100)) > 0) {
                    WebUtils.messageWarn("O percentual máximo de desconto não pode ultrapassar 100%");
                    return;
                }
                if (!Utils.empty(tabelaPrecoItem.getValorMaxDesc()) && tabelaPrecoItem.getValorMaxDesc().compareTo(tabelaPrecoItem.getValorCompra()) > 0) {
                    WebUtils.messageWarn("O valor máximo de desconto não pode ultrapassar o valor de compra");
                    return;
                }
                BigDecimal valorVenda = tabelaPrecoItem.getValorVenda();
                BigDecimal valorCompra = tabelaPrecoItem.getValorCompra();

                if (!Utils.empty(valorVenda) && !Utils.empty(valorCompra) && valorVenda.compareTo(valorCompra) < 0) {
                    WebUtils.messageWarn("O valor de venda não pode ser menor do que o valor de compra");
                    return;
                }
                if (!Utils.empty(tabelaPrecoItem.getValorMaxDesc()) && !Utils.empty(tabelaPrecoItem.getPercMaxDesc())) {
                    WebUtils.messageWarn("Informe um percentual maximo de desconto ou valor maximo de desconto");
                }
                List<TabelaPreco> tabelasExistentes = tabelaPrecoEJB.findAll(empresa);
                if (!Utils.empty(tabelasExistentes)) {
                    for (TabelaPreco tabelaExistente : tabelasExistentes) {
                        if (tabelaExistente.getPadrao() && tabelaPreco.getPadrao()) {
                            tabelaExistente.setPadrao(Boolean.FALSE);
                            tabelaPrecoEJB.merge(tabelaExistente);
                            WebUtils.messageInfo("A nova tabela passou a ser padrão");
                            tabelaPreco.setPadrao(Boolean.TRUE);
                        }
                    }

                } else if (!tabelaPreco.getPadrao()) {
                    tabelaPreco.setPadrao(Boolean.TRUE);

                }
            }

            tabelaPrecoEJB.merge(tabelaPreco);
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
        tabelaPreco = null;
        produto = null;
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

            tabelaPrecoEJB.removeTabelaPreco(tabelaPreco);
        } catch (GestorException e) {
            WebUtils.messageError("Erro ao excluir o registro. " + e.getMessage());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

}
