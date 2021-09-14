/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.OrcamentoItem;
import com.gestor.entidades.Produto;
import com.gestor.entidades.VeiculoCliente;
import com.gestor.enums.EnumStatusOrcamento;
import com.gestor.enums.EnumTipoPeriodo;
import com.gestor.util.ClienteRetorno;
import com.gestor.util.ClienteRetornoProduto;
import com.gestor.util.CurvaAbcCliente;
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
public class ClienteEJB extends AbstractEJB<Cliente> {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    @EJB
    private OrcamentoEJB orcamentoEJB;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClienteEJB() {
        super(Cliente.class);
    }

    public void removerCliente(Cliente cliente) throws GestorException {
        List<Orcamento> orcamentos = orcamentoEJB.getOrcamentosCliente(cliente);
        if (Utils.empty(orcamentos)) {
            this.remove(cliente);
        } else {
            throw new GestorException("Exclusão nao permitida, existe orcamentos vinculados ao cliente");
        }
    }

    @Override
    public List<Cliente> findAll(Empresa empresa) {
        Query q = getEntityManager().createQuery("select c from Cliente c where c.empresa =:empresa order by c.nome ").
                setParameter("empresa", empresa);
        return q.getResultList();
    }

    public Cliente gravarCliente(Cliente cliente) {
        return em.merge(cliente);
    }

    public BigDecimal getValorTotalCompras(Cliente cliente) {
        try {
            return (BigDecimal) em.createQuery("select sum(o.valorTotal) from Orcamento o where o.status =:status and o.cliente =:cliente").
                    setParameter("status", EnumStatusOrcamento.VENDA).
                    setParameter("cliente", cliente).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getDataUltimaCompra(Cliente cliente) {
        try {
            return (Date) em.createQuery("select max(o.dataOrcamento) from Orcamento o where o.status =:status and o.cliente =:cliente").
                    setParameter("status", EnumStatusOrcamento.VENDA).
                    setParameter("cliente", cliente).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getQuantidadeTotalCompras(Cliente cliente) {
        try {
            return (Long) em.createQuery("select count(o.id) from Orcamento o where o.status =:status and o.cliente =:cliente").
                    setParameter("status", EnumStatusOrcamento.VENDA).
                    setParameter("cliente", cliente).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Object> getProdutosMaisComprados(Cliente cliente) {
        try {
            List<Object> lista = em.createQuery(" select i.produto, sum(i.quantidade) qtd "
                    + "from Orcamento o JOIN o.orcamentoItemList i "
                    + "where o.status =:status and o.cliente =:cliente "
                    + "group by i.produto order by qtd desc").
                    setParameter("status", EnumStatusOrcamento.VENDA).
                    setParameter("cliente", cliente).setMaxResults(3).getResultList();
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Object> getRankingClientes(Empresa empresa) {
        try {
            List<Object> lista = em.createQuery(""
                    + " select c, sum(o.valorTotal) total "
                    + " from Orcamento o  "
                    + " JOIN o.cliente c "
                    + " where o.status =:status "
                    + " and o.usuario.empresa =:empresa"
                    + " group by c  order by total desc").
                    setParameter("status", EnumStatusOrcamento.VENDA).
                    setParameter("empresa", empresa).setMaxResults(20).getResultList();
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CurvaAbcCliente> getCurvaAbc(Empresa empresa, Date dataInicial, Date dataFinal) {
        BigDecimal totalFaturamento = orcamentoEJB.getTotalFaturamento(empresa, dataInicial, dataFinal);
        List<Object> resultado = this.getConsultaCurvaAbc(empresa, dataInicial, dataFinal);
        if (!Utils.empty(resultado)) {
            List<CurvaAbcCliente> curvaAbcList = new ArrayList<>();
            CurvaAbcCliente curvaAbc = null;
            BigDecimal percentualAcumulado = BigDecimal.ZERO;
            int idx = 0;
            for (Object object : resultado) {
                Object[] obj = (Object[]) object;
                Cliente p = (Cliente) obj[0];
                Long qtd = (Long) Utils.nvl(obj[1], Long.valueOf(0));
                BigDecimal valorTotal = (BigDecimal) Utils.nvl(obj[2], BigDecimal.ZERO);
                BigDecimal result = valorTotal.divide(totalFaturamento, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal percentual = result.multiply(BigDecimal.valueOf(100));
                percentualAcumulado = percentualAcumulado.add(percentual);
                String classificacao;
                if ((percentualAcumulado.compareTo(BigDecimal.valueOf(80)) <= 0) || idx == 0  )  {
                    classificacao = Constantes.CLASSIFICACAO_ABC_A;
                } else if (percentualAcumulado.compareTo(BigDecimal.valueOf(95)) <= 0 || idx == 1) {
                    classificacao = Constantes.CLASSIFICACAO_ABC_B;
                } else {
                    classificacao = Constantes.CLASSIFICACAO_ABC_C;
                }
                curvaAbc = new CurvaAbcCliente(p, BigDecimal.valueOf(qtd), valorTotal, valorTotal, percentual, percentualAcumulado, classificacao);
                curvaAbcList.add(curvaAbc);
                idx++;
            }
            return curvaAbcList;
        }
        return null;

    }

    private List<Object> getConsultaCurvaAbc(Empresa empresa, Date dataInicial, Date dataFinal) {
        StringBuilder sb = new StringBuilder();
        sb.append("select o.cliente ,count(o.id) qtd, sum(o.valorTotal) total ");
        sb.append(" from Orcamento o ");
        sb.append(" where o.usuario.empresa =:empresa  ");
        sb.append(" and o.status =:venda ");
        if (dataInicial != null) {
            sb.append(" and o.dataVenda >=:dataInicial");
        }
        if (dataFinal != null) {
            sb.append(" and o.dataVenda <=:dataFinal ");
        }
        sb.append(" and o.cliente.nome != :consumidor");
        sb.append(" group by o.cliente ");
        sb.append(" order by total desc ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("empresa", empresa);
        q.setParameter("venda", EnumStatusOrcamento.VENDA);
        q.setParameter("consumidor", "Consumidor");
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
// cliente utilizado na venda rapida

    public Cliente getClienteConsumidor(Empresa empresa) {
        try {
            return (Cliente) em.createQuery("select c from Cliente c where c.empresa =:empresa and c.nome =:consumidor ")
                    .setParameter("empresa", empresa)
                    .setParameter("consumidor", "Consumidor").getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

    public Query getConsultaRetornos(Empresa empresa) {
        return em.createQuery(
                " select distinct o from Orcamento o  join o.orcamentoItemList i "
                + " where o.usuario.empresa =:empresa "
                + " and i.produto.periodoRetorno != null "
                + " and o.status = :venda "
                + " and o.cliente.nome != :consumidor"
                + " order by i.orcamento.cliente.nome")
                .setParameter("empresa", empresa)
                .setParameter("venda", EnumStatusOrcamento.VENDA)
                .setParameter("consumidor", "Consumidor");
    }

    public List<ClienteRetorno> listarRetornosPrevistos(Empresa empresa) {
        Date hoje = new Date();
        try {
            List<Orcamento> orcamentos = this.getConsultaRetornos(empresa).getResultList();
            if (!Utils.empty(orcamentos)) {
//                HashMap<Cliente, List<ClienteRetornoProduto>> map = new HashMap();
                List<ClienteRetorno> clienteRetornos = new ArrayList<>();
                for (Orcamento orcamento : orcamentos) {
                    ClienteRetorno clienteRetorno = new ClienteRetorno();
                    clienteRetorno.setCliente(orcamento.getCliente());
                    Date dataVenda = orcamento.getDataVenda() != null ? orcamento.getDataVenda() : orcamento.getDataOrcamento();
                    List<OrcamentoItem> itens = orcamento.getOrcamentoItemList();
                    List<ClienteRetornoProduto> listaRet = new ArrayList<>();
                    for (OrcamentoItem iten : itens) {
                        Produto p = iten.getProduto();
                        Integer periodoRetorno = p.getPeriodoRetorno();
                        if(periodoRetorno == null){
                            continue;
                        }
                        EnumTipoPeriodo tipo = p.getTipoPeriodo();
                        Integer periodoEmDias = this.getPeriodoDias(periodoRetorno, tipo);
                        int difDatas = Utils.diffDate(dataVenda, hoje, Calendar.DAY_OF_YEAR);
                        Integer diasFaltantes = periodoEmDias - difDatas;
                        if (diasFaltantes < 0 || diasFaltantes > 10) {
                            continue;
                        }
                        ClienteRetornoProduto ret = new ClienteRetornoProduto(p, diasFaltantes);
                        listaRet.add(ret);
                    }
                    if (!Utils.empty(listaRet)) {
                        if (!Utils.empty(clienteRetornos) && clienteRetornos.contains(clienteRetorno)) {
                            int idx = clienteRetornos.indexOf(clienteRetorno);
                            ClienteRetorno cr = clienteRetornos.get(idx);
                            for (ClienteRetornoProduto crp : listaRet) {
                                cr.getProdutos().add(crp);
                            }
                        } else {
                            clienteRetorno.setProdutos(listaRet);
                            clienteRetornos.add(clienteRetorno);
                        }
                    }
                }
                return clienteRetornos;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    private Integer getPeriodoDias(Integer periodo, EnumTipoPeriodo tipo) {
        if (tipo.equals(EnumTipoPeriodo.DIAS)) {
            return periodo;
        }
        if (tipo.equals(EnumTipoPeriodo.MESES)) {
            return periodo * 30;
        } else {
            return periodo * 365;
        }
    }

    public List<Notificacao> getNotificacoesCliente(Empresa empresa) {

        List<CurvaAbcCliente> abcClientes = this.getCurvaAbc(empresa, null, null);
        if (!Utils.empty(abcClientes)) {
            Notificacao notificacao;
            List<Notificacao> notificacoes = new ArrayList<>();
            for (CurvaAbcCliente abcCliente : abcClientes) {
                if (abcCliente.getClassificacao().equals(Constantes.CLASSIFICACAO_ABC_A)) {
                    Cliente cliente = abcCliente.getCliente();
                    Date dataUltimaCompra = this.getDataUltimaCompra(cliente);
                    Date hoje = Calendar.getInstance().getTime();
                    int difDatas = Utils.diffDate(dataUltimaCompra, hoje, Calendar.DAY_OF_YEAR);
                    if (difDatas >= 60 && difDatas < 65) {
                        String texto = "O excelente cliente " + cliente.getNome() + " está há " + difDatas + " "
                                + " dias sem realizar uma compra ou serviço. "
                                + "Que tal ligar pra ele(a)? O seu telefone é " + cliente.getTelefone() + ".";
                        notificacao = new Notificacao(cliente, null, texto);
                        notificacoes.add(notificacao);
                    }
                }
            }
            return notificacoes;
        }

        return null;
    }

    public List<Notificacao> getNotificacaoClienteAniversario(Empresa empresa) {
        try {
            List<Cliente> clientes = em.createQuery("select e from Cliente e where e.empresa =:empresa and e.dataAniversario != null").setParameter("empresa", empresa).getResultList();
            if (!Utils.empty(clientes)) {
                List<Notificacao> notificacoes = new ArrayList<>();

                for (Cliente cliente : clientes) {
                    Date aniversario = cliente.getDataAniversario();
                    Calendar aniver = Calendar.getInstance();
                    aniver.setTime(aniversario);

                    int diaAniver = aniver.get(Calendar.DAY_OF_MONTH);
                    int mesAniver = aniver.get(Calendar.MONTH);
                    Calendar hoje = Calendar.getInstance();
                    int diaAtual = hoje.get(Calendar.DAY_OF_MONTH);
                    int mesAtual = hoje.get(Calendar.MONTH);
                    if (diaAniver == diaAtual && mesAniver == mesAtual) {
                        String telefone = Utils.empty(cliente.getTelefone()) ? "ops, ele não possui um telefone cadastrado :/" : cliente.getTelefone();
                        Notificacao not = new Notificacao(cliente, null, "O cliente " + cliente.getNome() + " está de aniversário hoje! O que acha de felicitá-lo? Seu telefone é " + telefone);
                        notificacoes.add(not);
                    }

                }
                return notificacoes;
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public void desativaVeiculo(VeiculoCliente veiculo){
        veiculo.setAtivo(false);
        em.merge(veiculo);
    }

}
