package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.modelo.Manejo;
import java.sql.SQLException;
import java.util.List;

/**
 * Operacoes de banco da tabela manejo.
 * A tela so conhece esta interface, nunca o SQL.
 */
public interface ManejoDAO {

    void inserir(Manejo manejo) throws SQLException;

    Manejo buscarPorId(int id) throws SQLException;

    List<Manejo> buscarPorTipo(String tipoProcedimento) throws SQLException;

    List<Manejo> listarTodos() throws SQLException;

    /** @return true se algum manejo foi alterado */
    boolean atualizar(Manejo manejo) throws SQLException;

    void excluir(int id) throws SQLException;
}
