/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "ordem_servico")
public class OrdemServico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "ORDEM_SERVICO_ID_GENERATOR", sequenceName = "GEN_ORDEM_SERVICO", allocationSize = 1, initialValue = 28)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDEM_SERVICO_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "id_cliente", referencedColumnName = "id")
    @ManyToOne
    private Cliente cliente;

    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuario;

    @JoinColumn(name = "id_usuario_executante", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioExecutante;

    @Column(name = "data_entrega_doc_originais")
    @Temporal(TemporalType.DATE)
    private Date dataEntregaDocOriginais;

    @Column(name = "data_emissao")
    @Temporal(TemporalType.DATE)
    private Date dataEmissao;

    @Column(name = "data_encaminhamento")
    @Temporal(TemporalType.DATE)
    private Date dataEncaminhamento;

    @Column(name = "data_limite_pendencias")
    @Temporal(TemporalType.DATE)
    private Date dataLimitePendencias;

    @Column(name = "data_ass_recibo")
    @Temporal(TemporalType.DATE)
    private Date dataAssRecibo;

    @Column(name = "data_vencimento_crv")
    @Temporal(TemporalType.DATE)
    private Date dataVencimentoCrv;

    @Column(name = "valor_pago")
    private BigDecimal valorPago;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "valor_saldo")
    private BigDecimal valorSaldo;

    @Column(name = "combustivel")
    private String combustivel;

    @Column(name = "placa_atual")
    private String placaAtual;

    @Column(name = "placa_anterior")
    private String placaAnterior;

    @Column(name = "renavan")
    private String renavan;

    @Column(name = "chassi")
    private String chassi;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "cor")
    private String cor;

    @Column(name = "armario")
    private String armario;

    @Column(name = "gaveta")
    private String gaveta;

    @Column(name = "pendencia_rg")
    private boolean pendenciaRg;

    @Column(name = "pendencia_comp_end")
    private boolean pendenciaCompEnd;

    @Column(name = "pendencia_cpf")
    private boolean pendenciaCPF;

    @Column(name = "pendencia_cnh")
    private boolean pendenciaCNH;

    @Column(name = "pendencia_cert_casamento")
    private boolean pendenciaCertCasamento;

    @Column(name = "pendencia_cert_divorcio")
    private boolean pendenciaCertDivorcio;

    @Column(name = "pendencia_carro_vistoria")
    private boolean pendenciaCarroVistoria;

    @Column(name = "pendencia_pgto_debitos")
    private boolean pendenciaPgtoDebitos;

    @Column(name = "pendencia_crv")
    private boolean pendenciaCRV;
    // contrato social
    @Column(name = "pendencia_contr_social")
    private boolean pendenciaContrSocial;

    @Column(name = "pendencia_declaracao")
    private boolean pendenciaDeclaracao;

    @Column(name = "pendencia_procuracao")
    private boolean pendenciaProcuracao;

    @Column(name = "pendencia_cart_trabalho")
    private boolean pendenciaCartTrabalho;

    @Column(name = "pendencia_dinheiro")
    private boolean pendenciaDinheiro;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "ordemServico")
    private List<OrdemServicoItem> itens;

    @Column(name = "observacao")
    private String observacao;
    
    private boolean concluida;

    public OrdemServico() {
    }

    public OrdemServico(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataEntregaDocOriginais() {
        return dataEntregaDocOriginais;
    }

    public void setDataEntregaDocOriginais(Date dataEntregaDocOriginais) {
        this.dataEntregaDocOriginais = dataEntregaDocOriginais;
    }

    public Date getDataEncaminhamento() {
        return dataEncaminhamento;
    }

    public void setDataEncaminhamento(Date dataEncaminhamento) {
        this.dataEncaminhamento = dataEncaminhamento;
    }

    public Date getDataLimitePendencias() {
        return dataLimitePendencias;
    }

    public void setDataLimitePendencias(Date dataLimitePendencias) {
        this.dataLimitePendencias = dataLimitePendencias;
    }

    public Date getDataAssRecibo() {
        return dataAssRecibo;
    }

    public void setDataAssRecibo(Date dataAssRecibo) {
        this.dataAssRecibo = dataAssRecibo;
    }

    public Date getDataVencimentoCrv() {
        return dataVencimentoCrv;
    }

    public void setDataVencimentoCrv(Date vencimentoCrv) {
        this.dataVencimentoCrv = vencimentoCrv;
    }

    public String getPlacaAtual() {
        return placaAtual;
    }

    public void setPlacaAtual(String placaAtual) {
        this.placaAtual = placaAtual;
    }

    public String getPlacaAnterior() {
        return placaAnterior;
    }

    public void setPlacaAnterior(String placaAnterior) {
        this.placaAnterior = placaAnterior;
    }

    public String getRenavan() {
        return renavan;
    }

    public void setRenavan(String renavan) {
        this.renavan = renavan;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getArmario() {
        return armario;
    }

    public void setArmario(String armario) {
        this.armario = armario;
    }

    public String getGaveta() {
        return gaveta;
    }

    public void setGaveta(String gaveta) {
        this.gaveta = gaveta;
    }

    public boolean isPendenciaRg() {
        return pendenciaRg;
    }

    public void setPendenciaRg(boolean pendenciaRg) {
        this.pendenciaRg = pendenciaRg;
    }

    public boolean isPendenciaCompEnd() {
        return pendenciaCompEnd;
    }

    public void setPendenciaCompEnd(boolean pendenciaCompEnd) {
        this.pendenciaCompEnd = pendenciaCompEnd;
    }

    public boolean isPendenciaCPF() {
        return pendenciaCPF;
    }

    public void setPendenciaCPF(boolean pendenciaCPF) {
        this.pendenciaCPF = pendenciaCPF;
    }

    public boolean isPendenciaCNH() {
        return pendenciaCNH;
    }

    public void setPendenciaCNH(boolean pendenciaCNH) {
        this.pendenciaCNH = pendenciaCNH;
    }

    public boolean isPendenciaCertCasamento() {
        return pendenciaCertCasamento;
    }

    public void setPendenciaCertCasamento(boolean pendenciaCertCasamento) {
        this.pendenciaCertCasamento = pendenciaCertCasamento;
    }

    public boolean isPendenciaCertDivorcio() {
        return pendenciaCertDivorcio;
    }

    public void setPendenciaCertDivorcio(boolean pendenciaCertDivorcio) {
        this.pendenciaCertDivorcio = pendenciaCertDivorcio;
    }

    public boolean isPendenciaCarroVistoria() {
        return pendenciaCarroVistoria;
    }

    public void setPendenciaCarroVistoria(boolean pendenciaCarroVistoria) {
        this.pendenciaCarroVistoria = pendenciaCarroVistoria;
    }

    public boolean isPendenciaPgtoDebitos() {
        return pendenciaPgtoDebitos;
    }

    public void setPendenciaPgtoDebitos(boolean pendenciaPgtoDebitos) {
        this.pendenciaPgtoDebitos = pendenciaPgtoDebitos;
    }

    public boolean isPendenciaCRV() {
        return pendenciaCRV;
    }

    public void setPendenciaCRV(boolean pendenciaCRV) {
        this.pendenciaCRV = pendenciaCRV;
    }

    public boolean isPendenciaContrSocial() {
        return pendenciaContrSocial;
    }

    public void setPendenciaContrSocial(boolean pendenciaContrSocial) {
        this.pendenciaContrSocial = pendenciaContrSocial;
    }

    public boolean isPendenciaDeclaracao() {
        return pendenciaDeclaracao;
    }

    public void setPendenciaDeclaracao(boolean pendenciaDeclaracao) {
        this.pendenciaDeclaracao = pendenciaDeclaracao;
    }

    public boolean isPendenciaProcuracao() {
        return pendenciaProcuracao;
    }

    public void setPendenciaProcuracao(boolean pendenciaProcuracao) {
        this.pendenciaProcuracao = pendenciaProcuracao;
    }

    public boolean isPendenciaCartTrabalho() {
        return pendenciaCartTrabalho;
    }

    public void setPendenciaCartTrabalho(boolean pendenciaCartTrabalho) {
        this.pendenciaCartTrabalho = pendenciaCartTrabalho;
    }

    public boolean isPendenciaDinheiro() {
        return pendenciaDinheiro;
    }

    public void setPendenciaDinheiro(boolean pendenciaDinheiro) {
        this.pendenciaDinheiro = pendenciaDinheiro;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuarioExecutante() {
        return usuarioExecutante;
    }

    public void setUsuarioExecutante(Usuario usuarioExecutante) {
        this.usuarioExecutante = usuarioExecutante;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorSaldo() {
        return valorSaldo;
    }

    public void setValorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
    }

    public List<OrdemServicoItem> getItens() {
        return itens;
    }

    public void setItens(List<OrdemServicoItem> itens) {
        this.itens = itens;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    public String getChassi() {
        return chassi;
    }

    public void setChassi(String chassi) {
        this.chassi = chassi;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }
    
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrdemServico)) {
            return false;
        }
        OrdemServico other = (OrdemServico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.OrdemServico[ id=" + id + " ]";
    }

}
