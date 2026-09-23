package com.mycompany.apicativo;

import javax.swing.JComboBox;

public class ItemCombo {

    private static final String SEPARADOR = " - ";

    public static String montar(int id, String descricao) {
        return id + SEPARADOR + descricao;
    }

    public static String idSelecionado(JComboBox<String> combo) {
        return combo.getSelectedItem().toString().split(SEPARADOR)[0];
    }
}
