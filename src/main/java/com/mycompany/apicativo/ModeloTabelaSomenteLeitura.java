package com.mycompany.apicativo;

import javax.swing.table.DefaultTableModel;

public class ModeloTabelaSomenteLeitura extends DefaultTableModel {

    @Override
    public boolean isCellEditable(int linha, int coluna) {
        return false;
    }
}
