package com.mycompany.apicativo;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.SwingUtilities;

public class Apicativo {

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            // O sistema agora abre pelo login; a TelaPrincipal
            // so aparece depois que o usuario se autentica.
            TelaLogin tela = new TelaLogin();
            tela.setVisible(true);
        });
    }
}
