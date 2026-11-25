package com.mycompany.apicativo.controladores;

import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.apicativo.dao.UsuarioDAO;
import com.mycompany.apicativo.modelo.Usuario;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Teste unitario: nao usa banco. O controlador recebe um DAO falso
 * (feito na mao, sem Mockito) que finge ter so o usuario admin/admin.
 */
class ControlaUsuarioTest {

    private DAOFalso dao;
    private ControlaUsuario controlador;

    @BeforeEach
    void montarCenario() {
        dao = new DAOFalso();
        controlador = new ControlaUsuario(dao);
    }

    // ---------------- validar ----------------

    @Test
    @DisplayName("caminho feliz: login e senha preenchidos passam")
    void camposPreenchidosPassam() {
        assertNull(controlador.validar("admin", "admin"));
    }

    @ParameterizedTest(name = "login [{0}] e recusado")
    @CsvSource({"''", "'   '"})
    @DisplayName("invalido: login vazio ou so com espacos")
    void loginVazio(String login) {
        assertEquals("Informe o login.", controlador.validar(login, "admin"));
    }

    @Test
    @DisplayName("invalido: login null")
    void loginNull() {
        assertEquals("Informe o login.", controlador.validar(null, "admin"));
    }

    @Test
    @DisplayName("invalido: senha vazia ou null")
    void senhaVazia() {
        assertEquals("Informe a senha.", controlador.validar("admin", ""));
        assertEquals("Informe a senha.", controlador.validar("admin", null));
    }

    @Test
    @DisplayName("borda: senha com um espaco so e aceita (espaco pode fazer parte da senha)")
    void senhaComEspaco() {
        assertNull(controlador.validar("admin", " "));
    }

    @Test
    @DisplayName("borda: login com exatamente 50 caracteres passa, com 51 nao")
    void tamanhoMaximoDoLogin() {
        assertNull(controlador.validar("a".repeat(50), "x"));
        assertNotNull(controlador.validar("a".repeat(51), "x"));
    }

    // ---------------- autenticar ----------------

    @Test
    @DisplayName("caminho feliz: admin/admin devolve o usuario")
    void autenticaAdmin() throws SQLException {
        Usuario usuario = controlador.autenticar("admin", "admin");
        assertNotNull(usuario);
        assertEquals("admin", usuario.getLogin());
    }

    @Test
    @DisplayName("espacos em volta do login sao ignorados")
    void ignoraEspacosNoLogin() throws SQLException {
        assertNotNull(controlador.autenticar("  admin  ", "admin"));
        assertEquals("admin", dao.ultimoLoginRecebido);
    }

    @Test
    @DisplayName("senha errada devolve null")
    void senhaErrada() throws SQLException {
        assertNull(controlador.autenticar("admin", "Admin"));
    }

    @Test
    @DisplayName("campos invalidos nem chegam no banco")
    void camposInvalidosNaoChamamODAO() throws SQLException {
        assertNull(controlador.autenticar("", "admin"));
        assertEquals(0, dao.chamadas);
    }

    @Test
    @DisplayName("erro de banco nao vira 'senha errada': a SQLException sobe")
    void erroDeBancoSobe() {
        dao.simularBancoForaDoAr = true;
        assertThrows(SQLException.class, () -> controlador.autenticar("admin", "admin"));
    }

    // ---------------- salvar ----------------

    @Test
    @DisplayName("salvar com dados validos chama o DAO com o login sem espacos")
    void salvarValido() throws SQLException {
        controlador.salvar(" Maria ", " maria ", "123");
        assertEquals("maria", dao.ultimoLoginRecebido);
    }

    @Test
    @DisplayName("invalido: salvar sem nome e recusado")
    void salvarSemNome() {
        assertThrows(IllegalArgumentException.class, () -> controlador.salvar("", "maria", "123"));
        assertEquals(0, dao.chamadas);
    }

    @Test
    @DisplayName("borda: nome com 150 caracteres passa, com 151 nao")
    void tamanhoMaximoDoNome() {
        assertNull(controlador.validarCadastro("n".repeat(150), "maria", "123"));
        assertNotNull(controlador.validarCadastro("n".repeat(151), "maria", "123"));
    }

    /** DAO de mentira: guarda o que recebeu e responde sem banco. */
    private static class DAOFalso implements UsuarioDAO {
        int chamadas;
        String ultimoLoginRecebido;
        boolean simularBancoForaDoAr;

        @Override
        public Usuario autenticar(String login, String senha) throws SQLException {
            chamadas++;
            ultimoLoginRecebido = login;
            if (simularBancoForaDoAr) {
                throw new SQLException("Connection refused", "08001");
            }
            if ("admin".equals(login) && "admin".equals(senha)) {
                return new Usuario(1, "Administrador", "admin");
            }
            return null;
        }

        @Override
        public void salvar(String nome, String login, String senha) {
            chamadas++;
            ultimoLoginRecebido = login;
        }
    }
}
