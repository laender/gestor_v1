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
public enum EnumUnidadeMedida {
    
    UN("UNIDADE"),
    LT("LITRO"),
    ML("ML"),
    M("METRO"),
    CM("CM"),
    KG("KG"),
    G("GRAMA");

    private final String value;

    EnumUnidadeMedida(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
