package com.mycompany.apicativo.controladores;

import com.mycompany.apicativo.dao.UsuarioDAO;
import com.mycompany.apicativo.dao.UsuarioDAOjdbc;
import com.mycompany.apicativo.modelo.Usuario;
import java.sql.SQLException;

/**
 * Regras do login: valida os campos e chama o DAO.
 * A tela fala com o controlador, o controlador fala com o DAO.
 *
 * O controlador nao mostra mais JOptionPane: antes, um erro de banco virava
 * uma caixinha e depois a tela ainda dizia "Login ou senha invalidos",
 * escondendo o problema real. Agora o erro sobe e a tela decide o que mostrar.
 * (E sem tela aqui dentro da pra testar com JUnit.)
 */
public class ControlaUsuario {

    public static final int TAMANHO_MAXIMO_LOGIN = 50;
    public static final int TAMANHO_MAXIMO_NOME = 150;

    private final UsuarioDAO usuarioDAO;

    public ControlaUsuario() {
        this(new UsuarioDAOjdbc());
    }

    /** Recebe o DAO de fora pra o teste poder usar um DAO falso, sem banco. */
    public ControlaUsuario(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * @return o usuario, ou null se os campos sao invalidos ou login/senha nao conferem
     * @throws SQLException se nao deu pra falar com o banco
     */
    public Usuario autenticar(String login, String senha) throws SQLException {
        if (validar(login, senha) != null) {
            return null;
        }
        return usuarioDAO.autenticar(login.trim(), senha);
    }

    public void salvar(String nome, String login, String senha) throws SQLException {
        String erro = validarCadastro(nome, login, senha);
        if (erro != null) {
            throw new IllegalArgumentException(erro);
        }
        usuarioDAO.salvar(nome.trim(), login.trim(), senha);
    }

    /** @return a mensagem de erro, ou null se os campos estao ok */
    public String validar(String login, String senha) {
        if (login == null || login.trim().isEmpty()) {
            return "Informe o login.";
        }
        if (login.trim().length() > TAMANHO_MAXIMO_LOGIN) {
            return "Login muito grande! Maximo " + TAMANHO_MAXIMO_LOGIN + " caracteres.";
        }
        if (senha == null || senha.isEmpty()) {
            return "Informe a senha.";
        }
        return null;
    }

    /** @return a mensagem de erro, ou null se os campos estao ok */
    public String validarCadastro(String nome, String login, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            return "Informe o nome.";
        }
        if (nome.trim().length() > TAMANHO_MAXIMO_NOME) {
            return "Nome muito grande! Maximo " + TAMANHO_MAXIMO_NOME + " caracteres.";
        }
        return validar(login, senha);
    }
}
