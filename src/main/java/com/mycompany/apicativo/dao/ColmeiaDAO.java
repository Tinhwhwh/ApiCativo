package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.modelo.Colmeia;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Operacoes de banco da tabela colmeia.
 * A tela so conhece esta interface, nunca o SQL.
 */
public interface ColmeiaDAO {

    void inserir(Colmeia colmeia) throws SQLException;

    Colmeia buscarPorId(int id) throws SQLException;

    List<Colmeia> buscarPorCodigo(String codigo) throws SQLException;

    List<Colmeia> listarTodos() throws SQLException;

    /** @return true se alguma colmeia foi alterada */
    boolean atualizar(Colmeia colmeia) throws SQLException;

    void excluir(int id) throws SQLException;

    // consultas usadas no relatorio da tela principal

    int contarTodas() throws SQLException;

    /** @return status -> quantidade, do status com mais colmeias pro com menos */
    Map<String, Integer> contarPorStatus() throws SQLException;

    /** @return nome do setor -> quantidade, so dos setores com mais de 1 colmeia */
    Map<String, Integer> contarPorSetorComMaisDeUma() throws SQLException;
}
