/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import com.gestor.controller.UsuarioController;
import com.gestor.entidades.Empresa;
import com.gestor.entidades.Usuario;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author laender
 */
public class WebUtils {

    public static void messageInfo(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }

    public static void messageRegistroGravado() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Registro Gravado com Sucesso!"));
    }

    public static void messageRegistroExcluido() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Registro Excluído com Sucesso!"));
    }

    public static void messageWarn(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atencao!", message));
    }

    public static void messageError(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", message));
    }

    public static void update(String componente) {
        org.primefaces.context.RequestContext.getCurrentInstance().update(componente);
    }

    public static void redirect(String page) {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        try {
            response.sendRedirect(page + ".xhtml");
        } catch (IOException ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Usuario getUsuario() {
        return managedBean(UsuarioController.class).getUsuario();
    }

    public static Empresa getEmpresa() {
        UsuarioController controller =  managedBean(UsuarioController.class);
        if(controller != null && controller.getUsuario() != null){
            return controller.getUsuario().getEmpresa();
        }
        WebUtils.redirect("index");
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T managedBean(Class<T> clz) {
        return managedBean(clz.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public static <T> T managedBean(String expression) {
        try {
            if (expression == null || expression.trim().isEmpty()) {
                return null;
            }
            expression = expression.trim();
            if (!expression.startsWith("#{")) {
                expression = "#{" + StringUtils.uncapitalize(expression) + "}";
            }
            FacesContext context = FacesContext.getCurrentInstance();

            return (T) createExpression(expression, Object.class
            ).getValue(context.getELContext());
        } catch (Exception e) {
        }
        return null;
    }

    private static boolean isValidExpression(String expression) {
        return expression.startsWith("#{") && expression.endsWith("}");
    }

    public static ValueExpression createExpression(String expression, Class type) {
        try {
            if (!isValidExpression(expression)) {
                expression = "#{".concat(expression).concat("}");
            }
            ELContext el = FacesContext.getCurrentInstance().getELContext();
            ExpressionFactory ef = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
            return ef.createValueExpression(el, expression, type);
        } catch (Exception e) {
        }
        return null;
    }

}
