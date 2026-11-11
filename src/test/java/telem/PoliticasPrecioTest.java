package telem;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import org.junit.Test;

public class PoliticasPrecioTest {
    private static final double DELTA = 0.000001;

    @Test
    public void normalMultiplicaPrecioPorDias() {
        assertEquals(150.0, new PoliticaNormal().calcularPrecio(50, 3), DELTA);
    }

    @Test
    public void temporadaAltaDuplicaPrecio() {
        assertEquals(300.0, new PoliticaTemporadaAlta().calcularPrecio(50, 3), DELTA);
    }

    @Test
    public void ofertonAplicaTreintaPorCientoHastaSieteDias() {
        PoliticaPrecio politica = new PoliticaOferton();
        assertEquals(105.0, politica.calcularPrecio(50, 3), DELTA);
        assertEquals(245.0, politica.calcularPrecio(50, 7), DELTA);
    }

    @Test
    public void ofertonAplicaDescuentoAdicionalDesdeOctavoDia() {
        assertEquals(252.0, new PoliticaOferton().calcularPrecio(50, 8), DELTA);
    }

    @Test
    public void frecuenteAplicaVeintePorCientoPrimerosTresDias() {
        PoliticaPrecio politica = new PoliticaClienteFrecuente();
        assertEquals(40.0, politica.calcularPrecio(50, 1), DELTA);
        assertEquals(120.0, politica.calcularPrecio(50, 3), DELTA);
    }

    @Test
    public void frecuenteAplicaCuarentaPorCientoSoloDesdeCuartoDia() {
        PoliticaPrecio politica = new PoliticaClienteFrecuente();
        assertEquals(150.0, politica.calcularPrecio(50, 4), DELTA);
        assertEquals(270.0, politica.calcularPrecio(50, 8), DELTA);
    }

    @Test
    public void alquilerCuentaAmbasFechasYDelegaEnLaEstrategia() {
        LocalDate inicio = LocalDate.of(2026, 11, 11);
        Alquiler alquiler = new Alquiler(
                new Turismo("ABC123", Categoria.NORMAL, 2020, 5, 400),
                new Cliente("Juan", "12345678A"), inicio, inicio.plusDays(2));
        assertEquals(3, alquiler.getDias());
        assertEquals(150.0, alquiler.calcularPrecio(new PoliticaNormal()), DELTA);
        assertEquals(300.0, alquiler.calcularPrecio(new PoliticaTemporadaAlta()), DELTA);
        assertEquals(105.0, alquiler.calcularPrecio(new PoliticaOferton()), DELTA);
        assertEquals(120.0, alquiler.calcularPrecio(new PoliticaClienteFrecuente()), DELTA);
        assertEquals(53.0, alquiler.calcularPrecio((precio, dias) -> precio + dias), DELTA);
    }

    @Test
    public void alquilerEnMismaFechaDuraUnDia() {
        LocalDate fecha = LocalDate.of(2026, 12, 31);
        Alquiler alquiler = new Alquiler(
                new Motocicleta("MOTO", Categoria.NORMAL, 2020, 125),
                new Cliente("Ana", "87654321B"), fecha, fecha);
        assertEquals(1, alquiler.getDias());
        assertEquals(30.0, alquiler.calcularPrecio(new PoliticaNormal()), DELTA);
    }
}
