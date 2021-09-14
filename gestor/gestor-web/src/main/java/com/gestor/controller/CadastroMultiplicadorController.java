/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.controller;

import com.gestor.ejb.MultiplicadorEJB;
import com.gestor.entidades.Multiplicador;
import com.gestor.entidades.Usuario;
import com.gestor.util.Utils;
import com.gestor.util.WebUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author laender
 */
@ManagedBean
@ViewScoped
public class CadastroMultiplicadorController implements Serializable, ICadastros {

    private Multiplicador multiplicador = null;

    @EJB
    private MultiplicadorEJB multiplicadorEJB;

    public CadastroMultiplicadorController() {
    }

    @PostConstruct
    private void init() {
        cancelar();
        
    }

    public void novo() {
        multiplicador = new Multiplicador();
        Multiplicador multDb = multiplicadorEJB.getMultiplicadorPadrao(WebUtils.getEmpresa());
        if (multDb == null) {
            multiplicador.setPadrao(true);
        }
        Usuario context = WebUtils.getUsuario();
        multiplicador.setEmpresa(context.getEmpresa());
        WebUtils.update("form");

    }

    public Multiplicador getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(Multiplicador multiplicador) {
        this.multiplicador = multiplicador;
        WebUtils.update("form");

    }

    public List<Multiplicador> listarMultiplicadores() {
        return multiplicadorEJB.findAll(WebUtils.getUsuario().getEmpresa());
    }

    public void salvar() {
        try {
            if (multiplicador == null) {
                WebUtils.messageWarn("Nenhum tipo de Pagamento Informado");
                return;
            }
            if (multiplicador.getFator() == null || multiplicador.getFator().compareTo(BigDecimal.ZERO) == 0) {
                WebUtils.messageWarn("Fator de multiplicação nao informado");
                return;
            }
           

            multiplicadorEJB.merge(multiplicador);
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
        multiplicador = null;
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
            multiplicadorEJB.excluir(multiplicador);
        } catch (Exception e) {
            WebUtils.messageError("Erro ao excluir o multiplicador. " + e.toString());
            return;
        }
        WebUtils.messageRegistroGravado();
        cancelar();
    }

}
