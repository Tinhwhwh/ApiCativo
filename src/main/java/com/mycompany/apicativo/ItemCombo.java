package com.mycompany.apicativo;

import javax.swing.JComboBox;

/**
 * Os combos mostram "id - descricao" (ex: "3 - Setor A").
 * Esta classe monta esse texto e tira o id de volta.
 */
public class ItemCombo {

    private static final String SEPARADOR = " - ";

    public static String montar(int id, String descricao) {
        return id + SEPARADOR + descricao;
    }

    public static String idSelecionado(JComboBox<String> combo) {
        if (combo.getSelectedItem() == null) {
            throw new IllegalArgumentException("Nenhum item selecionado no combo");
        }
        return String.valueOf(extrairId(combo.getSelectedItem().toString()));
    }

    /**
     * "3 - Setor A" vira 3. So o primeiro " - " separa o id, entao
     * descricao com hifen ("3 - Setor - Norte") tambem funciona.
     */
    public static int extrairId(String item) {
        if (item == null || !item.contains(SEPARADOR)) {
            throw new IllegalArgumentException("Item fora do formato 'id - descricao': " + item);
        }
        try {
            return Integer.parseInt(item.substring(0, item.indexOf(SEPARADOR)).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Item sem id numerico: " + item, e);
        }
    }
}
