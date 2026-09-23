package com.mycompany.apicativo;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.SwingUtilities;

public class Apicativo {

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}
