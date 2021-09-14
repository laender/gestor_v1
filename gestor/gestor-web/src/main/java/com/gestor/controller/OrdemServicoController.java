/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.ClienteEJB;
import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.OrdemServicoEJB;
import com.gestor.ejb.RelatoriosEJB;
import com.gestor.ejb.TabelaPrecoEJB;
import com.gestor.ejb.UnidadeMedidaEJB;
import com.gestor.ejb.UsuarioEJB;
import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.OrcamentoItem;
import com.gestor.entidades.Produto;
import com.gestor.entidades.OrdemServico;
import com.gestor.entidades.OrdemServicoItem;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TabelaPrecoItem;
import com.gestor.entidades.Usuario;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
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
public class OrdemServicoController implements Serializable, ICadastros {

    private OrdemServico ordemServico = null;

    private Produto produto;

    private Cliente cliente;

    private TabelaPreco tabelaPreco;

    @EJB
    private OrdemServicoEJB ordemServicoEJB;
    @EJB
    private ProdutoEJB produtoEJB;

    @EJB
    private UnidadeMedidaEJB unidadeMedidaEJB;

    @EJB
    private UsuarioEJB usuarioEJB;

    @EJB
    private ClienteEJB clienteEJB;

    @EJB
    private TabelaPrecoEJB tabelaPrecoEJB;

    private Empresa empresa;

    private OrdemServicoItem itemSelecionado;

    private HttpServletResponse response;
    private FacesContext context;
    private ByteArrayOutputStream baos;
    private InputStream stream;
    private Connection con;

    private BigDecimal valorDesconto = BigDecimal.ZERO;
    private BigDecimal percDesconto = BigDecimal.ZERO;
    private BigDecimal valorAcrescimo = BigDecimal.ZERO;
    private BigDecimal percAcrescimo = BigDecimal.ZERO;
    private String observacao = null;
    private List<OrdemServico> ordemServicos;

    private boolean listarOrdemServicosConcluidas;

    @EJB
    private RelatoriosEJB relatoriosEJB;

    public OrdemServicoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        empresa = WebUtils.getEmpresa();
        ordemServicos = this.listarOrdemServicosAbertas();
    }

    public void novo() {

        ordemServico = new OrdemServico();
        ordemServico.setDataEmissao(new Date());
        ordemServico.setUsuario(WebUtils.getUsuario());
        ordemServico.setUsuarioExecutante(WebUtils.getUsuario());
        ordemServico.setValorPago(BigDecimal.ZERO);
        ordemServico.setValorTotal(BigDecimal.ZERO);
        ordemServico.setValorSaldo(BigDecimal.ZERO);
        valorDesconto = BigDecimal.ZERO;
        percDesconto = BigDecimal.ZERO;
        valorAcrescimo = BigDecimal.ZERO;
        percAcrescimo = BigDecimal.ZERO;
        observacao = null;
        tabelaPreco = tabelaPrecoEJB.getTabelaPrecoPadrao(empresa);
        WebUtils.update("form");

    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        OrdemServico orcBd = ordemServicoEJB.find(ordemServico.getId());
        this.ordemServico = orcBd;
        this.calcularSubtotal();
        WebUtils.update("form");

    }

    public void recarregarListagem() {
        if (listarOrdemServicosConcluidas) {
            ordemServicos = this.listarOrdemServicosConcluidas();
        } else {
            ordemServicos = this.listarOrdemServicosAbertas();
        }
    }

    public List<OrdemServico> listarOrdemServicosAbertas() {
        return ordemServicoEJB.getOrdemServicosAbertas(empresa);
    }

    public List<OrdemServico> listarOrdemServicosConcluidas() {
        return ordemServicoEJB.getOrdemServicosConcluidas(empresa);
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void addProduto(Produto produto) {
        this.addProdutoInsumo(produto, null);
    }

    private void addProdutoInsumo(Produto produto, BigDecimal qtd) {
        List<OrdemServicoItem> itens = this.recuperaItens();
        if (itens != null && !itens.isEmpty()) {
            for (OrdemServicoItem tpi : itens) {
                Produto i = tpi.getProduto();
                if (i.equals(produto)) {
                    return;
                }
            }
        } else {
            itens = new ArrayList<>();
            ordemServico.setItens(itens);
        }

        OrdemServicoItem itemNovo = this.criarItem(produto, qtd);

        ordemServico.getItens().add(itemNovo);
        this.calcularSubtotal();
        //  WebUtils.update("form");
    }

    private OrdemServicoItem criarItem(Produto produto, BigDecimal qtd) {
        OrdemServicoItem itemNovo = new OrdemServicoItem();
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

        itemNovo.setOrdemServico(ordemServico);

        return itemNovo;
    }

    public void removeItem(OrdemServicoItem item) {
        List<OrdemServicoItem> itens = this.recuperaItens();
        itens.remove(item);
        this.calcularSubtotal();
        //  removendo aqui pq ele nao remove no metodo salvar...ele sai da lista mas nao é removido do banco :/ assim funciona
        //  ordemServicoEJB.removerItem(item);
        WebUtils.update("form");
    }

    public List<Produto> listarProdutos() {
        return produtoEJB.findAll(empresa);
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
            ordemServicoEJB.merge(ordemServico);

        } catch (Exception e) {
            e.printStackTrace();
            WebUtils.messageError("Erro ao salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        if (!imprimir) {
            WebUtils.messageRegistroGravado();
            cancelar();

        }
        cancelar();
    }

    @Override
    public void cancelar() {
        ordemServico = null;
        itemSelecionado = null;
        produto = null;
        tabelaPreco = null;
        cliente = null;
        inicializaerValores();
        ordemServicos = this.listarOrdemServicosAbertas();
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
            ordemServicoEJB.remove(ordemServico);
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
        ordemServico.setCliente(cliente);
        WebUtils.update("form");
    }

    public void calcularSubtotal() {
        List<OrdemServicoItem> itens = this.recuperaItens();
        ordemServico.setValorTotal(BigDecimal.ZERO);

        if (!Utils.empty(itens)) {
            for (OrdemServicoItem item : itens) {
                this.aplicarDescontoAcrescimo(item);
                BigDecimal subTotal = item.getValorTotal();

                item.setValorTotal(subTotal);
                BigDecimal valorPago = ordemServico.getValorPago();
                BigDecimal valorTotal = ordemServico.getValorTotal();
                ordemServico.setValorTotal(valorTotal.add(subTotal));

                ordemServico.setValorSaldo((valorTotal.add(subTotal)).subtract(valorPago));
            }
        }

        //    WebUtils.update("form");
    }

    private void aplicarDescontoAcrescimo(OrdemServicoItem item) {

        BigDecimal vlrUnit = Utils.nvl(item.getValorUnitario(), BigDecimal.ZERO);
        BigDecimal percDesc = Utils.nvl(item.getPercDesconto(), BigDecimal.ZERO);
        BigDecimal percAcresc = Utils.nvl(item.getPercAcrescimo(), BigDecimal.ZERO);

        BigDecimal qtd = item.getQuantidade();
        BigDecimal vlrProd = vlrUnit.multiply(qtd);

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

        BigDecimal subTotal = (vlrProd).add(item.getValorAcrescimo()).subtract(item.getValorDesconto());

        item.setValorTotal(subTotal);
    }

    public void selecionarItem(OrdemServicoItem item) {
        List<OrdemServicoItem> itens = this.recuperaItens();
        if (!Utils.empty(itens)) {
            for (OrdemServicoItem iten : itens) {
                if (!iten.getProduto().equals(item.getProduto()) && iten.isSelecionado()) {
                    iten.setSelecionado(false);
                }
            }
                
        }
        item.setSelecionado(true);
        itemSelecionado = item;
    }

    public void confirmaEdicao() {
        if (!acrescimoDescontoValidos()) {
            WebUtils.messageWarn("Acrescimo e desconto nao podem ser informados para o mesmo item");
            inicializaerValores();
            return;
        }

        List<OrdemServicoItem> itens = this.recuperaItens();
        if (!Utils.empty(itens)) {
            for (OrdemServicoItem iten : itens) {
                TabelaPrecoItem tpi = iten.getTabelaPrecoItem();
                if (iten.isSelecionado()) {
                    if (percDesconto != null && percDesconto.compareTo(BigDecimal.ZERO) > 0 && tpi != null) {

                        BigDecimal percMaxDesc = tpi.getPercMaxDesc();
                        if (percMaxDesc != null && percMaxDesc.compareTo(BigDecimal.ZERO) > 0) {
                            if (percMaxDesc.compareTo(percDesconto) < 0) {
                                WebUtils.messageWarn("O percentual de desconto(" + percDesconto + ") ultrapassa o maximo permitido(" + percMaxDesc + ") para o produto " + iten.getProduto().getDescricao());
                            }
                        }
                    }

                    if (percAcrescimo != null && percAcrescimo.compareTo(BigDecimal.ZERO) > 0) {
                        if (iten.getValorUnitario() == null || iten.getValorUnitario().compareTo(BigDecimal.ZERO) == 0) {
                            WebUtils.messageWarn("Informe o valor unitario do item antes de informar o valor/percentual de acrescimo");
                            return;
                        }
                    }

                    if (percDesconto != null && percDesconto.compareTo(BigDecimal.ZERO) > 0) {
                        if (iten.getValorUnitario() == null || iten.getValorUnitario().compareTo(BigDecimal.ZERO) == 0) {
                            WebUtils.messageWarn("Informe o valor unitario do item antes de informar o valor/percentual de acrescimo ou desconto");
                            return;
                        }
                    }
                    iten.setPercDesconto(percDesconto);
                    iten.setPercAcrescimo(percAcrescimo);
                    iten.setObservacao(observacao);
                }
            }
            this.calcularSubtotal();
        }
        inicializaerValores();

        WebUtils.update("form");
    }

    public void addQtd(OrdemServicoItem item) {
        item.setQuantidade(item.getQuantidade() != null ? item.getQuantidade().add(BigDecimal.ONE) : BigDecimal.ONE);
        this.calcularSubtotal();
    }

    public void minQtd(OrdemServicoItem item) {
        item.setQuantidade(item.getQuantidade() != null ? item.getQuantidade().subtract(BigDecimal.ONE) : BigDecimal.ONE);
        this.calcularSubtotal();
    }

    private void inicializaerValores() {
        valorDesconto = BigDecimal.ZERO;
        percDesconto = BigDecimal.ZERO;
        percAcrescimo = BigDecimal.ZERO;
        valorAcrescimo = BigDecimal.ZERO;
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

    public OrdemServicoItem getItemSelecionado() {
        return itemSelecionado;
    }

    public boolean acrescimoDescontoValidos() {
        if ((valorAcrescimo.compareTo(BigDecimal.ZERO) > 0 || percAcrescimo.compareTo(BigDecimal.ZERO) > 0) && (valorDesconto.compareTo(BigDecimal.ZERO) > 0 || percDesconto.compareTo(BigDecimal.ZERO) > 0)) {
            return false;
        }
        return true;
    }

    public TabelaPreco getTabelaPreco() {
        return tabelaPreco;
    }

    public void setTabelaPreco(TabelaPreco tabelaPreco) {
        this.tabelaPreco = tabelaPreco;
        if (tabelaPreco != null) {
            List<OrdemServicoItem> itens = this.recuperaItens();
            List<TabelaPrecoItem> tabelaPrecoItens = tabelaPreco.getTabelaPrecoItemList();
            if (!Utils.empty(itens)) {
                for (OrdemServicoItem iten : itens) {
                    iten.setValorAcrescimo(BigDecimal.ZERO);
                    iten.setValorDesconto(BigDecimal.ZERO);
                    iten.setPercAcrescimo(BigDecimal.ZERO);
                    iten.setPercDesconto(BigDecimal.ZERO);
                    iten.setValorUnitario(BigDecimal.ZERO);
                    iten.setQuantidade(BigDecimal.ONE);
                    iten.setValorCusto(BigDecimal.ZERO);
                    iten.setTabelaPrecoItem(null);
                    for (TabelaPrecoItem tpi : tabelaPrecoItens) {
                        if (tpi.getProduto().equals(iten.getProduto())) {
                            iten.setTabelaPrecoItem(tpi);
                            iten.setValorCusto(tpi.getValorCompra());
                            iten.setValorUnitario(tpi.getValorVenda());
                            iten.setTabelaPrecoItem(tpi);
                        }
                    }
                }
            }
        }
        this.calcularSubtotal();
        WebUtils.update("form");

    }

    public List<OrdemServicoItem> recuperaItens() {
        return ordemServico != null ? ordemServico.getItens() : null;
    }

    public void imprimirOrdemServico(OrdemServico orc) {
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

            JasperPrint print = relatoriosEJB.gerarOrdemServico(orc != null ? orc : ordemServico, parametros);
            JasperExportManager.exportReportToPdfStream(print, baos);
            Integer id = orc != null ? orc.getId() : ordemServico.getId();
            String fileName = "OrdemServico - " + id;
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

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getObservacao() {
        return observacao;
    }

    private boolean isValido() {
        if (ordemServico == null) {
            WebUtils.messageWarn("Nenhum ordemServico informado");
            return false;
        }

        if (ordemServico.getUsuario() == null) {
            WebUtils.messageWarn("Profissional nao informado");
            return false;
        }

        if (ordemServico.getCliente() == null) {
            WebUtils.messageWarn("Cliente nao informado");
            return false;
        }
        if (Utils.empty(ordemServico.getItens())) {
            WebUtils.messageWarn("Nenhum item informado");
            return false;
        }
        return true;
    }

    public boolean isItemValido() {
        for (OrdemServicoItem ordemServicoItem : ordemServico.getItens()) {
            if (!Utils.empty(ordemServicoItem.getPercDesconto()) && ordemServicoItem.getPercDesconto().compareTo(BigDecimal.valueOf(100)) > 0) {
                WebUtils.messageWarn("O percentual maximo de desconto nao pode ultrapassar 100%");
                return false;
            }
            if (ordemServicoItem.getValorUnitario() == null || ordemServicoItem.getValorUnitario().compareTo(BigDecimal.ZERO) == 0) {
                WebUtils.messageWarn("O produto " + ordemServicoItem.getProduto().getDescricao() + " nao possui valor, verifique ou remova o item");
                return false;
            }
            if (!Utils.empty(ordemServicoItem.getValorDesconto()) && ordemServicoItem.getValorDesconto().compareTo(ordemServicoItem.getValorTotal()) > 0) {
                WebUtils.messageWarn("O valor maximo de desconto nao pode ultrapassar o valor total do item " + ordemServicoItem.getProduto().getDescricao());
                return false;
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

    public void atualizaItem(OrdemServicoItem item) {
        if (!empresa.isControlaEstoque()) {
            return;
        }
        item.setSelecionado(true);
        this.calcularSubtotal();
    }

    public List<String> listarCombustiveis() {
        List<String> combustiveis = new ArrayList<>();
        combustiveis.add("ETANOL");
        combustiveis.add("DIESEL");
        combustiveis.add("GASOLINA");
        combustiveis.add("GNV");
        combustiveis.add("OUTROS");
        return combustiveis;
    }

    public void calcularSaldo() {
        BigDecimal valorPago = ordemServico.getValorPago();
        if (valorPago != null) {
            ordemServico.setValorSaldo(ordemServico.getValorTotal().subtract(ordemServico.getValorPago()));
        }
        WebUtils.update("form");
    }

    public List<OrdemServico> getOrdemServicos() {
        return ordemServicos;
    }

    public void setOrdemServicos(List<OrdemServico> ordemServicos) {
        this.ordemServicos = ordemServicos;
    }

    public boolean isListarOrdemServicosConcluidas() {
        return listarOrdemServicosConcluidas;
    }

    public void setListarOrdemServicosConcluidas(boolean listarOrdemServicosConcluidas) {
        this.listarOrdemServicosConcluidas = listarOrdemServicosConcluidas;
    }

}
