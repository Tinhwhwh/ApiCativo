package com.mycompany.apicativo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.validator.GenericValidator;

public class ConversorData {

    private static final String FORMATO_TELA = "dd/MM/yyyy";

    /** Confere se o texto e uma data real no formato dd/MM/yyyy (31/02 nao passa). */
    public static boolean isDataValida(String dataTela) {
        return dataTela != null && GenericValidator.isDate(dataTela.trim(), FORMATO_TELA, true);
    }

    public static Date paraData(String dataTela) {
        if (!isDataValida(dataTela)) {
            throw new IllegalArgumentException("Data invalida: " + dataTela);
        }
        try {
            SimpleDateFormat formatoTela = new SimpleDateFormat(FORMATO_TELA);
            formatoTela.setLenient(false);
            return formatoTela.parse(dataTela.trim());
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
