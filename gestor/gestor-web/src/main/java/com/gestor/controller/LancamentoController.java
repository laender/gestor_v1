/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.BancoEJB;
import com.gestor.ejb.CaixaEJB;
import com.gestor.ejb.ClienteEJB;
import com.gestor.ejb.FormaPagamentoEJB;
import com.gestor.ejb.FornecedorEJB;
import com.gestor.ejb.ProdutoEJB;
import com.gestor.ejb.LancamentoFinanceiroEJB;
import com.gestor.ejb.OrcamentoEJB;
import com.gestor.ejb.ProducaoEJB;
import com.gestor.ejb.RelatoriosEJB;
import com.gestor.ejb.TipoPagamentoEJB;
import com.gestor.ejb.TituloEJB;
import com.gestor.ejb.UsuarioEJB;
import com.gestor.entidades.Banco;
import com.gestor.entidades.Caixa;
import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.FormaPagamento;
import com.gestor.entidades.Fornecedor;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.LancamentoParcela;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.Producao;
import com.gestor.entidades.Produto;
import com.gestor.entidades.TipoPagamento;
import com.gestor.entidades.Titulo;
import com.gestor.entidades.TituloParcela;
import com.gestor.entidades.Usuario;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.enums.EnumTipoTitulo;
import com.gestor.util.GestorException;
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
public class LancamentoController implements Serializable, ICadastros {

    private LancamentoFinanceiro lancamentoFinanceiro = null;

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

    private boolean lancamentoEntrada = true;

    private Empresa empresa;

    @EJB
    private RelatoriosEJB relatoriosEJB;

    private List<TituloParcela> parcelasSelecionadas;
    private List<TituloParcela> parcelasLiquidacao;

    private boolean origemDiversas = true;
    private boolean fixarValor = false;
    private HttpServletResponse response;
    private FacesContext context;
    private ByteArrayOutputStream baos;

    public LancamentoController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        parcelasSelecionadas = null;
        empresa = WebUtils.getEmpresa();
        origemDiversas = false;
    }

    public void novo() {
        Caixa caixa;
        fixarValor = false;
        try {
            caixa = caixaEJB.getCaixaAberto(empresa);
        } catch (Exception e) {
            WebUtils.messageError(e.toString());
            // WebUtils.redirect("caixa");
            return;
        }
        lancamentoFinanceiro = new LancamentoFinanceiro();
        //   lancamentoFinanceiro.setCaixa(caixa);
        lancamentoFinanceiro.setDataLancamento(new Date());
        lancamentoFinanceiro.setUsuario(WebUtils.getUsuario());
        lancamentoFinanceiro.setValor(BigDecimal.ZERO);
        parcelasSelecionadas = null;

        EnumTipoTitulo tipo = lancamentoEntrada ? EnumTipoTitulo.RECEBER : EnumTipoTitulo.PAGAR;
        parcelasLiquidacao = tituloEJB.listarParcelasAbertasPorEmpresa(empresa, tipo);
        WebUtils.update("form");

    }

    public LancamentoFinanceiro getLancamentoFinanceiro() {
        return lancamentoFinanceiro;
    }

    public void setLancamentoFinanceiro(LancamentoFinanceiro lancamentoFinanceiro) {
        this.lancamentoFinanceiro = lancamentoFinanceiro;
        List<LancamentoParcela> parcelas = lancamentoFinanceiro.getParcelas();
        if (!Utils.empty(parcelas)) {
            origemDiversas = false;
            List<TituloParcela> parcSelecionadas = new ArrayList<>();
            for (LancamentoParcela lp : parcelas) {
                parcSelecionadas.add(lp.getParcela());
            }

            parcelasSelecionadas = parcSelecionadas;
        } else {
            origemDiversas = true;
        }

        WebUtils.update("form");
    }

    public List<LancamentoFinanceiro> listarLancamentosSaida() {
        return lancamentoFinanceiroEJB.getLancamentos(empresa, EnumTipoLancamento.SAIDA);
    }

    public List<LancamentoFinanceiro> listarLancamentosEntrada() {
        return lancamentoFinanceiroEJB.getLancamentos(empresa, EnumTipoLancamento.ENTRADA);
    }

    public List<FormaPagamento> listarFormaPagamentos() {
        return formaPagamentoEJB.findAll(empresa);
    }

    public List<Banco> listarBancos() {
        return bancoEJB.findAll(empresa);
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

    public List<Fornecedor> listarFornecedores() {
        return fornecedorEJB.findAll(empresa);
    }

    public List<TipoPagamento> listarTipoPagamentos() {
        return tipoPagamentoEJB.findAll(empresa);
    }

    public List<Orcamento> listarOrcamentos() {
        return orcamentoEJB.getVendas(empresa);
    }

    public List<Producao> listarProducao() {
        return producaoEJB.findAll();
    }

    public void addParcela(TituloParcela parcela) {
        if (parcela == null) {
            return;
        }
        if (parcelasSelecionadas == null) {
            parcelasSelecionadas = new ArrayList<>();
        }
        if (!parcelasSelecionadas.contains(parcela)) {
            parcelasSelecionadas.add(parcela);
            lancamentoFinanceiro.setValor(lancamentoFinanceiro.getValor().add(parcela.getValorSaldo()));
            Titulo titulo = parcela.getTitulo();
            lancamentoFinanceiro.setFormaPagamento(this.recuperaFormaPgtoAVista());
            lancamentoFinanceiro.setTipoPagamento(titulo != null ? titulo.getTipoPagamento() : null);
        }
    }

    private FormaPagamento recuperaFormaPgtoAVista() {
        return formaPagamentoEJB.getFormaPgtoAVista(empresa);
    }

    public void salvar(boolean viaVendaRapida) {
        try {
            if (!isLancamentoValido()) {
                return;
            }
            if (lancamentoFinanceiro.getCliente() != null && lancamentoFinanceiro.getFornecedor() != null) {
                WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
                return;
            }
            if (!Utils.empty(parcelasSelecionadas)) {
                if (lancamentoFinanceiro.getFormaPagamento() != null && lancamentoFinanceiro.getFormaPagamento().isVendaAPrazo()) {
                    String msg = "O" + (lancamentoEntrada ? " recebimento " : " pagamento ");
                    msg += "de parcelas não pode ser realizado com forma de pagamento A PRAZO.";
                    WebUtils.messageWarn(msg);
                    return;
                }
                if (lancamentoFinanceiro.getParcelas() == null) {
                    lancamentoFinanceiro.setParcelas(new ArrayList<>());
                }
                for (TituloParcela parcelasSelecionada : parcelasSelecionadas) {

                    LancamentoParcela lancamentoParcela = new LancamentoParcela();
                    lancamentoParcela.setLancamentoFinanceiro(lancamentoFinanceiro);
                    lancamentoParcela.setParcela(parcelasSelecionada);
                    lancamentoFinanceiro.getParcelas().add(lancamentoParcela);
                }

            }

            lancamentoFinanceiro.setTipoLancamento(lancamentoEntrada ? EnumTipoLancamento.ENTRADA : EnumTipoLancamento.SAIDA);
            lancamentoFinanceiroEJB.gravarLancamento(lancamentoFinanceiro);
        } catch (Exception e) {
            e.printStackTrace();
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        if (!viaVendaRapida) {
            WebUtils.messageRegistroGravado();
        }
        cancelar();
    }

    private boolean isLancamentoValido() {
        if (lancamentoFinanceiro == null) {
            WebUtils.messageWarn("Nenhum lançamento informado");
            return false;
        }
        if (lancamentoFinanceiro.getValor() == null || lancamentoFinanceiro.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            WebUtils.messageWarn("O valor deve ser informado e deve ser positivo");
            return false;
        }
        if (lancamentoFinanceiro.getTipoPagamento() == null) {
            WebUtils.messageWarn("Tipo de pagamento não informado");
            return false;
        }
        if (lancamentoFinanceiro.getFormaPagamento() == null && empresa.isFluxoCaixa()) {
            WebUtils.messageWarn("Forma de pagamento não informada");
            return false;
        }
        if (lancamentoFinanceiro.getCliente() != null && (lancamentoFinanceiro.getFornecedor() != null
                || !Utils.empty(parcelasSelecionadas)
                || lancamentoFinanceiro.getUsuarioFornecedor() != null
                || lancamentoFinanceiro.getOrcamento() != null)) {
            WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
            return false;
        }

        if (lancamentoFinanceiro.getFornecedor() != null && (!Utils.empty(parcelasSelecionadas))
                && lancamentoFinanceiro.getCliente() != null
                && lancamentoFinanceiro.getUsuarioFornecedor() != null
                && lancamentoFinanceiro.getOrcamento() != null) {
            WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
            return false;
        }
        if (lancamentoFinanceiro.getUsuarioFornecedor() != null && (!Utils.empty(parcelasSelecionadas))
                && lancamentoFinanceiro.getCliente() != null
                && lancamentoFinanceiro.getFornecedor() != null
                && lancamentoFinanceiro.getOrcamento() != null) {
            WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
            return false;
        }
        if (lancamentoFinanceiro.getOrcamento() != null && (!Utils.empty(parcelasSelecionadas))
                && lancamentoFinanceiro.getCliente() != null
                && lancamentoFinanceiro.getFornecedor() != null
                && lancamentoFinanceiro.getUsuarioFornecedor() != null) {
            WebUtils.messageWarn("O lancamento deve ter apenas uma origem");
            return false;
        }

        return true;
    }

    @Override
    public void cancelar() {
        lancamentoFinanceiro = null;
        lancamentoEntrada = true;
        parcelasSelecionadas = null;
        WebUtils.update("form");
    }

    @Override
    public void fechar() {
        cancelar();
        parcelasSelecionadas = null;
        WebUtils.redirect("../menu");
        WebUtils.update("form");
    }

    public void lancamentoEntrada(boolean entrada) {
        lancamentoEntrada = entrada;
    }

    public List<TituloParcela> getParcelasSelecionadas() {
        return parcelasSelecionadas;
    }

    public void setParcelasSelecionadas(List<TituloParcela> parcelasSelecionadas) {
        if (fixarValor) {
            return;
        }
        if (!Utils.empty(this.parcelasSelecionadas) && parcelasSelecionadas.size()>1) {
            for (TituloParcela tpExistente : this.parcelasSelecionadas) {
                Titulo tituloEx = tpExistente.getTitulo();
                String sacadoCedente = tituloEx.getSacadoCedente();
                for (TituloParcela tpNova : parcelasSelecionadas) {
                    Titulo tituloNovo = tpNova.getTitulo();
                    String sacadoCedenteNovo = tituloNovo.getSacadoCedente();
                    if (sacadoCedente != null && sacadoCedenteNovo != null
                            && !sacadoCedente.equals(sacadoCedenteNovo)) {
                        WebUtils.messageWarn("Para baixa em lote, selecione apenas parcelas do mesmo SACADO/CEDENTE");
                        return;
                    }
                }
            }
        }
        this.parcelasSelecionadas = parcelasSelecionadas;
        BigDecimal valorLancamento = BigDecimal.ZERO;
        if (!Utils.empty(parcelasSelecionadas)) {
            for (TituloParcela parcela : this.parcelasSelecionadas) {
                valorLancamento = valorLancamento.add(parcela.getValorSaldo());
                Titulo titulo = parcela.getTitulo();
                lancamentoFinanceiro.setFormaPagamento(this.recuperaFormaPgtoAVista());
                lancamentoFinanceiro.setTipoPagamento(titulo != null ? titulo.getTipoPagamento() : null);
            }
        }
        //  BigDecimal valorOriginalLcto = lancamentoFinanceiro.getValor();
        //  if (parcelasSelecionadas != null && parcelasSelecionadas.size() == 1
        //           && valorOriginalLcto.compareTo(BigDecimal.ZERO) > 0 && valorOriginalLcto.compareTo(valorLancamento) != 0) {

        //  }
        lancamentoFinanceiro.setValor(valorLancamento);

    }

    public void removeParcela(TituloParcela parcela) {
        parcelasSelecionadas.remove(parcela);
        lancamentoFinanceiro.setValor(lancamentoFinanceiro.getValor().subtract(parcela.getValorSaldo()));
        WebUtils.update("form");
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public boolean isOrigemDiversas() {
        return origemDiversas;
    }

    public void setOrigemDiversas(boolean origemDiversas) {
        this.origemDiversas = origemDiversas;
        if (origemDiversas) {
            parcelasSelecionadas = null;
        }
    }

    public void carregarPagamentoViaOrcamento(Orcamento orcamento) {
        this.novo();
        Titulo titulo = tituloEJB.getTituloPorOcamento(orcamento);
        List<TituloParcela> parcelas = titulo.getParcelas();
        this.setParcelasSelecionadas(parcelas);
        this.setOrigemDiversas(false);
    }

    @Override
    public void salvar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void excluir() {
        if (lancamentoFinanceiro == null) {
            WebUtils.messageWarn("Nenhum lancamento informado para exclusão");
            return;
        }
        try {
            lancamentoFinanceiroEJB.excluirLancamento(lancamentoFinanceiro);
            WebUtils.messageRegistroExcluido();
        } catch (GestorException ex) {
            WebUtils.messageError(ex.getMessage());
        }
        init();

    }

    public List<TituloParcela> getParcelasLiquidacao() {
        return parcelasLiquidacao;
    }

    public void setParcelasLiquidacao(List<TituloParcela> parcelasLiquidacao) {
        this.parcelasLiquidacao = parcelasLiquidacao;
    }

    public void fixarValor() {
        this.fixarValor = true;
    }

    public void imprimirRecibo(LancamentoFinanceiro lancamentoFinanceiro) {
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
            JasperPrint print = relatoriosEJB.gerarRecibo(lancamentoFinanceiro, parametros, empresa);
            JasperExportManager.exportReportToPdfStream(print, baos);
            String fileName = "Recibo - " + lancamentoFinanceiro.getId();
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

}
