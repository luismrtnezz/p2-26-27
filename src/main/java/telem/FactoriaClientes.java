package telem;

public class FactoriaClientes {
    public static Cliente getCliente(String jsonString) {
        return new Cliente(jsonString);
    }
}
