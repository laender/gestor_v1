/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author laender
 */
public class ValidateNotNull {

    public static ValidateNotNull getInstance() {
        return new ValidateNotNull();
    }

    List<String> valores = new ArrayList<>();

    
    public void addCampo(Object valor, String nomeCampo) {
        if (valor == null) {
            valores.add(nomeCampo);
            return;
        }

        if (valor instanceof String) {
            if (((String) valor).trim().isEmpty()) {
                valores.add(nomeCampo);
            }
        }

    }

    /**
     *
     * @throws GestorException
     */
    public void validar() throws GestorException {
        if (!valores.isEmpty()) {
            String msg = "";
            for (String valor : valores) {
                msg += valor + ",";
            }
            throw new GestorException("Campos obrigatórios não preenchidos:"+ msg);
        }

    }

}
