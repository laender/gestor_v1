/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.entidades;

import com.gestor.enums.EnumRamoAtividade;
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
import javax.persistence.Transient;

/**
 *
 * @author laender
 */
@Entity
@Table(name = "empresa")
public class Empresa extends Entidade {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "EMPRESA_ID_GENERATOR", sequenceName = "GEN_EMPRESA", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMPRESA_ID_GENERATOR")
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cnpj_cpf")
    private String cnpjCpf;

    @Column(name = "controla_estoque")
    private boolean controlaEstoque = false;

    @Column(name = "fluxo_caixa")
    private boolean fluxoCaixa = false;

    @Column(name = "caixa_acumulativo")
    private boolean caixaAcumulativo;
    
    @Column(name = "exibir_multiplicador")
    private boolean exibirMultiplicador = false;
    
    @Column(name = "exibir_venda_rapida")
    private boolean exibirVendaRapida = false;
    
    @Column(name = "permite_alterar_comissao")
    private boolean permiteAlterarComissao;
    
    @Column(name = "exibir_cad_banco")
    private boolean exibirCadBanco;

    @Column(name = "logo_image")
    private String logoImage;

    @Column(name = "logo")
    private byte[] logo;

    @Column(name = "telefone_principal")
    private String telefonePrincipal;

    @Column(name = "login_key")
    private String loginKey;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empresa")
    private List<EmpresaTelefone> telefones;

    @JoinColumn(name = "id_cidade", referencedColumnName = "id")
    @ManyToOne
    private Cidade cidade;

    @JoinColumn(name = "id_ramo_atividade", referencedColumnName = "id")
    @ManyToOne
    private RamoAtividade ramoAtividade;

    private String endereco;

    private Integer numero;

    private String instagram;

    private String bairro;

    @Column(name = "facebook_url")
    private String facebookUrl;

    @Column(name = "home_page")
    private String homePage;

    private String email;

    private boolean ativo;
    
    @Column(name = "comissao_total_venda")
    private boolean comissaoTotalVenda;
    
    @Column(name = "comissao_total_mao_obra")
    private boolean comissaoTotalMaoObra;

    @Transient
    private boolean fabrica;
    
    @Transient
    private boolean oficina;
    
    @Transient
    private boolean comercio;
    
    @Column(name = "data_inicio_testes")
    @Temporal(TemporalType.DATE)
    private Date dataInicioTestes;
    
    @Column(name = "data_inicio_efetivo")
    @Temporal(TemporalType.DATE)
    private Date dataInicioEfetivo;

    public Empresa() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }

    public boolean isControlaEstoque() {
        return controlaEstoque;
    }

    public void setControlaEstoque(boolean controlaEstoque) {
        this.controlaEstoque = controlaEstoque;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public String getLoginKey() {
        return loginKey;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefonePrincipal() {
        return telefonePrincipal;
    }

    public void setTelefonePrincipal(String telefonePrincipal) {
        this.telefonePrincipal = telefonePrincipal;
    }

    public List<EmpresaTelefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<EmpresaTelefone> telefones) {
        this.telefones = telefones;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public boolean isFluxoCaixa() {
        return fluxoCaixa;
    }

    public void setFluxoCaixa(boolean fluxoCaixa) {
        this.fluxoCaixa = fluxoCaixa;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isCaixaAcumulativo() {
        return caixaAcumulativo;
    }

    public void setCaixaAcumulativo(boolean caixaAcumulativo) {
        this.caixaAcumulativo = caixaAcumulativo;
    }

    public RamoAtividade getRamoAtividade() {
        return ramoAtividade;
    }

    public void setRamoAtividade(RamoAtividade ramoAtividade) {
        this.ramoAtividade = ramoAtividade;
    }

    public boolean isFabrica() {

        return ramoAtividade != null && ramoAtividade.getChave().equals(EnumRamoAtividade.FABRICA);
    }

    public boolean isComercio() {
        return ramoAtividade != null && ramoAtividade.getChave().equals(EnumRamoAtividade.COMERCIO);
    }

    public boolean isOficina() {
        return ramoAtividade != null && ramoAtividade.getChave().equals(EnumRamoAtividade.OFICINA);
    }

    public boolean isComissaoTotalVenda() {
        return comissaoTotalVenda;
    }

    public void setComissaoTotalVenda(boolean comissaoTotalVenda) {
        this.comissaoTotalVenda = comissaoTotalVenda;
    }

    public boolean isComissaoTotalMaoObra() {
        return comissaoTotalMaoObra;
    }

    public void setComissaoTotalMaoObra(boolean comissaoTotalMaoObra) {
        this.comissaoTotalMaoObra = comissaoTotalMaoObra;
    }

    public Date getDataInicioTestes() {
        return dataInicioTestes;
    }

    public void setDataInicioTestes(Date dataInicioTestes) {
        this.dataInicioTestes = dataInicioTestes;
    }

    public Date getDataInicioEfetivo() {
        return dataInicioEfetivo;
    }

    public void setDataInicioEfetivo(Date dataInicioEfetivo) {
        this.dataInicioEfetivo = dataInicioEfetivo;
    }

    public boolean isExibirMultiplicador() {
        return exibirMultiplicador;
    }

    public void setExibirMultiplicador(boolean exibirMultiplicador) {
        this.exibirMultiplicador = exibirMultiplicador;
    }

    public boolean isExibirVendaRapida() {
        return exibirVendaRapida;
    }

    public void setExibirVendaRapida(boolean exibirVendaRapida) {
        this.exibirVendaRapida = exibirVendaRapida;
    }

    public boolean isPermiteAlterarComissao() {
        return permiteAlterarComissao;
    }

    public void setPermiteAlterarComissao(boolean permiteAlterarComissao) {
        this.permiteAlterarComissao = permiteAlterarComissao;
    }

    public boolean isExibirCadBanco() {
        return exibirCadBanco;
    }

    public void setExibirCadBanco(boolean exibirCadBanco) {
        this.exibirCadBanco = exibirCadBanco;
    }
    
    
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Empresa)) {
            return false;
        }
        Empresa other = (Empresa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gestor.entidades.Empresa[ id=" + id + " ]";
    }

}
