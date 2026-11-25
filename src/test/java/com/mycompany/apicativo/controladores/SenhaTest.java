package com.mycompany.apicativo.controladores;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SenhaTest {

    @Test
    @DisplayName("hash de 'admin' e o mesmo que o PostgreSQL gera no ApiCativo.sql")
    void hashDoAdminBateComOBanco() {
        // mesmo valor de encode(sha256('admin'::bytea), 'hex')
        assertEquals("8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918",
                Senha.gerarHash("admin"));
    }

    @Test
    @DisplayName("borda: senha vazia tambem gera um hash de 64 caracteres")
    void senhaVaziaGeraHash() {
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                Senha.gerarHash(""));
    }

    @Test
    @DisplayName("hash sempre tem 64 caracteres hexadecimais minusculos")
    void formatoDoHash() {
        String hash = Senha.gerarHash("uma senha bem comprida com acentuação e números 123");
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("maiuscula e minuscula geram hashes diferentes")
    void diferenciaMaiusculas() {
        assertNotEquals(Senha.gerarHash("admin"), Senha.gerarHash("Admin"));
    }

    @Test
    @DisplayName("invalido: senha null e recusada")
    void senhaNullERecusada() {
        assertThrows(IllegalArgumentException.class, () -> Senha.gerarHash(null));
    }
}
