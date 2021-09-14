/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import javax.ejb.ApplicationException;

/**
 *
 * @author Laender
 */
@ApplicationException(rollback = true)
public class GestorException  extends Exception{
    
   public GestorException() {
        super();
    }

    public GestorException(String message) {
        super(message);
    }

    public GestorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GestorException(Throwable cause) {
        super(cause);
    }

    protected GestorException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
