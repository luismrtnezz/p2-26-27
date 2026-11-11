package telem;

/** Política normal: precio base por número de días de alquiler. */
public class PoliticaNormal implements PoliticaPrecio {

    @Override
    public double calcularPrecio(int precioBase, int dias) {
        return precioBase * dias;
    }
}