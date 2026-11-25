package com.mycompany.apicativo.controladores;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Gera o hash da senha. A senha em si nunca vai pro banco, so o hash.
 * Saiu do UsuarioDAOjdbc pra poder ser testada sem banco.
 */
public final class Senha {

    private Senha() {
    }

    /** SHA-256 da senha em hexadecimal minusculo (64 caracteres), igual ao sha256() do PostgreSQL. */
    public static String gerarHash(String senha) {
        if (senha == null) {
            throw new IllegalArgumentException("Senha nao pode ser null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponivel na JVM", e);
        }
    }
}
