package com.mycompany.apicativo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

class ConexaoTest {

    @ParameterizedTest(name = "SQLState {0} -> mensagem com \"{1}\"")
    @CsvSource({
        "28P01, senha",
        "3D000, nao existe",
        "08001, ligado",
        "42P01, tabela"
    })
    @DisplayName("erros comuns do PostgreSQL viram mensagem que da pra entender")
    void traduzErrosConhecidos(String estado, String trecho) {
        String mensagem = Conexao.mensagemAmigavel(new SQLException("erro original", estado));
        assertTrue(mensagem.contains(trecho), mensagem);
    }

    @Test
    @DisplayName("erro desconhecido mostra a mensagem original")
    void erroDesconhecido() {
        assertEquals("deu ruim", Conexao.mensagemAmigavel(new SQLException("deu ruim", "XX000")));
    }

    @Test
    @DisplayName("borda: erro sem SQLState nao estoura")
    void erroSemEstado() {
        assertEquals("sem estado", Conexao.mensagemAmigavel(new SQLException("sem estado")));
    }

    @Test
    @DisplayName("driver faltando no .jar ganha uma dica")
    void driverFaltando() {
        String mensagem = Conexao.mensagemAmigavel(new SQLException("No suitable driver found for jdbc:postgresql://x"));
        assertTrue(mensagem.contains("Driver"));
    }
}
