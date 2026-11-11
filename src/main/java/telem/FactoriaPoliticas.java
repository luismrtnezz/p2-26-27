package telem;

/** Centraliza la creacion de estrategias a partir del enumerado del profesor. */
public class FactoriaPoliticas {
    public static PoliticaPrecio getPolitica(PoliticasDePrecios politica) {
        if (politica == null) throw new IllegalArgumentException("Politica obligatoria");
        switch (politica) {
            case Normal: return new PoliticaNormal();
            case TemporadaAlta: return new PoliticaTemporadaAlta();
            case ClienteFrecuente: return new PoliticaClienteFrecuente();
            case Oferton: return new PoliticaOferton();
            default: throw new IllegalArgumentException("Politica desconocida: " + politica);
        }
    }

    public static void main(String[] args) {
        for (PoliticasDePrecios politica : PoliticasDePrecios.values()) {
            System.out.println(politica + ": " + getPolitica(politica).calcularPrecio(50, 8));
        }
    }
}
