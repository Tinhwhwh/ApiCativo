package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.controladores.Senha;
import com.mycompany.apicativo.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementacao do UsuarioDAO usando JDBC.
 * Todo o SQL da tabela usuario fica aqui.
 */
public class UsuarioDAOjdbc extends DAOBase implements UsuarioDAO {

    public UsuarioDAOjdbc() {
        super();
    }

    /** Usado nos testes de integracao (ver DAOBase). */
    public UsuarioDAOjdbc(Connection conexao) {
        super(conexao);
    }

    @Override
    public Usuario autenticar(String login, String senha) throws SQLException {
        // Corrigido: antes o login era colado direto no SQL e "admin' --"
        // entrava sem senha (SQL Injection). Com "?" o PreparedStatement
        // manda o texto como dado, nunca como parte do comando.
        String sql = "SELECT id, nome, login FROM usuario WHERE login = ? AND senha_hash = ?";
        return comConexao(conexao -> {
            try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                comando.setString(1, login);
                comando.setString(2, Senha.gerarHash(senha));
                try (ResultSet resultado = comando.executeQuery()) {
                    return resultado.next() ? montarUsuario(resultado) : null;
                }
            }
        });
    }

    @Override
    public void salvar(String nome, String login, String senha) throws SQLException {
        String sql = "INSERT INTO usuario (nome, login, senha_hash) VALUES (?, ?, ?)";
        comConexao(conexao -> {
            try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                comando.setString(1, nome);
                comando.setString(2, login);
                comando.setString(3, Senha.gerarHash(senha));
                return comando.executeUpdate();
            }
        });
    }

    private Usuario montarUsuario(ResultSet resultado) throws SQLException {
        return new Usuario(
                resultado.getInt("id"),
                resultado.getString("nome"),
                resultado.getString("login"));
    }
}
