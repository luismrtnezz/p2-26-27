package telem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.json.JSONObject;

/**
 * Modela un alquiler de un vehículo por parte de un cliente, con fechas de
 * inicio y fin.
 * Almacena los alquileres realizados en un array estático y proporciona métodos
 * para calcular el precio del alquiler.
 * Actúa como contexto del patrón Estrategia: delega el cálculo del precio en la
 * política de precios (PoliticaPrecio) que se le indique.
 * Alquiler
 */
public class Alquiler {

    private Vehiculo vehiculo;
    private Cliente cliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private static Alquiler[] alquileres = new Alquiler[1000];

    private static int contadorAlquileres = 0;

    public Alquiler(Vehiculo vehiculo, Cliente cliente, LocalDate fechaInicio, LocalDate fechaFin) {
        if (vehiculo == null || cliente == null || fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Los datos del alquiler son obligatorios");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha final no puede preceder a la inicial");
        }
        this.vehiculo = vehiculo;
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public static Alquiler[] obtenerAlquileres() {
        return alquileres;
    }

    public static void agregarAlquiler(Alquiler alquiler) {
        if (contadorAlquileres < alquileres.length) {
            alquileres[contadorAlquileres] = alquiler;
            contadorAlquileres++;
        } else {
            System.out.println("No se pueden agregar más alquileres. Capacidad máxima alcanzada.");
        }
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * Número de días del alquiler, incluyendo las fechas de inicio y fin.
     *
     * @return días de duración del alquiler
     */
    public int getDias() {
        return (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
    }

    /**
     * Calcula el precio del alquiler delegando en la política de precios indicada
     * (patrón Estrategia). El precio base se obtiene del vehículo alquilado.
     *
     * @param politica estrategia de cálculo aplicable a este alquiler
     * @return precio total del alquiler
     */
    public double calcularPrecio(PoliticaPrecio politica) {
        return politica.calcularPrecio(vehiculo.getPrecioBase(), getDias());
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }

    public static void main(String[] args) {
        // Crear instancias de Vehiculo y Cliente
        Vehiculo vehiculo1 = new Turismo("ABC123", Categoria.NORMAL, 2020, 5, 400.0);
        Cliente cliente1 = new Cliente("Juan Pérez", "123456789");

        LocalDate hoy = LocalDate.now();
        Alquiler alquiler1 = new Alquiler(vehiculo1, cliente1, hoy, hoy.plusDays(2)); // Alquiler de 3 días

        System.out.println(alquiler1.toString());
        System.out.println("--------------------------------------------------------");

        // Ejemplo del patrón Estrategia: el mismo alquiler con distintas políticas
        System.out.println("Política normal:            " + alquiler1.calcularPrecio(new PoliticaNormal()));
        System.out.println("Política temporada alta:    " + alquiler1.calcularPrecio(new PoliticaTemporadaAlta()));
        System.out.println("Política ofertón:           " + alquiler1.calcularPrecio(new PoliticaOferton()));
        System.out.println("Política cliente frecuente: " + alquiler1.calcularPrecio(new PoliticaClienteFrecuente()));
        System.out.println("--------------------------------------------------------");

        Vehiculo vehiculo2 = new VehiculoIndustrial("ABC123", Categoria.NORMAL, 2020, 1000, 3);
        Alquiler alquiler2 = new Alquiler(vehiculo2, cliente1, hoy, hoy.plusDays(2)); // Alquiler de 3 días

        System.out.println(alquiler2.toString());
        System.out.println("--------------------------------------------------------");
    }
}
