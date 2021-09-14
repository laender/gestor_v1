// 
// Decompiled by Procyon v0.5.36
// 

package com.gestor.ejb;

import java.util.Iterator;
import com.gestor.entidades.LancamentoParcela;
import java.util.Collection;
import com.gestor.entidades.Fornecedor;
import javax.persistence.Query;
import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Usuario;
import com.gestor.enums.EnumTipoLancamento;
import java.util.Calendar;
import com.gestor.entidades.FormaPagamento;
import java.util.List;
import com.gestor.entidades.TituloParcela;
import java.util.ArrayList;
import java.math.RoundingMode;
import java.math.MathContext;
import com.gestor.util.Utils;
import com.gestor.enums.EnumTipoTitulo;
import java.math.BigDecimal;
import java.util.Date;
import com.gestor.util.GestorException;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.LancamentoFinanceiro;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import com.gestor.entidades.Titulo;

@Stateless
@LocalBean
public class TituloEJB extends AbstractEJB<Titulo>
{
    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }
    
    public TituloEJB() {
        super(Titulo.class);
    }
    
    public void gerarTitulo(final LancamentoFinanceiro lancamentoFinanceiro, final Orcamento orcamento) throws GestorException {
        try {
            this.em.detach((lancamentoFinanceiro != null) ? lancamentoFinanceiro : orcamento);
            Titulo titulo = this.gerarTituloCabecalho(lancamentoFinanceiro, orcamento);
            titulo = this.gerarParcelas(titulo, lancamentoFinanceiro, orcamento);
            this.em.merge((Object)titulo);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new GestorException("Erro ao gerar o titulo financeiro");
        }
    }
    
    private Titulo gerarTituloCabecalho(final LancamentoFinanceiro lancamentoFinanceiro, final Orcamento orcamento) {
        final BigDecimal valorTitulo = (lancamentoFinanceiro != null) ? lancamentoFinanceiro.getValor() : orcamento.getValorTotal();
        final EnumTipoTitulo tipoTitulo = this.getTipoTitulo(lancamentoFinanceiro, orcamento);
        final Titulo titulo = new Titulo();
        titulo.setDataEmissao((orcamento != null) ? orcamento.getDataVenda() : new Date());
        titulo.setValorTitulo(valorTitulo);
        titulo.setValorSaldo(valorTitulo);
        titulo.setTipo(tipoTitulo);
        titulo.setTipoPagamento((lancamentoFinanceiro != null) ? lancamentoFinanceiro.getTipoPagamento() : orcamento.getTipoPagamento());
        titulo.setLancamentoFinanceiro(lancamentoFinanceiro);
        titulo.setOrcamento(orcamento);
        titulo.setSacadoCedente(this.getSacadoCedente(orcamento, lancamentoFinanceiro));
        titulo.setNumeroControle((lancamentoFinanceiro != null) ? lancamentoFinanceiro.getNumeroControle() : null);
        titulo.setUsuario((lancamentoFinanceiro != null) ? lancamentoFinanceiro.getUsuario() : orcamento.getUsuario());
        return titulo;
    }
    
    private Titulo gerarParcelas(final Titulo titulo, final LancamentoFinanceiro lancamentoFinanceiro, final Orcamento orcamento) throws GestorException {
        final FormaPagamento formaPagamento = (lancamentoFinanceiro != null) ? lancamentoFinanceiro.getFormaPagamento() : orcamento.getFormaPagamento();
        final Date dtVencPrimeiraParcOrct = (orcamento != null && orcamento.getDataVencimento() != null) ? orcamento.getDataVencimento() : null;
        if (formaPagamento == null) {
            throw new GestorException("Forma de pagamento não informada");
        }
        final int numParcelas = formaPagamento.isVendaAPrazo() ? Utils.nvl(formaPagamento.getNumeroParcelas(), Integer.valueOf(1)) : 1;
        final Integer diasVctPrimeiraParc = Utils.nvl(formaPagamento.getDiasVctoPrimeiraParcela(), Integer.valueOf(1));
        final Integer intervaloParcelas = Utils.nvl(formaPagamento.getIntervaloParcelas(), Integer.valueOf(0));
        final Date dataVctoPrimeiraParcela = (lancamentoFinanceiro != null) ? lancamentoFinanceiro.getDataVencimento() : dtVencPrimeiraParcOrct;
        Date dataVct = titulo.getDataEmissao();
        final BigDecimal valorParcela = titulo.getValorTitulo().divide(BigDecimal.valueOf(numParcelas), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
        final List<TituloParcela> parcelas = new ArrayList<TituloParcela>();
        BigDecimal valorTotalParcela = BigDecimal.ZERO;
        for (int i = 1; i <= numParcelas; ++i) {
            final TituloParcela tituloParcela = new TituloParcela();
            tituloParcela.setTitulo(titulo);
            tituloParcela.setNumeroParcela(Integer.valueOf(i));
            tituloParcela.setDataVencimento(this.adicionaDias(dataVct, diasVctPrimeiraParc, i, intervaloParcelas, dataVctoPrimeiraParcela));
            dataVct = tituloParcela.getDataVencimento();
            tituloParcela.setValorParcela(valorParcela);
            tituloParcela.setValorSaldo(valorParcela);
            parcelas.add(tituloParcela);
            valorTotalParcela = valorTotalParcela.add(valorParcela);
        }
        final BigDecimal dif = titulo.getValorTitulo().subtract(valorTotalParcela);
        if (dif.compareTo(BigDecimal.ZERO) > 0) {
            final TituloParcela primeiraParcela = parcelas.get(0);
            primeiraParcela.setValorParcela(primeiraParcela.getValorParcela().add(dif));
            primeiraParcela.setValorSaldo(primeiraParcela.getValorSaldo().add(dif));
        }
        titulo.setParcelas((List)parcelas);
        return titulo;
    }
    
    private Date adicionaDias(final Date data, final Integer diasVctPrimeiraParc, final int contador, final Integer intervalo, final Date dataVctoPrimeiraParcela) {
        final Calendar c = Calendar.getInstance();
        c.setTime(data);
        final int diasMes = (intervalo == 30) ? c.getActualMaximum(5) : intervalo;
        if (dataVctoPrimeiraParcela != null && contador == 1) {
            c.setTime(dataVctoPrimeiraParcela);
        }
        else {
            c.add(5, (contador == 1) ? ((int)diasVctPrimeiraParc) : diasMes);
        }
        return c.getTime();
    }
    
    public Date getDataVencimentoMensal(final int contador, final Date dataVct) {
        try {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(dataVct);
            return (Date)this.em.createNativeQuery("select current_date + interval '" + contador + " month' ").getSingleResult();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private EnumTipoTitulo getTipoTitulo(final LancamentoFinanceiro lancamentoFinanceiro, final Orcamento orcamento) {
        if (lancamentoFinanceiro != null) {
            return lancamentoFinanceiro.getTipoLancamento().equals((Object)EnumTipoLancamento.ENTRADA) ? EnumTipoTitulo.RECEBER : EnumTipoTitulo.PAGAR;
        }
        return EnumTipoTitulo.RECEBER;
    }
    
    public List<TituloParcela> listarParcelas(final Date dataInicial, final Date dataFinal, final boolean provisionados, final Usuario vendedor, final boolean realizados, final EnumTipoTitulo tipo, final Empresa empresa, final Cliente cliente, final Orcamento orcamento, final LancamentoFinanceiro lancamentoFinanceiro) {
        String sql = "select distinct tp from TituloParcela tp  INNER join tp.titulo t  LEFT join t.lancamentoFinanceiro lf  LEFT join t.orcamento o   where  1 = 1 ";
        if (empresa != null) {
            sql += " and tp.titulo.usuario.empresa =:empresa";
        }
        if (dataInicial != null) {
            sql += " and tp.dataVencimento >= :dataInicial ";
        }
        if (dataFinal != null) {
            sql += " and tp.dataVencimento <= :dataFinal ";
        }
        if (cliente != null) {
            sql += " and (lf.cliente =:cliente or o.cliente =:cliente) ";
        }
        if (vendedor != null) {
            sql += " and tp.titulo.usuario =:vendedor";
        }
        if (orcamento != null) {
            sql += " and tp.titulo.orcamento =:orcamento";
        }
        if (lancamentoFinanceiro != null) {
            sql += " and tp.titulo.lancamentoFinanceiro =:lancamentoFinanceiro";
        }
        if (provisionados && !realizados) {
            sql += " and tp.valorSaldo > 0";
        }
        if (realizados && !provisionados) {
            sql += " and tp.valorSaldo <= 0";
        }
        if (tipo != null) {
            sql += " and tp.titulo.tipo =:tipo";
        }
        sql += " order by tp.dataVencimento";
        try {
            final Query q = this.em.createQuery(sql);
            q.setParameter("empresa", (Object)empresa);
            if (dataInicial != null) {
                q.setParameter("dataInicial", (Object)dataInicial);
            }
            if (dataFinal != null) {
                q.setParameter("dataFinal", (Object)dataFinal);
            }
            if (orcamento != null) {
                q.setParameter("orcamento", (Object)orcamento);
            }
            if (lancamentoFinanceiro != null) {
                q.setParameter("lancamentoFinanceiro", (Object)lancamentoFinanceiro);
            }
            if (cliente != null) {
                q.setParameter("cliente", (Object)cliente);
            }
            if (vendedor != null) {
                q.setParameter("vendedor", (Object)vendedor);
            }
            if (tipo != null) {
                q.setParameter("tipo", (Object)tipo);
            }
            return (List<TituloParcela>)q.getResultList();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void excluir(final Titulo titulo) {
        this.em.remove((Object)titulo);
    }
    
    public Titulo getTituloPorOcamento(final Orcamento orcamento) {
        try {
            return (Titulo)this.em.createQuery("select t from Titulo t where t.orcamento =:orcamento").setParameter("orcamento", (Object)orcamento).getSingleResult();
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public Titulo getTituloPorLancamentoFinanceiro(final LancamentoFinanceiro lancamentoFinanceiro) {
        try {
            return (Titulo)this.em.createQuery("select t from Titulo t where t.lancamentoFinanceiro =:lancamento").setParameter("lancamento", (Object)lancamentoFinanceiro).getSingleResult();
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public List<TituloParcela> listarParcelasAbertasPorEmpresa(final Empresa empresa, final EnumTipoTitulo tipoTitulo) {
        try {
            return (List<TituloParcela>)this.em.createQuery("select t from TituloParcela t where  t.titulo.usuario.empresa =:empresa   and t.valorSaldo > 0 and t.titulo.tipo =:tipo  order by t.dataVencimento").setParameter("empresa", (Object)empresa).setParameter("tipo", (Object)tipoTitulo).getResultList();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private String getSacadoCedente(final Orcamento orcamento, final LancamentoFinanceiro lancamentoFinanceiro) {
        if (lancamentoFinanceiro == null) {
            return orcamento.getCliente().getNome();
        }
        final Cliente cliente = lancamentoFinanceiro.getCliente();
        final Usuario usuario = lancamentoFinanceiro.getUsuarioFornecedor();
        final Fornecedor fornecedor = lancamentoFinanceiro.getFornecedor();
        if (cliente != null) {
            return cliente.getNome();
        }
        if (usuario != null) {
            return usuario.getNome();
        }
        if (fornecedor != null) {
            return fornecedor.getNome();
        }
        return null;
    }
    
    public void liquidarParcelas(final LancamentoFinanceiro lancamentoFinanceiro) throws GestorException {
        try {
            final List<LancamentoParcela> parcelas = (List<LancamentoParcela>)lancamentoFinanceiro.getParcelas();
            BigDecimal valorLcto = lancamentoFinanceiro.getValor();
            BigDecimal valorLiquidacao = BigDecimal.ZERO;
            if (!Utils.empty((Collection)parcelas)) {
                for (final LancamentoParcela lancamentoParcela : parcelas) {
                    if (valorLcto.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                    final TituloParcela tituloParcela = lancamentoParcela.getParcela();
                    final BigDecimal saldoParcela = tituloParcela.getValorSaldo();
                    if (saldoParcela.compareTo(valorLcto) <= 0) {
                        tituloParcela.setValorSaldo(BigDecimal.ZERO);
                        valorLcto = valorLcto.subtract(saldoParcela);
                        valorLiquidacao = saldoParcela;
                        lancamentoParcela.setValorLiquidacao(valorLiquidacao);
                    }
                    else if (saldoParcela.compareTo(valorLcto) > 0) {
                        tituloParcela.setValorSaldo(saldoParcela.subtract(valorLcto));
                        valorLiquidacao = valorLcto;
                        lancamentoParcela.setValorLiquidacao(valorLiquidacao);
                    }
                    final Titulo titulo = tituloParcela.getTitulo();
                    titulo.setValorSaldo(titulo.getValorSaldo().subtract(valorLiquidacao));
                    this.em.merge((Object)tituloParcela);
                    this.em.merge((Object)titulo);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<LancamentoParcela> getLancamentoParcelas(final TituloParcela parcela) {
        try {
            return (List<LancamentoParcela>)this.em.createQuery("select t from LancamentoParcela t where t.parcela =:parcela ").setParameter("parcela", (Object)parcela).getResultList();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void salvarParcela(final TituloParcela parcela) {
        this.em.merge((Object)parcela);
    }
    
    public void excluiParcela(final TituloParcela parcela) throws GestorException {
        if (parcela.getValorParcela().compareTo(parcela.getValorSaldo()) > 0) {
            throw new GestorException("A parcela j\u00e1 teve uma baixa, a baixa deve ser estornada para excluir a parcela");
        }
        final Titulo titulo = parcela.getTitulo();
        titulo.setValorTitulo(titulo.getValorTitulo().subtract(parcela.getValorParcela()));
        titulo.setValorSaldo(titulo.getValorSaldo().subtract(parcela.getValorSaldo()));
        this.merge(titulo);
        this.em.remove(this.em.find((Class)TituloParcela.class, (Object)parcela.getId()));
        if (titulo.getParcelas().size() == 1) {
            this.em.remove(this.em.find((Class)Titulo.class, (Object)titulo.getId()));
        }
    }
}
