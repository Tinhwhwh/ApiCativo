package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.modelo.Usuario;
import java.sql.SQLException;

/**
 * Operacoes de banco da tabela usuario.
 * A tela so conhece esta interface, nunca o SQL.
 */
public interface UsuarioDAO {

    /** @return o usuario se login e senha conferem, ou null se nao conferem */
    Usuario autenticar(String login, String senha) throws SQLException;

    void salvar(String nome, String login, String senha) throws SQLException;
}
