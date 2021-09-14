/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.CaixaEJB;
import com.gestor.ejb.CidadeEJB;
import com.gestor.ejb.ClienteEJB;
import com.gestor.ejb.FormaPagamentoEJB;
import com.gestor.ejb.MultiplicadorEJB;
import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.OrcamentoEJB;
import com.gestor.ejb.ProdutoInsumoEJB;
import com.gestor.ejb.RelatoriosEJB;
import com.gestor.ejb.TabelaPrecoEJB;
import com.gestor.ejb.TipoOrcamentoEJB;
import com.gestor.ejb.TipoPagamentoEJB;
import com.gestor.ejb.TipoProdutoEJB;
import com.gestor.ejb.TituloEJB;
import com.gestor.ejb.UnidadeMedidaEJB;
import com.gestor.ejb.UsuarioEJB;
import com.gestor.ejb.VeiculoClienteEJB;
import com.gestor.entidades.Caixa;
import com.gestor.entidades.Cidade;
import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Estoque;
import com.gestor.entidades.FormaPagamento;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.LancamentoParcela;
import com.gestor.entidades.Multiplicador;
import com.gestor.entidades.Produto;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.OrcamentoItem;
import com.gestor.entidades.ProdutoInsumo;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TabelaPrecoItem;
import com.gestor.entidades.TipoOrcamento;
import com.gestor.entidades.TipoPagamento;
import com.gestor.entidades.TipoProduto;
import com.gestor.entidades.Titulo;
import com.gestor.entidades.TituloParcela;
import com.gestor.entidades.UnidadeMedida;
import com.gestor.entidades.Usuario;
import com.gestor.entidades.VeiculoCliente;
import com.gestor.enums.EnumStatusOrcamento;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.enums.EnumTipoProduto;
import com.gestor.enums.EnumUnidadeMedida;
import com.gestor.filtros.FiltroPaginacao;
import com.gestor.util.GestorException;
import com.gestor.util.OrcamentoControllerUtil;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import com.gestor.utils.UnidadeMedidaUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author laender
 */
@ManagedBean
@ViewScoped
public class OrcamentoController implements Serializable, ICadastros {

    private Orcamento orcamento = null;

    private Produto produto;

    private Cliente cliente;

    private TabelaPreco tabelaPreco;

    private TipoOrcamento tipoOrcamento;

    @EJB
    private OrcamentoEJB orcamentoEJB;
    @EJB
    private FormaPagamentoEJB formaPagamentoEJB;
    @EJB
    private ProdutoEJB produtoEJB;

    @EJB
    private UnidadeMedidaEJB unidadeMedidaEJB;

    @EJB
    private CidadeEJB cidadeEJB;

    @EJB
    private ProdutoInsumoEJB produtoInsumoEJB;

    @EJB
    private UsuarioEJB usuarioEJB;

    @EJB
    private ClienteEJB clienteEJB;

    private Usuario usuarioExecutante;

    @EJB
    private TipoOrcamentoEJB tipoOrcamentoEJB;

    @EJB
    private TipoPagamentoEJB tipoPagamentoEJB;

    @EJB
    private TabelaPrecoEJB tabelaPrecoEJB;

    @EJB
    private TipoProdutoEJB tipoProdutoEJB;

    private Empresa empresa;

    private OrcamentoItem itemSelecionado;

    @EJB
    private MultiplicadorEJB multiplicadorEJB;

    private BigDecimal totalGeral = BigDecimal.ZERO;
    private BigDecimal totalInsumos = BigDecimal.ZERO;
    private BigDecimal totalComissao = BigDecimal.ZERO;
    private BigDecimal totalMaoObra = BigDecimal.ZERO;

    private BigDecimal descontoTotalVenda = BigDecimal.ZERO;
    private BigDecimal acrescimoTotalVenda = BigDecimal.ZERO;

    private BigDecimal valorDesconto = BigDecimal.ZERO;
    private BigDecimal percDesconto = BigDecimal.ZERO;
    private BigDecimal valorAcrescimo = BigDecimal.ZERO;
    private BigDecimal percAcrescimo = BigDecimal.ZERO;
    private String observacao = null;

    private HttpServletResponse response;
    private FacesContext context;
    private ByteArrayOutputStream baos;

    private boolean venda = false;

    private boolean recalcularInsumos = true;
    private boolean existeInsumos = false;

    private List<UnidadeMedida> unidadesMedida;

    private LazyDataModel<Orcamento> model;

    @EJB
    private RelatoriosEJB relatoriosEJB;

    private List<Orcamento> orcamentos = null;

    private Caixa caixa;

    @EJB
    private CaixaEJB caixaEJB;

    @EJB
    private TituloEJB tituloEJB;

    private boolean disableAllFields = false;

    @EJB
    private VeiculoClienteEJB veiculoClienteEJB;

    private VeiculoCliente veiculoCliente;
    
    private boolean gambiarra = true;

    public OrcamentoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        gambiarra = true;
        empresa = WebUtils.getEmpresa();
        veiculoCliente = new VeiculoCliente();
        veiculoCliente.setAtivo(true);
        model = new LazyDataModel<Orcamento>() {

            private static final long serialVersionUID = 1L;

            @Override
            public List<Orcamento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {

                boolean asc = sortOrder.equals(sortOrder.ASCENDING);

                FiltroPaginacao filtro = new FiltroPaginacao(first, pageSize, sortField, empresa, filters, asc);

                setRowCount(orcamentoEJB.quantidadeFiltrados(filtro));

                return orcamentoEJB.getOrcamentosFiltrados(filtro);
            }

        };

        // orcamentos = orcamentoEJB.getOrcamentos(empresa);
    }

    public void novo() {

        orcamento = new Orcamento();
        orcamento.setDataOrcamento(new Date());
        orcamento.setUsuario(WebUtils.getUsuario());
        usuarioExecutante = WebUtils.getUsuario();
        orcamento.setUsuarioExecutante(usuarioExecutante);
        orcamento.setValorComissao(BigDecimal.ZERO);
        orcamento.setValorTotal(BigDecimal.ZERO);
        orcamento.setValorSaldo(BigDecimal.ZERO);
        venda = false;
        veiculoCliente = new VeiculoCliente();
        veiculoCliente.setAtivo(true);
        disableAllFields = false;
        unidadesMedida = unidadeMedidaEJB.findAll();
        existeInsumos = false;

        tabelaPreco = tabelaPrecoEJB.getTabelaPrecoPadrao(empresa);
        if (empresa.isExibirMultiplicador()) {
            Multiplicador multiplicador = multiplicadorEJB.getMultiplicadorPadrao(empresa);
            orcamento.setMultiplicador(multiplicador);
        }

        WebUtils.update("form");

    }

    public List<Orcamento> getOrcamentos() {
        return orcamentos;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        Orcamento orcBd = orcamentoEJB.find(orcamento.getId());
        this.orcamento = orcBd;
        EnumStatusOrcamento status = orcBd.getStatus();
        if (status != null) {
            this.venda = status.equals(EnumStatusOrcamento.VENDA);
        }
        this.setUsuarioExecutante(orcamento.getUsuarioExecutante());
        descontoTotalVenda = orcamento.getValorDesconto();
        acrescimoTotalVenda = orcamento.getValorAcrescimo();
        disableAllFields = status.equals(EnumStatusOrcamento.VENDA) || status.equals(EnumStatusOrcamento.CANCELADO);
        this.totalComissao = orcamento.getValorComissao();
        this.valorAcrescimo = orcamento.getValorAcrescimo();
        this.valorDesconto = orcamento.getValorDesconto();
        //this.calcularSubtotal();
      //  WebUtils.update("form");

    }

    public Produto getProduto() {
        return produto == null ? new Produto(WebUtils.getEmpresa()) : produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void setTipoOrcamento(TipoOrcamento tipoOrcamento) {
        this.tipoOrcamento = tipoOrcamento;
        List<OrcamentoItem> itens = this.recuperaItens();
        if (!Utils.empty(itens)) {
            orcamento.getOrcamentoItemList().clear();

        }
        if (tipoOrcamento != null) {
            List<Produto> produtos = tipoOrcamento.getProdutos();
            if (!Utils.empty(produtos)) {
                for (Produto p : produtos) {
                    this.addProduto(p);
                }
            }
            orcamento.setTipoOrcamento(tipoOrcamento);

        }
    }

    public TipoOrcamento getTipoOrcamento() {
        return tipoOrcamento;
    }

    public List<TipoOrcamento> listarTipoOrcamentos() {
        return tipoOrcamentoEJB.findAll(empresa);

    }

    public List<TipoPagamento> listarTipoPagamentos() {
        return tipoPagamentoEJB.findAll(empresa);

    }

    public List<FormaPagamento> listarFormaPagamentos() {
        return formaPagamentoEJB.findAll(empresa);
    }

    public List<TipoProduto> listarTiposProduto() {
        return tipoProdutoEJB.getTipoProdutos(empresa);
    }

    public List<Multiplicador> listarMultiplicadores() {
        return multiplicadorEJB.findAll(empresa);
    }

    public void addProduto(Produto produto) {
        this.addProdutoInsumo(produto, false, null);

    }

    private void addInsumo(Produto produto, BigDecimal qtd) {
        this.addProdutoInsumo(produto, true, qtd);
    }

    private void addProdutoInsumo(Produto produto, boolean insumo, BigDecimal qtd) {
        List<OrcamentoItem> itens = this.recuperaItens();
        if (Utils.empty(itens)) {
            itens = new ArrayList<>();
            orcamento.setOrcamentoItemList(itens);
        }
        if (!insumo) {
            List<ProdutoInsumo> insumos = produtoInsumoEJB.getProdutoInsumos(produto);
            if (!Utils.empty(insumos)) {
                produto.setProdutoInsumos(insumos); // adicionar lista de insumos do produto para recalculo do saldo depois sem requisitar EJB
                for (ProdutoInsumo i : insumos) {
                    this.addInsumo(i.getInsumo(), null);
                    existeInsumos = true;
                }
            }
        }

        OrcamentoItem itemNovo = this.criarItem(produto, qtd);

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
                WebUtils.messageWarn("Atenção. O estoque do produto " + estoque.getProduto().getDescricao() + " está zerado ou negativo");
            }
        } else {
            WebUtils.messageWarn("Atenção. Produto sem estoque");
            itemNovo.setUnidadeMedida(OrcamentoControllerUtil.getUnidadeMedidaPorSigla("UNIDADE", unidadeMedidaEJB));
            itemNovo.setUnidadeMedidaSelecionada("UNIDADE");
        }
        return itemNovo;
    }

    public void removeItem(OrcamentoItem item) {
        List<OrcamentoItem> itens = this.recuperaItens();
        itens.remove(item);
        this.calcularSubtotal();
        //  removendo aqui pq ele nao remove no metodo salvar...ele sai da lista mas nao é removido do banco :/ assim funciona
        //  orcamentoEJB.removerItem(item);
        WebUtils.update("form");
    }

    public List<Produto> listarProdutos() {
        return produtoEJB.getProdutoDiferenteDeInsumos(empresa);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioEJB.getUsuarios(empresa);
    }

    public List<Cliente> listarClientes() {
        return clienteEJB.findAll(empresa);
    }

    public List<TabelaPreco> listarTabelasPreco() {
        return tabelaPrecoEJB.findAll(empresa);
    }

    public void salvar(boolean imprimir) {
        try {
            if (!this.isValido()) {
                return;
            }

            if (!isItemValido()) {
                return;
            }
            orcamento.setStatus(this.venda ? EnumStatusOrcamento.VENDA : EnumStatusOrcamento.ORCAMENTO);
            if (this.venda) {
                if (orcamento.getDataVenda() == null) {
                    orcamento.setDataVenda(new Date());
                }
            }
            orcamento.setValorDesconto(descontoTotalVenda);
            orcamento.setValorAcrescimo(acrescimoTotalVenda);

            Orcamento orct = orcamentoEJB.gravarOrcamento(orcamento);
            orcamentoEJB.merge(orct);  // gambiarra feia mas nao tava atualizando o orcamento no servidor..... nao altere isso...deixa ai
            if (orct.getStatus().equals(EnumStatusOrcamento.VENDA) && !orct.getFormaPagamento().isVendaAPrazo()) {
                this.liquidarVenda(orct);
            }

        } catch (Exception e) {
            e.printStackTrace();
            WebUtils.messageError("Erro ao salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        if (!imprimir) {
            WebUtils.update("form");
            WebUtils.messageRegistroGravado();
            cancelar();

        }

        cancelar();
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
        lcto.setObservacao("BAIXA AUT. VENDA A VISTA");
        lcto.setCaixa(caixa);
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
        itemSelecionado = null;
        produto = null;
        tabelaPreco = null;
        venda = false;
        cliente = null;
        tipoOrcamento = null;
        totalGeral = BigDecimal.ZERO;
        totalInsumos = BigDecimal.ZERO;
        totalComissao = BigDecimal.ZERO;
        totalMaoObra = BigDecimal.ZERO;
        observacao = null;
        usuarioExecutante = null;
        orcamentos = orcamentoEJB.getOrcamentos(WebUtils.getEmpresa());
        inicializarValores();
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
            orcamento.setStatus(EnumStatusOrcamento.CANCELADO);
            orcamentoEJB.gravarOrcamento(orcamento);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao cancelar. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

    public Cliente getCliente() {
        return cliente == null ? new Cliente(this.empresa, new Date()) : cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null && !Utils.empty(cliente.getEndereco()) && !Utils.empty(orcamento.getObservacao())) {
            String end = cliente.getEndereco();
            Integer num = cliente.getNumero();

            orcamento.setObservacao(end + ", " + num);
        }
      
    }
    
    public void refresh(){
        WebUtils.update("form");
    }

    public void novoCliente() {
        cliente = new Cliente();
    }

    public void salvarNovoCliente() {
        this.cliente = this.getCliente();
        if (cliente == null) {
            WebUtils.messageWarn("Nenhum cliente Informado");
            return;
        }
        if (Utils.empty(cliente.getNome())) {
            WebUtils.messageWarn("Nome não informado");
            return;
        }
        cliente.setEmpresa(WebUtils.getEmpresa());
        cliente.setDataCadastro(new Date());

        if (!Utils.empty(cliente.getEmail()) && !Utils.emailValido(cliente.getEmail())) {
            WebUtils.messageWarn("Email Inválido");
            return;
        }
        this.cliente = clienteEJB.gravarCliente(cliente);
        orcamento.setCliente(cliente);
        WebUtils.update("form");
    }

    public void calcularSubtotal() {

        List<OrcamentoItem> orcamentoItens = this.recuperaItens();
        // recalculo das quantidades baseado no definido no insumo
        if (!Utils.empty(orcamentoItens)) {
            for (OrcamentoItem orcamentoIten : orcamentoItens) {
                if (recalcularInsumos) {
                    this.recalcularInsumos(orcamentoIten);
                }
            }

        }
        if (!this.acrescimoDescontoTotalVendaValidos()) {
            return;
        }
        totalComissao = BigDecimal.ZERO;
        totalInsumos = BigDecimal.ZERO;
        totalMaoObra = BigDecimal.ZERO;
        if (!Utils.empty(orcamentoItens)) {
            totalGeral = BigDecimal.ZERO;
            for (OrcamentoItem itemOrct : orcamentoItens) {

                // convertendo em unidade de medida diferente do que está no estoque
                if (empresa.isControlaEstoque()) {
                    this.converteUnidadeMedida(itemOrct);
                }

                if (Utils.empty(itemOrct.getUnidadeMedidaSelecionada()) && itemOrct.getUnidadeMedida() != null) {
                    itemOrct.setUnidadeMedidaSelecionada(itemOrct.getUnidadeMedida().getSigla().getValue());
                }
                this.aplicarDescontoAcrescimo(itemOrct);

                BigDecimal subTotal = itemOrct.getValorTotal();

                itemOrct.setValorTotal(subTotal);
                totalGeral = totalGeral.add(subTotal);
                if (itemOrct.isInsumo()) {
                    totalInsumos = totalInsumos.add(subTotal);
                }
                EnumTipoProduto tipoProd = itemOrct.getProduto().getTipoProduto().getEnumTiipoProduto();
                if (tipoProd != null && tipoProd.equals(EnumTipoProduto.MAO_DE_OBRA)) {
                    totalMaoObra = totalMaoObra.add(itemOrct.getValorTotal());
                }

            }

            descontoTotalVenda = Utils.nvl(descontoTotalVenda, BigDecimal.ZERO);
            acrescimoTotalVenda = Utils.nvl(acrescimoTotalVenda, BigDecimal.ZERO);
            totalGeral = totalGeral.add(acrescimoTotalVenda).subtract(descontoTotalVenda);
        } else {
            totalGeral = BigDecimal.ZERO;
            descontoTotalVenda = BigDecimal.ZERO;
            acrescimoTotalVenda = BigDecimal.ZERO;
            totalMaoObra = BigDecimal.ZERO;
            totalInsumos = BigDecimal.ZERO;
        }

        BigDecimal percComissao = orcamento.getUsuarioExecutante().getPercComissao();
        if (percComissao != null && percComissao.compareTo(BigDecimal.ZERO) > 0) {
            this.calculaComissao(percComissao);
        }
        //    WebUtils.update("form");
    }

    public void aplicarComissaoManual() {
        this.orcamento.setValorComissao(this.totalComissao);
    }

    private void aplicarDescontoAcrescimo(OrcamentoItem item) {

        BigDecimal vlrUnit = Utils.nvl(item.getValorUnitario(), BigDecimal.ZERO);
        BigDecimal vlrAcre = Utils.nvl(item.getValorAcrescimo(), BigDecimal.ZERO);
        BigDecimal percDesc = Utils.nvl(item.getPercDesconto(), BigDecimal.ZERO);
        BigDecimal percAcresc = Utils.nvl(item.getPercAcrescimo(), BigDecimal.ZERO);

        BigDecimal qtd = item.getQuantidade();
        BigDecimal vlrProd = vlrUnit.multiply(qtd != null ? qtd : BigDecimal.ONE);

        if (percDesc.compareTo(BigDecimal.ZERO) > 0) {
            item.setValorDesconto(percDesc.multiply((vlrProd)).divide(BigDecimal.valueOf(100)));
        }
        if (percDesc.compareTo(BigDecimal.ZERO) == 0) {
            item.setValorDesconto(BigDecimal.ZERO);
        }
        if (percAcresc.compareTo(BigDecimal.ZERO) > 0) {
            item.setValorAcrescimo((percAcresc.multiply(vlrProd)).divide(BigDecimal.valueOf(100)));
        }
        if (percAcresc.compareTo(BigDecimal.ZERO) == 0) {
            item.setValorAcrescimo(BigDecimal.ZERO);
        }
        Multiplicador multiplicador = orcamento.getMultiplicador();
        if (multiplicador != null) {
            vlrAcre = vlrProd.multiply(multiplicador.getFator());
            item.setValorAcrescimo(vlrAcre.subtract(vlrProd));
        } else {
            item.setValorAcrescimo(BigDecimal.ZERO);
        }

        BigDecimal subTotal = (vlrProd).add(item.getValorAcrescimo()).subtract(item.getValorDesconto());

        item.setValorTotal(subTotal);
    }

    private boolean acrescimoDescontoTotalVendaValidos() {
        if ((descontoTotalVenda != null && descontoTotalVenda.compareTo(BigDecimal.ZERO) > 0)
                && (acrescimoTotalVenda != null && acrescimoTotalVenda.compareTo(BigDecimal.ZERO) > 0)) {
            WebUtils.messageWarn("Nao é permitido acrescimo e desconto na mesma venda");
            descontoTotalVenda = BigDecimal.ZERO;
            acrescimoTotalVenda = BigDecimal.ZERO;
            return false;
        }
        return true;
    }

    private void converteUnidadeMedida(OrcamentoItem item) {
        String umSel = item.getUnidadeMedidaSelecionada();
        Estoque estoque = item.getProduto().getEstoque();
        if (estoque != null && !Utils.empty(umSel)) {
            UnidadeMedida umMedItem = OrcamentoControllerUtil.getUnidadeMedidaPorSigla(umSel, unidadeMedidaEJB);
            UnidadeMedida umEstoque = estoque.getUnidadeMedida();
            if (umMedItem != null && umEstoque != null && item.isSelecionado()) {
                BigDecimal divisor = UnidadeMedidaUtils.getDivisorConversao(umEstoque, umMedItem);
                if (divisor.compareTo(BigDecimal.ONE) != 0) {
                    BigDecimal vlrUnitItem = item.getValorUnitario() != null ? item.getValorUnitario() : BigDecimal.ZERO;
                    vlrUnitItem = vlrUnitItem.divide(divisor);
                    item.setValorUnitario(vlrUnitItem);
                } else {
                    item.setValorUnitario(this.recuperarValorOriginal(item));
                }
                item.setSelecionado(false);
            }
        }
    }

    private void calculaComissao(BigDecimal percComissao) {
        if (empresa.isComissaoTotalVenda()) {
            totalComissao = (totalGeral.multiply(percComissao)).divide(BigDecimal.valueOf(100));
        } else if (empresa.isComissaoTotalMaoObra() && totalMaoObra != null && totalMaoObra.compareTo(BigDecimal.ZERO) > 0) {
            totalComissao = (totalMaoObra.multiply(percComissao)).divide(BigDecimal.valueOf(100));
        }
        totalComissao = totalComissao.setScale(2, RoundingMode.HALF_EVEN);
        orcamento.setValorComissao(totalComissao);
    }

    private BigDecimal recuperarValorOriginal(OrcamentoItem item) {
        if (tabelaPreco != null) {
            List<TabelaPrecoItem> itensTabPreco = tabelaPreco.getTabelaPrecoItemList();
            if (!Utils.empty(itensTabPreco)) {
                for (TabelaPrecoItem tabelaPrecoItem : itensTabPreco) {
                    if (tabelaPrecoItem.getProduto().equals(item.getProduto())) {
                        return tabelaPrecoItem.getValorVenda();
                    }
                }
            }
        }
        return BigDecimal.ZERO;
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

    public void selecionarItem(OrcamentoItem item) {
        List<OrcamentoItem> itens = this.recuperaItens();
        if (!Utils.empty(itens)) {
            for (OrcamentoItem oi : itens) {
                if (!oi.getProduto().equals(item.getProduto()) && oi.isSelecionado()) {
                    oi.setSelecionado(false);
                }
            }

        }
        item.setSelecionado(true);
        gambiarra= true;
        itemSelecionado = item;
        valorAcrescimo = item.getValorAcrescimo();
        valorDesconto = item.getValorDesconto();
        percAcrescimo = item.getPercAcrescimo();
        percDesconto = item.getPercDesconto();
    }

    public void confirmaEdicao() {
        if (!acrescimoDescontoValidos()) {
            WebUtils.messageWarn("Acrescimo e desconto nao podem ser informados para o mesmo item");
            inicializarValores();
            return;
        }

        List<OrcamentoItem> itens = this.recuperaItens();
        if (!Utils.empty(itens)) {
            for (OrcamentoItem it : itens) {

                TabelaPrecoItem tpi = it.getTabelaPrecoItem();
                if (it.isSelecionado()) {
                    if (percDesconto != null && percDesconto.compareTo(BigDecimal.ZERO) > 0 && tpi != null) {

                        BigDecimal percMaxDesc = tpi.getPercMaxDesc();
                        if (percMaxDesc != null && percMaxDesc.compareTo(BigDecimal.ZERO) > 0) {
                            if (percMaxDesc.compareTo(percDesconto) < 0) {
                                WebUtils.messageWarn("O percentual de desconto(" + percDesconto + ") ultrapassa o maximo permitido(" + percMaxDesc + ") para o produto " + it.getProduto().getDescricao());
                            }
                        }
                    }

                    if (percAcrescimo != null && percAcrescimo.compareTo(BigDecimal.ZERO) > 0) {
                        if (it.getValorUnitario() == null || it.getValorUnitario().compareTo(BigDecimal.ZERO) == 0) {
                            WebUtils.messageWarn("Informe o valor unitario do item antes de informar o valor/percentual de acrescimo");
                            return;
                        }
                    }

                    if (percDesconto != null && percDesconto.compareTo(BigDecimal.ZERO) > 0) {
                        if (it.getValorUnitario() == null || it.getValorUnitario().compareTo(BigDecimal.ZERO) == 0) {
                            WebUtils.messageWarn("Informe o valor unitario do item antes de informar o valor/percentual de acrescimo ou desconto");
                            return;
                        }
                    }
                    it.setPercDesconto(percDesconto);
                    it.setPercAcrescimo(percAcrescimo);
                    it.setObservacao(observacao);
                }

            }

            this.calcularSubtotal();
            observacao = null;
        }
        inicializarValores();

        WebUtils.update("form");
    }

    public void addQtd(OrcamentoItem item) {
        item.setQuantidade(item.getQuantidade() != null ? item.getQuantidade().add(BigDecimal.ONE) : BigDecimal.ONE);
        this.calcularSubtotal();
    }

    public void minQtd(OrcamentoItem item) {
        item.setQuantidade(item.getQuantidade() != null ? item.getQuantidade().subtract(BigDecimal.ONE) : BigDecimal.ONE);
        this.calcularSubtotal();
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
        valorDesconto = BigDecimal.ZERO;
        percDesconto = BigDecimal.ZERO;
        percAcrescimo = BigDecimal.ZERO;
        valorAcrescimo = BigDecimal.ZERO;
        descontoTotalVenda = BigDecimal.ZERO;
        acrescimoTotalVenda = BigDecimal.ZERO;
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

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getPercDesconto() {
        return percDesconto;
    }

    public void setPercDesconto(BigDecimal percDesconto) {
        this.percDesconto = percDesconto;
    }

    public BigDecimal getValorAcrescimo() {
        return valorAcrescimo;
    }

    public void setValorAcrescimo(BigDecimal valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public BigDecimal getPercAcrescimo() {
        return percAcrescimo;
    }

    public void setPercAcrescimo(BigDecimal percAcrescimo) {
        this.percAcrescimo = percAcrescimo;
    }

    public OrcamentoItem getItemSelecionado() {
        return itemSelecionado;
    }

    public boolean acrescimoDescontoValidos() {
        return !((valorAcrescimo.compareTo(BigDecimal.ZERO) > 0 || percAcrescimo.compareTo(BigDecimal.ZERO) > 0) && (valorDesconto.compareTo(BigDecimal.ZERO) > 0 || percDesconto.compareTo(BigDecimal.ZERO) > 0));
    }

    public TabelaPreco getTabelaPreco() {
        return tabelaPreco;
    }

    public void setTabelaPreco(TabelaPreco tabelaPreco) {
        this.tabelaPreco = tabelaPreco;
        if (tabelaPreco != null) {
            List<OrcamentoItem> itens = this.recuperaItens();
            List<TabelaPrecoItem> tabelaPrecoItens = tabelaPreco.getTabelaPrecoItemList();
            if (!Utils.empty(itens)) {
                for (OrcamentoItem itemOrc : itens) {

                    itemOrc.setValorAcrescimo(BigDecimal.ZERO);
                    itemOrc.setValorDesconto(BigDecimal.ZERO);
                    itemOrc.setPercAcrescimo(BigDecimal.ZERO);
                    itemOrc.setPercDesconto(BigDecimal.ZERO);
                    itemOrc.setValorUnitario(BigDecimal.ZERO);
                    itemOrc.setQuantidade(BigDecimal.ONE);
                    itemOrc.setValorCusto(BigDecimal.ZERO);
                    itemOrc.setTabelaPrecoItem(null);
                    Estoque estoque = itemOrc.getProduto().getEstoque();
                    if (estoque != null) {
                        itemOrc.setUnidadeMedida(estoque.getUnidadeMedida());
                        itemOrc.setUnidadeMedidaSelecionada(estoque.getUnidadeMedida() != null ? estoque.getUnidadeMedida().getSigla().toString() : null);
                    }
                    for (TabelaPrecoItem tpi : tabelaPrecoItens) {

                        if (tpi.getProduto().equals(itemOrc.getProduto())) {
                            itemOrc.setTabelaPrecoItem(tpi);
                            itemOrc.setValorCusto(tpi.getValorCompra());
                            itemOrc.setValorUnitario(tpi.getValorVenda());
                            itemOrc.setTabelaPrecoItem(tpi);
                        }
                    }

                }

            }
        }
        this.calcularSubtotal();
        //  WebUtils.update("form");

    }

    public List<OrcamentoItem> recuperaItens() {
        return orcamento != null ? orcamento.getOrcamentoItemList() : null;
    }

    public void imprimirOrcamento(Orcamento orc) {
        this.context = FacesContext.getCurrentInstance();
        this.response = (HttpServletResponse) context.getExternalContext().getResponse();
        baos = new ByteArrayOutputStream();
        this.salvar(true);
        try {
            String logo = empresa.getLogoImage();
            if (logo == null) {
                logo = "empresas.png";
            }
            Map<String, Object> parametros = new HashMap<>();
            InputStream image = this.getClass().getResourceAsStream("/Images/" + logo);
            parametros.put("URL_IMAGE", image);

            JasperPrint print = relatoriosEJB.gerarOrcamento(orc != null ? orc : orcamento, parametros, empresa);
            JasperExportManager.exportReportToPdfStream(print, baos);
            Integer id = orc != null ? orc.getId() : orcamento.getId();
            String fileName = "Orcamento - " + id;
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

    public void setRecalcularInsumos(boolean recalcular) {
        this.recalcularInsumos = recalcular;
    }

    public boolean isRecalcularInsumos() {
        return recalcularInsumos;
    }

    public boolean isVenda() {
        return venda;
    }

    public void setVenda(boolean venda) {
        this.venda = venda;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getObservacao() {
        return observacao;
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

        if (this.venda && empresa.isFluxoCaixa() && orcamento.getFormaPagamento() == null) {
            WebUtils.messageWarn("Forma de pagamento nao informada");
            return false;
        }
        if (this.venda && empresa.isFluxoCaixa() && orcamento.getTipoPagamento() == null) {
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

    public List<String> listarUnidadeMedidas(Produto produto) {
        if (!WebUtils.getEmpresa().isControlaEstoque()) {
            return null;
        }
        // listando as unidades de medida do produto selecionado, caso a unidade seja a maior, lista a menor correspondente tambem
        UnidadeMedida unidMed = produto.getEstoque() != null ? produto.getEstoque().getUnidadeMedida() : null;
        List<String> retorno = new ArrayList<>();
        if (unidMed != null) {
            String unMed = unidMed.getSigla().getValue();
            if (!retorno.contains(unMed)) {
                retorno.add(unMed);
            }
            EnumUnidadeMedida unidadeConversao = OrcamentoControllerUtil.getUnidadeConversao(unidMed);
            String unConvert = unidadeConversao.getValue();
            if (!retorno.contains(unConvert)) {
                retorno.add(unConvert);
            }

        } else {
            retorno.add(EnumUnidadeMedida.UN.getValue());
            return retorno;
        }
        return retorno;
    }

    public void atualizaItem(OrcamentoItem item) {
        if (!empresa.isControlaEstoque()) {
            return;
        }
        item.setSelecionado(true);
        this.calcularSubtotal();
    }

    public void setUsuarioExecutante(Usuario usuarioExecutante) {
        this.usuarioExecutante = usuarioExecutante;
        orcamento.setUsuarioExecutante(usuarioExecutante);
        this.calcularSubtotal();
    }

    public Usuario getUsuarioExecutante() {
        return usuarioExecutante;
    }

    public BigDecimal getDescontoTotalVenda() {
        return descontoTotalVenda;
    }

    public void setDescontoTotalVenda(BigDecimal descontoTotalVenda) {
        this.descontoTotalVenda = descontoTotalVenda;
    }

    public BigDecimal getAcrescimoTotalVenda() {
        return acrescimoTotalVenda;
    }

    public void setAcrescimoTotalVenda(BigDecimal acrescimoTotalVenda) {
        this.acrescimoTotalVenda = acrescimoTotalVenda;
    }

    public List<Cidade> listarCidades() {
        return cidadeEJB.findAll();
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

    public void aplicarMultiplicador(Multiplicador multiplicador) {
        orcamento.setMultiplicador(multiplicador);
        this.calcularSubtotal();
    }

    public boolean isExisteInsumos() {
        return existeInsumos;
    }

    public void setExisteInsumos(boolean existeInsumos) {
        this.existeInsumos = existeInsumos;
    }

    public LazyDataModel<Orcamento> getModel() {
        return model;
    }

    public void validarPreVenda() {
        if (this.venda) {
            List<TipoPagamento> tiposPgto = this.listarTipoPagamentos();
            if (Utils.empty(tiposPgto)) {
                WebUtils.messageWarn("Deve ser cadastrado ao menos um Tipo de pagamento para efetivar uma VENDA");
                this.venda = false;
                return;
            }
            List<FormaPagamento> formaPgto = this.listarFormaPagamentos();
            if (Utils.empty(formaPgto)) {
                WebUtils.messageWarn("Deve ser cadastrado ao menos uma Forma de pagamento para efetivar uma VENDA");
                this.venda = false;
                return;
            }
            try {
                caixa = caixaEJB.getCaixaAberto(empresa);

            } catch (GestorException ex) {
                ex.printStackTrace();
                WebUtils.messageWarn(ex.getMessage());
                this.venda = false;

            }
            orcamento.setDataVenda(new Date());
        } else {
            orcamento.setDataVenda(null);
        }
    }

    public boolean isDisableAllFields() {
        return disableAllFields;
    }

    public void setDisableAllFields(boolean disableAllFields) {
        this.disableAllFields = disableAllFields;
    }

    public List<VeiculoCliente> listarVeiculos() {
        if (orcamento == null) {
            return null;
        }
        List<VeiculoCliente> veiculoClientes = veiculoClienteEJB.listarVeiculos(empresa, orcamento.getCliente());
        return veiculoClientes;
    }

    public List<VeiculoCliente> listarVeiculosCliente() {
        if (orcamento == null) {
            return null;
        }
        List<VeiculoCliente> veiculoClientes = veiculoClienteEJB.listarVeiculos(empresa, orcamento.getCliente());
        return veiculoClientes;
    }

    public void removerVeiculo() {
        List<OrcamentoItem> itens = this.recuperaItens();
        if (!Utils.empty(itens)) {
            for (OrcamentoItem it : itens) {
                if (it.isSelecionado()) {
                    it.setObservacao(null);
                    it.setVeiculo(null);
                    it.setSelecionado(false);
                     if(it.getId() != null){
                        orcamentoEJB.mergeItem(it);
                    }
                }
            }
        }

    }

    public void addVeiculo(VeiculoCliente veiculoCliente) {
        if (orcamento.getCliente() == null) {
            orcamento.setCliente(veiculoCliente.getCliente());
        }
        List<OrcamentoItem> itens = this.recuperaItens();
        if (!Utils.empty(itens)) {
            for (OrcamentoItem it : itens) {
                if (it.isSelecionado()) {
                    it.setObservacao(veiculoCliente.getVeiculo() + " - " + veiculoCliente.getPlacaVeiculo());
                    it.setVeiculo(veiculoCliente);
                    it.setSelecionado(false);
                    if(it.getId() != null){
                        orcamentoEJB.mergeItem(it);
                    }
                }
            }
        }

      //  WebUtils.update("form");
    }

    public VeiculoCliente getVeiculoCliente() {
        return veiculoCliente;
    }

    public void setVeiculoCliente(VeiculoCliente veiculoCliente) {
        this.veiculoCliente = veiculoCliente;
    }

    public void novoVeiculo() {
        veiculoCliente = new VeiculoCliente();
        veiculoCliente.setAtivo(true);
    }

    public void salvarNovoVeiculo() {
        if (orcamento.getCliente() == null) {
            WebUtils.messageWarn("É necessário informar um cliente antes de cadastrar um veiculo novo");
            return;
        }
        if (veiculoCliente == null) {
            WebUtils.messageWarn("Nenhum veículo informado");
            return;
        }
        if (Utils.empty(veiculoCliente.getVeiculo())) {
            WebUtils.messageWarn("Informe a descrição do veículo");
            return;
        }
        Cliente cliente = orcamento.getCliente();
        veiculoCliente.setCliente(cliente);
        List<VeiculoCliente> veiculos = cliente.getVeiculos();
        if (Utils.empty(veiculos)) {
            veiculos = new ArrayList<>();
            veiculos.add(veiculoCliente);
            cliente.setVeiculos(veiculos);
        } else {
            orcamento.getCliente().getVeiculos().add(veiculoCliente);
        }
        this.addVeiculo(veiculoCliente);
    }

    public boolean isGambiarra() {
        return gambiarra;
    }

    public void setGambiarra(boolean gambiarra) {
        this.gambiarra = gambiarra;
    }
    
    

}
