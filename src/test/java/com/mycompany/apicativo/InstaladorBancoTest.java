package com.mycompany.apicativo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Teste unitario do leitor do script (sem banco). */
class InstaladorBancoTest {

    @Test
    @DisplayName("separa os comandos pelo ponto e virgula")
    void separaComandos() {
        List<String> comandos = InstaladorBanco.separarComandos("CREATE TABLE a (x INT);\nINSERT INTO a VALUES (1);");
        assertEquals(List.of("CREATE TABLE a (x INT)", "INSERT INTO a VALUES (1)"), comandos);
    }

    @Test
    @DisplayName("ponto e virgula dentro de comentario nao quebra comando")
    void ignoraComentarios() {
        String script = "-- crie antes: CREATE DATABASE x;\n"
                + "   -- outro; comentario\n"
                + "SELECT 1;\n";
        assertEquals(List.of("SELECT 1"), InstaladorBanco.separarComandos(script));
    }

    @Test
    @DisplayName("borda: script vazio ou so com comentario nao gera comando")
    void scriptVazio() {
        assertTrue(InstaladorBanco.separarComandos("").isEmpty());
        assertTrue(InstaladorBanco.separarComandos("-- nada\n;\n  ;").isEmpty());
    }

    @Test
    @DisplayName("borda: ultimo comando sem ponto e virgula tambem entra")
    void ultimoSemPontoEVirgula() {
        assertEquals(List.of("SELECT 1", "SELECT 2"), InstaladorBanco.separarComandos("SELECT 1;\r\nSELECT 2"));
    }

    @Test
    @DisplayName("o ApiCativo.sql vai junto no .jar e cria todas as tabelas do sistema")
    void scriptEstaNoJar() throws SQLException {
        String script = InstaladorBanco.lerScript();
        for (String objeto : InstaladorBanco.OBJETOS_DO_BANCO) {
            assertTrue(script.contains(objeto), "script sem " + objeto);
        }
        assertTrue(script.contains("CREATE TABLE IF NOT EXISTS usuario"));
    }

    @Test
    @DisplayName("o script pode rodar de novo: todo CREATE TABLE tem IF NOT EXISTS")
    void scriptReexecutavel() throws SQLException {
        for (String comando : InstaladorBanco.separarComandos(InstaladorBanco.lerScript())) {
            if (comando.startsWith("CREATE TABLE")) {
                assertTrue(comando.startsWith("CREATE TABLE IF NOT EXISTS"), comando);
            }
            if (comando.startsWith("INSERT")) {
                assertTrue(comando.contains("WHERE NOT EXISTS"), comando);
            }
        }
    }
}
