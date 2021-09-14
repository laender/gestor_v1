/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Banco;
import com.gestor.entidades.Caixa;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.FormaPagamento;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.LancamentoParcela;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.TipoPagamento;
import com.gestor.entidades.Titulo;
import com.gestor.entidades.TituloParcela;
import com.gestor.entidades.Usuario;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.util.GestorException;
import com.gestor.util.Utils;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author laender
 */
@Stateless
public class LancamentoFinanceiroEJB extends AbstractEJB<LancamentoFinanceiro> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private OrcamentoEJB orcamentoEJB;

    @EJB
    private TituloEJB tituloEJB;

    @EJB
    CaixaEJB caixaEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LancamentoFinanceiroEJB() {
        super(LancamentoFinanceiro.class);
    }

    public List<LancamentoFinanceiro> getLancamentos(Empresa empresa, EnumTipoLancamento tipoLancamento) {
        String sql = "";
        if (tipoLancamento != null) {
            sql = "select u from LancamentoFinanceiro u where u.usuario.empresa =:empresa and u.tipoLancamento =:tpLcto";
        } else {
            sql = "select u from LancamentoFinanceiro u where u.usuario.empresa =:empresa ";
        }
        sql += " order by u.id desc";
        try {
            Query q = em.createQuery(sql);
            if (tipoLancamento != null) {
                q.setParameter("empresa", empresa)
                        .setParameter("tpLcto", tipoLancamento);
            } else {
                q.setParameter("empresa", empresa);
            }
            return (List<LancamentoFinanceiro>) q.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void gravarLancamento(LancamentoFinanceiro lancamentoFinanceiro) throws GestorException {
        //   final Orcamento orcamento = lancamentoFinanceiro.getOrcamento();
        final EnumTipoLancamento tipoLcto = lancamentoFinanceiro.getTipoLancamento();

        // caso foi selecionado alguma parcela, se trata de pagamento/recebimento de titulos apenas
        List<LancamentoParcela> parcelas = lancamentoFinanceiro.getParcelas();
        if (!Utils.empty(parcelas)) {
            for (LancamentoParcela parcela : parcelas) {
                final TituloParcela tituloParcela = parcela.getParcela();
                final Titulo titulo = tituloParcela.getTitulo();
                final Orcamento orcamento = titulo.getOrcamento();
                if (orcamento != null) {
                    // se tiver mais de uma parcela, o total do lcto sempre sera a soma dos saldos, do contrario pode ser uma baixa parcial
                    BigDecimal vlrLcto = parcelas.size() > 1 ? tituloParcela.getValorSaldo() : lancamentoFinanceiro.getValor();
                    orcamentoEJB.movimentarSaldo(orcamento, vlrLcto, tipoLcto);

                }
            }

            tituloEJB.liquidarParcelas(lancamentoFinanceiro);
        }

        // uma entrada e saída a vista só movimentam o caixa, nao gera titulo
        if (lancamentoFinanceiro.getFormaPagamento() != null && !lancamentoFinanceiro.getFormaPagamento().isVendaAPrazo()) {
            // efetua o registro do lancamento no caixa aberto do dia
            Caixa caixa = caixaEJB.getCaixaAberto(lancamentoFinanceiro.getUsuario().getEmpresa());
            lancamentoFinanceiro.setCaixa(caixa);
            this.merge(lancamentoFinanceiro);
            return;
        }

        //geração de titulo ou liquidação de parcelas
        if (Utils.empty(lancamentoFinanceiro.getParcelas()) && lancamentoFinanceiro.getFormaPagamento().isVendaAPrazo()) {
            tituloEJB.gerarTitulo(lancamentoFinanceiro, null);
        }

    }

    public List<LancamentoFinanceiro> getLancamentosPorCaixa(Caixa caixa) {
        try {
            return (List<LancamentoFinanceiro>) em.createQuery("select l from LancamentoFinanceiro l where l.caixa =:caixa").setParameter("caixa", caixa).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<LancamentoFinanceiro> getLancamentosPorTipoPagamento(TipoPagamento tipoPagamento) {
        try {
            return (List<LancamentoFinanceiro>) em.createQuery("select l from LancamentoFinanceiro l where l.tipoPagamento =:tipoPagamento ").setParameter("tipoPagamento", tipoPagamento).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<LancamentoFinanceiro> getLancamentosPorFormaPagamento(FormaPagamento frmPgto) {
        try {
            return (List<LancamentoFinanceiro>) em.createQuery("select l from LancamentoFinanceiro l where l.formaPagamento  =:frmPgto ").setParameter("frmPgto", frmPgto).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<LancamentoFinanceiro> getLancamentosPorBanco(Banco banco) {
        try {
            return (List<LancamentoFinanceiro>) em.createQuery("select l from LancamentoFinanceiro l where l.banco  =:banco ").
                    setParameter("banco", banco).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void excluirLancamento(LancamentoFinanceiro lancamentoFinanceiro) throws GestorException {
        EnumTipoLancamento tipoLctoInverso = lancamentoFinanceiro.getTipoLancamento().equals(EnumTipoLancamento.ENTRADA) ? EnumTipoLancamento.SAIDA : EnumTipoLancamento.ENTRADA;
        try {
            List<LancamentoParcela> lctoParcelas = lancamentoFinanceiro.getParcelas();
            if (!Utils.empty(lctoParcelas)) {
                for (LancamentoParcela lctoParcela : lctoParcelas) {
                    TituloParcela parcela = lctoParcela.getParcela();
                    BigDecimal saldoParcela = parcela.getValorSaldo();
                    BigDecimal vlrLiquidacao = lctoParcela.getValorLiquidacao();
                    parcela.setValorSaldo(saldoParcela.add(vlrLiquidacao));
                    Titulo titulo = parcela.getTitulo();
                    titulo.setValorSaldo(titulo.getValorSaldo().add(vlrLiquidacao));
                    Orcamento orcamento = titulo.getOrcamento();
                    if (orcamento != null) {
                        orcamentoEJB.movimentarSaldo(orcamento, vlrLiquidacao, tipoLctoInverso);
                    }
                    em.merge(titulo);
                    em.merge(parcela);
                }
            }
            Titulo tituloLcto = tituloEJB.getTituloPorLancamentoFinanceiro(lancamentoFinanceiro);
            if (tituloLcto != null) {
                if (tituloLcto.getValorSaldo().compareTo(tituloLcto.getValorTitulo()) != 0) {
                    throw new GestorException("O lançamento ja teve baixa realizada, deve ser estornado a(s) baixa(s) para o estorno do lançamento");
                } else {
                    em.remove(tituloLcto);
                }
            }

            // em.merge(estorno);
            // lancamentoFinanceiro.setEstornado(true);
            em.remove(em.find(LancamentoFinanceiro.class, lancamentoFinanceiro.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<LancamentoParcela> getLancamentoParcelasPorLancamentoFinanceiro(LancamentoFinanceiro lancamentoFinanceiro) {
        try {
            return (List<LancamentoParcela>) em.createQuery("select t from LancamentoParcela t where t.lancamentoFinanceiro =:lancamentoFinanceiro ")
                    .setParameter("lancamentoFinanceiro", lancamentoFinanceiro).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
