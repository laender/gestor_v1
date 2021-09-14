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
public enum EnumRamoAtividade {

    FABRICA("FABRICA"),
    OFICINA("OFICINA"),
    COMERCIO("COMERCIO"), 
    DESPACHANTE("DESPACHANTE");

    private final String value;

    EnumRamoAtividade(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
