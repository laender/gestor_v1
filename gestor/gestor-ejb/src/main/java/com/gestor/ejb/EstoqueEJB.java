/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.Estoque;
import com.gestor.entidades.MovimentoEstoque;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.Produto;
import com.gestor.entidades.UnidadeMedida;
import com.gestor.entidades.Usuario;
import com.gestor.enums.EnumStatusOrcamento;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.util.CurvaAbcEstoque;
import com.gestor.util.GestorException;
import com.gestor.util.Notificacao;
import com.gestor.util.Utils;
import com.gestor.utils.UnidadeMedidaUtils;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
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
public class EstoqueEJB extends AbstractEJB<Estoque> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private OrcamentoEJB orcamentoEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstoqueEJB() {
        super(Estoque.class);
    }

    public void salvar(Estoque estoque, String notaFiscal, Usuario usuario, EnumTipoLancamento tipo, String obs) throws GestorException {
        this.movimentarEstoque(estoque.getProduto(), estoque.getUnidadeMedida(), estoque.getQuantidade(), tipo, null, notaFiscal, usuario, obs, estoque);
    }

    public Estoque getEstoquePorProduto(Produto produto) {
        try {
            return (Estoque) em.createQuery("select e from Estoque e where e.produto =:produto").
                    setParameter("produto", produto).getSingleResult();
        } catch (Exception ex) {
            //  ex.printStackTrace();
            return null;
        }
    }

    public List<UnidadeMedida> getUnidadeMedidaEstoqueProduto(Produto produto) {
        try {
            return (List<UnidadeMedida>) em.createQuery("select distinct e.unidadeMedida from Estoque e where e.produto =:produto").
                    setParameter("produto", produto).getResultList();
        } catch (Exception ex) {
            //  ex.printStackTrace();
            return null;
        }
    }

    public Estoque getEstoquePorProdutoEUnidadeMedida(Produto produto, UnidadeMedida unidMed) {
        try {
            return (Estoque) em.createQuery("select e from Estoque e where e.produto =:produto and e.unidadeMedida =:unidMed").
                    setParameter("produto", produto).
                    setParameter("unidMed", unidMed).
                    getSingleResult();
        } catch (Exception ex) {
            //  ex.printStackTrace();
            return null;
        }
    }

    public List<Estoque> getEstoques(Empresa empresa) {
        try {
            return (List<Estoque>) em.createQuery("select e from Estoque e where e.produto.empresa =:empresa order by e.produto.descricao").
                    setParameter("empresa", empresa).getResultList();
        } catch (Exception ex) {
            //  ex.printStackTrace();
            return null;
        }
    }

    public List<Estoque> getSugestaoCompra(Empresa empresa) {
        try {
            return (List<Estoque>) em.createQuery("select e from Estoque e where e.produto.empresa =:empresa and e.quantidade < e.quantidadeMinima order by e.produto.descricao").
                    setParameter("empresa", empresa).getResultList();
        } catch (Exception ex) {
            //  ex.printStackTrace();
            return null;
        }
    }

    public void movimentarEstoque(Produto produto, UnidadeMedida unMedidaItem, BigDecimal qtd,
            EnumTipoLancamento tipoMvto, Orcamento orcamento,
            String notaFiscal, Usuario usuario, String obs, Estoque estoque) throws GestorException {
        Estoque estoqueBd = this.getEstoquePorProduto(produto);
        Estoque estoqueNew = null;
        //  em.detach(orcamento);
        if (estoqueBd != null && unMedidaItem != null) {
            UnidadeMedida uniMedEstoque = estoqueBd.getUnidadeMedida();
            if (!unMedidaItem.equals(uniMedEstoque)) {
                BigDecimal divisor = UnidadeMedidaUtils.getDivisorConversao(uniMedEstoque, unMedidaItem);
                if (tipoMvto.equals(EnumTipoLancamento.ENTRADA)) {
                    qtd = (qtd.divide(divisor));
                } else {
                    qtd = (qtd.divide(divisor));
                }
            }
        }
        Estoque estoquePersist = null;
        if (estoqueBd != null) {
            BigDecimal qtdEstoque = estoqueBd.getQuantidade();
            if (tipoMvto.equals(EnumTipoLancamento.SAIDA)) {
                estoqueBd.setQuantidade(qtdEstoque.subtract(qtd));
            } else {
                estoqueBd.setQuantidade(qtdEstoque.add(qtd));
            }
            if (estoque != null) {
                estoqueBd.setQuantidadeMaxima(estoque.getQuantidadeMaxima());
                estoqueBd.setQuantidadeMinima(estoque.getQuantidadeMinima());
            }
            estoquePersist = estoqueBd;
            // nao cria estoque a partir do orçamento
        } else if (orcamento == null) {
            estoqueNew = new Estoque();
            estoqueNew.setQuantidade(qtd);
            estoqueNew.setProduto(produto);
            estoqueNew.setUnidadeMedida(unMedidaItem);
            if (estoque != null) {
                estoqueNew.setQuantidadeMaxima(estoque.getQuantidadeMaxima());
                estoqueNew.setQuantidadeMinima(estoque.getQuantidadeMinima());
            }
            estoquePersist = estoqueNew;
        }
        if (estoquePersist != null) {

            MovimentoEstoque movimento = new MovimentoEstoque();
            movimento.setDataMovimento(orcamento != null ? orcamento.getDataVenda() :new Date());
            movimento.setQuantidade(qtd);
            movimento.setTipoMovimento(tipoMvto);
            movimento.setObservacao(obs);
            movimento.setIdOrcamento(orcamento != null ? orcamento.getId() : null);
            movimento.setNotaFiscal(notaFiscal);
            movimento.setUsuario(usuario);
            movimento.setEstoque(estoquePersist);
            //  List<MovimentoEstoque> mov = estoquePersist.getMovimentos();
            //  if(Utils.empty(mov)){
            //    mov = new ArrayList<>();

            // }
            //  estoquePersist.getMovimentos().add(movimento);
            em.merge(movimento);
        }

    }

    public List<MovimentoEstoque> listarMovimentos(Estoque estoque) {
        try {
            return (List<MovimentoEstoque>) em.createQuery("select m from MovimentoEstoque m where m.estoque =:estoque order by m.id desc").setParameter("estoque", estoque).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<MovimentoEstoque> listarMovimentos(Date dataInicial, Date dataFinal, Produto produto,
            EnumTipoLancamento tipo, String docEntradaSaida, Empresa empresa) {
        String sql = "select distinct m from MovimentoEstoque m "
                + " where  1 = 1"
                + " and m.estoque.produto.empresa =:empresa ";
        if (dataInicial != null) {
            sql += " and m.dataMovimento >= :dataInicial ";
        }
        if (dataFinal != null) {
            sql += " and m.dataMovimento <= :dataFinal ";
        }

        if (produto != null) {
            sql += " and m.estoque.produto =:produto";
        }
        if (tipo != null) {
            sql += " and m.tipoMovimento =:tipo";
        }
        if (!Utils.empty(docEntradaSaida)) {
            sql += " and m.notaFiscal like :docEntradaSaida ";
        }
        sql += " order by m.dataMovimento";

        try {
            Query q = em.createQuery(sql);
            q.setParameter("empresa", empresa);
            if (dataInicial != null) {
                q.setParameter("dataInicial", dataInicial);
            }
            if (dataFinal != null) {
                q.setParameter("dataFinal", dataFinal);
            }
            if (produto != null) {
                q.setParameter("produto", produto);
            }
            if (tipo != null) {
                q.setParameter("tipo", tipo);
            }
            if (!Utils.empty(docEntradaSaida)) {
                q.setParameter("docEntradaSaida", "%" + docEntradaSaida + "%");
            }
            return (List<MovimentoEstoque>) q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CurvaAbcEstoque> getCurvaAbc(final Empresa empresa,final Date dataInicial,final Date dataFinal) {
        final BigDecimal totalFaturamento = orcamentoEJB.getTotalFaturamento(empresa, dataInicial, dataFinal);
        final List<Object> resultado = this.getConsultaCurvaAbc(empresa, dataInicial, dataFinal);
        if (!Utils.empty(resultado)) {
            List<CurvaAbcEstoque> curvaAbcList = new ArrayList<>();
            CurvaAbcEstoque curvaAbc = null;
            BigDecimal percentualAcumulado = BigDecimal.ZERO;
            int idx = 0;
            for (Object object : resultado) {
                final Object[] obj = (Object[]) object;
                final Produto p = (Produto) obj[0];
                final UnidadeMedida um = (UnidadeMedida) obj[1];
                final BigDecimal qtd = (BigDecimal) Utils.nvl(obj[2], BigDecimal.ZERO);
                final BigDecimal valorTotal = (BigDecimal) Utils.nvl(obj[3], BigDecimal.ZERO);
                final BigDecimal result = valorTotal.divide(totalFaturamento, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
                final BigDecimal percentual = result.multiply(BigDecimal.valueOf(100));
                percentualAcumulado = percentualAcumulado.add(percentual);
                String classificacao;
                if ((percentualAcumulado.compareTo(BigDecimal.valueOf(80)) <= 0) || idx == 0  )  {
                    classificacao = Constantes.CLASSIFICACAO_ABC_A;
                } else if (percentualAcumulado.compareTo(BigDecimal.valueOf(95)) <= 0 || idx == 1) {
                    classificacao = Constantes.CLASSIFICACAO_ABC_B;
                } else {
                    classificacao = Constantes.CLASSIFICACAO_ABC_C;
                }
                curvaAbc = new CurvaAbcEstoque(p, um, qtd, null, valorTotal, percentual, percentualAcumulado, classificacao);
                curvaAbcList.add(curvaAbc);
                idx++;// preciso desse valor para pegar a primeira posicao da lista, sempre o produto da primeira posicao sera Classificacao A
            }
            return curvaAbcList;
        }
        return null;

    }
    

    private List<Object> getConsultaCurvaAbc(final Empresa empresa,final  Date dataInicial,final Date dataFinal) {
        StringBuilder sb = new StringBuilder();
        sb.append("select i.produto,i.unidadeMedida, sum(i.quantidade) qtd, sum(i.valorTotal) total ");
        sb.append(" from Orcamento o ");
        sb.append(" join o.orcamentoItemList i ");
        sb.append(" where o.usuario.empresa =:empresa  ");
        sb.append(" and o.status =:venda ");
        if (dataInicial != null) {
            sb.append(" and o.dataVenda >=:dataInicial");
        }
        if (dataFinal != null) {
            sb.append(" and o.dataVenda <=:dataFinal ");
        }
        sb.append(" group by i.produto,i.unidadeMedida ");
        sb.append(" order by total desc ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("empresa", empresa);
        q.setParameter("venda", EnumStatusOrcamento.VENDA);
        if (dataInicial != null) {
            q.setParameter("dataInicial", dataInicial);
        }
        if (dataFinal != null) {
            q.setParameter("dataFinal", dataFinal);
        }
        try {
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Notificacao> getNotificacoesEstoque(final Empresa empresa) {

        final List<CurvaAbcEstoque> abcEstoques = this.getCurvaAbc(empresa, null, null);
        if (!Utils.empty(abcEstoques)) {
            Notificacao notificacao;
            List<Notificacao> notificacoes = new ArrayList<>();
            for (CurvaAbcEstoque abcEstoque : abcEstoques) {
                if (abcEstoque.getClassificacao().equals(Constantes.CLASSIFICACAO_ABC_A)) {
                    Produto produto = abcEstoque.getProduto();
                    Estoque estoque = this.getEstoquePorProduto(produto);
                    if (estoque != null && estoque.getQuantidadeMinima() != null) {
                        BigDecimal qtdMin = estoque.getQuantidadeMinima();
                        String un = estoque.getUnidadeMedida() != null ? estoque.getUnidadeMedida().getSigla().getValue(): "UN";
                        if (qtdMin.compareTo(estoque.getQuantidade()) >= 0) {
                            BigDecimal dif = qtdMin.subtract(estoque.getQuantidade());
                            String texto = "O produto " + produto.getDescricao() + ", dentre os campeões de venda, está com o estoque baixo. "
                                    + "Você corre o risco de perder vendas. Complemente o seu estoque em ao menos "+ dif + " "+un+"S";
                            notificacao = new Notificacao(null, produto, texto);
                            notificacoes.add(notificacao);
                        }
                    }
                }
            }
            return notificacoes;
        }
        return null;
    }



}
