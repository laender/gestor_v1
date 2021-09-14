// 
// Decompiled by Procyon v0.5.36
// 

package com.gestor.controller;

import net.sf.jasperreports.engine.JasperPrint;
import java.io.InputStream;
import java.io.IOException;
import net.sf.jasperreports.engine.JRException;
import java.io.OutputStream;
import net.sf.jasperreports.engine.JasperExportManager;
import java.util.Map;
import java.util.HashMap;
import com.gestor.util.GestorException;
import com.gestor.entidades.Titulo;
import com.gestor.entidades.Producao;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.TipoPagamento;
import com.gestor.entidades.Fornecedor;
import com.gestor.entidades.Cliente;
import com.gestor.entidades.Usuario;
import com.gestor.entidades.Produto;
import com.gestor.entidades.Banco;
import com.gestor.entidades.FormaPagamento;
import com.gestor.enums.EnumTipoLancamento;
import java.util.Iterator;
import com.gestor.entidades.LancamentoParcela;
import java.util.ArrayList;
import java.util.Collection;
import com.gestor.util.Utils;
import com.gestor.entidades.Caixa;
import com.gestor.enums.EnumTipoTitulo;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import com.gestor.util.WebUtils;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import com.gestor.entidades.TituloParcela;
import java.util.List;
import com.gestor.ejb.RelatoriosEJB;
import com.gestor.entidades.Empresa;
import com.gestor.ejb.BancoEJB;
import com.gestor.ejb.CaixaEJB;
import com.gestor.ejb.TipoPagamentoEJB;
import com.gestor.ejb.OrcamentoEJB;
import com.gestor.ejb.FornecedorEJB;
import com.gestor.ejb.FormaPagamentoEJB;
import com.gestor.ejb.ClienteEJB;
import com.gestor.ejb.TituloEJB;
import com.gestor.ejb.UsuarioEJB;
import com.gestor.ejb.ProducaoEJB;
import com.gestor.ejb.ProdutoEJB;
import javax.ejb.EJB;
import com.gestor.ejb.LancamentoFinanceiroEJB;
import com.gestor.entidades.LancamentoFinanceiro;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class LancamentoController implements Serializable, ICadastros
{
    private LancamentoFinanceiro lancamentoFinanceiro;
    @EJB
    private LancamentoFinanceiroEJB lancamentoFinanceiroEJB;
    @EJB
    private ProdutoEJB produtoEJB;
    @EJB
    private ProducaoEJB producaoEJB;
    @EJB
    private UsuarioEJB usuarioEJB;
    @EJB
    private TituloEJB tituloEJB;
    @EJB
    private ClienteEJB clienteEJB;
    @EJB
    private FormaPagamentoEJB formaPagamentoEJB;
    @EJB
    private FornecedorEJB fornecedorEJB;
    @EJB
    private OrcamentoEJB orcamentoEJB;
    @EJB
    private TipoPagamentoEJB tipoPagamentoEJB;
    @EJB
    private CaixaEJB caixaEJB;
    @EJB
    private BancoEJB bancoEJB;
    private boolean lancamentoEntrada;
    private Empresa empresa;
    @EJB
    private RelatoriosEJB relatoriosEJB;
    private List<TituloParcela> parcelasSelecionadas;
    private List<TituloParcela> parcelasLiquidacao;
    private boolean origemDiversas;
    private boolean fixarValor;
    private HttpServletResponse response;
    private FacesContext context;
    private ByteArrayOutputStream baos;
    private TituloParcela parcelaAltVcto;
    private Date novaDataVencimento;
    
    public LancamentoController() {
        this.lancamentoFinanceiro = null;
        this.lancamentoEntrada = true;
        this.origemDiversas = true;
        this.fixarValor = false;
        this.parcelaAltVcto = new TituloParcela();
        this.novaDataVencimento = null;
    }
    
    @PostConstruct
    private void init() {
        this.cancelar();
        this.parcelasSelecionadas = null;
        this.empresa = WebUtils.getEmpresa();
        this.origemDiversas = false;
    }
    
    public void novo() {
        this.fixarValor = false;
        try {
            final Caixa caixa = this.caixaEJB.getCaixaAberto(this.empresa);
        }
        catch (Exception e) {
            WebUtils.messageError(e.toString());
            return;
        }
        (this.lancamentoFinanceiro = new LancamentoFinanceiro()).setDataLancamento(new Date());
        this.lancamentoFinanceiro.setUsuario(WebUtils.getUsuario());
        this.lancamentoFinanceiro.setValor(BigDecimal.ZERO);
        this.parcelasSelecionadas = null;
        final EnumTipoTitulo tipo = this.lancamentoEntrada ? EnumTipoTitulo.RECEBER : EnumTipoTitulo.PAGAR;
        this.parcelasLiquidacao = (List<TituloParcela>)this.tituloEJB.listarParcelasAbertasPorEmpresa(this.empresa, tipo);
        WebUtils.update("form");
    }
    
    public LancamentoFinanceiro getLancamentoFinanceiro() {
        return this.lancamentoFinanceiro;
    }
    
    public void setLancamentoFinanceiro(final LancamentoFinanceiro lancamentoFinanceiro) {
        this.lancamentoFinanceiro = lancamentoFinanceiro;
        final List<LancamentoParcela> parcelas = (List<LancamentoParcela>)lancamentoFinanceiro.getParcelas();
        if (!Utils.empty((Collection)parcelas)) {
            this.origemDiversas = false;
            final List<TituloParcela> parcSelecionadas = new ArrayList<TituloParcela>();
            for (final LancamentoParcela lp : parcelas) {
                parcSelecionadas.add(lp.getParcela());
            }
            this.parcelasSelecionadas = parcSelecionadas;
        }
        else {
            this.origemDiversas = true;
        }
        WebUtils.update("form");
    }
    
    public List<LancamentoFinanceiro> listarLancamentosSaida() {
        return (List<LancamentoFinanceiro>)this.lancamentoFinanceiroEJB.getLancamentos(this.empresa, EnumTipoLancamento.SAIDA);
    }
    
    public List<LancamentoFinanceiro> listarLancamentosEntrada() {
        return (List<LancamentoFinanceiro>)this.lancamentoFinanceiroEJB.getLancamentos(this.empresa, EnumTipoLancamento.ENTRADA);
    }
    
    public List<FormaPagamento> listarFormaPagamentos() {
        return (List<FormaPagamento>)this.formaPagamentoEJB.findAll(this.empresa);
    }
    
    public List<Banco> listarBancos() {
        return (List<Banco>)this.bancoEJB.findAll(this.empresa);
    }
    
    public List<Produto> listarProdutos() {
        return (List<Produto>)this.produtoEJB.findAll(this.empresa);
    }
    
    public List<Usuario> listarUsuarios() {
        return (List<Usuario>)this.usuarioEJB.getUsuarios(this.empresa);
    }
    
    public List<Cliente> listarClientes() {
        return (List<Cliente>)this.clienteEJB.findAll(this.empresa);
    }
    
    public List<Fornecedor> listarFornecedores() {
        return (List<Fornecedor>)this.fornecedorEJB.findAll(this.empresa);
    }
    
    public List<TipoPagamento> listarTipoPagamentos() {
        return (List<TipoPagamento>)this.tipoPagamentoEJB.findAll(this.empresa);
    }
    
    public List<Orcamento> listarOrcamentos() {
        return (List<Orcamento>)this.orcamentoEJB.getVendas(this.empresa);
    }
    
    public List<Producao> listarProducao() {
        return (List<Producao>)this.producaoEJB.findAll();
    }
    
    public void addParcela(final TituloParcela parcela) {
        if (parcela == null) {
            return;
        }
        if (this.parcelasSelecionadas == null) {
            this.parcelasSelecionadas = new ArrayList<TituloParcela>();
        }
        if (!this.parcelasSelecionadas.contains(parcela)) {
            this.parcelasSelecionadas.add(parcela);
            this.lancamentoFinanceiro.setValor(this.lancamentoFinanceiro.getValor().add(parcela.getValorSaldo()));
            final Titulo titulo = parcela.getTitulo();
            this.lancamentoFinanceiro.setFormaPagamento(this.recuperaFormaPgtoAVista());
            this.lancamentoFinanceiro.setTipoPagamento((titulo != null) ? titulo.getTipoPagamento() : null);
        }
    }
    
    private FormaPagamento recuperaFormaPgtoAVista() {
        return this.formaPagamentoEJB.getFormaPgtoAVista(this.empresa);
    }
    
    public void salvar(final boolean viaVendaRapida) {
        try {
            if (!this.isLancamentoValido()) {
                return;
            }
            if (this.lancamentoFinanceiro.getCliente() != null && this.lancamentoFinanceiro.getFornecedor() != null) {
                WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
                return;
            }
            if (!Utils.empty((Collection)this.parcelasSelecionadas)) {
                if (this.lancamentoFinanceiro.getFormaPagamento() != null && this.lancamentoFinanceiro.getFormaPagamento().isVendaAPrazo()) {
                    String msg = "O" + (this.lancamentoEntrada ? " recebimento " : " pagamento ");
                    msg += "de parcelas não pode ser realizado com forma de pagamento A PRAZO.";
                    WebUtils.messageWarn(msg);
                    return;
                }
                if (this.lancamentoFinanceiro.getParcelas() == null) {
                    this.lancamentoFinanceiro.setParcelas((List)new ArrayList());
                }
                for (final TituloParcela parcelasSelecionada : this.parcelasSelecionadas) {
                    final LancamentoParcela lancamentoParcela = new LancamentoParcela();
                    lancamentoParcela.setLancamentoFinanceiro(this.lancamentoFinanceiro);
                    lancamentoParcela.setParcela(parcelasSelecionada);
                    this.lancamentoFinanceiro.getParcelas().add(lancamentoParcela);
                }
            }
            this.lancamentoFinanceiro.setTipoLancamento(this.lancamentoEntrada ? EnumTipoLancamento.ENTRADA : EnumTipoLancamento.SAIDA);
            this.lancamentoFinanceiroEJB.gravarLancamento(this.lancamentoFinanceiro);
        }
        catch (Exception e) {
            e.printStackTrace();
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        if (!viaVendaRapida) {
            WebUtils.messageRegistroGravado();
        }
        this.cancelar();
    }
    
    private boolean isLancamentoValido() {
        if (this.lancamentoFinanceiro == null) {
            WebUtils.messageWarn("Nenhum lan\u00e7amento informado");
            return false;
        }
        if (this.lancamentoFinanceiro.getValor() == null || this.lancamentoFinanceiro.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            WebUtils.messageWarn("O valor deve ser informado e deve ser positivo");
            return false;
        }
        if (this.lancamentoFinanceiro.getTipoPagamento() == null) {
            WebUtils.messageWarn("Tipo de pagamento n\u00e3o informado");
            return false;
        }
        if (this.lancamentoFinanceiro.getFormaPagamento() == null && this.empresa.isFluxoCaixa()) {
            WebUtils.messageWarn("Forma de pagamento não informada");
            return false;
        }
        if (this.lancamentoFinanceiro.getCliente() != null && (this.lancamentoFinanceiro.getFornecedor() != null || !Utils.empty((Collection)this.parcelasSelecionadas) || this.lancamentoFinanceiro.getUsuarioFornecedor() != null || this.lancamentoFinanceiro.getOrcamento() != null)) {
            WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
            return false;
        }
        if (this.lancamentoFinanceiro.getFornecedor() != null && !Utils.empty((Collection)this.parcelasSelecionadas) && this.lancamentoFinanceiro.getCliente() != null && this.lancamentoFinanceiro.getUsuarioFornecedor() != null && this.lancamentoFinanceiro.getOrcamento() != null) {
            WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
            return false;
        }
        if (this.lancamentoFinanceiro.getUsuarioFornecedor() != null && !Utils.empty((Collection)this.parcelasSelecionadas) && this.lancamentoFinanceiro.getCliente() != null && this.lancamentoFinanceiro.getFornecedor() != null && this.lancamentoFinanceiro.getOrcamento() != null) {
            WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
            return false;
        }
        if (this.lancamentoFinanceiro.getOrcamento() != null && !Utils.empty((Collection)this.parcelasSelecionadas) && this.lancamentoFinanceiro.getCliente() != null && this.lancamentoFinanceiro.getFornecedor() != null && this.lancamentoFinanceiro.getUsuarioFornecedor() != null) {
            WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
            return false;
        }
        return true;
    }
    
    public void cancelar() {
        this.lancamentoFinanceiro = null;
        this.lancamentoEntrada = true;
        this.parcelasSelecionadas = null;
        this.parcelaAltVcto = new TituloParcela();
        this.novaDataVencimento = null;
        WebUtils.update("form");
    }
    
    public void fechar() {
        this.cancelar();
        this.parcelasSelecionadas = null;
        WebUtils.redirect("../menu");
        WebUtils.update("form");
    }
    
    public void lancamentoEntrada(final boolean entrada) {
        this.lancamentoEntrada = entrada;
    }
    
    public List<TituloParcela> getParcelasSelecionadas() {
        return this.parcelasSelecionadas;
    }
    
    public void setParcelasSelecionadas(final List<TituloParcela> parcelasSelecionadas) {
        if (this.fixarValor) {
            return;
        }
        if (!Utils.empty((Collection)this.parcelasSelecionadas) && parcelasSelecionadas != null && parcelasSelecionadas.size() > 1) {
            for (final TituloParcela tpExistente : this.parcelasSelecionadas) {
                final Titulo tituloEx = tpExistente.getTitulo();
                final String sacadoCedente = tituloEx.getSacadoCedente();
                for (final TituloParcela tpNova : parcelasSelecionadas) {
                    final Titulo tituloNovo = tpNova.getTitulo();
                    final String sacadoCedenteNovo = tituloNovo.getSacadoCedente();
                    if (sacadoCedente != null && sacadoCedenteNovo != null && !sacadoCedente.equals(sacadoCedenteNovo)) {
                        WebUtils.messageWarn("Para baixa em lote, selecione apenas parcelas do mesmo SACADO/CEDENTE");
                        return;
                    }
                }
            }
        }
        this.parcelasSelecionadas = parcelasSelecionadas;
        BigDecimal valorLancamento = BigDecimal.ZERO;
        if (!Utils.empty((Collection)parcelasSelecionadas)) {
            for (final TituloParcela parcela : this.parcelasSelecionadas) {
                valorLancamento = valorLancamento.add(parcela.getValorSaldo());
                final Titulo titulo = parcela.getTitulo();
                this.lancamentoFinanceiro.setFormaPagamento(this.recuperaFormaPgtoAVista());
                this.lancamentoFinanceiro.setTipoPagamento((titulo != null) ? titulo.getTipoPagamento() : null);
            }
        }
        this.lancamentoFinanceiro.setValor(valorLancamento);
    }
    
    public void removeParcela(final TituloParcela parcela) {
        this.parcelasSelecionadas.remove(parcela);
        this.lancamentoFinanceiro.setValor(this.lancamentoFinanceiro.getValor().subtract(parcela.getValorSaldo()));
        WebUtils.update("form");
    }
    
    public void setEmpresa(final Empresa empresa) {
        this.empresa = empresa;
    }
    
    public Empresa getEmpresa() {
        return this.empresa;
    }
    
    public boolean isOrigemDiversas() {
        return this.origemDiversas;
    }
    
    public void setOrigemDiversas(final boolean origemDiversas) {
        this.origemDiversas = origemDiversas;
        if (origemDiversas) {
            this.parcelasSelecionadas = null;
        }
    }
    
    public void carregarPagamentoViaOrcamento(final Orcamento orcamento) {
        this.novo();
        final Titulo titulo = this.tituloEJB.getTituloPorOcamento(orcamento);
        final List<TituloParcela> parcelas = (List<TituloParcela>)titulo.getParcelas();
        this.setParcelasSelecionadas(parcelas);
        this.setOrigemDiversas(false);
    }
    
    public void salvar() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void excluir() {
        if (this.lancamentoFinanceiro == null) {
            WebUtils.messageWarn("Nenhum lancamento informado para exclusão");
            return;
        }
        try {
            this.lancamentoFinanceiroEJB.excluirLancamento(this.lancamentoFinanceiro);
            WebUtils.messageRegistroExcluido();
        }
        catch (GestorException ex) {
            WebUtils.messageError(ex.getMessage());
        }
        this.init();
    }
    
    public List<TituloParcela> getParcelasLiquidacao() {
        return this.parcelasLiquidacao;
    }
    
    public void setParcelasLiquidacao(final List<TituloParcela> parcelasLiquidacao) {
        this.parcelasLiquidacao = parcelasLiquidacao;
    }
    
    public void fixarValor() {
        this.fixarValor = true;
    }
    
    public void imprimirRecibo(final LancamentoFinanceiro lancamentoFinanceiro) {
        this.context = FacesContext.getCurrentInstance();
        this.response = (HttpServletResponse)this.context.getExternalContext().getResponse();
        this.baos = new ByteArrayOutputStream();
        this.salvar(true);
        try {
            String logo = this.empresa.getLogoImage();
            if (logo == null) {
                logo = "empresas.png";
            }
            final Map<String, Object> parametros = new HashMap<String, Object>();
            final InputStream image = this.getClass().getResourceAsStream("/Images/" + logo);
            parametros.put("URL_IMAGE", image);
            final JasperPrint print = this.relatoriosEJB.gerarRecibo(lancamentoFinanceiro, (Map)parametros, this.empresa);
            JasperExportManager.exportReportToPdfStream(print, (OutputStream)this.baos);
            final String fileName = "Recibo - " + lancamentoFinanceiro.getId();
            this.response.reset();
            this.response.setContentType("application/pdf");
            this.response.setContentLength(this.baos.size());
            this.response.setHeader("Content-disposition", "inline; filename=" + fileName + ".pdf");
            this.response.getOutputStream().write(this.baos.toByteArray());
            this.response.getOutputStream().flush();
            this.response.getOutputStream().close();
            this.context.responseComplete();
        }
        catch (JRException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void alterarVencimento() {
        if (this.novaDataVencimento == null) {
            WebUtils.messageWarn("Informe a nova data de vencimento");
            return;
        }
        this.parcelaAltVcto.setDataVencimento(this.novaDataVencimento);
        this.tituloEJB.salvarParcela(this.parcelaAltVcto);
        this.novo();
    }
    
    public void selecionarParcelaAltVcto(final TituloParcela parcela) {
        this.parcelaAltVcto = parcela;
    }
    
    public Date getNovaDataVencimento() {
        return this.novaDataVencimento;
    }
    
    public void setNovaDataVencimento(final Date novaDataVencimento) {
        this.novaDataVencimento = novaDataVencimento;
    }
    
    public TituloParcela getParcelaAltVcto() {
        return this.parcelaAltVcto;
    }
    
    public void setParcelaAltVcto(final TituloParcela parcelaAltVcto) {
        this.parcelaAltVcto = parcelaAltVcto;
    }
    
    public void excluiParcela(final TituloParcela parcela) {
        try {
            this.tituloEJB.excluiParcela(parcela);
        }
        catch (Exception e) {
            WebUtils.messageWarn(e.getMessage());
        }
        WebUtils.messageInfo("Parcela removida");
        this.novo();
    }
}
