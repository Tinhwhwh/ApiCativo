package com.mycompany.apicativo.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.apicativo.modelo.Usuario;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Teste de integracao: UsuarioDAOjdbc + PostgreSQL de teste, com rollback. */
class UsuarioDAOTest {

    private static Connection conexao;
    private UsuarioDAO dao;

    @BeforeAll
    static void abrirConexao() throws SQLException {
        conexao = BancoDeTeste.conectar();
    }

    @AfterAll
    static void fecharConexao() throws SQLException {
        if (conexao != null) {
            conexao.close();
        }
    }

    @BeforeEach
    void iniciar() throws SQLException {
        conexao.setAutoCommit(false);
        dao = new UsuarioDAOjdbc(conexao);
    }

    @AfterEach
    void desfazer() throws SQLException {
        conexao.rollback();
    }

    @Test
    @DisplayName("regressao: admin/admin do script entra no sistema")
    void adminPadraoEntra() throws SQLException {
        Usuario usuario = dao.autenticar("admin", "admin");
        assertNotNull(usuario, "o usuario padrao admin/admin tem que existir e conferir");
        assertEquals("Administrador", usuario.getNome());
    }

    @Test
    @DisplayName("senha errada nao entra")
    void senhaErrada() throws SQLException {
        assertNull(dao.autenticar("admin", "errada"));
    }

    @Test
    @DisplayName("login que nao existe nao entra")
    void loginInexistente() throws SQLException {
        assertNull(dao.autenticar("ninguem", "admin"));
    }

    @Test
    @DisplayName("seguranca: SQL Injection no login nao entra (antes entrava com admin' --)")
    void sqlInjectionNaoEntra() throws SQLException {
        assertNull(dao.autenticar("admin' --", "qualquer coisa"));
        assertNull(dao.autenticar("' OR '1'='1", "' OR '1'='1"));
    }

    @Test
    @DisplayName("usuario salvo consegue entrar com a propria senha")
    void salvarEAutenticar() throws SQLException {
        dao.salvar("Tin", "tin_teste", "abelha123");
        Usuario lido = dao.autenticar("tin_teste", "abelha123");
        assertNotNull(lido);
        assertEquals("Tin", lido.getNome());
        assertNull(dao.autenticar("tin_teste", "abelha"));
    }

    @Test
    @DisplayName("invalido: login repetido e recusado pelo banco (UNIQUE)")
    void loginRepetido() {
        assertThrows(SQLException.class, () -> dao.salvar("Outro admin", "admin", "123"));
    }
}
