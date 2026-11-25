package com.mycompany.apicativo.dao;

import com.mycompany.apicativo.ConfiguracaoBanco;
import com.mycompany.apicativo.Conexao;
import com.mycompany.apicativo.InstaladorBanco;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Assumptions;

/**
 * Abre a conexao com o banco de TESTE (apicativo_teste), nunca o de verdade.
 *
 * Se o banco de teste nao existe, ele e criado e recebe as tabelas do
 * ApiCativo.sql. Se o PostgreSQL nao estiver ligado, os testes de integracao
 * aparecem como "pulados" (skipped) com o motivo, em vez de quebrar o build;
 * na CI do GitHub o PostgreSQL sempre esta ligado e eles rodam de verdade.
 */
public final class BancoDeTeste {

    private BancoDeTeste() {
    }

    public static Connection conectar() throws SQLException {
        ConfiguracaoBanco config = ConfiguracaoBanco.paraTestes();
        try {
            InstaladorBanco.preparar(config);
        } catch (SQLException erro) {
            Assumptions.assumeTrue(false, "Banco de teste fora do ar (" + config.montarUrl() + "): "
                    + Conexao.mensagemAmigavel(erro));
        }
        return Conexao.conectar(config);
    }
}
