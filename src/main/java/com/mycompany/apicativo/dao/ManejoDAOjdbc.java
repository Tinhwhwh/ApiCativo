package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.Conexao;
import com.mycompany.apicativo.modelo.Manejo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao do ManejoDAO usando JDBC e PreparedStatement.
 * Todo o SQL da tabela manejo fica aqui.
 */
public class ManejoDAOjdbc implements ManejoDAO {

    @Override
    public void inserir(Manejo manejo) throws SQLException {
        String sql = "INSERT INTO manejo (tipo_procedimento, descricao) VALUES (?, ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, manejo.getTipoProcedimento());
            comando.setString(2, manejo.getDescricao());
            comando.executeUpdate();
        }
    }

    @Override
    public Manejo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM manejo WHERE id_manejo = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return montarManejo(resultado);
                }
                return null;
            }
        }
    }

    @Override
    public List<Manejo> buscarPorTipo(String tipoProcedimento) throws SQLException {
        String sql = "SELECT * FROM manejo WHERE tipo_procedimento ILIKE ? ORDER BY id_manejo";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, "%" + tipoProcedimento + "%");
            return montarLista(comando);
        }
    }

    @Override
    public List<Manejo> listarTodos() throws SQLException {
        String sql = "SELECT * FROM manejo ORDER BY id_manejo";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            return montarLista(comando);
        }
    }

    @Override
    public boolean atualizar(Manejo manejo) throws SQLException {
        String sql = "UPDATE manejo SET tipo_procedimento = ?, descricao = ? WHERE id_manejo = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, manejo.getTipoProcedimento());
            comando.setString(2, manejo.getDescricao());
            comando.setInt(3, manejo.getId());
            return comando.executeUpdate() > 0;
        }
    }

    @Override
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM manejo WHERE id_manejo = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    private List<Manejo> montarLista(PreparedStatement comando) throws SQLException {
        List<Manejo> manejos = new ArrayList<>();
        try (ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                manejos.add(montarManejo(resultado));
            }
        }
        return manejos;
    }

    private Manejo montarManejo(ResultSet resultado) throws SQLException {
        String descricao = resultado.getString("descricao");
        return new Manejo(
                resultado.getInt("id_manejo"),
                resultado.getString("tipo_procedimento"),
                descricao == null ? "" : descricao);
    }
}
