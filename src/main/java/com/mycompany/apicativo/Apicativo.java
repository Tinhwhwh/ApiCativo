package com.mycompany.apicativo;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Apicativo {

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null, 
                "Sistema ApiCativo", 
                "ApiCativo - Controle Apícola", 
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
}