package com.mycompany.apicativo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Deixa o banco pronto pra usar, pra o .jar abrir em qualquer PC que tenha
 * o PostgreSQL instalado, sem precisar rodar nada no pgAdmin antes:
 *   1. se o banco (apicativo) nao existe, cria;
 *   2. se falta alguma tabela, roda o ApiCativo.sql que vai dentro do .jar.
 * O script so cria o que falta, entao num banco que ja tem dados nada se perde.
 */
public class InstaladorBanco {

    private static final String SCRIPT = "/banco/ApiCativo.sql";
    private static final String CODIGO_BANCO_NAO_EXISTE = "3D000";

    /** Tabelas e view que o sistema usa. */
    static final String[] OBJETOS_DO_BANCO = {
        "localizacao", "tecnico", "manejo", "colmeia", "colmeia_manejo", "vw_historico_completo", "usuario"
    };

    private InstaladorBanco() {
    }

    public static void preparar(ConfiguracaoBanco config) throws SQLException {
        criarBancoSeNaoExistir(config);
        try (Connection conexao = Conexao.conectar(config)) {
            if (!estruturaCompleta(conexao)) {
                rodarScript(conexao);
            }
        }
    }

    static void criarBancoSeNaoExistir(ConfiguracaoBanco config) throws SQLException {
        if (bancoExiste(config)) {
            return;
        }
        if (!ConfiguracaoBanco.isNomeDeBancoValido(config.getBanco())) {
            throw new SQLException("Nome de banco invalido: " + config.getBanco());
        }
        // o banco "postgres" sempre existe, entao conecta nele pra criar o nosso
        try (Connection conexao = Conexao.conectar(config.comBanco("postgres"));
             Statement comando = conexao.createStatement()) {
            comando.executeUpdate("CREATE DATABASE \"" + config.getBanco() + "\"");
        }
    }

    private static boolean bancoExiste(ConfiguracaoBanco config) throws SQLException {
        try {
            Conexao.conectar(config).close();
            return true;
        } catch (SQLException erro) {
            if (CODIGO_BANCO_NAO_EXISTE.equals(erro.getSQLState())) {
                return false;
            }
            throw erro; // senha errada, servidor desligado... nao da pra resolver aqui
        }
    }

    static boolean estruturaCompleta(Connection conexao) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.tables "
                + "WHERE table_schema = 'public' AND table_name = ANY (?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setArray(1, conexao.createArrayOf("varchar", OBJETOS_DO_BANCO));
            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) == OBJETOS_DO_BANCO.length;
            }
        }
    }

    /** Roda o script inteiro numa transacao: ou entra tudo, ou nada. */
    static void rodarScript(Connection conexao) throws SQLException {
        boolean autoCommitAntes = conexao.getAutoCommit();
        conexao.setAutoCommit(false);
        try (Statement comando = conexao.createStatement()) {
            for (String sql : separarComandos(lerScript())) {
                comando.execute(sql);
            }
            conexao.commit();
        } catch (SQLException erro) {
            conexao.rollback();
            throw erro;
        } finally {
            conexao.setAutoCommit(autoCommitAntes);
        }
    }

    static String lerScript() throws SQLException {
        try (InputStream entrada = InstaladorBanco.class.getResourceAsStream(SCRIPT)) {
            if (entrada == null) {
                throw new SQLException("Script " + SCRIPT + " nao encontrado dentro do sistema");
            }
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SQLException("Erro ao ler " + SCRIPT + ": " + e.getMessage(), e);
        }
    }

    /**
     * Quebra o script em comandos separados por ";".
     * Linhas de comentario (comecando com --) sao ignoradas antes,
     * porque podem ter ";" no meio do texto.
     */
    static List<String> separarComandos(String script) {
        StringBuilder semComentarios = new StringBuilder();
        for (String linha : script.split("\\R")) {
            if (!linha.trim().startsWith("--")) {
                semComentarios.append(linha).append('\n');
            }
        }
        List<String> comandos = new ArrayList<>();
        for (String comando : semComentarios.toString().split(";")) {
            if (!comando.isBlank()) {
                comandos.add(comando.trim());
            }
        }
        return comandos;
    }
}
