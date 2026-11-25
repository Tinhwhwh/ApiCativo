package com.mycompany.apicativo;

import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.apicativo.dao.BancoDeTeste;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Confere que o sistema consegue preparar o banco sozinho (o que faz o .jar abrir em outro PC). */
class InstaladorBancoIntegracaoTest {

    private int contarAdmins(Connection conexao) throws SQLException {
        try (Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery("SELECT COUNT(*) FROM usuario WHERE login = 'admin'")) {
            resultado.next();
            return resultado.getInt(1);
        }
    }

    @Test
    @DisplayName("preparar o banco duas vezes nao quebra nem duplica o admin")
    void prepararDuasVezes() throws SQLException {
        try (Connection conexao = BancoDeTeste.conectar()) {
            InstaladorBanco.preparar(ConfiguracaoBanco.paraTestes());
            InstaladorBanco.preparar(ConfiguracaoBanco.paraTestes());
            assertEquals(1, contarAdmins(conexao));
        }
    }

    @Test
    @DisplayName("regressao: rodar o script de novo num banco que ja tem tabelas funciona")
    void scriptDeNovoNoBancoPronto() throws SQLException {
        try (Connection conexao = BancoDeTeste.conectar()) {
            InstaladorBanco.rodarScript(conexao);
            assertEquals(1, contarAdmins(conexao));
        }
    }

    @Test
    @DisplayName("invalido: banco que nao existe vira uma mensagem clara")
    void bancoInexistente() throws SQLException {
        BancoDeTeste.conectar().close(); // so roda se o PostgreSQL de teste estiver no ar
        ConfiguracaoBanco inexistente = ConfiguracaoBanco.paraTestes().comBanco("banco_que_nao_existe_xyz");
        SQLException erro = assertThrows(SQLException.class, () -> Conexao.conectar(inexistente).close());
        assertEquals("3D000", erro.getSQLState());
        assertTrue(Conexao.mensagemAmigavel(erro).contains("nao existe"));
    }
}
