/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.CidadeEJB;
import com.gestor.ejb.EmpresaEJB;
import com.gestor.ejb.FormaPagamentoEJB;
import com.gestor.ejb.RamoAtividadeEJB;
import com.gestor.ejb.TabelaPrecoEJB;
import com.gestor.ejb.TipoPagamentoEJB;
import com.gestor.ejb.UsuarioEJB;
import com.gestor.entidades.Cidade;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.FormaPagamento;
import com.gestor.entidades.RamoAtividade;
import com.gestor.entidades.TabelaPreco;
import com.gestor.entidades.TipoPagamento;
import com.gestor.entidades.Usuario;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author laender
 */
@ManagedBean
@ViewScoped
public class CadastroEmpresaController implements Serializable, ICadastros {

    private Empresa empresa = null;

    private List<Empresa> empresas;

    @EJB
    private EmpresaEJB empresaEJB;
    @EJB
    private RamoAtividadeEJB ramoAtividadeEJB;

    @EJB
    private CidadeEJB cidadeEJB;

    private UploadedFile file;

    @EJB
    private UsuarioEJB usuarioEJB;

    @EJB
    private FormaPagamentoEJB formaPagamentoEJB;
    @EJB
    private TipoPagamentoEJB tipoPagamentoEJB;
    
    @EJB
    private TabelaPrecoEJB tabelaPrecoEJB;

    public CadastroEmpresaController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        empresas = this.getEmpresas();
    }

    public void novo() {
        empresa = new Empresa();
        empresa.setAtivo(true);
        empresa.setFluxoCaixa(true);
        empresa.setLogoImage("empresas.jpg");
        empresa.setInstagram(null);
        WebUtils.update("form");

    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        WebUtils.update("form");

    }

    public List<Empresa> getEmpresas() {
        return empresaEJB.findAll();
    }

    public void setEmpresas(List<Empresa> empresas) {
        this.empresas = empresas;
    }

    public List<Cidade> listarCidades() {
        return cidadeEJB.findAll();
    }

    public List<RamoAtividade> listarRamosAtividade() {
        return ramoAtividadeEJB.findAll();
    }

    @Override
    public void salvar() {
        try {
            if (empresa == null) {
                WebUtils.messageWarn("Nenhum Empresa Informado");
                return;
            }
            if (Utils.empty(empresa.getNome())) {
                WebUtils.messageWarn("Nome nao informado");
                return;
            }
            if (!Utils.empty(empresa.getEmail()) && !this.emailValido(empresa.getEmail())) {
                WebUtils.messageWarn("Email Invalido");
                return;
            }
            if (empresa.getRamoAtividade() == null) {
                WebUtils.messageWarn("Informe o ramo de atividade");
                return;
            }
            if (empresa.isComissaoTotalMaoObra() && empresa.isComissaoTotalVenda()) {
                WebUtils.messageWarn("Deve ter apenas uma opcao de comissao selecionada");
                return;
            }
            if (Utils.empty(empresa.getLoginKey())) {
                WebUtils.messageWarn("Deve ser informado a chave de login (LoginKey)");
                return;
            }
            if (empresa.isComissaoTotalMaoObra() && empresa.isComissaoTotalVenda()) {
                WebUtils.messageWarn("Deve ser informado se a comissão é sobre o total da venda OU sobre o total de mão de obra/serviço");
                return;
            }
            if(empresa.getInstagram()!= null && empresa.getInstagram().equals("")){
                empresa.setInstagram(null);
            }
            if(empresa.getCnpjCpf()!= null && empresa.getCnpjCpf().equals("")){
                empresa.setCnpjCpf(null);
            }
            if (file != null) {
                empresa.setLogo(file.getContents());
            }
            Empresa empresaBd = empresaEJB.getEmpresaPorLogin(this.empresa.getLoginKey());
            if (empresaBd != null && this.empresa.getId() == null) {
                WebUtils.messageWarn("Login Key ja cadastrado");
                return;
            }
            if (empresaBd != null && empresa.getId() != null && !empresa.getId().equals(empresaBd.getId())) {
                WebUtils.messageWarn("Login Key já cadastrado para a empresa " + empresaBd.getNome());
                return;
            }
            boolean gerarCadastrosDefault = false;
            if (empresa.getId() == null) {
                gerarCadastrosDefault = true;
            } 
            Empresa emp = empresaEJB.merge(empresa);
            if(gerarCadastrosDefault){
                this.addCadastrosDefault(emp);
            }
            

        } catch (Exception e) {
            WebUtils.messageError("Erro ao Salvar: " + e.toString());
            WebUtils.update("form");
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
    }

    public boolean emailValido(String email) {
        Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher m = p.matcher(email);
        return m.find();
    }

    @Override
    public void cancelar() {
        empresa = null;
        WebUtils.update("form");
    }

    @Override
    public void fechar() {
        cancelar();
        WebUtils.redirect("../menuAdmin");
        WebUtils.update("form");
    }

    public void excluir() {
        try {
            List<Usuario> usuarios = usuarioEJB.getUsuarios(empresa);
            for (Usuario usuario : usuarios) {
                usuarioEJB.excluirTodos(usuario);
            }

            empresaEJB.remove(empresa);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir o Empresa. " + e.toString());
            return;
        }
        WebUtils.messageInfo("Registro Excluido com Sucesso");
        cancelar();
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void handleFileUpload(FileUploadEvent event) {
        this.file = event.getFile();
        //  FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        //FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private void addCadastrosDefault(Empresa emp) {
        this.addUsuarioDefault(emp);
        this.addFormaPgtoDefault(emp);
        this.addTipoPgtoDefault(emp);
        this.addTabelaPrecoDefault(emp);
    }

    private void addTipoPgtoDefault(Empresa empresa) {
        TipoPagamento tipoDinheiro = new TipoPagamento("DINHEIRO", empresa);
        TipoPagamento tipoCheque = new TipoPagamento("CHEQUE", empresa);
        TipoPagamento tipoBoleto = new TipoPagamento("BOLETO", empresa);
        TipoPagamento tipoCC = new TipoPagamento("CARTÃO CRÉDITO", empresa);
        TipoPagamento tipoCD = new TipoPagamento("CARTÃO DÉBITO", empresa);

        List<TipoPagamento> tipos = new ArrayList<>();
        tipos.add(tipoCD);
        tipos.add(tipoDinheiro);
        tipos.add(tipoBoleto);
        tipos.add(tipoCC);
        tipos.add(tipoCheque);
        for (TipoPagamento tipo : tipos) {
            tipoPagamentoEJB.merge(tipo);
        }

    }

    private void addFormaPgtoDefault(Empresa empresa) {
        FormaPagamento avista = new FormaPagamento("A VISTA", false, 0, 0, 0, empresa);
        FormaPagamento prazo30 = new FormaPagamento("PRAZO 30 DIAS", true, 30, 30, 1, empresa);
        FormaPagamento prazo3060 = new FormaPagamento("PRAZO 30/60", true, 30, 30, 2, empresa);
        List<FormaPagamento> formasPgto = new ArrayList<>();
        formasPgto.add(avista);
        formasPgto.add(prazo30);
        formasPgto.add(prazo3060);
        for (FormaPagamento formaPagamento : formasPgto) {
            formaPagamentoEJB.merge(formaPagamento);
        }
    }

    private void addUsuarioDefault(Empresa empresa) {
        Usuario usuario = new Usuario();
        usuario.setNome("Administrador");
        usuario.setLogin("admin");
        usuario.setAdministrador(true);
        usuario.setAtivo(true);
        usuario.setSenha("123");
        usuario.setEmpresa(empresa);
        usuario.setCidade(empresa.getCidade());

        usuarioEJB.salvar(usuario);
    }
    
    private void addTabelaPrecoDefault(Empresa empresa){
        TabelaPreco tabelaPreco = new TabelaPreco();
        tabelaPreco.setEmpresa(empresa);
        tabelaPreco.setDescricao("TABELA PREÇO PADRÃO");
        tabelaPreco.setPadrao(true);
        tabelaPrecoEJB.merge(tabelaPreco);
    }

}
