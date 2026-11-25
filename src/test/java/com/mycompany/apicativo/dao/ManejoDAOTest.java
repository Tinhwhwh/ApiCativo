package com.mycompany.apicativo.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.apicativo.modelo.Manejo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ManejoDAOTest {

    private static Connection conexao;
    private ManejoDAO dao;

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
        dao = new ManejoDAOjdbc(conexao);
    }

    @AfterEach
    void desfazer() throws SQLException {
        conexao.rollback();
    }

    private Manejo inserir(String nome) throws SQLException {
        dao.inserir(new Manejo(0, nome, "criado pelo teste"));
        List<Manejo> encontrados = dao.buscarPorTipo(nome);
        assertEquals(1, encontrados.size());
        return encontrados.get(0);
    }

    @Test
    @DisplayName("inserir e buscar de volta pelo id")
    void inserirEBuscarPorId() throws SQLException {
        Manejo inserido = inserir("Manejo Teste Ida e Volta");
        Manejo lido = dao.buscarPorId(inserido.getId());
        assertEquals("Manejo Teste Ida e Volta", lido.getTipoProcedimento());
        assertEquals("criado pelo teste", lido.getDescricao());
    }

    @Test
    @DisplayName("listarTodos traz o registro inserido")
    void listarTodos() throws SQLException {
        Manejo inserido = inserir("Manejo Teste Lista");
        assertTrue(dao.listarTodos().stream().anyMatch(item -> item.getId() == inserido.getId()));
    }

    @Test
    @DisplayName("atualizar muda o registro e devolve true")
    void atualizar() throws SQLException {
        Manejo registro = inserir("Manejo Teste Antes");
        registro.setTipoProcedimento("Manejo Teste Depois");
        assertTrue(dao.atualizar(registro));
        assertEquals("Manejo Teste Depois", dao.buscarPorId(registro.getId()).getTipoProcedimento());
    }

    @Test
    @DisplayName("borda: atualizar id que nao existe devolve false, buscar devolve null")
    void idInexistente() throws SQLException {
        assertFalse(dao.atualizar(new Manejo(-1, "fantasma", "")));
        assertNull(dao.buscarPorId(-1));
    }

    @Test
    @DisplayName("excluir some com o registro")
    void excluir() throws SQLException {
        Manejo registro = inserir("Manejo Teste Excluir");
        dao.excluir(registro.getId());
        assertNull(dao.buscarPorId(registro.getId()));
    }

    @Test
    @DisplayName("invalido: nome null e recusado pelo banco (NOT NULL)")
    void nomeNull() {
        assertThrows(SQLException.class, () -> dao.inserir(new Manejo(0, null, "x")));
    }
}
