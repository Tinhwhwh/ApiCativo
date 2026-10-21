package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.Conexao;
import com.mycompany.apicativo.modelo.Colmeia;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementacao do ColmeiaDAO usando JDBC e PreparedStatement.
 * Todo o SQL da tabela colmeia fica aqui.
 */
public class ColmeiaDAOjdbc implements ColmeiaDAO {

    // as consultas trazem o nome do setor junto pra mostrar na tela
    private static final String SELECT_COM_SETOR = "SELECT c.*, l.nome_setor FROM colmeia c "
            + "JOIN localizacao l ON l.id_localizacao = c.id_localizacao ";

    @Override
    public void inserir(Colmeia colmeia) throws SQLException {
        String sql = "INSERT INTO colmeia (codigo_identificador, data_instalacao, status, tamanho_caixa, estilo_caixa, id_localizacao) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherCampos(comando, colmeia);
            comando.executeUpdate();
        }
    }

    @Override
    public Colmeia buscarPorId(int id) throws SQLException {
        String sql = SELECT_COM_SETOR + "WHERE c.id_colmeia = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return montarColmeia(resultado);
                }
                return null;
            }
        }
    }

    @Override
    public List<Colmeia> buscarPorCodigo(String codigo) throws SQLException {
        String sql = SELECT_COM_SETOR + "WHERE c.codigo_identificador ILIKE ? ORDER BY c.id_colmeia";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, "%" + codigo + "%");
            return montarLista(comando);
        }
    }

    @Override
    public List<Colmeia> listarTodos() throws SQLException {
        String sql = SELECT_COM_SETOR + "ORDER BY c.id_colmeia";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            return montarLista(comando);
        }
    }

    @Override
    public boolean atualizar(Colmeia colmeia) throws SQLException {
        String sql = "UPDATE colmeia SET codigo_identificador = ?, data_instalacao = ?, status = ?, tamanho_caixa = ?, "
                + "estilo_caixa = ?, id_localizacao = ? WHERE id_colmeia = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherCampos(comando, colmeia);
            comando.setInt(7, colmeia.getId());
            return comando.executeUpdate() > 0;
        }
    }

    @Override
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM colmeia WHERE id_colmeia = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    @Override
    public int contarTodas() throws SQLException {
        String sql = "SELECT COUNT(*) FROM colmeia";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            resultado.next();
            return resultado.getInt(1);
        }
    }

    @Override
    public Map<String, Integer> contarPorStatus() throws SQLException {
        String sql = "SELECT status, COUNT(*) AS quantidade FROM colmeia GROUP BY status ORDER BY quantidade DESC";
        return montarContagem(sql, "status");
    }

    @Override
    public Map<String, Integer> contarPorSetorComMaisDeUma() throws SQLException {
        String sql = "SELECT l.nome_setor, COUNT(c.id_colmeia) AS quantidade FROM localizacao l "
                + "JOIN colmeia c ON c.id_localizacao = l.id_localizacao "
                + "GROUP BY l.nome_setor HAVING COUNT(c.id_colmeia) > 1";
        return montarContagem(sql, "nome_setor");
    }

    // mesma ordem de parametros no INSERT e no UPDATE (1 a 6)
    private void preencherCampos(PreparedStatement comando, Colmeia colmeia) throws SQLException {
        comando.setString(1, colmeia.getCodigo());
        comando.setDate(2, colmeia.getDataInstalacao() == null ? null : new Date(colmeia.getDataInstalacao().getTime()));
        comando.setString(3, colmeia.getStatus());
        comando.setString(4, colmeia.getTamanhoCaixa());
        comando.setString(5, colmeia.getEstiloCaixa());
        comando.setInt(6, colmeia.getIdLocalizacao());
    }

    private Map<String, Integer> montarContagem(String sql, String colunaNome) throws SQLException {
        // LinkedHashMap pra manter a ordem que veio do banco
        Map<String, Integer> contagem = new LinkedHashMap<>();
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                contagem.put(resultado.getString(colunaNome), resultado.getInt("quantidade"));
            }
        }
        return contagem;
    }

    private List<Colmeia> montarLista(PreparedStatement comando) throws SQLException {
        List<Colmeia> colmeias = new ArrayList<>();
        try (ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                colmeias.add(montarColmeia(resultado));
            }
        }
        return colmeias;
    }

    private Colmeia montarColmeia(ResultSet resultado) throws SQLException {
        Colmeia colmeia = new Colmeia();
        colmeia.setId(resultado.getInt("id_colmeia"));
        colmeia.setCodigo(resultado.getString("codigo_identificador"));
        colmeia.setDataInstalacao(resultado.getDate("data_instalacao"));
        colmeia.setStatus(resultado.getString("status"));
        colmeia.setTamanhoCaixa(resultado.getString("tamanho_caixa"));
        colmeia.setEstiloCaixa(resultado.getString("estilo_caixa"));
        colmeia.setIdLocalizacao(resultado.getInt("id_localizacao"));
        colmeia.setNomeSetor(resultado.getString("nome_setor"));
        return colmeia;
    }
}
