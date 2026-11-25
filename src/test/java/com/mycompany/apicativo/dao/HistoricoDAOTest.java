package com.mycompany.apicativo.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.apicativo.ConversorData;
import com.mycompany.apicativo.modelo.Colmeia;
import com.mycompany.apicativo.modelo.Historico;
import com.mycompany.apicativo.modelo.Localizacao;
import com.mycompany.apicativo.modelo.Manejo;
import com.mycompany.apicativo.modelo.Tecnico;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HistoricoDAOTest {

    private static Connection conexao;
    private HistoricoDAO dao;
    private int idColmeia;
    private int idManejo;
    private int idTecnico;

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
        dao = new HistoricoDAOjdbc(conexao);

        // cenario: um setor, uma colmeia, um manejo e um tecnico so deste teste
        LocalizacaoDAO setores = new LocalizacaoDAOjdbc(conexao);
        setores.inserir(new Localizacao(0, "Setor Teste Historico", ""));
        int idSetor = setores.buscarPorNome("Setor Teste Historico").get(0).getId();

        ColmeiaDAO colmeias = new ColmeiaDAOjdbc(conexao);
        Colmeia colmeia = new Colmeia();
        colmeia.setCodigo("TESTE-HIST");
        colmeia.setStatus("Ativa");
        colmeia.setIdLocalizacao(idSetor);
        colmeias.inserir(colmeia);
        idColmeia = colmeias.buscarPorCodigo("TESTE-HIST").get(0).getId();

        ManejoDAO manejos = new ManejoDAOjdbc(conexao);
        manejos.inserir(new Manejo(0, "Manejo Teste Historico", ""));
        idManejo = manejos.buscarPorTipo("Manejo Teste Historico").get(0).getId();

        TecnicoDAO tecnicos = new TecnicoDAOjdbc(conexao);
        tecnicos.inserir(new Tecnico(0, "Tecnico Teste Historico", ""));
        idTecnico = tecnicos.buscarPorNome("Tecnico Teste Historico").get(0).getId();
    }

    @AfterEach
    void desfazer() throws SQLException {
        conexao.rollback();
    }

    private Historico novoRegistro(String data, String observacoes) {
        Historico historico = new Historico();
        historico.setIdColmeia(idColmeia);
        historico.setIdManejo(idManejo);
        historico.setIdTecnico(idTecnico);
        historico.setDataRealizacao(ConversorData.paraData(data));
        historico.setObservacoes(observacoes);
        return historico;
    }

    @Test
    @DisplayName("inserir e ler pela view, com os nomes no lugar dos ids")
    void inserirELerPelaView() throws SQLException {
        dao.inserir(novoRegistro("20/11/2026", "tudo certo"));
        List<Historico> registros = dao.listarPorColmeia(idColmeia);
        assertEquals(1, registros.size());
        Historico lido = registros.get(0);
        assertEquals("TESTE-HIST", lido.getCodigoColmeia());
        assertEquals("Manejo Teste Historico", lido.getTipoProcedimento());
        assertEquals("Tecnico Teste Historico", lido.getNomeTecnico());
        assertEquals("Setor Teste Historico", lido.getNomeSetor());
        assertEquals("20/11/2026", ConversorData.paraTela(lido.getDataRealizacao()));
        assertEquals("tudo certo", dao.buscarPorId(lido.getId()).getObservacoes());
    }

    @Test
    @DisplayName("lista do mais recente pro mais antigo")
    void ordemMaisRecentePrimeiro() throws SQLException {
        dao.inserir(novoRegistro("01/01/2026", "velho"));
        dao.inserir(novoRegistro("15/06/2026", "novo"));
        List<Historico> registros = dao.listarPorTecnico(idTecnico);
        assertEquals("novo", registros.get(0).getObservacoes());
        assertEquals("velho", registros.get(1).getObservacoes());
    }

    @Test
    @DisplayName("borda: dois manejos no mesmo dia, o ultimo cadastrado e o 'ultimo manejo'")
    void desempateNoMesmoDia() throws SQLException {
        dao.inserir(novoRegistro("31/12/2099", "primeiro do dia"));
        dao.inserir(novoRegistro("31/12/2099", "segundo do dia"));
        assertEquals("segundo do dia", dao.buscarUltimo().getObservacoes());
    }

    @Test
    @DisplayName("borda: observacao null volta como texto vazio")
    void observacaoNull() throws SQLException {
        dao.inserir(novoRegistro("20/11/2026", null));
        assertEquals("", dao.listarPorColmeia(idColmeia).get(0).getObservacoes());
    }

    @Test
    @DisplayName("atualizar e excluir um registro")
    void atualizarEExcluir() throws SQLException {
        dao.inserir(novoRegistro("20/11/2026", "antes"));
        Historico registro = dao.listarPorColmeia(idColmeia).get(0);
        registro.setObservacoes("depois");
        assertTrue(dao.atualizar(registro));
        assertEquals("depois", dao.buscarPorId(registro.getId()).getObservacoes());

        dao.excluir(registro.getId());
        assertNull(dao.buscarPorId(registro.getId()));
        assertTrue(dao.listarTodos().stream().noneMatch(h -> h.getId() == registro.getId()));
    }

    @Test
    @DisplayName("borda: atualizar registro que nao existe devolve false")
    void atualizarInexistente() throws SQLException {
        Historico registro = novoRegistro("20/11/2026", "");
        registro.setId(-1);
        assertFalse(dao.atualizar(registro));
    }

    @Test
    @DisplayName("invalido: tecnico que nao existe e recusado (chave estrangeira)")
    void tecnicoInexistente() {
        Historico registro = novoRegistro("20/11/2026", "");
        registro.setIdTecnico(-1);
        assertThrows(SQLException.class, () -> dao.inserir(registro));
    }

    @Test
    @DisplayName("invalido: excluir colmeia que tem historico e recusado")
    void colmeiaComHistoricoNaoExclui() throws SQLException {
        dao.inserir(novoRegistro("20/11/2026", ""));
        ColmeiaDAO colmeias = new ColmeiaDAOjdbc(conexao);
        assertThrows(SQLException.class, () -> colmeias.excluir(idColmeia));
    }
}
