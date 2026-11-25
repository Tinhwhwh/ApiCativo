package com.mycompany.apicativo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConfiguracaoBancoTest {

    @Test
    @DisplayName("o padrao aponta pro banco apicativo no localhost:5432")
    void urlPadrao() {
        assertEquals("jdbc:postgresql://localhost:5432/apicativo", ConfiguracaoBanco.padrao().montarUrl());
    }

    @Test
    @DisplayName("comBanco troca so o nome do banco")
    void trocaBanco() {
        ConfiguracaoBanco outra = new ConfiguracaoBanco("10.0.0.5", 5433, "apicativo", "tin", "segredo").comBanco("postgres");
        assertEquals("jdbc:postgresql://10.0.0.5:5433/postgres", outra.montarUrl());
        assertEquals("tin", outra.getUsuario());
        assertEquals("segredo", outra.getSenha());
    }

    @Test
    @DisplayName("caminho feliz: dados normais passam na validacao")
    void validaDadosNormais() {
        assertNull(ConfiguracaoBanco.validar("localhost", "5432", "apicativo", "postgres"));
    }

    @ParameterizedTest(name = "porta [{0}] valida? {1}")
    @CsvSource({"1, true", "65535, true", "0, false", "65536, false", "-1, false", "abc, false", "'', false"})
    @DisplayName("borda e invalido: porta entre 1 e 65535")
    void validaPorta(String porta, boolean esperado) {
        assertEquals(esperado, ConfiguracaoBanco.validar("localhost", porta, "apicativo", "postgres") == null);
    }

    @Test
    @DisplayName("invalido: servidor e usuario vazios")
    void camposObrigatorios() {
        assertNotNull(ConfiguracaoBanco.validar("", "5432", "apicativo", "postgres"));
        assertNotNull(ConfiguracaoBanco.validar("localhost", "5432", "apicativo", " "));
        assertNotNull(ConfiguracaoBanco.validar(null, "5432", "apicativo", "postgres"));
    }

    @ParameterizedTest(name = "nome de banco [{0}] valido? {1}")
    @CsvSource({
        "apicativo, true",
        "apicativo_teste, true",
        "_banco, true",
        "1banco, false",
        "'apicativo\"; DROP DATABASE postgres; --', false",
        "meu banco, false",
        "'', false"
    })
    @DisplayName("nome do banco so aceita letras, numeros e _ (protege o CREATE DATABASE)")
    void validaNomeDoBanco(String nome, boolean esperado) {
        assertEquals(esperado, ConfiguracaoBanco.isNomeDeBancoValido(nome));
    }

    @Test
    @DisplayName("borda: nome de banco com 63 caracteres passa, com 64 nao (limite do PostgreSQL)")
    void tamanhoDoNomeDoBanco() {
        assertTrue(ConfiguracaoBanco.isNomeDeBancoValido("b".repeat(63)));
        assertFalse(ConfiguracaoBanco.isNomeDeBancoValido("b".repeat(64)));
    }

    @Test
    @DisplayName("arquivo vazio usa tudo padrao")
    void propertiesVazioUsaPadrao() {
        ConfiguracaoBanco config = ConfiguracaoBanco.deProperties(new Properties());
        assertEquals(ConfiguracaoBanco.padrao().montarUrl(), config.montarUrl());
        assertEquals("postgres", config.getSenha());
    }

    @Test
    @DisplayName("gravar e ler de volta da a mesma configuracao")
    void idaEVoltaNoProperties() {
        ConfiguracaoBanco original = new ConfiguracaoBanco("servidor", 6543, "fazenda", "tin", "s3nh@ com espaco");
        ConfiguracaoBanco lida = ConfiguracaoBanco.deProperties(original.paraProperties());
        assertEquals(original.montarUrl(), lida.montarUrl());
        assertEquals("tin", lida.getUsuario());
        assertEquals("s3nh@ com espaco", lida.getSenha());
    }

    @Test
    @DisplayName("borda: senha vazia no arquivo e respeitada (nao volta pro padrao)")
    void senhaVaziaERespeitada() {
        Properties propriedades = new Properties();
        propriedades.setProperty("senha", "");
        assertEquals("", ConfiguracaoBanco.deProperties(propriedades).getSenha());
    }

    @Test
    @DisplayName("invalido: porta que nao e numero no arquivo estoura")
    void portaInvalidaNoArquivo() {
        Properties propriedades = new Properties();
        propriedades.setProperty("porta", "cinco");
        assertThrows(NumberFormatException.class, () -> ConfiguracaoBanco.deProperties(propriedades));
    }

    @Test
    @DisplayName("salvar grava na pasta do usuario e carregar le de volta")
    void salvarECarregar() throws IOException {
        String pastaOriginal = System.getProperty("user.home");
        Path pastaTemporaria = Files.createTempDirectory("apicativo-teste");
        try {
            System.setProperty("user.home", pastaTemporaria.toString());
            new ConfiguracaoBanco("outro-pc", 5544, "fazenda", "tin", "abc").salvar();
            assertTrue(Files.exists(pastaTemporaria.resolve(ConfiguracaoBanco.NOME_ARQUIVO)));

            ConfiguracaoBanco lida = ConfiguracaoBanco.carregar();
            assertEquals("jdbc:postgresql://outro-pc:5544/fazenda", lida.montarUrl());
            assertEquals("abc", lida.getSenha());
        } finally {
            System.setProperty("user.home", pastaOriginal);
            Files.deleteIfExists(pastaTemporaria.resolve(ConfiguracaoBanco.NOME_ARQUIVO));
            Files.deleteIfExists(pastaTemporaria);
        }
    }

    @Test
    @DisplayName("borda: sem arquivo nenhum, carregar usa o padrao")
    void carregarSemArquivo() throws IOException {
        String pastaOriginal = System.getProperty("user.home");
        Path pastaVazia = Files.createTempDirectory("apicativo-vazio");
        try {
            System.setProperty("user.home", pastaVazia.toString());
            assertEquals(ConfiguracaoBanco.padrao().montarUrl(), ConfiguracaoBanco.carregar().montarUrl());
        } finally {
            System.setProperty("user.home", pastaOriginal);
            Files.deleteIfExists(pastaVazia);
        }
    }
}
