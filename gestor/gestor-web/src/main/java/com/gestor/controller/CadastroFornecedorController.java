/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.CidadeEJB;
import com.gestor.ejb.FornecedorEJB;
import com.gestor.entidades.Cidade;
import com.gestor.entidades.Fornecedor;
import com.gestor.filtros.FiltroPaginacao;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class CadastroFornecedorController implements Serializable, ICadastros {

    private Fornecedor fornecedor = null;

    private List<Fornecedor> fornecedors;

    @EJB
    private FornecedorEJB fornecedorEJB;

    @EJB
    private CidadeEJB cidadeEJB;

    private LazyDataModel<Cidade> modelCidade;

    public CadastroFornecedorController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        fornecedors = this.getFornecedors();

        modelCidade = new LazyDataModel<Cidade>() {

            private static final long serialVersionUID = 1L;

            @Override
            public List<Cidade> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {

                boolean asc = sortOrder.equals(sortOrder.ASCENDING);

                FiltroPaginacao filtro = new FiltroPaginacao(first, pageSize, sortField, null, filters, asc);

                setRowCount(cidadeEJB.quantidadeFiltrados(filtro));

                return cidadeEJB.getCidadesFiltradas(filtro);
            }

        };
    }

    public void novo() {
        fornecedor = new Fornecedor();
        fornecedor.setEmpresa(WebUtils.getEmpresa());
        WebUtils.update("form");

    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
        WebUtils.update("form");

    }

    public List<Fornecedor> getFornecedors() {
        return fornecedorEJB.findAll(WebUtils.getEmpresa());
    }

    public void setFornecedors(List<Fornecedor> fornecedors) {
        this.fornecedors = fornecedors;
    }

    public List<Cidade> listarCidades() {
        return cidadeEJB.findAll();
    }

    public void salvar() {
        try {
            if (fornecedor == null) {
                WebUtils.messageWarn("Nenhum fornecedor Informado");
                return;
            }
            if (Utils.empty(fornecedor.getNome())) {
                WebUtils.messageWarn("Nome não informado");
                return;
            }
            if (!Utils.empty(fornecedor.getEmail()) && !this.emailValido(fornecedor.getEmail())) {
                WebUtils.messageWarn("Email Inválido");
                return;
            }

            fornecedorEJB.merge(fornecedor);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
    }

    public boolean emailValido(String email) {
        System.out.println("Metodo de validacao de email");
        Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher m = p.matcher(email);
        return m.find();
    }

    @Override
    public void cancelar() {
        fornecedor = null;
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
            fornecedorEJB.remove(fornecedor);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir o fornecedor. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

    public LazyDataModel<Cidade> getModelCidade() {
        return modelCidade;
    }

}
