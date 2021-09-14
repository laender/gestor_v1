/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.enums;

/**
 *
 * @author laender
 */
public enum EnumTipoTitulo {

    PAGAR("PAGAR"),
    RECEBER("RECEBER");

    private final String value;

    EnumTipoTitulo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
