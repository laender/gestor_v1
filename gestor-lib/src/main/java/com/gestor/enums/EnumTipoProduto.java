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
public enum EnumTipoProduto {

    PRODUTO("PRODUTO"),
    INSUMO("INSUMO"),
    FRETE("FRETE"),
    MAO_DE_OBRA("MAO_DE_OBRA");

    private final String value;

    EnumTipoProduto(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
