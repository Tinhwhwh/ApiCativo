package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.Conexao;
import com.mycompany.apicativo.modelo.Tecnico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao do TecnicoDAO usando JDBC e PreparedStatement.
 * Todo o SQL da tabela tecnico fica aqui.
 */
public class TecnicoDAOjdbc implements TecnicoDAO {

    @Override
    public void inserir(Tecnico tecnico) throws SQLException {
        String sql = "INSERT INTO tecnico (nome, descricao_tecnico) VALUES (?, ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, tecnico.getNome());
            comando.setString(2, tecnico.getDescricao());
            comando.executeUpdate();
        }
    }

    @Override
    public Tecnico buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM tecnico WHERE id_tecnico = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return montarTecnico(resultado);
                }
                return null;
            }
        }
    }

    @Override
    public List<Tecnico> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM tecnico WHERE nome ILIKE ? ORDER BY id_tecnico";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, "%" + nome + "%");
            return montarLista(comando);
        }
    }

    @Override
    public List<Tecnico> listarTodos() throws SQLException {
        String sql = "SELECT * FROM tecnico ORDER BY id_tecnico";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            return montarLista(comando);
        }
    }

    @Override
    public boolean atualizar(Tecnico tecnico) throws SQLException {
        String sql = "UPDATE tecnico SET nome = ?, descricao_tecnico = ? WHERE id_tecnico = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, tecnico.getNome());
            comando.setString(2, tecnico.getDescricao());
            comando.setInt(3, tecnico.getId());
            return comando.executeUpdate() > 0;
        }
    }

    @Override
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM tecnico WHERE id_tecnico = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    private List<Tecnico> montarLista(PreparedStatement comando) throws SQLException {
        List<Tecnico> tecnicos = new ArrayList<>();
        try (ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                tecnicos.add(montarTecnico(resultado));
            }
        }
        return tecnicos;
    }

    private Tecnico montarTecnico(ResultSet resultado) throws SQLException {
        String descricao = resultado.getString("descricao_tecnico");
        return new Tecnico(
                resultado.getInt("id_tecnico"),
                resultado.getString("nome"),
                descricao == null ? "" : descricao);
    }
}
