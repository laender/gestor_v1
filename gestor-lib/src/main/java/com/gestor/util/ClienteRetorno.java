/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import com.gestor.entidades.Cliente;
import java.util.List;

/**
 *
 * @author laend
 */
public class ClienteRetorno {

    private Cliente cliente;

    private List<ClienteRetornoProduto> produtos;
    
    public ClienteRetorno(){
        
    }

    public ClienteRetorno(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ClienteRetornoProduto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ClienteRetornoProduto> produtos) {
        this.produtos = produtos;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && ((ClienteRetorno)obj).getCliente() != null ){
           return ((ClienteRetorno)obj).getCliente().equals(this.getCliente());
        }
        return false;
//        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }
    
    

}
