/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.BancoEJB;
import com.gestor.ejb.CaixaEJB;
import com.gestor.ejb.ClienteEJB;
import com.gestor.ejb.EstoqueEJB;
import com.gestor.ejb.OrcamentoEJB;
import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.TabelaPrecoEJB;
import com.gestor.ejb.TituloEJB;
import com.gestor.ejb.UsuarioEJB;
import com.gestor.entidades.Banco;
import com.gestor.entidades.Caixa;
import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Estoque;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.MovimentoEstoque;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.OrcamentoItem;
import com.gestor.entidades.Produto;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TabelaPrecoItem;
import com.gestor.entidades.TituloParcela;
import com.gestor.entidades.Usuario;
import com.gestor.enums.EnumStatusOrcamento;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.enums.EnumTipoProduto;
import com.gestor.enums.EnumTipoTitulo;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class GerenciadorController implements Serializable {

    @EJB
    private OrcamentoEJB orcamentoEJB;

    @EJB
    private TituloEJB tituloEJB;

    @EJB
    private ClienteEJB clienteEJB;

    @EJB
    private UsuarioEJB usuarioEJB;

    @EJB
    private CaixaEJB caixaEJB;

    @EJB
    private BancoEJB bancoEJB;

    @EJB
    private EstoqueEJB estoqueEJB;

    @EJB
    private ProdutoEJB produtoEJB;

    @EJB
    private TabelaPrecoEJB tabelaPrecoEJB;

    private Date dataInicial;
    private Date dataFinal;
    private Cliente cliente;
    private Orcamento orcamento;
    private Usuario usuario;
    private Banco banco;
    private List<Orcamento> orcamentos;
    List<MovimentoEstoque> movimentosEstoque;
    private List<Caixa> caixas;
    private Produto produto;
    private long hoje;
    private EnumTipoLancamento tipoLancamento;
    private BigDecimal totalGeralEntradas = BigDecimal.ZERO;
    private BigDecimal totalGeralSaidas = BigDecimal.ZERO;
    private BigDecimal totalCaixaPeriodo = BigDecimal.ZERO;
    private BigDecimal totalValorCompra = BigDecimal.ZERO;
    private BigDecimal totalComissao = BigDecimal.ZERO;

    private List<LancamentoFinanceiro> lancamentos;
    private List<TituloParcela> parcelas = null;

    private boolean somenteVendas = false;
    private boolean somenteCancelados = false;
    private boolean somenteOrcamentos = false;
    private boolean orcamentoAberto = false;
    private boolean titulosReceber = false;
    private boolean titulosPagar = false;
    private boolean exibirDetalhesCaixa = false;
    private Empresa empresa;
    private EnumTipoTitulo tipoTitulo;
    private LancamentoFinanceiro lancamentoFinanceiro;
    private List<Orcamento> vendasComissoes;

    private boolean titulosProvisionados = true;
    private boolean titulosRealizados = false;
    private boolean entradaEstoque = true;
    private boolean saidaEstoque = true;
    private BigDecimal totalPagar = BigDecimal.ZERO;
    private BigDecimal totalReceber = BigDecimal.ZERO;
    private String docEntradaSaidaEstoque;
    
    private BigDecimal totalVendasOrcamentos = BigDecimal.ZERO;
    private BigDecimal totalCustos = BigDecimal.ZERO;
    private BigDecimal totalLucro = BigDecimal.ZERO;

    public GerenciadorController() {
    }

    @PostConstruct
    private void init() {
        orcamentos = null;
        parcelas = null;
        caixas = null;
        banco = null;
        titulosProvisionados = true;
        titulosRealizados = true;
        totalGeralEntradas = BigDecimal.ZERO;
        totalGeralSaidas = BigDecimal.ZERO;
        totalCaixaPeriodo = BigDecimal.ZERO;
        docEntradaSaidaEstoque = null;
        totalPagar = BigDecimal.ZERO;
        totalReceber = BigDecimal.ZERO;

        exibirDetalhesCaixa = false;
        tipoTitulo = null;
        lancamentoFinanceiro = null;

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        dataInicial = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        dataFinal = cal.getTime();

        somenteCancelados = false;
        somenteOrcamentos = false;
        somenteVendas = false;
        orcamentoAberto = false;
        titulosPagar = true;
        titulosReceber = true;
        produto = null;
        empresa = WebUtils.getUsuario().getEmpresa();

    }

    public void gerarOrcamentos() {
        totalLucro = BigDecimal.ZERO;
        totalCustos = BigDecimal.ZERO;
        totalVendasOrcamentos = BigDecimal.ZERO;
        totalComissao = BigDecimal.ZERO;
        
        if (!datasValidas()) {
            WebUtils.messageWarn("A data inicial não pode ser maior do que a data final");
            return;
        }
        if (!WebUtils.getUsuario().isAdministrador()) {
            WebUtils.messageWarn("Somente usuario administrador pode gerar relatorios");
            return;
        }

        EnumStatusOrcamento status = null;
        List<EnumStatusOrcamento> array = new ArrayList<>();
        if (somenteCancelados) {
            status = EnumStatusOrcamento.CANCELADO;
            array.add(status);
        }
        if (somenteVendas) {
            status = EnumStatusOrcamento.VENDA;
            array.add(status);
        }
        if (somenteOrcamentos) {
            status = EnumStatusOrcamento.ORCAMENTO;
            array.add(status);
        }
        if (dataInicial == null) {
            WebUtils.messageWarn("Informe a data inicial");
            return;
        }
        if (dataFinal == null) {
            WebUtils.messageWarn("Informe a data final");
            return;
        }
        if (!datasValidas()) {
            WebUtils.messageWarn("A data inicial não pode ser maior do que a data final");
            return;
        }
        orcamentos = orcamentoEJB.listarOrcamentos(empresa, dataInicial, dataFinal, orcamento, usuario, cliente, array, orcamentoAberto);
        if (!Utils.empty(orcamentos)) {
            for (Orcamento orc : orcamentos) {
                BigDecimal totalCustos = BigDecimal.ZERO;
                BigDecimal totalLucro;
                BigDecimal totalAcrescimo = BigDecimal.ZERO;
                for (OrcamentoItem item : orc.getOrcamentoItemList()) {
                    BigDecimal valorCusto = Utils.nvl(item.getValorCusto(), BigDecimal.ZERO);
                    EnumTipoProduto tipoProdt = item.getProduto().getTipoProduto().getEnumTiipoProduto();
                    if (tipoProdt != null && tipoProdt.equals(EnumTipoProduto.FRETE)) {
                        valorCusto = valorCusto.equals(BigDecimal.ZERO) ? item.getValorUnitario() : valorCusto;
                    }
                    // se a empresa trabalha com multiplicador, o valor de custo é o valor lançado manualmente (unitario)
                    if (item.getOrcamento().getMultiplicador() != null) {
                        valorCusto = item.getValorUnitario();
                    }
                    totalCustos = totalCustos.add(valorCusto.multiply(item.getQuantidade()));
                    totalAcrescimo = totalAcrescimo.add(Utils.nvl(item.getValorAcrescimo(), BigDecimal.ZERO));
                }
                BigDecimal valorComissao = orc.getValorComissao() != null ? orc.getValorComissao() : BigDecimal.ZERO;
                // se a empresa trabalha com o indice multiplicador o lucro dela é isso - comissao
                if (orc.getMultiplicador() != null) {
                    totalLucro = totalAcrescimo.subtract(valorComissao);
                } else {
                    totalLucro = orc.getValorTotal().subtract(totalCustos).subtract(valorComissao);
                }
                orc.setTotalLucro(totalLucro);
                orc.setTotalCustos(totalCustos);
                this.totalCustos = this.totalCustos.add(totalCustos);
                this.totalLucro = this.totalLucro.add(totalLucro);
                this.totalComissao = this.totalComissao.add(valorComissao);
                this.totalVendasOrcamentos = this.totalVendasOrcamentos.add(orc.getValorTotal());
            }

        }
        WebUtils.update("form");
    }

    public void gerarLivroCaixa() {
        if (!WebUtils.getUsuario().isAdministrador()) {
            WebUtils.messageWarn("Somente usuario administrador pode gerar relatorios");
            return;
        }
        if (dataInicial == null) {
            WebUtils.messageWarn("Informe a data inicial");
            return;
        }
        if (dataFinal == null) {
            WebUtils.messageWarn("Informe a data final");
            return;
        }
        if (!datasValidas()) {
            WebUtils.messageWarn("A data inicial não pode ser maior do que a data final");
            return;
        }
        caixas = caixaEJB.getCaixas(empresa, dataInicial, dataFinal, usuario);
        totalGeralEntradas = BigDecimal.ZERO;
        totalGeralSaidas = BigDecimal.ZERO;
        totalCaixaPeriodo = BigDecimal.ZERO;
        if (!Utils.empty(caixas)) {
            for (Caixa caixa : caixas) {
                Double totalEntradas = 0d;
                Double totalSaidas = 0d;

                List<LancamentoFinanceiro> novosLctos = new ArrayList<>();
                List<LancamentoFinanceiro> lancamentos = caixa.getLancamentosFinanceiros();
                if (!Utils.empty(lancamentos)) {
                    for (LancamentoFinanceiro lancamento : lancamentos) {
                        //   if (empresa.isFluxoCaixa() && lancamento.getFormaPagamento() != null && lancamento.getFormaPagamento().isVendaAPrazo()) {
                        //      continue;
                        //  }
                        if (banco != null && (lancamento.getBanco() == null || !lancamento.getBanco().equals(banco))) {
                            continue;
                        }
                        if (lancamento.getTipoLancamento().equals(EnumTipoLancamento.ENTRADA)) {
                            totalEntradas += lancamento.getValor().doubleValue();
                        } else {
                            totalSaidas += lancamento.getValor().doubleValue();
                        }
                        novosLctos.add(lancamento);
                    }
                    caixa.setLancamentosFinanceiros(lancamentos);
                }
                totalGeralEntradas = totalGeralEntradas.add(BigDecimal.valueOf(totalEntradas));
                totalGeralSaidas = totalGeralSaidas.add(BigDecimal.valueOf(totalSaidas));
                caixa.setTotalEntradas(BigDecimal.valueOf(totalEntradas));
                caixa.setTotalSaidas(BigDecimal.valueOf(totalSaidas));

                Double saldo = totalEntradas - totalSaidas;
                Double valorInicial = caixa.getValorInicial() != null ? caixa.getValorInicial().doubleValue() : 0d;
                Double totalSaldo = saldo + valorInicial;
                caixa.setSaldo(BigDecimal.valueOf(totalSaldo));
                caixa.setValorFinal(caixa.getSaldo());
                if (valorInicial.compareTo(totalSaldo) != 0) {
                    totalCaixaPeriodo = totalCaixaPeriodo.add(BigDecimal.valueOf(totalSaldo));

                }
            }
            // totalCaixaPeriodo = totalGeralEntradas.subtract(totalGeralSaidas);
        }
        WebUtils.update("form");
    }

    public List<Usuario> listarUsuarios() {
        return usuarioEJB.getUsuarios(empresa);
    }

    public List<Cliente> listarClientes() {
        return clienteEJB.findAll(empresa);
    }

    public List<Orcamento> listarOrcamentos() {
        EnumStatusOrcamento status = null;
        List<EnumStatusOrcamento> array = new ArrayList<>();
        if (somenteCancelados) {
            status = EnumStatusOrcamento.CANCELADO;
            array.add(status);
        }
        if (somenteVendas) {
            status = EnumStatusOrcamento.VENDA;
            array.add(status);
        }
        if (somenteOrcamentos) {
            status = EnumStatusOrcamento.ORCAMENTO;
            array.add(status);
        }
        return status != null ? orcamentoEJB.getOrcamentosPorStatus(array, empresa) : orcamentoEJB.getOrcamentos(empresa);
    }

    public void listarVendasComissoes() {
        totalComissao = BigDecimal.ZERO;
        List<EnumStatusOrcamento> array = new ArrayList<>();
        array.add(EnumStatusOrcamento.VENDA);
        vendasComissoes = orcamentos = orcamentoEJB.listarOrcamentos(empresa, dataInicial, dataFinal, orcamento, usuario, null, array, false);
        if (!Utils.empty(vendasComissoes)) {
            for (Orcamento orcamento : vendasComissoes) {
                BigDecimal valorComissao = orcamento.getValorComissao() != null ? orcamento.getValorComissao() : BigDecimal.ZERO;
                totalComissao = totalComissao.add(valorComissao);
                List<OrcamentoItem> itens = orcamento.getOrcamentoItemList();
                for (OrcamentoItem item : itens) {
                    EnumTipoProduto tipoProd = item.getProduto().getTipoProduto().getEnumTiipoProduto();
                    if (tipoProd != null && tipoProd.equals(EnumTipoProduto.MAO_DE_OBRA)) {
                        BigDecimal totalMaoObraOrct = orcamento.getTotalMaoObra() != null ? orcamento.getTotalMaoObra() : BigDecimal.ZERO;
                        orcamento.setTotalMaoObra(totalMaoObraOrct.add(item.getValorTotal()));
                    }
                }
            }
        }
    }

    public void listarParcelas() {
        totalPagar = BigDecimal.ZERO;
        totalReceber = BigDecimal.ZERO;
        parcelas = null;
        if (titulosPagar && !titulosReceber) {
            tipoTitulo = EnumTipoTitulo.PAGAR;
        }
        if (titulosReceber && !titulosPagar) {
            tipoTitulo = EnumTipoTitulo.RECEBER;
        }
        if (!titulosPagar && !titulosReceber) {
            tipoTitulo = null;
        }
        if (titulosPagar && titulosReceber) {
            tipoTitulo = null;
        }
        if (!datasValidas()) {
            WebUtils.messageWarn("A data inicial não pode ser maior do que a data final");
            return;
        }

        parcelas = tituloEJB.listarParcelas(dataInicial, dataFinal, titulosProvisionados, usuario, titulosRealizados, tipoTitulo, empresa, cliente, orcamento, lancamentoFinanceiro);
        if (!Utils.empty(parcelas)) {
            for (TituloParcela parcela : parcelas) {
                BigDecimal saldoParcela = parcela.getValorSaldo();
                if (parcela.getTitulo().getTipo().equals(EnumTipoTitulo.PAGAR)) {
                    totalPagar = totalPagar.add(saldoParcela);
                } else {
                    totalReceber = totalReceber.add(saldoParcela);
                }
            }

        }
        WebUtils.update("form");
    }

    public List<TituloParcela> listarParcelasVencidas() {
        titulosProvisionados = true;
        titulosRealizados = false;
        usuario = null;
        dataInicial = null;
        tipoTitulo = null;
        empresa = WebUtils.getEmpresa();
        dataFinal = new Date();
        return tituloEJB.listarParcelas(dataInicial, dataFinal, titulosProvisionados, usuario, titulosRealizados, tipoTitulo, empresa, cliente, orcamento, lancamentoFinanceiro);
    }

    public void listarMovimentacoesEstoque() {
        totalValorCompra = BigDecimal.ZERO;
        if (entradaEstoque) {
            tipoLancamento = EnumTipoLancamento.ENTRADA;
        }
        if (saidaEstoque) {
            tipoLancamento = EnumTipoLancamento.SAIDA;
        }
        if (entradaEstoque && saidaEstoque) {
            tipoLancamento = null;
        }
        if (!datasValidas()) {
            WebUtils.messageWarn("A data inicial não pode ser maior do que a data final");
            return;
        }
        movimentosEstoque = estoqueEJB.listarMovimentos(dataInicial, dataFinal, produto, tipoLancamento, docEntradaSaidaEstoque, WebUtils.getEmpresa());
        if (!Utils.empty(movimentosEstoque)) {
            TabelaPreco tabelaPreco = tabelaPrecoEJB.getTabelaPrecoPadrao(WebUtils.getEmpresa());
            List<TabelaPrecoItem> itensTabelaPreco = tabelaPreco.getTabelaPrecoItemList();
            for (MovimentoEstoque movimentoEstoque : movimentosEstoque) {
                if (movimentoEstoque.getTipoMovimento().equals(EnumTipoLancamento.SAIDA)) {
                    continue;
                }
                Produto produto = movimentoEstoque.getEstoque().getProduto();
                for (TabelaPrecoItem tabelaPrecoItem : itensTabelaPreco) {
                    if (produto != null && tabelaPrecoItem.getProduto().equals(produto)) {
                        BigDecimal valorCompra = tabelaPrecoItem.getValorCompra().multiply(movimentoEstoque.getQuantidade());
                        movimentoEstoque.setValorMovimento(valorCompra);
                        totalValorCompra = totalValorCompra.add(valorCompra);
                    }
                }
            }
        }
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public List<LancamentoFinanceiro> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(List<LancamentoFinanceiro> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Orcamento> getOrcamentos() {
        return orcamentos;
    }

    public void setOrcamentos(List<Orcamento> orcamentos) {
        this.orcamentos = orcamentos;
    }

    public void fechar() {
        orcamentos = null;
        caixas = null;
        WebUtils.redirect("../menu");
        WebUtils.update("form");
    }

    public boolean isSomenteVendas() {
        return somenteVendas;
    }

    public void setSomenteVendas(boolean somenteVendas) {
        this.somenteVendas = somenteVendas;
    }

    public boolean isSomenteCancelados() {
        return somenteCancelados;
    }

    public void setOrcamentoAberto(boolean orcamentoAberto) {
        this.orcamentoAberto = orcamentoAberto;
    }

    public boolean isOrcamentoAberto() {
        return orcamentoAberto;
    }

    public void setSomenteCancelados(boolean somenteCancelados) {
        this.somenteCancelados = somenteCancelados;
    }

    public boolean isSomenteOrcamentos() {
        return somenteOrcamentos;
    }

    public void setSomenteOrcamentos(boolean somenteOrcamentos) {
        this.somenteOrcamentos = somenteOrcamentos;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public List<Caixa> getCaixas() {
        return caixas;
    }

    public void setCaixas(List<Caixa> caixas) {
        this.caixas = caixas;
    }

    public boolean datasValidas() {
        if (dataInicial != null && dataFinal != null) {
            if (dataInicial.equals(dataFinal)) {
                return true;
            }
            return dataInicial.before(dataFinal);
        }
        return true;
    }

    public BigDecimal getTotalGeralEntradas() {
        return totalGeralEntradas;
    }

    public void setTotalGeralEntradas(BigDecimal totalGeralEntradas) {
        this.totalGeralEntradas = totalGeralEntradas;
    }

    public BigDecimal getTotalGeralSaidas() {
        return totalGeralSaidas;
    }

    public void setTotalGeralSaidas(BigDecimal totalGeralSaidas) {
        this.totalGeralSaidas = totalGeralSaidas;
    }

    public BigDecimal getTotalCaixaPeriodo() {
        return totalCaixaPeriodo;
    }

    public void setTotalCaixaPeriodo(BigDecimal totalCaixaPeriodo) {
        this.totalCaixaPeriodo = totalCaixaPeriodo;
    }

    public boolean isExibirDetalhesCaixa() {
        return exibirDetalhesCaixa;
    }

    public void setExibirDetalhesCaixa(boolean exibirDetalhesCaixa) {
        this.exibirDetalhesCaixa = exibirDetalhesCaixa;
    }

    public EnumTipoTitulo getTipoTitulo() {
        return tipoTitulo;
    }

    public void setTipoTitulo(EnumTipoTitulo tipoTitulo) {
        this.tipoTitulo = tipoTitulo;
    }

    public LancamentoFinanceiro getLancamentoFinanceiro() {
        return lancamentoFinanceiro;
    }

    public void setLancamentoFinanceiro(LancamentoFinanceiro lancamentoFinanceiro) {
        this.lancamentoFinanceiro = lancamentoFinanceiro;
    }

    public boolean isTitulosProvisionados() {
        return titulosProvisionados;
    }

    public void setTitulosProvisionados(boolean titulosProvisionados) {
        this.titulosProvisionados = titulosProvisionados;
    }

    public boolean isTitulosRealizados() {
        return titulosRealizados;
    }

    public void setTitulosRealizados(boolean titulosRealizados) {
        this.titulosRealizados = titulosRealizados;
    }

    public boolean isTitulosReceber() {
        return titulosReceber;
    }

    public void setTitulosReceber(boolean titulosReceber) {
        this.titulosReceber = titulosReceber;
    }

    public boolean isTitulosPagar() {
        return titulosPagar;
    }

    public void setTitulosPagar(boolean titulosPagar) {
        this.titulosPagar = titulosPagar;
    }

    public List<TituloParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<TituloParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public long getHoje() {
        return new Date().getTime();
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public BigDecimal getTotalReceber() {
        return totalReceber;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public List<Banco> listarBancos() {
        return bancoEJB.findAll(empresa);
    }

    public EnumTipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(EnumTipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public List<Produto> listarProdutos() {
        return produtoEJB.findAll(WebUtils.getEmpresa());
    }

    public List<MovimentoEstoque> getMovimentosEstoque() {
        return movimentosEstoque;
    }

    public void setMovimentosEstoque(List<MovimentoEstoque> movimentosEstoque) {
        this.movimentosEstoque = movimentosEstoque;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getDocEntradaSaidaEstoque() {
        return docEntradaSaidaEstoque;
    }

    public void setDocEntradaSaidaEstoque(String docEntradaSaidaEstoque) {
        this.docEntradaSaidaEstoque = docEntradaSaidaEstoque;
    }

    public BigDecimal getTotalValorCompra() {
        return totalValorCompra;
    }

    public void setTotalValorCompra(BigDecimal totalValorCompra) {
        this.totalValorCompra = totalValorCompra;
    }

    public boolean isEntradaEstoque() {
        return entradaEstoque;
    }

    public void setEntradaEstoque(boolean entradaEstoque) {
        this.entradaEstoque = entradaEstoque;
    }

    public boolean isSaidaEstoque() {
        return saidaEstoque;
    }

    public void setSaidaEstoque(boolean saidaEstoque) {
        this.saidaEstoque = saidaEstoque;
    }

    public List<Estoque> listarSugestaoCompra() {
        return estoqueEJB.getSugestaoCompra(WebUtils.getEmpresa());
    }

    public List<Orcamento> getVendasComissoes() {
        return vendasComissoes;
    }

    public void setVendasComissoes(List<Orcamento> vendasComissoes) {
        this.vendasComissoes = vendasComissoes;
    }

    public BigDecimal getTotalComissao() {
        return totalComissao;
    }

    public void setTotalComissao(BigDecimal totalComissao) {
        this.totalComissao = totalComissao;
    }

    public BigDecimal getTotalVendasOrcamentos() {
        return totalVendasOrcamentos;
    }

    public void setTotalVendasOrcamentos(BigDecimal totalVendasOrcamentos) {
        this.totalVendasOrcamentos = totalVendasOrcamentos;
    }

    public BigDecimal getTotalCustos() {
        return totalCustos;
    }

    public void setTotalCustos(BigDecimal totalCustos) {
        this.totalCustos = totalCustos;
    }

    public BigDecimal getTotalLucro() {
        return totalLucro;
    }

    public void setTotalLucro(BigDecimal totalLucro) {
        this.totalLucro = totalLucro;
    }

    
}
