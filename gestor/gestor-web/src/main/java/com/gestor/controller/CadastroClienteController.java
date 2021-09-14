/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.CidadeEJB;
import com.gestor.ejb.ClienteEJB;
import com.gestor.entidades.Cidade;
import com.gestor.entidades.Cliente;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Produto;
import com.gestor.entidades.VeiculoCliente;
import com.gestor.filtros.FiltroPaginacao;
import com.gestor.util.ClienteValor;
import com.gestor.util.CurvaAbcCliente;
import com.gestor.util.ProdutoQuantidade;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author laender
 */
@ManagedBean
@ViewScoped
public class CadastroClienteController implements Serializable, ICadastros {

    private Cliente cliente = null;

    private List<Cliente> clientes;

    @EJB
    private ClienteEJB clienteEJB;

    @EJB
    private CidadeEJB cidadeEJB;

    private Empresa empresa = null;

    private BigDecimal valorTotalCompra;
    private Long qtdTotalCompras;
    private Date dataUltimaCompra;
    private List<ProdutoQuantidade> produtosMaisComprados;
    private List<ClienteValor> rankingClientes;
    private List<CurvaAbcCliente> curvaAbcClientes;
    private Date dataInicial;
    private Date dataFinal;
    private LazyDataModel<Cidade> modelCidade;

    private VeiculoCliente veiculoCliente;

    public CadastroClienteController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        clientes = this.getClientes();
        empresa = WebUtils.getEmpresa();
        rankingClientes = carregarRanking();
        veiculoCliente = new VeiculoCliente();
        veiculoCliente.setAtivo(true);
        modelCidade = new LazyDataModel<Cidade>() {

            private static final long serialVersionUID = 1L;

            @Override
            public List<Cidade> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {

                boolean asc = sortOrder.equals(sortOrder.ASCENDING);

                FiltroPaginacao filtro = new FiltroPaginacao(first, pageSize, sortField, empresa, filters, asc);

                setRowCount(cidadeEJB.quantidadeFiltrados(filtro));

                return cidadeEJB.getCidadesFiltradas(filtro);
            }

        };

        WebUtils.update("form");

    }

    public void novo() {
        this.cancelar();
        cliente = new Cliente();
        cliente.setEmpresa(empresa);
        cliente.setDataCadastro(new Date());

        WebUtils.update("form");

    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) {
            valorTotalCompra = clienteEJB.getValorTotalCompras(cliente);
            qtdTotalCompras = clienteEJB.getQuantidadeTotalCompras(cliente);
            dataUltimaCompra = clienteEJB.getDataUltimaCompra(cliente);
            List<Object> lista = clienteEJB.getProdutosMaisComprados(cliente);
            if (!Utils.empty(lista)) {
                produtosMaisComprados = new ArrayList<>();
                for (Object object : lista) {
                    Object[] obj = (Object[]) object;
                    Produto p = (Produto) obj[0];
                    BigDecimal qtd = (BigDecimal) obj[1];
                    ProdutoQuantidade d = new ProdutoQuantidade(p, qtd);
                    produtosMaisComprados.add(d);
                }
            }
        }
        veiculoCliente = new VeiculoCliente();
        veiculoCliente.setAtivo(true);
        WebUtils.update("form");
    }

    public List<ClienteValor> carregarRanking() {
        List<Object> lista = clienteEJB.getRankingClientes(WebUtils.getEmpresa());
        List<ClienteValor> retorno = null;
        if (!Utils.empty(lista)) {
            retorno = new ArrayList<>();
            int i = 1;
            for (Object object : lista) {
                Object[] obj = (Object[]) object;
                Cliente c = (Cliente) obj[0];
                BigDecimal valor = (BigDecimal) obj[1];
                ClienteValor d = new ClienteValor(c, valor, i);
                retorno.add(d);
                i++;
            }
        }
        return retorno;
    }

    public List<Cliente> getClientes() {
        return clienteEJB.findAll(empresa);
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public List<Cidade> listarCidades() {
        return cidadeEJB.findAll();
    }

    public void salvar() {
        try {
            if (cliente == null) {
                WebUtils.messageWarn("Nenhum cliente Informado");
                return;
            }
            if (Utils.empty(cliente.getNome())) {
                WebUtils.messageWarn("Nome não informado");
                return;
            }

            if (!Utils.empty(cliente.getEmail()) && !Utils.emailValido(cliente.getEmail())) {
                WebUtils.messageWarn("Email Inválido");
                return;
            }
            clienteEJB.merge(cliente);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
    }

    @Override
    public void cancelar() {
        cliente = null;
        valorTotalCompra = BigDecimal.ZERO;
        qtdTotalCompras = 0L;
        dataUltimaCompra = null;
        produtosMaisComprados = null;
        veiculoCliente = new VeiculoCliente();
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
            clienteEJB.removerCliente(cliente);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir o cliente. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

    public BigDecimal getValorTotalCompra() {
        return valorTotalCompra;
    }

    public void setValorTotalCompra(BigDecimal valorTotalCompra) {
        this.valorTotalCompra = valorTotalCompra;
    }

    public Long getQtdTotalCompras() {
        return qtdTotalCompras;
    }

    public void setQtdTotalCompras(Long qtdTotalCompras) {
        this.qtdTotalCompras = qtdTotalCompras;
    }

    public Date getDataUltimaCompra() {
        return dataUltimaCompra;
    }

    public void setDataUltimaCompra(Date dataUltimaCompra) {
        this.dataUltimaCompra = dataUltimaCompra;
    }

    public List<ProdutoQuantidade> getProdutosMaisComprados() {
        return produtosMaisComprados;
    }

    public void setProdutosMaisComprados(List<ProdutoQuantidade> produtosMaisComprados) {
        this.produtosMaisComprados = produtosMaisComprados;
    }

    public List<ClienteValor> getRankingClientes() {
        return rankingClientes;
    }

    public void setRankingClientes(List<ClienteValor> rankingClientes) {
        this.rankingClientes = rankingClientes;
    }

    public void carregarCurvaABC() {
        curvaAbcClientes = clienteEJB.getCurvaAbc(WebUtils.getEmpresa(), dataInicial, dataFinal);
    }

    public List<CurvaAbcCliente> getCurvaAbcClientes() {
        return curvaAbcClientes;
    }

    public void setCurvaAbcClientes(List<CurvaAbcCliente> curvaAbcClientes) {
        this.curvaAbcClientes = curvaAbcClientes;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public LazyDataModel<Cidade> getModelCidade() {
        return modelCidade;
    }

    public void addVeiculo() {
        if (veiculoCliente == null) {
            WebUtils.messageWarn("Nenhum veículo informado");
            return;
        }
        if (Utils.empty(veiculoCliente.getVeiculo())) {
            WebUtils.messageWarn("Informe a descrição do veículo");
            return;
        }
        veiculoCliente.setCliente(cliente);
        cliente.getVeiculos().add(veiculoCliente);
        veiculoCliente = new VeiculoCliente();
        veiculoCliente.setAtivo(true);
       // clienteEJB.merge(cliente);
    }

    public void removeVeiculo(VeiculoCliente veiculo) {
        cliente.getVeiculos().remove(veiculo);
        clienteEJB.desativaVeiculo(veiculo);

    }

    public VeiculoCliente getVeiculoCliente() {
        return veiculoCliente;
    }

    public void setVeiculoCliente(VeiculoCliente veiculoCliente) {
        this.veiculoCliente = veiculoCliente;
    }

}
