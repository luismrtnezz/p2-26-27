package telem;

/**
 * Política ofertón: 30% de descuento sobre el precio base y, si el alquiler
 * dura más de 7 días, un 10% de descuento adicional sobre el total.
 */
public class PoliticaOferton implements PoliticaPrecio {

    private static final double FACTOR_DESCUENTO = 0.7; // se paga el 70%
    private static final double FACTOR_ADICIONAL = 0.9; // se paga el 90%
    private static final int DIAS_PARA_DESCUENTO_ADICIONAL = 7;

    @Override
    public double calcularPrecio(int precioBase, int dias) {
        double total = precioBase * FACTOR_DESCUENTO * dias;
        if (dias > DIAS_PARA_DESCUENTO_ADICIONAL) {
            total = total * FACTOR_ADICIONAL;
        }
        return total;
    }
}