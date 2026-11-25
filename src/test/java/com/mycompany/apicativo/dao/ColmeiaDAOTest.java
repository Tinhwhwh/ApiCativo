package com.mycompany.apicativo.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.apicativo.ConversorData;
import com.mycompany.apicativo.modelo.Colmeia;
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

class ColmeiaDAOTest {

    private static Connection conexao;
    private ColmeiaDAO dao;
    private int idSetor;

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
        dao = new ColmeiaDAOjdbc(conexao);
        // toda colmeia precisa de um setor: cada teste cria o seu
        LocalizacaoDAO setores = new LocalizacaoDAOjdbc(conexao);
        setores.inserir(new Localizacao(0, "Setor Teste Colmeias", ""));
        idSetor = setores.buscarPorNome("Setor Teste Colmeias").get(0).getId();
    }

    @AfterEach
    void desfazer() throws SQLException {
        conexao.rollback();
    }

    private Colmeia novaColmeia(String codigo) {
        Colmeia colmeia = new Colmeia();
        colmeia.setCodigo(codigo);
        colmeia.setDataInstalacao(ConversorData.paraData("10/03/2026"));
        colmeia.setStatus("Ativa");
        colmeia.setTamanhoCaixa("Padrao");
        colmeia.setEstiloCaixa("Langstroth");
        colmeia.setIdLocalizacao(idSetor);
        return colmeia;
    }

    private Colmeia inserir(String codigo) throws SQLException {
        dao.inserir(novaColmeia(codigo));
        List<Colmeia> encontradas = dao.buscarPorCodigo(codigo);
        assertEquals(1, encontradas.size());
        return encontradas.get(0);
    }

    @Test
    @DisplayName("inserir e buscar pelo id, ja com o nome do setor")
    void inserirEBuscarPorId() throws SQLException {
        Colmeia lida = dao.buscarPorId(inserir("TESTE-001").getId());
        assertEquals("TESTE-001", lida.getCodigo());
        assertEquals("10/03/2026", ConversorData.paraTela(lida.getDataInstalacao()));
        assertEquals("Ativa", lida.getStatus());
        assertEquals("Setor Teste Colmeias", lida.getNomeSetor());
    }

    @Test
    @DisplayName("borda: colmeia sem data de instalacao e aceita")
    void semData() throws SQLException {
        Colmeia colmeia = novaColmeia("TESTE-SEM-DATA");
        colmeia.setDataInstalacao(null);
        dao.inserir(colmeia);
        assertNull(dao.buscarPorCodigo("TESTE-SEM-DATA").get(0).getDataInstalacao());
    }

    @Test
    @DisplayName("invalido: codigo repetido e recusado (UNIQUE)")
    void codigoRepetido() throws SQLException {
        inserir("TESTE-REPETIDO");
        assertThrows(SQLException.class, () -> dao.inserir(novaColmeia("TESTE-REPETIDO")));
    }

    @Test
    @DisplayName("invalido: setor que nao existe e recusado (chave estrangeira)")
    void setorInexistente() {
        Colmeia colmeia = novaColmeia("TESTE-SEM-SETOR");
        colmeia.setIdLocalizacao(-1);
        assertThrows(SQLException.class, () -> dao.inserir(colmeia));
    }

    @Test
    @DisplayName("atualizar, listar e excluir")
    void atualizarListarExcluir() throws SQLException {
        Colmeia colmeia = inserir("TESTE-ANTES");
        colmeia.setCodigo("TESTE-DEPOIS");
        colmeia.setStatus("Inativa");
        assertTrue(dao.atualizar(colmeia));
        assertEquals("Inativa", dao.buscarPorId(colmeia.getId()).getStatus());
        assertTrue(dao.listarTodos().stream().anyMatch(c -> c.getCodigo().equals("TESTE-DEPOIS")));

        dao.excluir(colmeia.getId());
        assertNull(dao.buscarPorId(colmeia.getId()));
    }

    @Test
    @DisplayName("borda: atualizar colmeia que nao existe devolve false")
    void atualizarInexistente() throws SQLException {
        Colmeia colmeia = novaColmeia("TESTE-FANTASMA");
        colmeia.setId(-1);
        assertFalse(dao.atualizar(colmeia));
    }

    @Test
    @DisplayName("relatorio: contarTodas sobe 1 a cada colmeia inserida")
    void contarTodas() throws SQLException {
        int antes = dao.contarTodas();
        inserir("TESTE-CONTA");
        assertEquals(antes + 1, dao.contarTodas());
    }

    @Test
    @DisplayName("relatorio: contarPorStatus conta a colmeia no status dela")
    void contarPorStatus() throws SQLException {
        Colmeia colmeia = novaColmeia("TESTE-STATUS");
        colmeia.setStatus("Status Teste Unico");
        dao.inserir(colmeia);
        assertEquals(1, dao.contarPorStatus().get("Status Teste Unico"));
    }

    @Test
    @DisplayName("relatorio borda: setor com 1 colmeia nao aparece, com 2 aparece")
    void setoresComMaisDeUma() throws SQLException {
        inserir("TESTE-SETOR-1");
        assertFalse(dao.contarPorSetorComMaisDeUma().containsKey("Setor Teste Colmeias"));
        inserir("TESTE-SETOR-2");
        assertEquals(2, dao.contarPorSetorComMaisDeUma().get("Setor Teste Colmeias"));
    }
}
