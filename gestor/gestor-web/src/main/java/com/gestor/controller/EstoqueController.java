/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.EstoqueEJB;
import com.gestor.ejb.FornecedorEJB;
import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.TabelaPrecoEJB;
import com.gestor.ejb.UnidadeMedidaEJB;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Estoque;
import com.gestor.entidades.Fornecedor;
import com.gestor.entidades.MovimentoEstoque;
import com.gestor.entidades.Produto;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TabelaPrecoItem;
import com.gestor.entidades.UnidadeMedida;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.enums.EnumUnidadeMedida;
import com.gestor.util.CurvaAbcEstoque;
import com.gestor.util.GestorException;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
public class EstoqueController implements Serializable, ICadastros {

    private Estoque estoque = null;
    private UnidadeMedida unidadeMedida;
    List<CurvaAbcEstoque> curvaAbcEstoques;
    @EJB
    private EstoqueEJB estoqueEJB;
    @EJB
    private ProdutoEJB produtoEJB;

    @EJB
    private FornecedorEJB fornecedorEJB;

    private String docEntradaSaida;

    private String observacao;

    private Empresa empresa = null;

    private boolean entrada = true;
    private String codigoBarras = null;
    private List<Estoque> estoques = null;
    private BigDecimal valorCompra = BigDecimal.ZERO;
    private BigDecimal valorVenda = BigDecimal.ZERO;

    private boolean listarSugestoesCompra = false;
    @EJB
    private TabelaPrecoEJB tabelaPrecoEJB;

    @EJB
    private UnidadeMedidaEJB unidadeMedidaEJB;

    private boolean atualizarValorEstoqueAntigo = true;

    private Date dataInicial;
    private Date dataFinal;

    private BigDecimal percentualMargem;

    public EstoqueController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        curvaAbcEstoques = null;
        percentualMargem = null;
        empresa = WebUtils.getUsuario().getEmpresa();

    }

    public void novo() {
        estoque = new Estoque();
        docEntradaSaida = null;
        valorCompra = BigDecimal.ZERO;
        valorVenda = BigDecimal.ZERO;
        percentualMargem = null;

        WebUtils.update("form");
    }

    public List<Estoque> listarEstoques() {
        List<Estoque> estoques = estoqueEJB.getEstoques(WebUtils.getEmpresa());
        List<Estoque> estoquesSugestao = new ArrayList<>();
        if (!Utils.empty(estoques) && listarSugestoesCompra) {
            for (Estoque estoque1 : estoques) {
                if (estoque1.getQuantidadeMinima() != null && estoque1.getQuantidade().compareTo(estoque1.getQuantidadeMinima()) < 0) {
                    estoquesSugestao.add(estoque1);
                }
            }

            return estoquesSugestao;
        }
        return estoques;
    }

    public List<Fornecedor> listarFornecedores() {
        return fornecedorEJB.findAll(empresa);
    }

    public List<Produto> listarProdutos() {
        return produtoEJB.findAll(empresa);
    }

    public List<MovimentoEstoque> listarMovimentacao() {
        return estoqueEJB.listarMovimentos(estoque);
    }

    public List<UnidadeMedida> listarUnidadeMedida() {
        return unidadeMedidaEJB.findAll();

    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
        if (estoques != null) {
            for (Estoque estoque1 : estoques) {
                if (estoque1.isSelecionado()) {
                    estoque1.setUnidadeMedida(unidadeMedida);
                    estoque1.setSelecionado(false);
                    return;
                }
            }
        }
        WebUtils.update("form");
    }

    public void salvar() {
        try {
            // na edicao sera possivel apenas alterar os valores
            if (estoque != null && estoque.getId() != null) {
                if ((valorCompra != null && valorVenda != null) && valorCompra.compareTo(valorVenda) > 0) {
                    WebUtils.messageWarn("Valor de compra maior do que o valor de venda");
                    return;
                }
                if ((estoque.getQuantidadeMinima() != null && estoque.getQuantidadeMaxima() != null)
                        && estoque.getQuantidadeMaxima().compareTo(estoque.getQuantidadeMinima()) < 0) {
                    WebUtils.messageWarn("A quantidade maxima deve ser maior que a quantidade minima");
                    return;
                }
                this.atualizarValorEstoque(estoque.getProduto(), valorCompra, valorVenda);
                // estoqueEJB.salvar(estoque, docEntradaSaida, WebUtils.getUsuario(), entrada ? EnumTipoLancamento.ENTRADA : EnumTipoLancamento.SAIDA);
                estoqueEJB.merge(estoque);
                WebUtils.messageInfo("Estoque atualizado");
                cancelar();
                return;
            }
            if (estoques == null) {
                WebUtils.messageWarn("Estoque não informado");
                return;
            }
            for (Estoque estoqueTela : estoques) {
                if (estoqueTela.getQuantidade() == null || estoqueTela.getQuantidade().compareTo(BigDecimal.ZERO) == 0) {
                    WebUtils.messageWarn("Exite um item na lista sem quantidade. Verifique");
                    return;
                }
                if (estoqueTela.getProduto() == null) {
                    WebUtils.messageWarn("Produto não informado");
                    return;
                }
                if (estoqueTela.getUnidadeMedida() == null) {
                    WebUtils.messageWarn("Unidade de medida nao informada");
                    return;
                }
                if ((estoqueTela.getValorUnitCompra() != null && estoqueTela.getValorUnitVenda() != null)
                        && (estoqueTela.getValorUnitCompra().compareTo(BigDecimal.ZERO) > 0
                        && estoqueTela.getValorUnitVenda().compareTo(BigDecimal.ZERO) > 0)) {
                    if (estoqueTela.getValorUnitCompra().compareTo(estoqueTela.getValorUnitVenda()) > 0) {
                        WebUtils.messageWarn("O valor de compra do produto " + estoqueTela.getProduto().getDescricao() + " deve ser menor ou igual ao valor de venda");
                        return;
                    }
                }
                Estoque estoqueBd = estoqueEJB.getEstoquePorProduto(estoqueTela.getProduto());
                if (estoqueBd != null) {
                    estoqueBd.setQuantidade(estoqueBd.getQuantidade().add(estoqueTela.getQuantidade()));
                    this.atualizarValorEstoque(estoqueBd.getProduto(), estoqueTela.getValorUnitCompra(), estoqueTela.getValorUnitVenda());

                } else {
                    this.atualizarValorEstoque(estoqueTela.getProduto(), estoqueTela.getValorUnitCompra(), estoqueTela.getValorUnitVenda());
                }
                EnumTipoLancamento tipoLcto = entrada ? EnumTipoLancamento.ENTRADA : EnumTipoLancamento.SAIDA;
                estoqueEJB.salvar(estoqueTela, docEntradaSaida, WebUtils.getUsuario(), tipoLcto, observacao);

            }

        } catch (GestorException e) {
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
    }

    private void atualizarValorEstoque(Produto produto, BigDecimal valorCompra, BigDecimal valorVenda) {
        List<TabelaPrecoItem> itens = tabelaPrecoEJB.getTabelaPrecoPorProduto(produto);
        if (valorCompra == null && valorVenda == null) {
            return;
        }
        TabelaPrecoItem tabelaPrecoItem = null;
        if (!Utils.empty(itens)) {
            for (TabelaPrecoItem item : itens) {
                if (item.getTabelaPreco().getPadrao()) {
                    if (valorCompra != null) {
                        item.setValorCompra(valorCompra);
                    }
                    if (valorVenda != null) {
                        item.setValorVenda(valorVenda);
                    }
                    tabelaPrecoEJB.atualizarItem(item);
                    return;
                }
            }

        }
        tabelaPrecoItem = new TabelaPrecoItem();
        TabelaPreco tabelaPreco = tabelaPrecoEJB.getTabelaPrecoPadrao(WebUtils.getEmpresa());
        if (tabelaPreco == null) {
            tabelaPreco = new TabelaPreco();
            tabelaPreco.setDescricao("TABELA PADRÃO");
            tabelaPreco.setEmpresa(WebUtils.getEmpresa());
            tabelaPreco.setPadrao(true);
        }
        tabelaPrecoItem.setTabelaPreco(tabelaPreco);
        tabelaPrecoItem.setProduto(produto);

        if (valorCompra != null) {
            tabelaPrecoItem.setValorCompra(valorCompra);
        }
        if (valorVenda != null) {
            tabelaPrecoItem.setValorVenda(valorVenda);
        }

        tabelaPrecoEJB.atualizarItem(tabelaPrecoItem);
    }

    @Override
    public void cancelar() {
        listarSugestoesCompra = false;
        estoque = null;
        docEntradaSaida = null;
        estoques = null;
        observacao = null;
        valorCompra = BigDecimal.ZERO;
        valorVenda = BigDecimal.ZERO;
        codigoBarras = null;
        percentualMargem = null;
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
            estoqueEJB.remove(estoque);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir:" + e.toString());
            return;
        }
        WebUtils.messageInfo("Item do estoque removido juntamente com suas movimentacoes");
        cancelar();
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
        if (estoque != null) {
            List<TabelaPrecoItem> itens = tabelaPrecoEJB.getTabelaPrecoPorProduto(estoque.getProduto());
            if (!Utils.empty(itens)) {
                for (TabelaPrecoItem item : itens) {
                    if (item.getTabelaPreco().getPadrao()) {
                        valorCompra = item.getValorCompra();
                        valorVenda = item.getValorVenda();
                    }
                }
            }
        }
    }

    public String getDocEntradaSaida() {
        return docEntradaSaida;
    }

    public void setDocEntradaSaida(String docEntradaSaida) {
        this.docEntradaSaida = docEntradaSaida;
    }

    public void addProduto(Produto produto) {
        if (!Utils.empty(estoques)) {
            for (Estoque e : estoques) {
                Produto p = e.getProduto();
                if (p.equals(produto)) {
                    return;
                }
            }
        } else {
            estoques = new ArrayList<>();
            estoques.add(this.criarEstoque(produto));
            return;
        }

        estoques.add(this.criarEstoque(produto));
    }

    private Estoque criarEstoque(Produto produto) {
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(BigDecimal.ZERO);
        Estoque estoqueExitente = produto.getEstoque();
        UnidadeMedida unidadeMedida = (estoqueExitente != null && produto.getEstoque().getUnidadeMedida() != null)
                ? produto.getEstoque().getUnidadeMedida() : unidadeMedidaEJB.getUnidadeMedidaPorChave(EnumUnidadeMedida.UN);
        estoque.setQuantidadeMinima(estoqueExitente != null ? estoqueExitente.getQuantidadeMinima() : null);
        estoque.setQuantidadeMaxima(estoqueExitente != null ? estoqueExitente.getQuantidadeMaxima() : null);
        estoque.setUnidadeMedida(unidadeMedida);

        List<TabelaPrecoItem> itens = tabelaPrecoEJB.getTabelaPrecoPorProduto(produto);

        if (itens != null && !itens.isEmpty()) {
            for (TabelaPrecoItem tpi : itens) {
                if (tpi.getTabelaPreco().getPadrao()) {
                    estoque.setValorUnitCompra(tpi.getValorCompra());
                    estoque.setValorUnitVenda(tpi.getValorVenda());
                }
            }
        }

        return estoque;
    }

    public void removeEstoque(Estoque estoque) {
        if (!Utils.empty(estoques)) {
            estoques.remove(estoque);
            WebUtils.update("form");
        }
    }

    public List<Estoque> getEstoques() {
        return estoques;
    }

    public void setEstoques(List<Estoque> estoques) {
        this.estoques = estoques;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public boolean isAtualizarEstoqueAntigo() {
        return atualizarValorEstoqueAntigo;
    }

    public void setAtualizarEstoqueAntigo(boolean atualizarEstoqueAntigo) {
        this.atualizarValorEstoqueAntigo = atualizarEstoqueAntigo;
    }

    public boolean isEntrada() {
        return entrada;
    }

    public void setEntrada(boolean entrada) {
        this.entrada = entrada;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isListarSugestoesCompra() {
        return listarSugestoesCompra;
    }

    public void setListarSugestoesCompra(boolean listarSugestoesCompra) {
        this.listarSugestoesCompra = listarSugestoesCompra;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public void carregarCurvaABC() {
        curvaAbcEstoques = estoqueEJB.getCurvaAbc(WebUtils.getEmpresa(), dataInicial, dataFinal);
    }

    public List<CurvaAbcEstoque> getCurvaAbcEstoques() {
        return curvaAbcEstoques;
    }

    public void setCurvaAbcEstoques(List<CurvaAbcEstoque> curvaAbcEstoques) {
        this.curvaAbcEstoques = curvaAbcEstoques;
    }

    public void recuperaProdutoComCodBarras() {
        Produto produto = produtoEJB.getProdutoPorCodigoBarras(WebUtils.getEmpresa(), codigoBarras);
        if (produto != null) {
            this.addProduto(produto);
        } else {
            WebUtils.messageWarn("Nenhum produto localizado");
        }
        codigoBarras = null;

    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public BigDecimal getPercentualMargem() {
        return percentualMargem;
    }

    public void setPercentualMargem(BigDecimal percentualMargem) {
        this.percentualMargem = percentualMargem;
        // this.aplicarMargem();
    }

    public void aplicarMargem() {
        if (Utils.empty(estoques)) {
            WebUtils.messageWarn("Adicione ao menos um ítem para aplicar a margem informada");
            return;
        }
        if (percentualMargem == null || percentualMargem.compareTo(BigDecimal.ZERO) <= 0) {
            WebUtils.messageWarn("Informe um percentual de margem para aplicar");
            return;
        }
        for (Estoque e : estoques) {
            BigDecimal vlrCompra = e.getValorUnitCompra();
            BigDecimal vlrVenda;
            if (vlrCompra != null && vlrCompra.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal vlrMargem = vlrCompra.multiply(percentualMargem).divide(BigDecimal.valueOf(100));
                vlrVenda = vlrCompra.add(vlrMargem);
                e.setValorUnitVenda(vlrVenda.setScale(2, RoundingMode.HALF_UP));
            }
        }

    }
}
