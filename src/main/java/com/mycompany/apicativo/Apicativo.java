package com.mycompany.apicativo;

import com.formdev.flatlaf.FlatDarkLaf;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Apicativo {

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(Apicativo::iniciar);
    }

    /**
     * Antes de abrir o login, garante que o banco esta acessivel e com as
     * tabelas criadas. Se nao conectar, pede os dados do PostgreSQL ate dar
     * certo ou a pessoa cancelar.
     */
    private static void iniciar() {
        ConfiguracaoBanco config = Conexao.getConfiguracao();
        boolean configuracaoMudou = false;

        while (true) {
            try {
                InstaladorBanco.preparar(config);
                break;
            } catch (SQLException erro) {
                config = DialogoConfiguracaoBanco.pedir(config, Conexao.mensagemAmigavel(erro));
                if (config == null) {
                    System.exit(0);
                    return;
                }
                configuracaoMudou = true;
            }
        }

        Conexao.usar(config);
        if (configuracaoMudou) {
            salvar(config);
        }

        // O sistema abre pelo login; a TelaPrincipal
        // so aparece depois que o usuario se autentica.
        new TelaLogin().setVisible(true);
    }

    private static void salvar(ConfiguracaoBanco config) {
        try {
            config.salvar();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Conectou, mas nao consegui salvar a configuracao: " + e.getMessage()
                    + "\nNa proxima vez vai ser preciso digitar de novo.");
        }
    }
}
