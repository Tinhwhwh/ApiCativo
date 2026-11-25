package com.mycompany.apicativo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Dados de acesso ao PostgreSQL (servidor, porta, banco, usuario e senha).
 *
 * Antes isso ficava fixo no Conexao.java, entao o .jar so funcionava num PC
 * com o mesmo usuario/senha do PostgreSQL. Agora os dados ficam no arquivo
 * apicativo.properties, que o proprio sistema cria quando a pessoa digita
 * os dados na tela de configuracao.
 *
 * Onde o arquivo e procurado, nessa ordem:
 *   1. na pasta de onde o sistema foi aberto (do lado do .jar)
 *   2. na pasta do usuario (C:\Users\fulano no Windows, /home/fulano no Linux)
 * Se nao achar nenhum, usa o padrao: localhost:5432, banco apicativo, postgres/postgres.
 */
public class ConfiguracaoBanco {

    public static final String NOME_ARQUIVO = "apicativo.properties";

    public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 5432;
    public static final String BANCO_PADRAO = "apicativo";
    public static final String USUARIO_PADRAO = "postgres";
    public static final String SENHA_PADRAO = "postgres";

    private final String host;
    private final int porta;
    private final String banco;
    private final String usuario;
    private final String senha;

    public ConfiguracaoBanco(String host, int porta, String banco, String usuario, String senha) {
        this.host = host;
        this.porta = porta;
        this.banco = banco;
        this.usuario = usuario;
        this.senha = senha == null ? "" : senha;
    }

    public static ConfiguracaoBanco padrao() {
        return new ConfiguracaoBanco(HOST_PADRAO, PORTA_PADRAO, BANCO_PADRAO, USUARIO_PADRAO, SENHA_PADRAO);
    }

    /**
     * Banco usado nos testes de integracao (nunca o banco de verdade).
     * Da pra trocar pelas variaveis de ambiente APICATIVO_TESTE_HOST, _PORTA,
     * _BANCO, _USUARIO e _SENHA (a CI do GitHub usa o padrao mesmo).
     */
    public static ConfiguracaoBanco paraTestes() {
        return new ConfiguracaoBanco(
                variavel("APICATIVO_TESTE_HOST", HOST_PADRAO),
                Integer.parseInt(variavel("APICATIVO_TESTE_PORTA", String.valueOf(PORTA_PADRAO))),
                variavel("APICATIVO_TESTE_BANCO", "apicativo_teste"),
                variavel("APICATIVO_TESTE_USUARIO", USUARIO_PADRAO),
                variavel("APICATIVO_TESTE_SENHA", SENHA_PADRAO));
    }

    /** A mesma configuracao, mas apontando pra outro banco do mesmo servidor. */
    public ConfiguracaoBanco comBanco(String outroBanco) {
        return new ConfiguracaoBanco(host, porta, outroBanco, usuario, senha);
    }

    public String montarUrl() {
        return "jdbc:postgresql://" + host + ":" + porta + "/" + banco;
    }

    /**
     * Confere os campos digitados na tela de configuracao.
     * @return a mensagem de erro, ou null se esta tudo certo
     */
    public static String validar(String host, String porta, String banco, String usuario) {
        if (host == null || host.isBlank()) {
            return "Informe o servidor (normalmente localhost).";
        }
        if (porta == null || !porta.trim().matches("\\d{1,5}")) {
            return "A porta tem que ser um numero (normalmente 5432).";
        }
        int numeroPorta = Integer.parseInt(porta.trim());
        if (numeroPorta < 1 || numeroPorta > 65535) {
            return "A porta tem que estar entre 1 e 65535.";
        }
        if (!isNomeDeBancoValido(banco)) {
            return "O nome do banco so pode ter letras, numeros e _ (ex: apicativo).";
        }
        if (usuario == null || usuario.isBlank()) {
            return "Informe o usuario do PostgreSQL (normalmente postgres).";
        }
        return null;
    }

    /**
     * O nome do banco entra num CREATE DATABASE, que nao aceita "?" de
     * PreparedStatement. Por isso so deixamos letras, numeros e _ (evita SQL Injection).
     */
    public static boolean isNomeDeBancoValido(String banco) {
        return banco != null && banco.matches("[A-Za-z_][A-Za-z0-9_]{0,62}");
    }

    // ---------------------------------------------------------------
    // Leitura e gravacao do apicativo.properties
    // ---------------------------------------------------------------

    public static ConfiguracaoBanco carregar() {
        for (Path arquivo : new Path[]{arquivoNaPastaAtual(), arquivoNaPastaDoUsuario()}) {
            if (Files.isRegularFile(arquivo)) {
                try (InputStream entrada = Files.newInputStream(arquivo)) {
                    Properties propriedades = new Properties();
                    propriedades.load(entrada);
                    return deProperties(propriedades);
                } catch (IOException | RuntimeException e) {
                    System.err.println("Nao consegui ler " + arquivo + ": " + e.getMessage());
                }
            }
        }
        return padrao();
    }

    /** Grava na pasta do usuario, que sempre deixa escrever. */
    public void salvar() throws IOException {
        try (OutputStream saida = Files.newOutputStream(arquivoNaPastaDoUsuario())) {
            paraProperties().store(saida, "Configuracao do banco do ApiCativo");
        }
    }

    /** Campo vazio ou faltando usa o valor padrao. */
    static ConfiguracaoBanco deProperties(Properties propriedades) {
        String porta = propriedades.getProperty("porta", String.valueOf(PORTA_PADRAO)).trim();
        return new ConfiguracaoBanco(
                ler(propriedades, "host", HOST_PADRAO),
                porta.isEmpty() ? PORTA_PADRAO : Integer.parseInt(porta),
                ler(propriedades, "banco", BANCO_PADRAO),
                ler(propriedades, "usuario", USUARIO_PADRAO),
                propriedades.getProperty("senha", SENHA_PADRAO));
    }

    Properties paraProperties() {
        Properties propriedades = new Properties();
        propriedades.setProperty("host", host);
        propriedades.setProperty("porta", String.valueOf(porta));
        propriedades.setProperty("banco", banco);
        propriedades.setProperty("usuario", usuario);
        propriedades.setProperty("senha", senha);
        return propriedades;
    }

    private static String ler(Properties propriedades, String chave, String padrao) {
        String valor = propriedades.getProperty(chave);
        return valor == null || valor.isBlank() ? padrao : valor.trim();
    }

    private static String variavel(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private static Path arquivoNaPastaAtual() {
        return Paths.get(NOME_ARQUIVO);
    }

    private static Path arquivoNaPastaDoUsuario() {
        return Paths.get(System.getProperty("user.home"), NOME_ARQUIVO);
    }

    public String getHost() {
        return host;
    }

    public int getPorta() {
        return porta;
    }

    public String getBanco() {
        return banco;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }
}
