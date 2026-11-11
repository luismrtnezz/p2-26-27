package telem;

/**
 * Interfaz del patrón Estrategia: contrato común de todas las políticas
 * de cálculo del precio de un alquiler.
 * El contexto (Alquiler) usa esta interfaz sin conocer la estrategia concreta.
 */
public interface PoliticaPrecio {

    /**
     * Calcula el precio total de un alquiler.
     *
     * @param precioBase precio base diario del vehículo
     * @param dias       número de días de alquiler
     * @return precio total del alquiler
     */
    double calcularPrecio(int precioBase, int dias);
}