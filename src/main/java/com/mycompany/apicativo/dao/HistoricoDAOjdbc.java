package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.Conexao;
import com.mycompany.apicativo.modelo.Historico;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao do HistoricoDAO usando JDBC e PreparedStatement.
 * Grava na tabela colmeia_manejo e le da view vw_historico_completo,
 * que ja traz os nomes no lugar dos ids.
 */
public class HistoricoDAOjdbc implements HistoricoDAO {

    private static final String SELECT_VIEW = "SELECT * FROM vw_historico_completo ";
    private static final String MAIS_RECENTE_PRIMEIRO = " ORDER BY data_realizacao DESC";

    @Override
    public void inserir(Historico historico) throws SQLException {
        String sql = "INSERT INTO colmeia_manejo (id_colmeia, id_manejo, data_realizacao, observacoes, id_tecnico) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherCampos(comando, historico);
            comando.executeUpdate();
        }
    }

    @Override
    public Historico buscarPorId(int id) throws SQLException {
        String sql = SELECT_VIEW + "WHERE id_colmeia_manejo = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return montarHistorico(resultado);
                }
                return null;
            }
        }
    }

    @Override
    public List<Historico> listarTodos() throws SQLException {
        String sql = SELECT_VIEW + MAIS_RECENTE_PRIMEIRO;
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            return montarLista(comando);
        }
    }

    @Override
    public List<Historico> listarPorColmeia(int idColmeia) throws SQLException {
        String sql = SELECT_VIEW + "WHERE id_colmeia = ?" + MAIS_RECENTE_PRIMEIRO;
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idColmeia);
            return montarLista(comando);
        }
    }

    @Override
    public List<Historico> listarPorTecnico(int idTecnico) throws SQLException {
        String sql = SELECT_VIEW + "WHERE id_tecnico = ?" + MAIS_RECENTE_PRIMEIRO;
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idTecnico);
            return montarLista(comando);
        }
    }

    @Override
    public Historico buscarUltimo() throws SQLException {
        String sql = SELECT_VIEW + MAIS_RECENTE_PRIMEIRO + " LIMIT 1";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            if (resultado.next()) {
                return montarHistorico(resultado);
            }
            return null;
        }
    }

    @Override
    public boolean atualizar(Historico historico) throws SQLException {
        String sql = "UPDATE colmeia_manejo SET id_colmeia = ?, id_manejo = ?, data_realizacao = ?, observacoes = ?, "
                + "id_tecnico = ? WHERE id_colmeia_manejo = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherCampos(comando, historico);
            comando.setInt(6, historico.getId());
            return comando.executeUpdate() > 0;
        }
    }

    @Override
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM colmeia_manejo WHERE id_colmeia_manejo = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    // mesma ordem de parametros no INSERT e no UPDATE (1 a 5)
    private void preencherCampos(PreparedStatement comando, Historico historico) throws SQLException {
        comando.setInt(1, historico.getIdColmeia());
        comando.setInt(2, historico.getIdManejo());
        comando.setDate(3, new Date(historico.getDataRealizacao().getTime()));
        comando.setString(4, historico.getObservacoes());
        comando.setInt(5, historico.getIdTecnico());
    }

    private List<Historico> montarLista(PreparedStatement comando) throws SQLException {
        List<Historico> registros = new ArrayList<>();
        try (ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                registros.add(montarHistorico(resultado));
            }
        }
        return registros;
    }

    private Historico montarHistorico(ResultSet resultado) throws SQLException {
        Historico historico = new Historico();
        historico.setId(resultado.getInt("id_colmeia_manejo"));
        historico.setIdColmeia(resultado.getInt("id_colmeia"));
        historico.setCodigoColmeia(resultado.getString("codigo_identificador"));
        historico.setIdManejo(resultado.getInt("id_manejo"));
        historico.setTipoProcedimento(resultado.getString("tipo_procedimento"));
        historico.setIdTecnico(resultado.getInt("id_tecnico"));
        historico.setNomeTecnico(resultado.getString("tecnico"));
        historico.setNomeSetor(resultado.getString("nome_setor"));
        historico.setDataRealizacao(resultado.getDate("data_realizacao"));
        String observacoes = resultado.getString("observacoes");
        historico.setObservacoes(observacoes == null ? "" : observacoes);
        return historico;
    }
}
