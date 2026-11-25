package com.mycompany.apicativo.controladores;

import com.mycompany.apicativo.dao.UsuarioDAO;
import com.mycompany.apicativo.dao.UsuarioDAOjdbc;
import com.mycompany.apicativo.modelo.Usuario;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Regras do login: valida os campos e chama o DAO.
 * A tela fala com o controlador, o controlador fala com o DAO.
 */
public class ControlaUsuario {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOjdbc();

    public Usuario autenticar(String login, String senha) {
        try {
            return usuarioDAO.autenticar(login, senha);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao autenticar usuario: " + ex.getMessage());
            return null;
        }
    }

    public void salvar(String nome, String login, String senha) {
        try {
            usuarioDAO.salvar(nome, login, senha);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar usuario: " + ex.getMessage());
        }
    }

    /** @return a mensagem de erro, ou null se os campos estao ok */
    public String validar(String login, String senha) {
        if (login == null || login.trim().isEmpty()) {
            return "Informe o login.";
        }
        if (senha == null || senha.isEmpty()) {
            return "Informe a senha.";
        }
        return null;
    }
}
