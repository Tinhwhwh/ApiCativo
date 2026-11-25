package com.mycompany.apicativo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConversorDataTest {

    @ParameterizedTest(name = "[{0}] valida? {1}")
    @CsvSource({
        // caminho feliz
        "25/11/2026, true",
        // bordas: primeiro/ultimo dia do ano e ano bissexto
        "01/01/2026, true",
        "31/12/2026, true",
        "29/02/2024, true",
        "29/02/2025, false",
        "31/04/2026, false",
        "00/01/2026, false",
        "01/13/2026, false",
        // formato errado
        "1/1/2026, false",
        "2026-11-25, false",
        "25/11/26, false",
        "abc, false",
        "'', false"
    })
    @DisplayName("isDataValida aceita so datas reais no formato dd/MM/yyyy")
    void validaDatas(String texto, boolean esperado) {
        assertEquals(esperado, ConversorData.isDataValida(texto));
    }

    @Test
    @DisplayName("invalido: null nao e data valida (e nao estoura)")
    void nullNaoEValido() {
        assertFalse(ConversorData.isDataValida(null));
    }

    @Test
    @DisplayName("caminho feliz: paraData monta o dia, mes e ano certos")
    void converteParaData() {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(ConversorData.paraData("05/03/2026"));
        assertEquals(5, calendario.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.MARCH, calendario.get(Calendar.MONTH));
        assertEquals(2026, calendario.get(Calendar.YEAR));
    }

    @Test
    @DisplayName("ida e volta: paraTela(paraData(x)) devolve o mesmo texto")
    void idaEVolta() {
        assertEquals("29/02/2024", ConversorData.paraTela(ConversorData.paraData("29/02/2024")));
    }

    @Test
    @DisplayName("espacos em volta da data sao ignorados")
    void ignoraEspacos() {
        assertTrue(ConversorData.isDataValida(" 25/11/2026 "));
        assertEquals("25/11/2026", ConversorData.paraTela(ConversorData.paraData(" 25/11/2026 ")));
    }

    @Test
    @DisplayName("invalido: paraData recusa data que nao existe e null")
    void paraDataRecusaInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> ConversorData.paraData("31/02/2026"));
        assertThrows(IllegalArgumentException.class, () -> ConversorData.paraData(null));
    }

    @Test
    @DisplayName("borda: paraTela de null devolve texto vazio")
    void paraTelaNull() {
        assertEquals("", ConversorData.paraTela(null));
    }

    @Test
    @DisplayName("paraTela sempre usa dois digitos no dia e no mes")
    void paraTelaComZeros() {
        Calendar calendario = Calendar.getInstance();
        calendario.clear();
        calendario.set(2026, Calendar.JANUARY, 2);
        Date data = calendario.getTime();
        assertEquals("02/01/2026", ConversorData.paraTela(data));
    }
}
