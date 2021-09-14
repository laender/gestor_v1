/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import com.gestor.entidades.Empresa;
import com.gestor.entidades.Usuario;
import com.gestor.util.GestorException;
import com.gestor.util.Utils;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author laender
 */
@LocalBean
@Stateless
public class UsuarioEJB {

    @PersistenceContext(unitName = "gestor-pu")
    private EntityManager em;

    public Usuario getUsuario(String login, String senha, Empresa empresa) throws GestorException {

        try {
            Usuario usuario = (Usuario) em.createQuery("select u from Usuario u "
                    + "where u.login = :login "
                    //                    + " and u.senha =:senha "
                    + " and u.empresa =:empresa "
                    + " and u.ativo = true "
                    + " and u.empresa.ativo = true "
                    + " order by u.nome ").
                    setParameter("login", login)
                    .setParameter("empresa", empresa).
                    getSingleResult();
            if (usuario != null) {
                String senhaUsu = usuario.getSenha();
                if (senhaUsu != null) {
                    String senhaDecrypt = new String(Utils.decrypt(senhaUsu.getBytes()));
                    if (senha.equals(senhaDecrypt)) {
                        return usuario;
                    }
                }
            }
        } catch (NoResultException ex) {
            ex.printStackTrace();
            throw new GestorException("Usuario ou empresa nao cadastrados ou inativos");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new GestorException("Falha ao acessar o banco de dados, comunique ao suporte tecnico");
        }
        return null;
    }

    public Usuario getUsuarioPainelAdmin(String login, String senha) throws GestorException {
        try {
            Usuario usuario = (Usuario) em.createQuery("select u from Usuario u where u.login = :login  and u.ativo = true and u.userSystem = true ").
                    setParameter("login", login).
                    getSingleResult();
            if (usuario != null) {
                String senhaUsu = usuario.getSenha();
                if (senhaUsu != null) {
                    String senhaDecrypt = new String(Utils.decrypt(senhaUsu.getBytes()));
                 //   if (senha.equals(senhaDecrypt)) {
                        return usuario;
                  //  }
                }
            }

        } catch (NoResultException ex) {
            ex.printStackTrace();
            throw new GestorException("Usuario informado deve ser um usuario administrador do sistema");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new GestorException("Falha ao acessar o banco de dados, comunique ao suporte tecnico");
        }
         return null;
    }

    public List<Usuario> getUsuarios(Empresa empresa) {

        try {
            return (List<Usuario>) em.createQuery("select u from Usuario u where u.empresa =:empresa")
                    .setParameter("empresa", empresa)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Usuario getUsuario(String login, Empresa empresa) {
        try {
            return (Usuario) em.createQuery("select u from Usuario u where u.login = :login and u.empresa =:empresa ").
                    setParameter("login", login).
                    setParameter("empresa", empresa).
                    getSingleResult();
        } catch (Exception ex) {
            //     ex.printStackTrace();
            return null;
        }
    }

    public Usuario getUsuarioById(Integer id) {
        return (Usuario) em.find(Usuario.class, id);
    }

    public void excluir(Usuario usuario) throws GestorException {
        if (usuario != null) {
            if (usuario.isAdministrador()) {
                throw new GestorException("Não é permitido a exclusão do usuário do sistema");
            }
            Usuario usu = this.getUsuarioById(usuario.getId());
            em.remove(usu);
            return;
        }
        throw new GestorException("Nenhum usuário informado para exclusão");
    }

    public void excluirTodos(Usuario usuario) throws GestorException {
        if (usuario != null) {

            Usuario usu = this.getUsuarioById(usuario.getId());
            em.remove(usu);
            return;
        }
        throw new GestorException("Nenhum usuário informado para exclusão");
    }

    public void salvar(Usuario usuario) {
        String senha = usuario.getSenha();
        if(!Utils.empty(senha)){
            String senhaEnc = new String(Utils.encrypt(senha.getBytes()));
            usuario.setSenha(senhaEnc);
        }
        em.merge(usuario);
    }


}
