package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.Conexao;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Parte comum de todos os DAOs JDBC: de onde vem a conexao.
 *
 * - No sistema, cada operacao abre uma conexao nova e fecha no final
 *   (construtor sem parametro, igual era antes).
 * - Nos testes de integracao, o teste entrega uma conexao pronta, com
 *   autoCommit desligado. O DAO usa essa conexao e NAO fecha ela, pra o
 *   teste poder dar rollback no @AfterEach e o banco voltar como estava.
 */
public abstract class DAOBase {

    private final Connection conexaoRecebida;

    protected DAOBase() {
        this(null);
    }

    protected DAOBase(Connection conexao) {
        this.conexaoRecebida = conexao;
    }

    /** O que cada metodo do DAO faz com a conexao. */
    @FunctionalInterface
    protected interface Operacao<T> {
        T executar(Connection conexao) throws SQLException;
    }

    protected <T> T comConexao(Operacao<T> operacao) throws SQLException {
        if (conexaoRecebida != null) {
            return operacao.executar(conexaoRecebida);
        }
        try (Connection conexao = Conexao.conectar()) {
            return operacao.executar(conexao);
        }
    }
}
