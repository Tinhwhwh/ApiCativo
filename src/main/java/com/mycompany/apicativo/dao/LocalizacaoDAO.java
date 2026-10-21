package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.modelo.Localizacao;
import java.sql.SQLException;
import java.util.List;

/**
 * Operacoes de banco da tabela localizacao.
 * A tela so conhece esta interface, nunca o SQL.
 */
public interface LocalizacaoDAO {

    void inserir(Localizacao localizacao) throws SQLException;

    Localizacao buscarPorId(int id) throws SQLException;

    List<Localizacao> buscarPorNome(String nomeSetor) throws SQLException;

    List<Localizacao> listarTodos() throws SQLException;

    /** @return true se algum setor foi alterado */
    boolean atualizar(Localizacao localizacao) throws SQLException;

    void excluir(int id) throws SQLException;
}
