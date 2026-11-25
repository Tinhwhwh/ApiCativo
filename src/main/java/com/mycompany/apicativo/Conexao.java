package com.mycompany.apicativo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abre conexoes com o PostgreSQL usando a ConfiguracaoBanco.
 *
 * Antes, quando dava erro, este metodo mostrava uma mensagem e devolvia null.
 * O DAO seguia com a conexao null e estourava um NullPointerException que
 * ninguem tratava: na tela de login parecia que o botao Entrar nao fazia nada.
 * Agora o erro sobe como SQLException e quem chamou decide o que mostrar.
 */
public class Conexao {

    private static ConfiguracaoBanco configuracao;

    private Conexao() {
    }

    public static synchronized ConfiguracaoBanco getConfiguracao() {
        if (configuracao == null) {
            configuracao = ConfiguracaoBanco.carregar();
        }
        return configuracao;
    }

    public static synchronized void usar(ConfiguracaoBanco novaConfiguracao) {
        configuracao = novaConfiguracao;
    }

    public static Connection conectar() throws SQLException {
        return conectar(getConfiguracao());
    }

    public static Connection conectar(ConfiguracaoBanco config) throws SQLException {
        // sem isso, com o PostgreSQL desligado, a tela fica travada um tempao
        DriverManager.setLoginTimeout(5);
        return DriverManager.getConnection(config.montarUrl(), config.getUsuario(), config.getSenha());
    }

    /**
     * Traduz os erros mais comuns de conexao pra algo que da pra entender.
     * Os codigos (SQLState) sao os do PostgreSQL.
     */
    public static String mensagemAmigavel(SQLException erro) {
        String estado = erro.getSQLState() == null ? "" : erro.getSQLState();
        switch (estado) {
            case "28P01":
            case "28000":
                return "Usuario ou senha do PostgreSQL incorretos.";
            case "3D000":
                return "O banco de dados nao existe.";
            case "08001":
            case "08004":
            case "08006":
                return "Nao foi possivel falar com o PostgreSQL. Ele esta instalado e ligado? "
                        + "Confira o servidor e a porta.";
            case "42P01":
                return "Falta tabela no banco. Rode o ApiCativo.sql ou abra o sistema de novo pra ele criar.";
            default:
                if (erro.getMessage() != null && erro.getMessage().contains("No suitable driver")) {
                    return "Driver do PostgreSQL nao encontrado. Gere o .jar com mvn package (Clean and Build).";
                }
                return erro.getMessage();
        }
    }
}
