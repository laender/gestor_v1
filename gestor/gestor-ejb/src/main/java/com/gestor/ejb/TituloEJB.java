/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.FormaPagamento;
import com.gestor.entidades.Fornecedor;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.LancamentoParcela;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.Titulo;
import com.gestor.entidades.TituloParcela;
import com.gestor.entidades.Usuario;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.enums.EnumTipoTitulo;
import com.gestor.util.GestorException;
import com.gestor.util.Utils;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author laender
 */
@Stateless
@LocalBean
public class TituloEJB extends AbstractEJB<Titulo> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TituloEJB() {
        super(Titulo.class);
    }

    public void gerarTitulo(LancamentoFinanceiro lancamentoFinanceiro, Orcamento orcamento) throws GestorException {

        try {
            em.detach(lancamentoFinanceiro != null ? lancamentoFinanceiro : orcamento);

            Titulo titulo = this.gerarTituloCabecalho(lancamentoFinanceiro, orcamento);

            titulo = this.gerarParcelas(titulo, lancamentoFinanceiro, orcamento);

            em.merge(titulo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GestorException("Erro ao gerar o titulo financeiro");
        }

    }

    private Titulo gerarTituloCabecalho(LancamentoFinanceiro lancamentoFinanceiro, Orcamento orcamento) {
        BigDecimal valorTitulo = lancamentoFinanceiro != null ? lancamentoFinanceiro.getValor() : orcamento.getValorTotal();
        EnumTipoTitulo tipoTitulo = this.getTipoTitulo(lancamentoFinanceiro, orcamento);
        Titulo titulo = new Titulo();
        titulo.setDataEmissao(orcamento != null ? orcamento.getDataVenda() : new Date());
        titulo.setValorTitulo(valorTitulo);
        titulo.setValorSaldo(valorTitulo);
        titulo.setTipo(tipoTitulo);
        titulo.setTipoPagamento(lancamentoFinanceiro != null ? lancamentoFinanceiro.getTipoPagamento() : orcamento.getTipoPagamento());
        titulo.setLancamentoFinanceiro(lancamentoFinanceiro);
        titulo.setOrcamento(orcamento);
        titulo.setSacadoCedente(this.getSacadoCedente(orcamento, lancamentoFinanceiro));
        titulo.setNumeroControle(lancamentoFinanceiro != null ? lancamentoFinanceiro.getNumeroControle() : null);
        titulo.setUsuario(lancamentoFinanceiro != null ? lancamentoFinanceiro.getUsuario() : orcamento.getUsuario());
        return titulo;
    }

    private Titulo gerarParcelas(Titulo titulo, LancamentoFinanceiro lancamentoFinanceiro, Orcamento orcamento) throws GestorException {
        TituloParcela tituloParcela;
        FormaPagamento formaPagamento = lancamentoFinanceiro != null ? lancamentoFinanceiro.getFormaPagamento() : orcamento.getFormaPagamento();
        Date dtVencPrimeiraParcOrct = orcamento != null && orcamento.getDataVencimento() != null ? orcamento.getDataVencimento(): null;
        if (formaPagamento == null) {
            throw new GestorException("Forma de pagamento não informada");
        }
        int numParcelas = formaPagamento.isVendaAPrazo() ? Utils.nvl(formaPagamento.getNumeroParcelas(), 1) : 1;
        Integer diasVctPrimeiraParc = Utils.nvl(formaPagamento.getDiasVctoPrimeiraParcela(), 1);

        Integer intervaloParcelas = Utils.nvl(formaPagamento.getIntervaloParcelas(), 0);
        Date dataVctoPrimeiraParcela = lancamentoFinanceiro != null ? lancamentoFinanceiro.getDataVencimento() : dtVencPrimeiraParcOrct;
        
        Date dataVct = titulo.getDataEmissao();
        BigDecimal valorParcela = titulo.getValorTitulo().divide(BigDecimal.valueOf(numParcelas), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
        List<TituloParcela> parcelas = new ArrayList<>();
        BigDecimal valorTotalParcela = BigDecimal.ZERO;
        for (int i = 1; i <= numParcelas; i++) {
            tituloParcela = new TituloParcela();
            tituloParcela.setTitulo(titulo);
            tituloParcela.setNumeroParcela(i);
            tituloParcela.setDataVencimento(this.adicionaDias(dataVct, diasVctPrimeiraParc, i, intervaloParcelas,dataVctoPrimeiraParcela));
            dataVct = tituloParcela.getDataVencimento();
            tituloParcela.setValorParcela(valorParcela);
            tituloParcela.setValorSaldo(valorParcela);
            parcelas.add(tituloParcela);
            valorTotalParcela = valorTotalParcela.add(valorParcela);
        }
        BigDecimal dif = titulo.getValorTitulo().subtract(valorTotalParcela);
        if (dif.compareTo(BigDecimal.ZERO) > 0) {
            TituloParcela primeiraParcela = parcelas.get(0);
            primeiraParcela.setValorParcela(primeiraParcela.getValorParcela().add(dif));
            primeiraParcela.setValorSaldo(primeiraParcela.getValorSaldo().add(dif));
        }
        titulo.setParcelas(parcelas);

        return titulo;
    }

    private Date adicionaDias(Date data, Integer diasVctPrimeiraParc, int contador, Integer intervalo, Date dataVctoPrimeiraParcela) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        int diasMes = intervalo == 30 ? c.getActualMaximum(Calendar.DAY_OF_MONTH) : intervalo;
        if (dataVctoPrimeiraParcela != null && contador == 1) {
            c.setTime(dataVctoPrimeiraParcela);
        } else {
            c.add(Calendar.DATE, +(contador == 1 ? diasVctPrimeiraParc : diasMes));
        }
        return c.getTime();
    }

    public Date getDataVencimentoMensal(int contador, Date dataVct) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataVct);
            return (Date) em.createNativeQuery("select current_date + interval '" + contador + " month' ").getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private EnumTipoTitulo getTipoTitulo(LancamentoFinanceiro lancamentoFinanceiro, Orcamento orcamento) {
        if (lancamentoFinanceiro != null) {
            return lancamentoFinanceiro.getTipoLancamento().equals(EnumTipoLancamento.ENTRADA) ? EnumTipoTitulo.RECEBER : EnumTipoTitulo.PAGAR;
        }
        return EnumTipoTitulo.RECEBER;
    }

    public List<TituloParcela> listarParcelas(Date dataInicial, Date dataFinal,
            boolean provisionados, Usuario vendedor,
            boolean realizados,
            EnumTipoTitulo tipo,
            Empresa empresa, Cliente cliente,
            Orcamento orcamento,
            LancamentoFinanceiro lancamentoFinanceiro) {
        String sql = "select distinct tp from TituloParcela tp "
                + " INNER join tp.titulo t "
                + " LEFT join t.lancamentoFinanceiro lf "
                + " LEFT join t.orcamento o  "
                + " where  1 = 1 ";
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
            Query q = em.createQuery(sql);
            q.setParameter("empresa", empresa);
            if (dataInicial != null) {
                q.setParameter("dataInicial", dataInicial);
            }
            if (dataFinal != null) {
                q.setParameter("dataFinal", dataFinal);
            }
            if (orcamento != null) {
                q.setParameter("orcamento", orcamento);
            }
            if (lancamentoFinanceiro != null) {
                q.setParameter("lancamentoFinanceiro", lancamentoFinanceiro);
            }
            if (cliente != null) {
                q.setParameter("cliente", cliente);
            }
            if (vendedor != null) {
                q.setParameter("vendedor", vendedor);
            }
            if (tipo != null) {
                q.setParameter("tipo", tipo);
            }
            return (List<TituloParcela>) q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void excluir(Titulo titulo) {
        em.remove(titulo);
    }

    public Titulo getTituloPorOcamento(Orcamento orcamento) {
        try {
            return (Titulo) em.createQuery("select t from Titulo t where t.orcamento =:orcamento")
                    .setParameter("orcamento", orcamento).getSingleResult();

        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    public Titulo getTituloPorLancamentoFinanceiro(LancamentoFinanceiro lancamentoFinanceiro) {
        try {
            return (Titulo) em.createQuery("select t from Titulo t where t.lancamentoFinanceiro =:lancamento")
                    .setParameter("lancamento", lancamentoFinanceiro).getSingleResult();

        } catch (Exception e) {
          //  e.printStackTrace();
        }
        return null;
    }

    public List<TituloParcela> listarParcelasAbertasPorEmpresa(Empresa empresa, EnumTipoTitulo tipoTitulo) {
        try {
            return (List<TituloParcela>) em.createQuery("select t from TituloParcela t "
                    + "where  t.titulo.usuario.empresa =:empresa  "
                    + " and t.valorSaldo > 0"
                    + " and t.titulo.tipo =:tipo "
                    + " order by t.dataVencimento")
                    .setParameter("empresa", empresa)
                    .setParameter("tipo", tipoTitulo)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSacadoCedente(Orcamento orcamento, LancamentoFinanceiro lancamentoFinanceiro) {
        if (lancamentoFinanceiro != null) {
            Cliente cliente = lancamentoFinanceiro.getCliente();
            Usuario usuario = lancamentoFinanceiro.getUsuarioFornecedor();
            Fornecedor fornecedor = lancamentoFinanceiro.getFornecedor();

            if (cliente != null) {
                return cliente.getNome();
            }
            if (usuario != null) {
                return usuario.getNome();
            }
            if (fornecedor != null) {
                return fornecedor.getNome();
            }
        } else {
            return orcamento.getCliente().getNome();
        }
        return null;
    }

    public void liquidarParcelas(LancamentoFinanceiro lancamentoFinanceiro) throws GestorException {
        try {
            List<LancamentoParcela> parcelas = lancamentoFinanceiro.getParcelas();
            BigDecimal valorLcto = lancamentoFinanceiro.getValor();
            BigDecimal valorLiquidacao = BigDecimal.ZERO;
            if (!Utils.empty(parcelas)) {
                for (LancamentoParcela lancamentoParcela : parcelas) {
                    if (valorLcto.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                    TituloParcela tituloParcela = lancamentoParcela.getParcela();
                    BigDecimal saldoParcela = tituloParcela.getValorSaldo();
                    if (saldoParcela.compareTo(valorLcto) <= 0) {
                        tituloParcela.setValorSaldo(BigDecimal.ZERO);
                        valorLcto = valorLcto.subtract(saldoParcela);
                        valorLiquidacao = saldoParcela;
                        lancamentoParcela.setValorLiquidacao(valorLiquidacao);

                    } else if (saldoParcela.compareTo(valorLcto) > 0) {
                        tituloParcela.setValorSaldo(saldoParcela.subtract(valorLcto));
                        valorLiquidacao = valorLcto;
                        lancamentoParcela.setValorLiquidacao(valorLiquidacao);
                    }
                    Titulo titulo = tituloParcela.getTitulo();
                    titulo.setValorSaldo(titulo.getValorSaldo().subtract(valorLiquidacao));
                    em.merge(tituloParcela);
                    em.merge(titulo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LancamentoParcela> getLancamentoParcelas(TituloParcela parcela) {
        try {
            return (List<LancamentoParcela>) em.createQuery("select t from LancamentoParcela t where t.parcela =:parcela ")
                    .setParameter("parcela", parcela).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
