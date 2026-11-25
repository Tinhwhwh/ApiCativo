package com.mycompany.apicativo.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.apicativo.modelo.Localizacao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocalizacaoDAOTest {

    private static Connection conexao;
    private LocalizacaoDAO dao;

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
        dao = new LocalizacaoDAOjdbc(conexao);
    }

    @AfterEach
    void desfazer() throws SQLException {
        conexao.rollback();
    }

    /** Cada teste cria o seu setor e descobre o id dele (ninguem conta com o id 1). */
    private Localizacao inserirSetor(String nome) throws SQLException {
        dao.inserir(new Localizacao(0, nome, "criado pelo teste"));
        List<Localizacao> encontrados = dao.buscarPorNome(nome);
        assertEquals(1, encontrados.size());
        return encontrados.get(0);
    }

    @Test
    @DisplayName("inserir e buscar de volta pelo id")
    void inserirEBuscarPorId() throws SQLException {
        Localizacao inserido = inserirSetor("Setor Teste Ida e Volta");
        Localizacao lido = dao.buscarPorId(inserido.getId());
        assertEquals("Setor Teste Ida e Volta", lido.getNomeSetor());
        assertEquals("criado pelo teste", lido.getDescricao());
    }

    @Test
    @DisplayName("busca por nome ignora maiusculas e acha pedaco do nome")
    void buscarPorPedacoDoNome() throws SQLException {
        inserirSetor("Setor Teste Eucalipto");
        assertFalse(dao.buscarPorNome("teste eucal").isEmpty());
    }

    @Test
    @DisplayName("listarTodos traz o setor inserido")
    void listarTodos() throws SQLException {
        Localizacao inserido = inserirSetor("Setor Teste Lista");
        assertTrue(dao.listarTodos().stream().anyMatch(l -> l.getId() == inserido.getId()));
    }

    @Test
    @DisplayName("atualizar muda o nome e devolve true")
    void atualizar() throws SQLException {
        Localizacao setor = inserirSetor("Setor Teste Antes");
        setor.setNomeSetor("Setor Teste Depois");
        assertTrue(dao.atualizar(setor));
        assertEquals("Setor Teste Depois", dao.buscarPorId(setor.getId()).getNomeSetor());
    }

    @Test
    @DisplayName("borda: atualizar id que nao existe devolve false")
    void atualizarInexistente() throws SQLException {
        assertFalse(dao.atualizar(new Localizacao(-1, "fantasma", "")));
    }

    @Test
    @DisplayName("excluir some com o setor")
    void excluir() throws SQLException {
        Localizacao setor = inserirSetor("Setor Teste Excluir");
        dao.excluir(setor.getId());
        assertNull(dao.buscarPorId(setor.getId()));
    }

    @Test
    @DisplayName("descricao null volta como texto vazio")
    void descricaoNull() throws SQLException {
        dao.inserir(new Localizacao(0, "Setor Teste Sem Descricao", null));
        assertEquals("", dao.buscarPorNome("Setor Teste Sem Descricao").get(0).getDescricao());
    }

    @Test
    @DisplayName("invalido: nome null e recusado pelo banco (NOT NULL)")
    void nomeNull() {
        assertThrows(SQLException.class, () -> dao.inserir(new Localizacao(0, null, "x")));
    }
}
