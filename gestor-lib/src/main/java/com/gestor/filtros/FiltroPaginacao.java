/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.filtros;

import com.gestor.entidades.Empresa;
import java.util.Map;

/**
 *
 * @author laend
 */
public class FiltroPaginacao {

    private static final long serialVersionUID = 1L;

    private int primeiroRegistro;
    private int quantidadeRegistros;
    private String propriedadeOrdenacao;
    private boolean ascendente;
    private Empresa empresa;
    private Map<String, Object> filtros;

    public FiltroPaginacao(int primeiroRegistro, int quantidadeRegistros, 
            String propriedadeOrdenacao,
            Empresa empresa,Map<String, Object> filtros, 
            boolean asc ) {
        this.primeiroRegistro = primeiroRegistro;
        this.quantidadeRegistros = quantidadeRegistros;
        this.propriedadeOrdenacao = propriedadeOrdenacao;
        this.empresa = empresa;
        this.filtros = filtros;
        this.ascendente = asc;
    }
    

    public int getPrimeiroRegistro() {
        return primeiroRegistro;
    }

    public void setPrimeiroRegistro(int primeiroRegistro) {
        this.primeiroRegistro = primeiroRegistro;
    }

    public int getQuantidadeRegistros() {
        return quantidadeRegistros;
    }

    public void setQuantidadeRegistros(int quantidadeRegistros) {
        this.quantidadeRegistros = quantidadeRegistros;
    }

    public String getPropriedadeOrdenacao() {
        return propriedadeOrdenacao;
    }

    public void setPropriedadeOrdenacao(String propriedadeOrdenacao) {
        this.propriedadeOrdenacao = propriedadeOrdenacao;
    }

    public boolean isAscendente() {
        return ascendente;
    }

    public void setAscendente(boolean ascendente) {
        this.ascendente = ascendente;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public void setFiltros(Map<String, Object> filtros) {
        this.filtros = filtros;
    }

    public Map<String, Object> getFiltros() {
        return filtros;
    }
    
    
}
