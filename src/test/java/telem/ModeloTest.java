package telem;

import static org.junit.Assert.*;
import java.time.LocalDate;
import org.junit.Test;

public class ModeloTest {
    @Test public void preciosBaseDeTodasLasCategorias() {
        int[][] precios = {{50,80,100},{30,50,60},{70,100,120}};
        for (int i = 0; i < Categoria.values().length; i++) {
            Categoria c = Categoria.values()[i];
            Vehiculo[] vs = {new Turismo("T",c,2020,5,400), new Motocicleta("M",c,2021,125),
                    new VehiculoIndustrial("I",c,2022,1000,2)};
            for (int j = 0; j < vs.length; j++) {
                assertEquals(precios[j][i], vs[j].getPrecioBase());
                assertTrue(vs[j].isDisponible());
                vs[j].setDisponible(false);
                assertFalse(vs[j].isDisponible());
            }
        }
    }

    @Test public void clienteConservaDatos() {
        Cliente c = new Cliente("Ana", "001");
        assertEquals("Ana", c.getNombre());
        assertEquals("001", c.getDni());
    }

    @Test public void duracionCruzaCambioDeAnnio() {
        Alquiler a = new Alquiler(new Turismo("T",Categoria.NORMAL,2020,5,400),
                new Cliente("Ana","001"), LocalDate.of(2026,12,31), LocalDate.of(2027,1,2));
        assertEquals(3,a.getDias());
    }

    @Test(expected=IllegalArgumentException.class) public void rechazaFechasInvertidas() {
        new Alquiler(new Turismo("T",Categoria.NORMAL,2020,5,400), new Cliente("Ana","001"),
                LocalDate.of(2026,11,12), LocalDate.of(2026,11,11));
    }
}
