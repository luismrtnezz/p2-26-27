package telem;

import static org.junit.Assert.*;
import org.junit.Test;

public class FactoriaPoliticasTest {
    @Test public void factoriaCreaCadaPolitica() {
        assertTrue(FactoriaPoliticas.getPolitica(PoliticasDePrecios.Normal) instanceof PoliticaNormal);
        assertTrue(FactoriaPoliticas.getPolitica(PoliticasDePrecios.TemporadaAlta) instanceof PoliticaTemporadaAlta);
        assertTrue(FactoriaPoliticas.getPolitica(PoliticasDePrecios.Oferton) instanceof PoliticaOferton);
        assertTrue(FactoriaPoliticas.getPolitica(PoliticasDePrecios.ClienteFrecuente) instanceof PoliticaClienteFrecuente);
        assertEquals(252,FactoriaPoliticas.getPolitica(PoliticasDePrecios.Oferton).calcularPrecio(50,8),0.000001);
    }

    @Test(expected=IllegalArgumentException.class) public void rechazaPoliticaNula() {
        FactoriaPoliticas.getPolitica(null);
    }
}
