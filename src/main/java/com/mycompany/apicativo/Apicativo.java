package com.mycompany.apicativo;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Apicativo {

    public static void main(String[] args) {
        // Inicializa o tema FlatLaf
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null, 
                "Sistema ApiCativo iniciado com sucesso!", 
                "ApiCativo - Controle Apícola", 
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
}