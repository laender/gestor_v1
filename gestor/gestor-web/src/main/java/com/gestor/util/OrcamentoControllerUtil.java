/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import com.gestor.ejb.UnidadeMedidaEJB;
import com.gestor.entidades.UnidadeMedida;
import com.gestor.enums.EnumUnidadeMedida;

/**
 *
 * @author laender
 */
public class OrcamentoControllerUtil {

    public static UnidadeMedida getUnidadeMedidaPorSigla(String sigla, UnidadeMedidaEJB unidadeMedidaEJB) {
        switch (sigla) {
            case "ML":
                return unidadeMedidaEJB.getUnidadeMedidaPorChave(EnumUnidadeMedida.ML);
            case "UNIDADE":
                return unidadeMedidaEJB.getUnidadeMedidaPorChave(EnumUnidadeMedida.UN);
            case "METRO":
                return unidadeMedidaEJB.getUnidadeMedidaPorChave(EnumUnidadeMedida.M);
            case "CM":
                return unidadeMedidaEJB.getUnidadeMedidaPorChave(EnumUnidadeMedida.CM);
            case "GRAMA":
                return unidadeMedidaEJB.getUnidadeMedidaPorChave(EnumUnidadeMedida.G);
            case "KG":
                return unidadeMedidaEJB.getUnidadeMedidaPorChave(EnumUnidadeMedida.KG);
            case "LITRO":
                return unidadeMedidaEJB.getUnidadeMedidaPorChave(EnumUnidadeMedida.LT);
            default:
                return null;
        }
    }

    public static EnumUnidadeMedida getUnidadeConversao(UnidadeMedida unidadePrincipal) {

        if (unidadePrincipal.getSigla().equals(EnumUnidadeMedida.KG)) {
            return EnumUnidadeMedida.G;
        }
        if (unidadePrincipal.getSigla().equals(EnumUnidadeMedida.M)) {
            return EnumUnidadeMedida.CM;
        }
        if (unidadePrincipal.getSigla().equals(EnumUnidadeMedida.LT)) {
            return EnumUnidadeMedida.ML;
        }
        if (unidadePrincipal.getSigla().equals(EnumUnidadeMedida.UN)) {
            return EnumUnidadeMedida.UN;
        }
        if (unidadePrincipal.getSigla().equals(EnumUnidadeMedida.ML)) {
            return EnumUnidadeMedida.ML;
        }
        if (unidadePrincipal.getSigla().equals(EnumUnidadeMedida.CM)) {
            return EnumUnidadeMedida.CM;
        }

        if (unidadePrincipal.getSigla().equals(EnumUnidadeMedida.G)) {
            return EnumUnidadeMedida.G;
        }
        if (unidadePrincipal.getSigla().equals(EnumUnidadeMedida.ML)) {
            return EnumUnidadeMedida.ML;
        }

        return null;
    }
}
