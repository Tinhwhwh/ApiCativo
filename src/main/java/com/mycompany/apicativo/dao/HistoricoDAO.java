package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.modelo.Historico;
import java.sql.SQLException;
import java.util.List;

/**
 * Operacoes de banco do historico de manejos (tabela colmeia_manejo
 * e view vw_historico_completo).
 * A tela so conhece esta interface, nunca o SQL.
 */
public interface HistoricoDAO {

    void inserir(Historico historico) throws SQLException;

    Historico buscarPorId(int id) throws SQLException;

    /** Lista do mais recente pro mais antigo. */
    List<Historico> listarTodos() throws SQLException;

    List<Historico> listarPorColmeia(int idColmeia) throws SQLException;

    List<Historico> listarPorTecnico(int idTecnico) throws SQLException;

    /** @return o manejo mais recente, ou null se o historico estiver vazio */
    Historico buscarUltimo() throws SQLException;

    /** @return true se algum registro foi alterado */
    boolean atualizar(Historico historico) throws SQLException;

    void excluir(int id) throws SQLException;
}
