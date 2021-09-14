/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.LancamentoFinanceiro;
import com.gestor.entidades.Orcamento;
import com.gestor.entidades.OrdemServico;
import com.gestor.enums.EnumRamoAtividade;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author laender
 */
@Stateless
@LocalBean
public class RelatoriosEJB {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

 
    public JasperPrint gerarOrcamento(Orcamento orcamento, Map<String, Object> parametros, Empresa empresa) throws JRException {

        InputStream stream = this.getClass().getResourceAsStream("/reports/Orcamento.jasper");
        try {
            JasperReport report = (JasperReport) JRLoader.loadObject(stream);
            parametros.put("ID_ORCAMENTO", orcamento.getId());
            parametros.put("OFICINA", empresa.getRamoAtividade().getChave().equals(EnumRamoAtividade.OFICINA));
            parametros.put("SUBREPORT", "/reports/OrcamentoItem.jasper");
            Connection con = this.getConexao();
            JasperPrint print = JasperFillManager.fillReport(report, parametros, con);
        this.fecharConexao(con);
            return print;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;

    }
    public JasperPrint gerarOrdemServico(OrdemServico ordemServico, Map<String, Object> parametros) throws JRException {

        InputStream stream = this.getClass().getResourceAsStream("/reports/OrdemServico.jasper");
        try {
            JasperReport report = (JasperReport) JRLoader.loadObject(stream);
            parametros.put("ID_ORDEM_SERVICO", ordemServico.getId());
            parametros.put("SUBREPORT", "/reports/OrdemServicoItem.jasper");
            Connection con = this.getConexao();
            JasperPrint print = JasperFillManager.fillReport(report, parametros, con);
        this.fecharConexao(con);
            return print;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;

    }
    
    public JasperPrint gerarRecibo(LancamentoFinanceiro lctoFin, Map<String, Object> parametros, Empresa empresa) throws JRException {

        InputStream stream = this.getClass().getResourceAsStream("/reports/Recibo.jasper");
        try {
            JasperReport report = (JasperReport) JRLoader.loadObject(stream);
            parametros.put("ID_LANCAMENTO", lctoFin.getId());
            parametros.put("EMPRESA", empresa.getNome());
            parametros.put("END_EMPRESA", empresa.getEndereco()+", "+empresa.getNumero()+" - "+empresa.getBairro());
            parametros.put("TEL_EMPRESA", empresa.getTelefonePrincipal());
            
            Connection con = this.getConexao();
            JasperPrint print = JasperFillManager.fillReport(report, parametros, con);
        this.fecharConexao(con);
            return print;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;

    }
    

    public Connection getConexao() {
        Connection con = null;
        try {
            Class.forName("org.postgresql.Driver");
            con = (Connection) DriverManager.getConnection("jdbc:postgresql://"+Constantes.H0ST+":"+Constantes.PORTA+"/"+Constantes.BANCO, Constantes.USUARIO, Constantes.SENHA);
            return con;

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return con;
    }

    public void fecharConexao(Connection con) {
        try {
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

