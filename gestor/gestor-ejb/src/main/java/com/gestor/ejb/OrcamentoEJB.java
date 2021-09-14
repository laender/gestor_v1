/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Estoque;
import com.gestor.entidades.FormaPagamento;
import com.gestor.entidades.Multiplicador;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.OrcamentoItem;
import com.gestor.entidades.OrcamentoVeiculo;
import com.gestor.entidades.Produto;
import com.gestor.entidades.ProdutoInsumo;
import com.gestor.entidades.TipoOrcamento;
import com.gestor.entidades.TipoPagamento;
import com.gestor.entidades.Titulo;
import com.gestor.entidades.UnidadeMedida;
import com.gestor.entidades.Usuario;
import com.gestor.enums.EnumStatusOrcamento;
import com.gestor.enums.EnumTipoLancamento;
import com.gestor.filtros.FiltroPaginacao;
import com.gestor.util.GestorException;
import com.gestor.util.Notificacao;
import com.gestor.util.Utils;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
public class OrcamentoEJB extends AbstractEJB<Orcamento> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private EstoqueEJB estoqueEJB;

    @EJB
    private TituloEJB tituloEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrcamentoEJB() {
        super(Orcamento.class);
    }

    public Orcamento gravarOrcamento(Orcamento orcamento) throws GestorException {
        final Usuario usuarioOrcamento = orcamento.getUsuario();
        List<OrcamentoItem> itens = orcamento.getOrcamentoItemList();
        BigDecimal valorTotal = BigDecimal.ZERO;
        final Empresa empresa = orcamento.getUsuario().getEmpresa();
        final EnumStatusOrcamento status = orcamento.getStatus();
        for (OrcamentoItem item : itens) {
            valorTotal = valorTotal.add(item.getValorTotal());
        }
        BigDecimal vlrDesconto = orcamento.getValorDesconto();
        BigDecimal vlrAcrescimo = orcamento.getValorAcrescimo();
        if (vlrDesconto != null) {
            valorTotal = valorTotal.subtract(vlrDesconto);
        }
        if (vlrAcrescimo != null) {
            valorTotal = valorTotal.add(vlrAcrescimo);
        }
        orcamento.setValorTotal(valorTotal);
        orcamento.setValorSaldo(valorTotal);

        //  Orcamento orct = em.merge(orcamento);
        for (OrcamentoItem item : itens) {
            // responsavel por somar o valor do insumo ao valor do produto e imprimir somente os produtos no orcamento
            this.embutirValorInsumoProduto(item, itens);
        }

        // removendo item forçadamente pois via relacionamento nao funciona
        if (orcamento.getId() != null) {
            Orcamento orcDB = this.find(orcamento.getId());
            List<OrcamentoItem> itensDb = orcDB.getOrcamentoItemList();
            for (OrcamentoItem itemDb : itensDb) {
                if (!itens.contains(itemDb)) {
                    this.removerItem(itemDb);
                }
            }
            for (OrcamentoItem item : itens) {
                em.merge(item);
            }
        }
        Orcamento orctBd = em.merge(orcamento);
        if (!status.equals(EnumStatusOrcamento.ORCAMENTO)) {
            if (empresa.isControlaEstoque()) {
                this.movimentarEstoque(orctBd);
            }
            this.movimentarFinanceiro(orctBd);
        }
        return orctBd;
    }

    private void movimentarEstoque(Orcamento orcamento) throws GestorException {
        EnumStatusOrcamento status = orcamento.getStatus();
        EnumTipoLancamento tipoLcto = status.equals(EnumStatusOrcamento.VENDA) ? EnumTipoLancamento.SAIDA : EnumTipoLancamento.ENTRADA;
        List<OrcamentoItem> itens = orcamento.getOrcamentoItemList();
        for (OrcamentoItem item : itens) {
            estoqueEJB.movimentarEstoque(item.getProduto(), item.getUnidadeMedida(), item.getQuantidade(), tipoLcto, orcamento, null, orcamento.getUsuario(), null, null);
        }
    }

    private void movimentarFinanceiro(Orcamento orcamento) throws GestorException {
        EnumStatusOrcamento status = orcamento.getStatus();
        if (status.equals(EnumStatusOrcamento.VENDA)) {
            tituloEJB.gerarTitulo(null, orcamento);
        }
        if (status.equals(EnumStatusOrcamento.CANCELADO)) {
            Titulo titulo = tituloEJB.getTituloPorOcamento(orcamento);
            if (titulo != null && titulo.getValorSaldo().compareTo(titulo.getValorTitulo()) < 0) {
                throw new GestorException("O orçamento ja teve um pagamento realizado, o pagamento deve ser estornado antes do cancelamento.");
            }
            if (titulo != null) {
                tituloEJB.remove(titulo);
            }
        }
    }

    private void embutirValorInsumoProduto(OrcamentoItem item, List<OrcamentoItem> itens) {
        Produto prd = item.getProduto();
        List<ProdutoInsumo> insumos = prd.getProdutoInsumos();
        if (!Utils.empty(insumos)) {
            BigDecimal valorEmbutido = item.getValorTotal();
            for (ProdutoInsumo prdIns : insumos) {
                for (OrcamentoItem it : itens) {
                    if (it.getProduto().equals(prdIns.getInsumo())) {
                        valorEmbutido = valorEmbutido.add(it.getValorTotal());
                    }
                }
            }
            item.setValorEmbutido(valorEmbutido);
            //em.merge(item);
            // se nao tiver nenhum insumo vinculado ao produto, tera que ser apresentado no orcamento
        }
    }

    public List<Orcamento> getOrcamentos(Empresa empresa) {

        try {
            return (List<Orcamento>) em.createQuery("select o from Orcamento o where o.usuario.empresa =:empresa order by o.id desc")
                    .setParameter("empresa", empresa)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Orcamento> getOrcamentosFiltrados(FiltroPaginacao filtro) {

        return (List<Orcamento>) this.criarQueryFiltro(filtro).
                setFirstResult(filtro.getPrimeiroRegistro())
                .setMaxResults(filtro.getQuantidadeRegistros())
                .getResultList();
    }

    private Query criarQueryFiltro(FiltroPaginacao filtro) {
        Map<String, Object> filters = filtro.getFiltros();
        String filtroId = (String) filters.get("id");
        String filtroNome = (String) filters.get("cliente.nome");
        String filtroPlaca = (String) filters.get("placaVeiculo");

        String sortField = filtro.getPropriedadeOrdenacao();
        boolean asc = filtro.isAscendente();

        String sql = "select distinct o from Orcamento o left join o.orcamentoItemList i left join i.veiculo c  where o.usuario.empresa =:empresa   ";
        if (!Utils.empty(filtroNome)) {
            sql += " and o.cliente.nome like :nomeCliente ";
        }
        if (!Utils.empty(filtroId)) {
            sql += " and CAST(o.id as varchar) like :id ";
        }
        if (!Utils.empty(filtroPlaca)) {
            sql += " and c.placaVeiculo like :placa or c.veiculo like :placa ";
        }
        if (!Utils.empty(sortField)) {
            sql += " order by o." + sortField; // atualmente é o unico campo possui sortBy
            if (!asc) {
                sql += " desc";
            }
        } else {
            sql += "order by o.id desc";
        }
        Query q = em.createQuery(sql);
        q.setParameter("empresa", filtro.getEmpresa());
        if (!Utils.empty(filtroNome)) {
            q.setParameter("nomeCliente", "%" + filtroNome + "%");
        }
        if (!Utils.empty(filtroId)) {
            q.setParameter("id", "%" + filtroId + "%");
        }
        if (!Utils.empty(filtroPlaca)) {
            q.setParameter("placa", "%" + filtroPlaca + "%");
        }
        return q;
    }

    public int quantidadeFiltrados(FiltroPaginacao filtro) {
        Query q = this.criarQueryFiltro(filtro);
        try {
            if (!Utils.empty(q.getResultList())) {
                return q.getResultList().size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Orcamento> getOrcamentosPorTipo(TipoOrcamento tipoOrcamento) {

        try {
            return (List<Orcamento>) em.createQuery("select o from Orcamento o where o.tipoOrcamento =:tipo")
                    .setParameter("tipo", tipoOrcamento)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public List<Orcamento> getOrcamentosCliente(Cliente cliente) {

        try {
            return (List<Orcamento>) em.createQuery("select o from Orcamento o where o.cliente =:cliente")
                    .setParameter("cliente", cliente)
                    .getResultList();
        } catch (Exception ex) {
            //  ex.printStackTrace();
            return null;
        }
    }

    public List<Orcamento> getVendas(Empresa empresa) {

        try {
            return (List<Orcamento>) em.createQuery("select o from Orcamento o where o.usuario.empresa =:empresa and o.status =:statusOrcamento")
                    .setParameter("empresa", empresa)
                    .setParameter("statusOrcamento", EnumStatusOrcamento.VENDA)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Orcamento> getOrcamentosPorFormaPagamento(FormaPagamento frmPgto) {

        try {
            return (List<Orcamento>) em.createQuery("select o from Orcamento o where o.formaPagamento =:frmPgto")
                    .setParameter("frmPgto", frmPgto)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Orcamento> getOrcamentosPorStatus(List<EnumStatusOrcamento> status, Empresa empresa) {
        String sql = "select o from Orcamento o where o.usuario.empresa =:empresa order by o.dataOrcamento";

        Query q = em.createQuery(sql);
        q.setParameter("empresa", empresa);

        List<Orcamento> orcamentos = q.getResultList();
        List<Orcamento> retorno = new ArrayList<>();
        if (!Utils.empty(orcamentos)) {
            for (Orcamento orcamento : orcamentos) {
                for (EnumStatusOrcamento s : status) {
                    if (orcamento.getStatus().equals(s)) {
                        retorno.add(orcamento);
                    }
                }
            }
        }
        return retorno;

    }

    public void movimentarSaldo(Orcamento orcamento, BigDecimal valorMovimentado, EnumTipoLancamento tipoLancamento) throws GestorException {
        BigDecimal saldoOrcamento = orcamento.getValorSaldo();
        if (tipoLancamento.equals(EnumTipoLancamento.ENTRADA)) {
            //  if (saldoOrcamento.compareTo(valorMovimentado) < 0) {
            //    throw new GestorException("O valor do saldo do orcamento (" + saldoOrcamento + ") é menor do que o valor do pagamento (" + valorMovimentado + ")");
            //  }
            orcamento.setValorSaldo(saldoOrcamento.subtract(valorMovimentado));
        } else {
            //  if ((saldoOrcamento.add(valorMovimentado)).compareTo(orcamento.getValorTotal()) > 0) {
            //     throw new GestorException("O valor  da devolucao (" + valorMovimentado + ") somado com o saldo devedor do orcamento(" + saldoOrcamento + ")e maior do que o valor total do mesmo(" + orcamento.getValorTotal() + ")");
            // }
            orcamento.setValorSaldo(saldoOrcamento.add(valorMovimentado));
        }
        em.merge(orcamento);
    }

    public void movimentarEstoque(BigDecimal quantidade, Produto produto, UnidadeMedida unidMed) throws GestorException {
        Estoque estoque = this.estoqueEJB.getEstoquePorProdutoEUnidadeMedida(produto, unidMed);
        if (estoque != null) {
            BigDecimal qtdEstoque = estoque.getQuantidade();
            estoque.setQuantidade(qtdEstoque.subtract(quantidade));
            em.merge(estoque);
        }

    }

    public List<Orcamento> listarOrcamentos(Empresa empresa, Date dataInicial, Date dataFinal, Orcamento orcamento, Usuario usuario, Cliente cliente, List<EnumStatusOrcamento> status, boolean orcamentoAberto) {
        String sql = "select o from Orcamento o where o.usuarioExecutante.empresa =:empresa ";
        if (dataInicial != null) {
            sql += " and o.dataOrcamento >= :dataInicial ";
        }
        if (dataFinal != null) {
            sql += " and o.dataOrcamento <= :dataFinal ";
        }
        if (usuario != null) {
            sql += " and o.usuarioExecutante =:usuario";
        }
        if (cliente != null) {
            sql += " and o.cliente =:cliente";
        }
        if (orcamento != null) {
            sql += " and o =:orcamento";
        }
        if (orcamentoAberto) {
            sql += " and o.valorSaldo > 0 ";
        }
        sql += " order by o.dataOrcamento ";
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
            if (cliente != null) {
                q.setParameter("cliente", cliente);
            }
            if (usuario != null) {
                q.setParameter("usuario", usuario);
            }
            List<Orcamento> orcamentos = q.getResultList();
            List<Orcamento> retorno = new ArrayList<>();
            if (!Utils.empty(orcamentos) && !Utils.empty(status)) {
                for (Orcamento o : orcamentos) {
                    for (EnumStatusOrcamento s : status) {
                        if (o.getStatus().equals(s)) {
                            retorno.add(o);
                        }
                    }
                }
            } else {
                return orcamentos;
            }

            return retorno;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removerItem(OrcamentoItem item) {
        OrcamentoItem orcamentoItem = em.find(OrcamentoItem.class, item.getId());
        em.remove(orcamentoItem);
    }

    public List<OrcamentoItem> getItensPorProduto(Produto produto) {
        try {
            List<OrcamentoItem> retorno = em.createQuery("select i from OrcamentoItem i where i.produto =:produto").setParameter("produto", produto).getResultList();
            return retorno;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Orcamento> getOrcamentoPortipoPagamento(TipoPagamento tipoPagamento) {
        try {
            List<Orcamento> retorno = em.createQuery("select i from Orcamento i where i.tipoPagamento =:tipoPagamento").
                    setParameter("tipoPagamento", tipoPagamento).getResultList();
            return retorno;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getTotalFaturamento(Empresa empresa, Date dataInicial, Date dataFinal) {
        String sql = "select sum(o.valorTotal) from Orcamento o where o.usuario.empresa =:empresa and o.status =:venda";
        if (dataInicial != null) {
            sql += " and o.dataVenda >=:dataInicial ";
        }
        if (dataFinal != null) {
            sql += " and o.dataVenda <=:dataFinal ";
        }
        try {
            Query q = em.createQuery(sql);
            q.setParameter("empresa", empresa);
            q.setParameter("venda", EnumStatusOrcamento.VENDA);
            if (dataInicial != null) {
                q.setParameter("dataInicial", dataInicial);
            }
            if (dataFinal != null) {
                q.setParameter("dataFinal", dataFinal);
            }
            return (BigDecimal) q.getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Notificacao getNotificacaoFaturamentoPeriodoAnterior(Empresa empresa) {
        Date dataFimMesAtual = Calendar.getInstance().getTime();
        Calendar primeiroDiaMesCalendar = Calendar.getInstance();
        primeiroDiaMesCalendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dataInicioMesAtual = primeiroDiaMesCalendar.getTime();

        Calendar diaAtualMesAnteriorCalendar = Calendar.getInstance();
        int mesAtual = Calendar.getInstance().get(Calendar.MONTH);
        int mesAnterior = 0;
        if (mesAtual == 1) {
            mesAnterior = 12;
        } else {
            mesAnterior = mesAtual - 1;
        }
        diaAtualMesAnteriorCalendar.set(Calendar.MONTH, mesAnterior);
        int diaAtual = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int totalDiasMesAnterior = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        int diaAtualMesAntes = totalDiasMesAnterior < diaAtual ? totalDiasMesAnterior : diaAtual;
        diaAtualMesAnteriorCalendar.set(Calendar.DAY_OF_MONTH, diaAtualMesAntes);
        Date dataFimMesAnterior = diaAtualMesAnteriorCalendar.getTime();
        diaAtualMesAnteriorCalendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dataInicioMesAnterior = diaAtualMesAnteriorCalendar.getTime();

        BigDecimal totalFaturamentoMesAnterior = this.getTotalFaturamento(empresa, dataInicioMesAnterior, dataFimMesAnterior);
        BigDecimal totalFaturamentoMesAtual = this.getTotalFaturamento(empresa, dataInicioMesAtual, dataFimMesAtual);
        Notificacao notificacao = null;
        if (totalFaturamentoMesAnterior != null && totalFaturamentoMesAtual != null) {
            if (totalFaturamentoMesAtual.compareTo(totalFaturamentoMesAnterior) > 0) {
                BigDecimal dif = totalFaturamentoMesAtual.subtract(totalFaturamentoMesAnterior);
                BigDecimal percAumento = (dif.multiply(BigDecimal.valueOf(100))).divide(totalFaturamentoMesAtual, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
                notificacao = new Notificacao(null, null, "O faturamento AUMENTOU  em " + percAumento + " % referente ao mesmo período do mês anterior.");
                notificacao.setFaturamentoAumentou(true);
            } else {
                BigDecimal dif = totalFaturamentoMesAnterior.subtract(totalFaturamentoMesAtual);
                BigDecimal percAumento = (dif.multiply(BigDecimal.valueOf(100))).divide(totalFaturamentoMesAnterior, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
                notificacao = new Notificacao(null, null, "O faturamento DIMINUIU em " + percAumento + " % referente ao mesmo período do mês anterior.");
            }
            if (totalFaturamentoMesAtual.compareTo(totalFaturamentoMesAnterior) == 0) {
                notificacao = new Notificacao(null, null, "O faturamento do mês atual não sofreu oscilaçao referente ao mesmo período do mês anterior.");
                notificacao.setFaturamentoIgual(true);
            }
        }

        return notificacao;
    }

    public List<Orcamento> getOrcamentoPorMultiplicador(Multiplicador multiplicador) {
        try {
            return (List<Orcamento>) em.createQuery("select o from Orcamento o where o.multiplicador =:multiplicador").setParameter("multiplicador", multiplicador).getResultList();
        } catch (Exception e) {
        }
        return null;
    }

    public void removeVeiculo(OrcamentoVeiculo orcamentoVeiculo) {
        try {
            OrcamentoVeiculo orctVeic = em.find(OrcamentoVeiculo.class, orcamentoVeiculo.getId());
            em.remove(orctVeic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mergeItem(OrcamentoItem item) {
        em.merge(item);
    }

}
