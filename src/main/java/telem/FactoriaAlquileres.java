package telem;

public class FactoriaAlquileres {
    public static Alquiler getAlquiler(String jsonString) {
        return new Alquiler(jsonString);
    }
}
