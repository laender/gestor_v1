/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.utils;

import com.gestor.entidades.UnidadeMedida;
import com.gestor.enums.EnumUnidadeMedida;
import java.math.BigDecimal;

/**
 *
 * @author laender
 */
public abstract class UnidadeMedidaUtils {

    public static BigDecimal getDivisorConversao(UnidadeMedida uniMedidaPrincipal, UnidadeMedida uniMedidaSaida) {
        if (uniMedidaPrincipal.equals(uniMedidaSaida)) {
            return BigDecimal.ONE;
        }
        if ((uniMedidaPrincipal.getSigla().equals(EnumUnidadeMedida.KG) && uniMedidaSaida.getSigla().equals(EnumUnidadeMedida.G))
                || (uniMedidaPrincipal.getSigla().equals(EnumUnidadeMedida.LT) && uniMedidaSaida.getSigla().equals(EnumUnidadeMedida.ML))) {
            return BigDecimal.valueOf(1000);
        }
        if (uniMedidaPrincipal.getSigla().equals(EnumUnidadeMedida.M) && uniMedidaSaida.getSigla().equals(EnumUnidadeMedida.CM)) {
            return BigDecimal.valueOf(100);
        }

        return BigDecimal.ONE;
    }

    
}
