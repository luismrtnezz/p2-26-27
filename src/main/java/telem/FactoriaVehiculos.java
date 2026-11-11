package telem;

import org.json.JSONObject;

/** Selecciona el constructor concreto a partir del campo tipo del JSON. */
public class FactoriaVehiculos {
    public static Vehiculo getVehiculo(String jsonString) {
        String tipo = new JSONObject(jsonString).getString("tipo");
        switch (tipo) {
            case "Turismo": return new Turismo(jsonString);
            case "Motocicleta": return new Motocicleta(jsonString);
            case "VehiculoIndustrial": return new VehiculoIndustrial(jsonString);
            default: throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        }
    }

    public static void main(String[] args) {
        Vehiculo original = new Turismo("1234ABC", Categoria.NORMAL, 2020, 5, 450);
        System.out.println(getVehiculo(original.toString()));
        Cliente cliente = new Cliente("Ana", "001");
        System.out.println(FactoriaClientes.getCliente(cliente.toString()));
        java.time.LocalDate fecha = java.time.LocalDate.of(2026, 11, 11);
        Alquiler alquiler = new Alquiler(original, cliente, fecha, fecha.plusDays(2));
        System.out.println(FactoriaAlquileres.getAlquiler(alquiler.toString()));
    }
}
