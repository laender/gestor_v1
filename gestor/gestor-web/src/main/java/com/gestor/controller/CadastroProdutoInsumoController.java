/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.ProdutoInsumoEJB;
import com.gestor.ejb.TabelaPrecoEJB;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Produto;
import com.gestor.entidades.ProdutoInsumo;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TabelaPrecoItem;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class CadastroProdutoInsumoController implements Serializable, ICadastros {

    private Produto produtoPrincipal = null;

    private List<ProdutoInsumo> produtoInsumos = null;
    private List<ProdutoInsumo> produtoInsumosToRemove = null;
    private ProdutoInsumo produtoInsumo = null;

    @EJB
    private ProdutoEJB produtoEJB;

    @EJB
    private ProdutoInsumoEJB produtoInsumoEJB;

    private Empresa empresa = null;

    private List<Produto> produtos;

    @EJB
    private TabelaPrecoEJB tabelaPrecoEJB;

    private TabelaPreco tabelaPreco;
    private BigDecimal totalCusto = BigDecimal.ZERO;
    private BigDecimal totalVenda = BigDecimal.ZERO;

    public CadastroProdutoInsumoController() {
    }

    @PostConstruct
    private void init() {
        empresa = WebUtils.getUsuario().getEmpresa();
        produtos = this.produtoInsumoEJB.getProdutoInsumos(empresa);
        if (!Utils.empty(produtos)) {
            for (Produto p : produtos) {
                p.setProdutoInsumos(this.produtoInsumoEJB.getProdutoInsumos(p));
            }
        }

    }

    public void novo() {
        produtoPrincipal = new Produto();
        produtoInsumo = new ProdutoInsumo();
        tabelaPreco = tabelaPrecoEJB.getTabelaPrecoPadrao(WebUtils.getEmpresa());
        WebUtils.update("form");

    }

    public List<Produto> listarProdutos() {
        return produtoEJB.getProdutosSemInsumo(empresa);
    }

    public List<Produto> getInsumos() {
        if (produtoPrincipal != null) {
            return produtoEJB.getProdutosDiferentes(produtoPrincipal, empresa);
        }
        return null;
    }

    public void addInsumo(Produto insumo) {

        List<ProdutoInsumo> insumosAdd = produtoPrincipal.getProdutoInsumos();
        ProdutoInsumo prdInsumoNovo;
        if (insumosAdd != null && !insumosAdd.isEmpty()) {
            for (ProdutoInsumo pi : insumosAdd) {
                Produto i = pi.getInsumo();
                if (i.equals(insumo)) {
                    return;
                }
            }
        } else {
            insumosAdd = new ArrayList<>();
            prdInsumoNovo = new ProdutoInsumo();
            prdInsumoNovo.setInsumo(insumo);
            prdInsumoNovo.setProduto(produtoPrincipal);
            insumosAdd.add(prdInsumoNovo);
            produtoPrincipal.setProdutoInsumos(insumosAdd);
            this.calcularValorCustoVenda();
            return;
        }
        prdInsumoNovo = new ProdutoInsumo();
        prdInsumoNovo.setInsumo(insumo);
        prdInsumoNovo.setProduto(produtoPrincipal);
        insumosAdd.add(prdInsumoNovo);
      //  this.calcularValorCustoVenda();

    }

    public void removeInsumo(ProdutoInsumo produtoInsumo) {
        List<ProdutoInsumo> insumos = produtoPrincipal.getProdutoInsumos();
        if (produtoInsumosToRemove == null || produtoInsumosToRemove.isEmpty()) {
            produtoInsumosToRemove = new ArrayList<>();
        }
        if (insumos != null && !insumos.isEmpty()) {
            for (ProdutoInsumo pi : insumos) {
                Produto i = pi.getInsumo();
                if (i.equals(produtoInsumo.getInsumo())) {
                    insumos.remove(produtoInsumo);
                    produtoInsumosToRemove.add(produtoInsumo);
                    this.calcularValorCustoVenda();
                    return;
                }
            }
        }
    }

    public void salvar() {
        try {
            if (produtoPrincipal == null) {
                WebUtils.messageWarn("Produto principal não Informado");
                return;
            }

            for (ProdutoInsumo pi : produtoPrincipal.getProdutoInsumos()) {
                produtoInsumoEJB.merge(pi);
            }
            if (produtoInsumosToRemove != null && !produtoInsumosToRemove.isEmpty()) {
                for (ProdutoInsumo produtoInsumoToRemove : produtoInsumosToRemove) {
                    produtoInsumoEJB.remove(produtoInsumoToRemove);
                }
            }

        } catch (Exception e) {
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
        init();
    }

    @Override
    public void cancelar() {
        produtoPrincipal = null;
        produtos = null;
        produtoInsumos = null;
        produtoInsumo = null;
        produtoInsumosToRemove = null;
        totalCusto = BigDecimal.ZERO;
        totalVenda = BigDecimal.ZERO;
        
        init();
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
            if (produtoPrincipal != null) {
                List<ProdutoInsumo> produtoInsumos = produtoPrincipal.getProdutoInsumos();
                if (!Utils.empty(produtoInsumos)) {
                    for (ProdutoInsumo pi : produtoInsumos) {
                        produtoInsumoEJB.remove(pi);
                    }
                }
            }

        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir o vinculo de produto e insumo " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

    public Produto getProdutoPrincipal() {
        return produtoPrincipal;
    }

    public void setProdutoPrincipal(Produto produtoPrincipal) {
        this.produtoPrincipal = produtoPrincipal;
        tabelaPreco = tabelaPrecoEJB.getTabelaPrecoPadrao(WebUtils.getEmpresa());    
        this.calcularValorCustoVenda();
        WebUtils.update("form");
    }

    public List<Produto> listarProdutosComInsumo() {
        return produtoInsumoEJB.getProdutoInsumos(empresa);
    }

    public void setProdutoInsumos(List<ProdutoInsumo> produtoInsumos) {
        this.produtoInsumos = produtoInsumos;
    }

    public ProdutoInsumo getProdutoInsumo() {
        return produtoInsumo;
    }

    public void setProdutoInsumo(Produto produto) {
        if (produto != null) {
            produtoPrincipal = produto;
            produtoPrincipal.setProdutoInsumos(produtoInsumoEJB.getProdutoInsumos(produto));
            this.calcularValorCustoVenda();
        }
    }

    public List<ProdutoInsumo> getProdutoInsumos() {
        return produtoInsumos;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public TabelaPreco getTabelaPreco() {
        return tabelaPreco;
    }

    public void setTabelaPreco(TabelaPreco tabelaPreco) {
        this.tabelaPreco = tabelaPreco;
    }

    public BigDecimal getTotalCusto() {
        return totalCusto;
    }

    public void setTotalCusto(BigDecimal totalCusto) {
        this.totalCusto = totalCusto;
    }

    public BigDecimal getTotalVenda() {
        return totalVenda;
    }

    public void setTotalVenda(BigDecimal totalVenda) {
        this.totalVenda = totalVenda;
    }
    

    public void calcularValorCustoVenda() {
        totalCusto = BigDecimal.ZERO;
        totalVenda = BigDecimal.ZERO;
        if (produtoPrincipal != null && !Utils.empty(produtoPrincipal.getProdutoInsumos())) {
            for (ProdutoInsumo produtoInsumo : produtoPrincipal.getProdutoInsumos()) {
               Produto insumo  = produtoInsumo.getInsumo();
               BigDecimal qtd  = Utils.nvl(produtoInsumo.getQuantidade(), BigDecimal.ZERO);
               if(tabelaPreco != null){
                   List<TabelaPrecoItem> tabelaPrecoItems = tabelaPreco.getTabelaPrecoItemList();
                   for (TabelaPrecoItem tpi : tabelaPrecoItems) {
                       if(tpi.getProduto().equals(insumo)){
                           BigDecimal vlrCusto = tpi.getValorCompra();
                           BigDecimal vlrVenda = tpi.getValorVenda();
                           totalCusto = totalCusto.add(vlrCusto.multiply(qtd));
                           totalVenda = totalVenda.add(vlrVenda.multiply(qtd));
                       }
                   }
               }
            }
        }

    }

}
