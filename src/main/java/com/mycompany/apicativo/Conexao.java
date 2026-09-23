package com.mycompany.apicativo;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Conexao {

    public static Connection conectar() {
        try {
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/apicativo", "postgres", "postgres");
            return con;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar no banco: " + e.getMessage());
            return null;
        }
    }
}
