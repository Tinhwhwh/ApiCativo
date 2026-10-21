package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.Conexao;
import com.mycompany.apicativo.modelo.Localizacao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao do LocalizacaoDAO usando JDBC e PreparedStatement.
 * Todo o SQL da tabela localizacao fica aqui.
 */
public class LocalizacaoDAOjdbc implements LocalizacaoDAO {

    @Override
    public void inserir(Localizacao localizacao) throws SQLException {
        String sql = "INSERT INTO localizacao (nome_setor, descricao_localizacao) VALUES (?, ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, localizacao.getNomeSetor());
            comando.setString(2, localizacao.getDescricao());
            comando.executeUpdate();
        }
    }

    @Override
    public Localizacao buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM localizacao WHERE id_localizacao = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return montarLocalizacao(resultado);
                }
                return null;
            }
        }
    }

    @Override
    public List<Localizacao> buscarPorNome(String nomeSetor) throws SQLException {
        String sql = "SELECT * FROM localizacao WHERE nome_setor ILIKE ? ORDER BY id_localizacao";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, "%" + nomeSetor + "%");
            return montarLista(comando);
        }
    }

    @Override
    public List<Localizacao> listarTodos() throws SQLException {
        String sql = "SELECT * FROM localizacao ORDER BY id_localizacao";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            return montarLista(comando);
        }
    }

    @Override
    public boolean atualizar(Localizacao localizacao) throws SQLException {
        String sql = "UPDATE localizacao SET nome_setor = ?, descricao_localizacao = ? WHERE id_localizacao = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, localizacao.getNomeSetor());
            comando.setString(2, localizacao.getDescricao());
            comando.setInt(3, localizacao.getId());
            return comando.executeUpdate() > 0;
        }
    }

    @Override
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM localizacao WHERE id_localizacao = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    private List<Localizacao> montarLista(PreparedStatement comando) throws SQLException {
        List<Localizacao> localizacoes = new ArrayList<>();
        try (ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                localizacoes.add(montarLocalizacao(resultado));
            }
        }
        return localizacoes;
    }

    private Localizacao montarLocalizacao(ResultSet resultado) throws SQLException {
        String descricao = resultado.getString("descricao_localizacao");
        return new Localizacao(
                resultado.getInt("id_localizacao"),
                resultado.getString("nome_setor"),
                descricao == null ? "" : descricao);
    }
}
