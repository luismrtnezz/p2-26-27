package telem;

/**
 * Política cliente frecuente: 20% de descuento sobre el precio base
 * los tres primeros días y 40% de descuento a partir del cuarto día.
 */
public class PoliticaClienteFrecuente implements PoliticaPrecio {

    private static final double FACTOR_PRIMEROS_DIAS = 0.8; // 20% de descuento
    private static final double FACTOR_RESTO_DIAS = 0.6; // 40% de descuento
    private static final int DIAS_PRIMER_TRAMO = 3;

    @Override
    public double calcularPrecio(int precioBase, int dias) {
        int diasPrimerTramo = Math.min(dias, DIAS_PRIMER_TRAMO);
        int diasSegundoTramo = Math.max(0, dias - DIAS_PRIMER_TRAMO);
        return precioBase * FACTOR_PRIMEROS_DIAS * diasPrimerTramo
                + precioBase * FACTOR_RESTO_DIAS * diasSegundoTramo;
    }
}