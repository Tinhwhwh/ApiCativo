package com.mycompany.apicativo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.validator.GenericValidator;

public class ConversorData {

    private static final String FORMATO_TELA = "dd/MM/yyyy";
    private static final String FORMATO_BANCO = "yyyy-MM-dd";

    public static boolean isDataValida(String dataTela) {
        return GenericValidator.isDate(dataTela, FORMATO_TELA, true);
    }

    public static String paraBanco(String dataTela) {
        try {
            SimpleDateFormat formatoTela = new SimpleDateFormat(FORMATO_TELA);
            formatoTela.setLenient(false);
            return new SimpleDateFormat(FORMATO_BANCO).format(formatoTela.parse(dataTela));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Data invalida: " + dataTela, e);
        }
    }

    public static String paraTela(Date data) {
        if (data == null) {
            return "";
        }
        return new SimpleDateFormat(FORMATO_TELA).format(data);
    }
}
