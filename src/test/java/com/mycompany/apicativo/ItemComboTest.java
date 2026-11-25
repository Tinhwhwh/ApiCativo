package com.mycompany.apicativo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ItemComboTest {

    @Test
    @DisplayName("caminho feliz: monta 'id - descricao'")
    void monta() {
        assertEquals("3 - Setor A", ItemCombo.montar(3, "Setor A"));
    }

    @Test
    @DisplayName("ida e volta: o id montado volta igual")
    void idaEVolta() {
        assertEquals(42, ItemCombo.extrairId(ItemCombo.montar(42, "Colmeia 42")));
    }

    @Test
    @DisplayName("borda: descricao com ' - ' no meio nao confunde o id")
    void descricaoComHifen() {
        assertEquals(7, ItemCombo.extrairId("7 - Setor - Norte"));
    }

    @Test
    @DisplayName("borda: id 0 e ids grandes")
    void idsNoLimite() {
        assertEquals(0, ItemCombo.extrairId("0 - nada"));
        assertEquals(Integer.MAX_VALUE, ItemCombo.extrairId(Integer.MAX_VALUE + " - grande"));
    }

    @ParameterizedTest(name = "[{0}] e recusado")
    @CsvSource({"Setor A", "abc - Setor A", "' - Setor A'", "''"})
    @DisplayName("invalido: texto fora do formato ou sem id numerico")
    void recusaFormatoErrado(String item) {
        assertThrows(IllegalArgumentException.class, () -> ItemCombo.extrairId(item));
    }

    @Test
    @DisplayName("invalido: null e recusado")
    void recusaNull() {
        assertThrows(IllegalArgumentException.class, () -> ItemCombo.extrairId(null));
    }
}
