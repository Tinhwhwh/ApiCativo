package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.modelo.Tecnico;
import java.sql.SQLException;
import java.util.List;

/**
 * Operacoes de banco da tabela tecnico.
 * A tela so conhece esta interface, nunca o SQL.
 */
public interface TecnicoDAO {

    void inserir(Tecnico tecnico) throws SQLException;

    Tecnico buscarPorId(int id) throws SQLException;

    List<Tecnico> buscarPorNome(String nome) throws SQLException;

    List<Tecnico> listarTodos() throws SQLException;

    /** @return true se algum tecnico foi alterado */
    boolean atualizar(Tecnico tecnico) throws SQLException;

    void excluir(int id) throws SQLException;
}
