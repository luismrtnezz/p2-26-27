package telem;

/** Política temporada alta: se duplica el precio base diario. */
public class PoliticaTemporadaAlta implements PoliticaPrecio {

    @Override
    public double calcularPrecio(int precioBase, int dias) {
        return precioBase * 2 * dias;
    }
}