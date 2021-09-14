/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.CaixaEJB;
import com.gestor.ejb.ClienteEJB;
import com.gestor.ejb.EstoqueEJB;
import com.gestor.ejb.FormaPagamentoEJB;
import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.OrcamentoEJB;
import com.gestor.ejb.ProdutoInsumoEJB;
import com.gestor.ejb.RelatoriosEJB;
import com.gestor.ejb.TabelaPrecoEJB;
import com.gestor.ejb.TipoPagamentoEJB;
import com.gestor.ejb.TipoProdutoEJB;
import com.gestor.ejb.TituloEJB;
import com.gestor.ejb.UnidadeMedidaEJB;
import com.gestor.entidades.Caixa;
import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Estoque;
import com.gestor.entidades.FormaPagamento;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.LancamentoParcela;
import com.gestor.entidades.Produto;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.OrcamentoItem;
import com.gestor.entidades.ProdutoInsumo;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TabelaPrecoItem;
import com.gestor.entidades.TipoPagamento;
import com.gestor.entidades.TipoProduto;
import com.gestor.entidades.Titulo;
import com.gestor.entidades.TituloParcela;
import com.gestor.entidades.Usuario;
import com.gestor.enums.EnumStatusOrcamento;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.enums.EnumTipoProduto;
import com.gestor.util.GestorException;
import com.gestor.util.OrcamentoControllerUtil;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.jms.Connection;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author laender
 */
@ManagedBean
@ViewScoped
public class VendaRapidaController implements Serializable, ICadastros {

    private Orcamento orcamento = null;

    private Produto produto;

    private TabelaPreco tabelaPreco;

    @EJB
    private OrcamentoEJB orcamentoEJB;
    @EJB
    private FormaPagamentoEJB formaPagamentoEJB;
    @EJB
    private ProdutoEJB produtoEJB;

    @EJB
    private UnidadeMedidaEJB unidadeMedidaEJB;

    @EJB
    private TituloEJB tituloEJB;

    @EJB
    private ClienteEJB clienteEJB;

    @EJB
    private EstoqueEJB estoqueEJB;

    @EJB
    private TipoPagamentoEJB tipoPagamentoEJB;

    @EJB
    private CaixaEJB caixaEJB;

    private Caixa caixa;

    private Empresa empresa;

    private OrcamentoItem itemSelecionado;

    @EJB
    private ProdutoInsumoEJB produtoInsumoEJB;

    private BigDecimal totalGeral = BigDecimal.ZERO;
    private BigDecimal totalSubTotal = BigDecimal.ZERO;
    private BigDecimal totalInsumos = BigDecimal.ZERO;
    private BigDecimal totalComissao = BigDecimal.ZERO;
    private BigDecimal totalMaoObra = BigDecimal.ZERO;
    private BigDecimal percentualDesconto = BigDecimal.ZERO;
    private BigDecimal valorPago = BigDecimal.ZERO;
    private BigDecimal valorTroco = BigDecimal.ZERO;

    private String codigoBarras = null;

    private HttpServletResponse response;
    private FacesContext context;
    private ByteArrayOutputStream baos;
    private InputStream stream;
    private Connection con;
    private boolean enableButtons = true;

    @EJB
    private TipoProdutoEJB tipoProdutoEJB;

    @EJB
    private TabelaPrecoEJB tabelaPrecoEJB;

    Usuario usuario = null;

    @EJB
    private RelatoriosEJB relatoriosEJB;

    public VendaRapidaController() {
    }

    @PostConstruct
    public void init() {
        cancelar();
        empresa = WebUtils.getEmpresa();
        usuario = WebUtils.getUsuario();
        orcamento = new Orcamento();
        orcamento.setStatus(EnumStatusOrcamento.VENDA);
        orcamento.setUsuario(usuario);
        orcamento.setUsuarioExecutante(usuario);
        orcamento.setDataOrcamento(new Date());
        orcamento.setDataVenda(new Date());
        orcamento.setValorTotal(BigDecimal.ZERO);
        orcamento.setValorSaldo(BigDecimal.ZERO);
        orcamento.setCliente(this.recuperaClienteConsumidor());
        orcamento.setOrcamentoItemList(new ArrayList<>());

        List<TipoPagamento> tiposPgto = this.listarTipoPagamentos();
        if (Utils.empty(tiposPgto)) {
            enableButtons = false;
        }
        List<FormaPagamento> formaPgto = this.listarFormaPagamentos();
        if (Utils.empty(formaPgto)) {
            enableButtons = false;
        }

        try {
            caixa = caixaEJB.getCaixaAberto(empresa);

        } catch (GestorException ex) {
            ex.printStackTrace();
            enableButtons = false;
        }
        tabelaPreco = tabelaPrecoEJB.getTabelaPrecoPadrao(empresa);
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public Produto getProduto() {
        return produto == null ? new Produto(WebUtils.getEmpresa()) : produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public List<TipoPagamento> listarTipoPagamentos() {
        return tipoPagamentoEJB.findAll(empresa);

    }

    public List<FormaPagamento> listarFormaPagamentos() {
        return formaPagamentoEJB.findAll(empresa);
    }

    public void addProduto(Produto produto) {
        this.addProdutoInsumo(produto, false, null);

    }

    private void addInsumo(Produto produto, BigDecimal qtd) {
        this.addProdutoInsumo(produto, true, qtd);
    }

    private void addProdutoInsumo(Produto produto, boolean insumo, BigDecimal qtd) {
        List<OrcamentoItem> itens = this.recuperaItens();
        if (itens != null && !itens.isEmpty()) {
            for (OrcamentoItem tpi : itens) {
                Produto i = tpi.getProduto();
                if (tpi.getValorUnitario() == null || tpi.getValorUnitario().compareTo(BigDecimal.ZERO) == 0) {
                    WebUtils.messageWarn("O produto " + tpi.getProduto().getDescricao() + " está sem preço, adicione o valor ou remova o item da lista");
                    return;
                }
                if (i.equals(produto)) {
                    tpi.setQuantidade(tpi.getQuantidade().add(BigDecimal.ONE));
                    this.calcularSubtotal();
                    return;
                }
            }
        } else {
            itens = new ArrayList<>();
            orcamento.setOrcamentoItemList(itens);
        }
        if (!insumo) {
            List<ProdutoInsumo> insumos = produtoInsumoEJB.getProdutoInsumos(produto);
            if (!Utils.empty(insumos)) {
                produto.setProdutoInsumos(insumos); // adicionar lista de insumos do produto para recalculo do saldo depois sem requisitar EJB
                for (ProdutoInsumo insumo1 : insumos) {
                    this.addInsumo(insumo1.getInsumo(), null);
                }
            }
        }

        OrcamentoItem itemNovo = this.criarItem(produto, qtd);
        itemSelecionado = itemNovo;

        orcamento.getOrcamentoItemList().add(itemNovo);
        this.calcularSubtotal();
        //  WebUtils.update("form");
    }

    private OrcamentoItem criarItem(Produto produto, BigDecimal qtd) {
        OrcamentoItem itemNovo = new OrcamentoItem();
        List<TabelaPrecoItem> tabelaPrecoItems = produto.getTabelaPrecoItens();
        if (!Utils.empty(tabelaPrecoItems)) {
            for (TabelaPrecoItem tabelaPrecoItem : tabelaPrecoItems) {
                if ((tabelaPreco != null && tabelaPreco.equals(tabelaPrecoItem.getTabelaPreco())) || tabelaPrecoItem.getTabelaPreco().getPadrao()) {
                    itemNovo.setTabelaPrecoItem(tabelaPrecoItem);
                    itemNovo.setValorCusto(tabelaPrecoItem.getValorCompra());
                    itemNovo.setValorUnitario(tabelaPrecoItem.getValorVenda());
                    itemNovo.setQuantidade(qtd != null ? qtd : BigDecimal.ONE);
                    itemNovo.setValorDesconto(BigDecimal.ZERO);
                    itemNovo.setValorAcrescimo(BigDecimal.ZERO);
                    itemNovo.setPercAcrescimo(BigDecimal.ZERO);
                    itemNovo.setPercDesconto(BigDecimal.ZERO);
                    itemNovo.setTabelaPrecoItem(tabelaPrecoItem);
                }
            }

        }
        itemNovo.setProduto(produto);

        if (WebUtils.getEmpresa().isControlaEstoque()) {
            itemNovo = this.montarEstoqueDefault(itemNovo);
        }

        itemNovo.setOrcamento(orcamento);

        return itemNovo;
    }

    private OrcamentoItem montarEstoqueDefault(OrcamentoItem itemNovo) {
        Estoque estoque = itemNovo.getProduto().getEstoque();
        if (estoque != null) {
            itemNovo.setUnidadeMedida(estoque.getUnidadeMedida());
            itemNovo.setUnidadeMedidaSelecionada(estoque.getUnidadeMedida().getSigla().getValue());
            if (estoque.getQuantidade().compareTo(BigDecimal.ZERO) <= 0 && estoque.getProduto().getTipoProduto().getEnumTiipoProduto() != null
                    && !estoque.getProduto().getTipoProduto().getEnumTiipoProduto().equals(EnumTipoProduto.MAO_DE_OBRA)) {
            }
        } else {
            itemNovo.setUnidadeMedida(OrcamentoControllerUtil.getUnidadeMedidaPorSigla("UNIDADE", unidadeMedidaEJB));
            itemNovo.setUnidadeMedidaSelecionada("UNIDADE");
        }
        return itemNovo;
    }

    public void removeItem(OrcamentoItem item) {
        List<OrcamentoItem> itens = this.recuperaItens();
        itens.remove(item);
        this.calcularSubtotal();
        WebUtils.update("form");
    }

    public List<Produto> listarProdutos() {
        return produtoEJB.getProdutoDiferenteDeInsumos(empresa);
    }

    public void salvar(boolean imprimir) {
        Orcamento orct;
        try {
            if (!this.isValido()) {
                return;
            }

            if (!isItemValido()) {
                return;
            }

            orct = orcamentoEJB.gravarOrcamento(orcamento);
            if (!orct.getFormaPagamento().isVendaAPrazo()) {
                this.liquidarVenda(orct);
            }

        } catch (Exception e) {
            e.printStackTrace();
            WebUtils.messageError("Erro ao salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        if (!imprimir) {
            WebUtils.messageRegistroGravado();
            init();
        } else {
            init();
            WebUtils.update("form");
            this.imprimirOrcamento(orct);
        }

    }

    private void liquidarVenda(Orcamento orcamento) {
        FormaPagamento frmPgto = orcamento.getFormaPagamento();
        if (frmPgto.isVendaAPrazo()) {
            return;
        }
        LancamentoFinanceiro lcto = new LancamentoFinanceiro();
        lcto.setDataLancamento(new Date());
        lcto.setFormaPagamento(orcamento.getFormaPagamento());
        lcto.setTipoPagamento(orcamento.getTipoPagamento());
        lcto.setUsuario(WebUtils.getUsuario());
        lcto.setValor(orcamento.getValorTotal());
        lcto.setCaixa(caixa);
        lcto.setObservacao("BAIXA AUT. VENDA A VISTA");
        Titulo titulo = tituloEJB.getTituloPorOcamento(orcamento);
        // se a venda é a vista sempre será gerado apenas uma parcela portanto eu vou acessar direto o primeiro elemento da lista
        TituloParcela parcela = titulo.getParcelas().get(0);
        LancamentoParcela lancamentoParcela = new LancamentoParcela();
        lancamentoParcela.setLancamentoFinanceiro(lcto);
        lancamentoParcela.setParcela(parcela);

        List<TituloParcela> parcelas = new ArrayList<>();
        parcelas.add(parcela);
        lcto.setTipoLancamento(EnumTipoLancamento.ENTRADA);

        LancamentoController controller = WebUtils.managedBean(LancamentoController.class);
        controller.setLancamentoFinanceiro(lcto);
        controller.setParcelasSelecionadas(parcelas);
        controller.lancamentoEntrada(true);
        controller.salvar(true);
    }

    @Override
    public void cancelar() {
        orcamento = null;
        produto = null;
        tabelaPreco = null;
        codigoBarras = null;
        itemSelecionado = null;
        inicializarValores();
        // WebUtils.update("form");
    }

    @Override
    public void fechar() {
        cancelar();
        WebUtils.redirect("../menu");
        WebUtils.update("form");
    }

    public void excluir() {
        try {
            orcamento.setStatus(EnumStatusOrcamento.CANCELADO);
            orcamentoEJB.gravarOrcamento(orcamento);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao cancelar. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

    public void calcularSubtotal() {
        List<OrcamentoItem> itens = this.recuperaItens();
        // recalculo das quantidades baseado no definido no insumo
        if (!Utils.empty(itens)) {
            for (OrcamentoItem item : itens) {
                this.recalcularInsumos(item);

            }
        }

        totalComissao = BigDecimal.ZERO;
        totalInsumos = BigDecimal.ZERO;
        totalMaoObra = BigDecimal.ZERO;
        if (!Utils.empty(itens)) {
            totalGeral = BigDecimal.ZERO;
            totalSubTotal = BigDecimal.ZERO;
            for (OrcamentoItem item : itens) {
                if (Utils.empty(item.getUnidadeMedidaSelecionada()) && item.getUnidadeMedida() != null) {
                    item.setUnidadeMedidaSelecionada(item.getUnidadeMedida().getSigla().getValue());
                }

                BigDecimal vlrUnit = Utils.nvl(item.getValorUnitario(), BigDecimal.ZERO);
                BigDecimal vlrDsc = Utils.nvl(item.getValorDesconto(), BigDecimal.ZERO);
                BigDecimal percDesc = Utils.nvl(item.getPercDesconto(), BigDecimal.ZERO);

                BigDecimal qtd = item.getQuantidade();
                BigDecimal vlrProd = vlrUnit.multiply(qtd);

                if (percDesc.compareTo(BigDecimal.ZERO) > 0) {
                    item.setValorDesconto(percDesc.multiply((vlrProd)).divide(BigDecimal.valueOf(100)));
                } else if (percDesc.compareTo(BigDecimal.ZERO) == 0) {
                    item.setValorDesconto(BigDecimal.ZERO);
                }

                BigDecimal subTotal = (vlrProd).subtract(vlrDsc);

                item.setValorTotal(subTotal);
                //BigDecimal valorDescontoOrcamento = orcamento.getValorDesconto();
                totalSubTotal = totalSubTotal.add(subTotal);
                totalGeral = totalGeral.add(subTotal);

                if (item.isInsumo()) {
                    totalInsumos = totalInsumos.add(subTotal);
                }
                EnumTipoProduto tipoProd = item.getProduto().getTipoProduto().getEnumTiipoProduto();
                if (tipoProd != null && tipoProd.equals(EnumTipoProduto.MAO_DE_OBRA)) {
                    totalMaoObra = totalMaoObra.add(item.getValorTotal());
                }
            }

            if (percentualDesconto != null && percentualDesconto.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal valorDesconto = (percentualDesconto.multiply((totalGeral)).divide(BigDecimal.valueOf(100)));
                orcamento.setValorDesconto(valorDesconto);
                totalGeral = totalGeral.subtract(valorDesconto);
            } else {
                orcamento.setValorDesconto(BigDecimal.ZERO);
            }
        }
        BigDecimal percComissao = orcamento.getUsuarioExecutante().getPercComissao();
        if (percComissao
                != null && percComissao.compareTo(BigDecimal.ZERO)
                > 0) {
            this.calculaComissao(percComissao);
        }
        this.calcularTroco();
        //    WebUtils.update("form");
    }

    private void calculaComissao(BigDecimal percComissao) {
        if (empresa.isComissaoTotalVenda()) {
            totalComissao = (totalGeral.multiply(percComissao)).divide(BigDecimal.valueOf(100));
        } else if (empresa.isComissaoTotalMaoObra() && totalMaoObra != null && totalMaoObra.compareTo(BigDecimal.ZERO) > 0) {
            totalComissao = (totalMaoObra.multiply(percComissao)).divide(BigDecimal.valueOf(100));
        }
        orcamento.setValorComissao(totalComissao);

    }

    public BigDecimal getTotalGeral() {
        return totalGeral;
    }

    public void setTotalGeral(BigDecimal totalGeral) {
        this.totalGeral = totalGeral;
    }

    public BigDecimal getTotalComissao() {
        return totalComissao;
    }

    private void inicializarValores() {
        totalGeral = BigDecimal.ZERO;
        totalSubTotal = BigDecimal.ZERO;
        totalInsumos = BigDecimal.ZERO;
        totalComissao = BigDecimal.ZERO;
        totalMaoObra = BigDecimal.ZERO;
        percentualDesconto = BigDecimal.ZERO;
        valorPago = BigDecimal.ZERO;
        valorTroco = BigDecimal.ZERO;
    }

    public void setTotalComissao(BigDecimal totalComissao) {
        this.totalComissao = totalComissao;
    }

    public BigDecimal getTotalInsumos() {
        return totalInsumos;
    }

    public void setTotalInsumos(BigDecimal totalInsumos) {
        this.totalInsumos = totalInsumos;
    }

    public OrcamentoItem getItemSelecionado() {
        return itemSelecionado == null ? new OrcamentoItem() : itemSelecionado;
    }

    public void setItemSelecionado(OrcamentoItem itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public List<OrcamentoItem> recuperaItens() {
        return orcamento != null ? orcamento.getOrcamentoItemList() : null;
    }

    public void imprimirOrcamento(Orcamento orc) {
        this.context = FacesContext.getCurrentInstance();
        this.response = (HttpServletResponse) context.getExternalContext().getResponse();
        baos = new ByteArrayOutputStream();
        // this.salvar(true);
        try {
            String logo = empresa.getLogoImage();
            if (logo == null) {
                logo = "empresas.png";
            }
            Map<String, Object> parametros = new HashMap<>();
            InputStream image = this.getClass().getResourceAsStream("/Images/" + logo);
            parametros.put("URL_IMAGE", image);

            JasperPrint print = relatoriosEJB.gerarOrcamento(orc != null ? orc : orcamento, parametros, getEmpresa());
            JasperExportManager.exportReportToPdfStream(print, baos);
            Integer id = orc != null ? orc.getId() : orcamento.getId();
            String fileName = "Venda - " + id;
            response.reset();
            response.setContentType("application/pdf");
            response.setContentLength(baos.size());
            response.setHeader("Content-disposition", "inline; filename=" + fileName + ".pdf");
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();
            response.getOutputStream().close();

            context.responseComplete();

        } catch (JRException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    private boolean isValido() {
        if (orcamento == null) {
            WebUtils.messageWarn("Nenhum orcamento informado");
            return false;
        }

        if (orcamento.getUsuario() == null) {
            WebUtils.messageWarn("Profissional nao informado");
            return false;
        }

        if (orcamento.getFormaPagamento() == null) {
            WebUtils.messageWarn("Forma de pagamento nao informada");
            return false;
        }
        if (orcamento.getTipoPagamento() == null) {
            WebUtils.messageWarn("Tipo de pagamento nao informado");
            return false;
        }

        if (orcamento.getCliente() == null) {
            WebUtils.messageWarn("Cliente nao informado");
            return false;
        }
        if (Utils.empty(orcamento.getOrcamentoItemList())) {
            WebUtils.messageWarn("Nenhum item informado");
            return false;
        }
        return true;
    }

    public boolean isItemValido() {
        for (OrcamentoItem orcamentoItem : orcamento.getOrcamentoItemList()) {
            if (!Utils.empty(orcamentoItem.getPercDesconto()) && orcamentoItem.getPercDesconto().compareTo(BigDecimal.valueOf(100)) > 0) {
                WebUtils.messageWarn("O percentual maximo de desconto nao pode ultrapassar 100%");
                return false;
            }
            if (orcamentoItem.getValorUnitario() == null || orcamentoItem.getValorUnitario().compareTo(BigDecimal.ZERO) == 0) {
                WebUtils.messageWarn("O produto " + orcamentoItem.getProduto().getDescricao() + " nao possui valor, verifique ou remova o item");
                return false;
            }
            if (!Utils.empty(orcamentoItem.getValorDesconto()) && orcamentoItem.getValorDesconto().compareTo(orcamentoItem.getValorTotal()) > 0) {
                WebUtils.messageWarn("O valor maximo de desconto nao pode ultrapassar o valor total do item " + orcamentoItem.getProduto().getDescricao());
                return false;
            }
            //setando o objeto unidade de medida pois é setado string unidade de medida (sim, mas dentro do datatable nao consegui selecionar objeto)
            if (empresa.isControlaEstoque()) {
                String unMedSel = orcamentoItem.getUnidadeMedidaSelecionada();
                if (!Utils.empty(unMedSel)) {
                    orcamentoItem.setUnidadeMedida(OrcamentoControllerUtil.getUnidadeMedidaPorSigla(orcamentoItem.getUnidadeMedidaSelecionada(), unidadeMedidaEJB));
                }
            }
        }
        return true;

    }

    @Override
    public void salvar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String concatenaObservacao(String obs) {
        return !Utils.empty(obs) ? "(" + obs + ")" : null;
    }

    public void atualizaItem(OrcamentoItem item) {
        if (!empresa.isControlaEstoque()) {
            return;
        }
        item.setSelecionado(true);
        this.calcularSubtotal();
    }

    private Cliente recuperaClienteConsumidor() {
        Cliente cliente = clienteEJB.getClienteConsumidor(empresa);
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setNome("Consumidor");
            cliente.setEmpresa(empresa);
            cliente = clienteEJB.gravarCliente(cliente);
        }
        return cliente;
    }

    @Override
    public void novo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public List<Cliente> listarClientes() {
        return clienteEJB.findAll(empresa);
    }

    public BigDecimal recuperaQtdItens() {
        if (orcamento == null) {
            return null;
        }
        List<OrcamentoItem> list = orcamento.getOrcamentoItemList();
        if (list != null) {
            BigDecimal qtd = BigDecimal.ZERO;
            for (OrcamentoItem orcamentoItem : list) {
                qtd = qtd.add(orcamentoItem.getQuantidade());
            }
            return qtd;
        }
        return null;
    }

    public BigDecimal getTotalSubTotal() {
        return totalSubTotal;
    }

    public void setTotalSubTotal(BigDecimal totalSubTotal) {
        this.totalSubTotal = totalSubTotal;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public void calcularTroco() {
        if (valorPago != null && valorPago.compareTo(BigDecimal.ZERO) > 0) {
            valorTroco = totalGeral.subtract(valorPago);
            valorTroco = valorTroco.multiply(BigDecimal.valueOf(-1));
            return;
        }
        valorTroco = null;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorTroco() {
        return valorTroco;
    }

    public void setValorTroco(BigDecimal valorTroco) {
        this.valorTroco = valorTroco;
    }

    private void recalcularInsumos(OrcamentoItem item) {
        Produto prod = item.getProduto();
        List<ProdutoInsumo> insumos = prod.getProdutoInsumos();

        if (Utils.empty(insumos)) {
            insumos = produtoInsumoEJB.getProdutoInsumos(prod);
        }
        if (!Utils.empty(insumos)) {
            for (ProdutoInsumo prodInsumo : insumos) {
                List<OrcamentoItem> itens = this.recuperaItens();
                for (OrcamentoItem itemOrct : itens) {
                    Produto p = itemOrct.getProduto();
                    if (p.equals(prodInsumo.getInsumo())) {
                        itemOrct.setInsumo(true);
                        BigDecimal qtdItem = item.getQuantidade() != null ? item.getQuantidade() : BigDecimal.ZERO;
                        itemOrct.setQuantidade(prodInsumo.getQuantidade() != null ? prodInsumo.getQuantidade().multiply(qtdItem) : BigDecimal.ONE);
                    }
                }
            }
        }
        // calculo de insumos que nao sao diretamente vinculados a um produto, mas sim pelo tipo INSUMO
        EnumTipoProduto tipoProd = item.getProduto().getTipoProduto().getEnumTiipoProduto();
        if (tipoProd != null && tipoProd.equals(EnumTipoProduto.INSUMO)) {
            item.setInsumo(true);
        }

    }

    public void setEnableButtons(boolean enableButtons) {
        this.enableButtons = enableButtons;
    }

    public boolean isEnableButtons() {
        return enableButtons;
    }

    public void novoProduto() {
        produto = new Produto();
        produto.setEmpresa(WebUtils.getEmpresa());

    }

    public void gravarNovoProduto() {
        if (produto == null) {
            WebUtils.messageWarn("Nenhum produto informado");
            return;
        }
        if (Utils.empty(produto.getDescricao())) {
            WebUtils.messageWarn("Informe a descrição do produto");
            return;
        }
        if (produto.getTipoProduto() == null) {
            WebUtils.messageWarn("Informe o tipo do produto");
            return;
        }
        Produto p = produtoEJB.merge(produto);
        this.addProduto(p);

    }

    public List<TipoProduto> listarTiposProduto() {
        return tipoProdutoEJB.getTipoProdutos(empresa);
    }

}
