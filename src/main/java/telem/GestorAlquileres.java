package telem;

import java.time.LocalDate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/** Gestor del profesor completado: catalogos, alquileres, devoluciones y persistencia. */
public class GestorAlquileres {
    private int contadorAlquileres = 0;
    private Alquiler[] alquileres = new Alquiler[1000];
    private Cliente[] clientes = new Cliente[10];
    private Vehiculo[] vehiculos = new Vehiculo[100];
    // Estado separado del historial: una devolucion no borra el alquiler.
    private boolean[] activos = new boolean[1000];
    // Un lector compartido evita perder lineas por el buffering de varios lectores.
    private final Scanner entrada = new Scanner(System.in);

    public GestorAlquileres() {
        inicializarClientes();
        inicializarVehiculos();
    }

    private void inicializarClientes() {
        clientes[0] = new Cliente("Clara Videncia", "001");
        clientes[1] = new Cliente("Aitor Menta", "002");
        clientes[2] = new Cliente("Lola Mento", "003");
        clientes[3] = new Cliente("Armando Guerra", "004");
        clientes[4] = new Cliente("Ana Lítica", "005");
        clientes[5] = new Cliente("Carmelo Cotón", "006");
    }

    private void inicializarVehiculos() {
        vehiculos[0] = new Turismo("TNA2020", Categoria.NORMAL, 2020, 5, 450.0);
        vehiculos[1] = new Motocicleta("MNA2021", Categoria.NORMAL, 2021, 200);
        vehiculos[2] = new VehiculoIndustrial("CNA2019", Categoria.NORMAL, 2019, 1400, 2);
        vehiculos[3] = new Turismo("TSA2024", Categoria.SUPERIOR, 2024, 5, 550.0);
        vehiculos[4] = new Motocicleta("MSA2021", Categoria.SUPERIOR, 2021, 500);
        vehiculos[5] = new VehiculoIndustrial("CSA2025", Categoria.SUPERIOR, 2025, 2400, 3);
        vehiculos[6] = new Turismo("TEA2026", Categoria.ESPECIAL, 2026, 7, 650.0);
        vehiculos[7] = new Motocicleta("MEA2026", Categoria.ESPECIAL, 2026, 1000);
        vehiculos[8] = new VehiculoIndustrial("CEA2025", Categoria.ESPECIAL, 2025, 4400, 5);

    }


    public Vehiculo[] obtenerVehiculos(String tipo) {
        return Arrays.stream(vehiculos).filter(v -> v != null && v.getTipo().equals(tipo))
                .toArray(Vehiculo[]::new);
    }

    public Vehiculo[] obtenerVehiculos(String tipo, Categoria categoria) {
        return Arrays.stream(obtenerVehiculos(tipo)).filter(v -> v.getCategoria() == categoria)
                .toArray(Vehiculo[]::new);
    }

    public Cliente buscarCliente(String dni) {
        for (Cliente c : clientes) if (c != null && c.getDni().equals(dni)) return c;
        return null;
    }

    public Vehiculo buscarVehiculo(String matricula) {
        for (Vehiculo v : vehiculos) if (v != null && v.getMatricula().equals(matricula)) return v;
        return null;
    }

    public void agregarAlquiler(Alquiler alquiler) {
        if (alquiler == null) throw new IllegalArgumentException("Alquiler obligatorio");
        if (contadorAlquileres == alquileres.length)
            throw new IllegalStateException("Capacidad maxima de alquileres alcanzada");
        Vehiculo v = buscarVehiculo(alquiler.getVehiculo().getMatricula());
        Cliente c = buscarCliente(alquiler.getCliente().getDni());
        if (v == null || c == null) throw new IllegalArgumentException("Cliente o vehiculo no registrado");
        if (!v.isDisponible()) throw new IllegalStateException("Vehiculo no disponible");
        alquileres[contadorAlquileres] = new Alquiler(v, c, alquiler.getFechaInicio(), alquiler.getFechaFin());
        activos[contadorAlquileres++] = true;
        v.setDisponible(false);
    }

    public Alquiler[] obtenerAlquileres() {
        return Arrays.copyOf(alquileres, contadorAlquileres);
    }

    public Alquiler[] obtenerAlquileres(Cliente cliente) {
        if (cliente == null) throw new IllegalArgumentException("Cliente obligatorio");
        return Arrays.stream(obtenerAlquileres())
                .filter(a -> a.getCliente().getDni().equals(cliente.getDni())).toArray(Alquiler[]::new);
    }

    /** El umbral es el numero minimo de alquileres previos. */
    private boolean esClienteFrecuente(Cliente cliente, int umbral) {
        return obtenerAlquileres(cliente).length >= umbral;
    }

    /** Se aplica la tarifa frecuente con MAS DE TRES alquileres previos, como indica el gestor. */
    public double alquilar(Cliente cliente, Vehiculo vehiculo, int dias) {
        if (dias <= 0) throw new IllegalArgumentException("La duracion debe ser positiva");
        if (cliente == null || vehiculo == null) throw new IllegalArgumentException("Datos obligatorios");
        Cliente registrado = buscarCliente(cliente.getDni());
        Vehiculo registradoV = buscarVehiculo(vehiculo.getMatricula());
        if (registrado == null || registradoV == null) throw new IllegalArgumentException("Datos no registrados");
        PoliticasDePrecios politica = esClienteFrecuente(registrado, 4)
                ? PoliticasDePrecios.ClienteFrecuente : PoliticasDePrecios.Normal;
        LocalDate inicio = LocalDate.now();
        Alquiler alquiler = new Alquiler(registradoV, registrado, inicio, inicio.plusDays(dias - 1L));
        double precio = alquiler.calcularPrecio(FactoriaPoliticas.getPolitica(politica));
        agregarAlquiler(alquiler);
        return precio;
    }

    public void devolverVehiculo(Cliente cliente, String matricula) {
        if (cliente == null) throw new IllegalArgumentException("Cliente obligatorio");
        for (int i = 0; i < contadorAlquileres; i++) {
            Alquiler a = alquileres[i];
            if (activos[i] && a.getCliente().getDni().equals(cliente.getDni())
                    && a.getVehiculo().getMatricula().equals(matricula)) {
                activos[i] = false;
                a.getVehiculo().setDisponible(true);
                return;
            }
        }
        throw new IllegalArgumentException("No existe un alquiler activo de ese cliente y vehiculo");
    }

    /** Guarda catalogos, historial y devoluciones en una instantanea JSON. */
    public void guardarDatos(String archivo) throws IOException {
        JSONObject raiz = new JSONObject();
        JSONArray cs = new JSONArray(), vs = new JSONArray(), as = new JSONArray();
        for (Cliente c : clientes) if (c != null) cs.put(new JSONObject(c.toString()));
        for (Vehiculo v : vehiculos) if (v != null) vs.put(new JSONObject(v.toString()));
        for (int i = 0; i < contadorAlquileres; i++) {
            JSONObject a = new JSONObject(alquileres[i].toString());
            as.put(a.put("activo", activos[i]));
        }
        raiz.put("clientes", cs).put("vehiculos", vs).put("alquileres", as);
        Files.write(Paths.get(archivo), raiz.toString(2).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Recupera una instantanea sustituyendo el estado actual, sin duplicar datos.
     * Valida antes de asignar los arrays: un error deja intacto el estado anterior.
     * Los alquileres se enlazan a los mismos objetos de los catalogos.
     */
    public void cargarDatos(String archivo) throws IOException {
        JSONObject raiz = new JSONObject(new String(Files.readAllBytes(Paths.get(archivo)), StandardCharsets.UTF_8));
        JSONArray cs = raiz.getJSONArray("clientes"), vs = raiz.getJSONArray("vehiculos"),
                as = raiz.getJSONArray("alquileres");
        if (cs.length() > clientes.length || vs.length() > vehiculos.length || as.length() > alquileres.length)
            throw new IllegalArgumentException("El fichero supera la capacidad del gestor");
        Cliente[] nuevosClientes = new Cliente[clientes.length];
        Vehiculo[] nuevosVehiculos = new Vehiculo[vehiculos.length];
        Alquiler[] nuevosAlquileres = new Alquiler[alquileres.length];
        boolean[] nuevosActivos = new boolean[activos.length];
        java.util.Map<String, Cliente> porDni = new java.util.HashMap<>();
        java.util.Map<String, Vehiculo> porMatricula = new java.util.HashMap<>();
        java.util.Set<String> ocupados = new java.util.HashSet<>();
        for (int i = 0; i < cs.length(); i++) {
            Cliente c = FactoriaClientes.getCliente(cs.getJSONObject(i).toString());
            if (porDni.put(c.getDni(), c) != null) throw new IllegalArgumentException("DNI duplicado");
            nuevosClientes[i] = c;
        }
        for (int i = 0; i < vs.length(); i++) {
            Vehiculo v = FactoriaVehiculos.getVehiculo(vs.getJSONObject(i).toString());
            if (porMatricula.put(v.getMatricula(), v) != null) throw new IllegalArgumentException("Matricula duplicada");
            nuevosVehiculos[i] = v;
        }
        for (int i = 0; i < as.length(); i++) {
            JSONObject json = as.getJSONObject(i);
            Alquiler a = FactoriaAlquileres.getAlquiler(json.toString());
            Cliente c = porDni.get(a.getCliente().getDni());
            Vehiculo v = porMatricula.get(a.getVehiculo().getMatricula());
            if (c == null || v == null) throw new IllegalArgumentException("Referencia inexistente");
            nuevosAlquileres[i] = new Alquiler(v, c, a.getFechaInicio(), a.getFechaFin());
            nuevosActivos[i] = json.getBoolean("activo");
            if (nuevosActivos[i] && !ocupados.add(v.getMatricula()))
                throw new IllegalArgumentException("Dos alquileres activos del mismo vehiculo");
        }
        for (Vehiculo v : nuevosVehiculos) {
            if (v != null && v.isDisponible() == ocupados.contains(v.getMatricula()))
                throw new IllegalArgumentException("Disponibilidad incoherente");
        }
        clientes = nuevosClientes;
        vehiculos = nuevosVehiculos;
        alquileres = nuevosAlquileres;
        activos = nuevosActivos;
        contadorAlquileres = as.length();
    }

    private String leerLinea() {
        if (!entrada.hasNextLine()) throw new java.util.NoSuchElementException("Fin de entrada");
        return entrada.nextLine();
    }

    private int leerEntero() {
        try { return Integer.parseInt(leerLinea().trim()); }
        catch (NumberFormatException e) { return Integer.MIN_VALUE; }
    }

    public Cliente identificarCliente() {
        for (int i = 0; i < clientes.length; i++) {
            if (clientes[i] != null) System.out.println(i + " ---> " + clientes[i].getNombre());
        }
        System.out.println("Numero de cliente (-1 para volver):");
        int numero = leerEntero();
        if (numero == -1) return null;
        if (numero < 0 || numero >= clientes.length || clientes[numero] == null) {
            System.out.println("Cliente invalido");
            return null;
        }
        return clientes[numero];
    }

    public Vehiculo seleccionarVehiculo(Cliente cliente) {
        System.out.println("1 Turismo, 2 Motocicleta, 3 VehiculoIndustrial, 0 Cancelar");
        String tipo;
        switch (leerEntero()) {
            case 1: tipo = "Turismo"; break;
            case 2: tipo = "Motocicleta"; break;
            case 3: tipo = "VehiculoIndustrial"; break;
            case 0: return null;
            default: System.out.println("Tipo invalido"); return null;
        }
        System.out.println("1 Normal, 2 Superior, 3 Especial, 4 Cualquiera");
        Vehiculo[] elegibles;
        switch (leerEntero()) {
            case 1: elegibles = obtenerVehiculos(tipo, Categoria.NORMAL); break;
            case 2: elegibles = obtenerVehiculos(tipo, Categoria.SUPERIOR); break;
            case 3: elegibles = obtenerVehiculos(tipo, Categoria.ESPECIAL); break;
            case 4: elegibles = obtenerVehiculos(tipo); break;
            default: System.out.println("Categoria invalida"); return null;
        }
        elegibles = Arrays.stream(elegibles).filter(Vehiculo::isDisponible).toArray(Vehiculo[]::new);
        if (elegibles.length == 0) {
            System.out.println("No hay vehiculos disponibles");
            return null;
        }
        for (int i = 0; i < elegibles.length; i++) System.out.println(i + " ---> " + elegibles[i]);
        System.out.println("Numero de vehiculo (-1 para cancelar):");
        int indice = leerEntero();
        if (indice == -1) return null;
        if (indice < 0 || indice >= elegibles.length) {
            System.out.println("Indice invalido");
            return null;
        }
        return elegibles[indice];
    }

    public void menuPrincipal() {
        while (true) {
            try {
                System.out.println("Gestion de alquileres: 1 Cliente, 2 Guardar, 3 Recuperar, 0 Salir");
                int opcion = leerEntero();
                if (opcion == 0) return;
                if (opcion == 2 || opcion == 3) {
                    System.out.println("Ruta del fichero JSON (recuperar sustituye los datos actuales):");
                    String ruta = leerLinea();
                    if (opcion == 2) guardarDatos(ruta); else cargarDatos(ruta);
                    System.out.println("Operacion completada.");
                    continue;
                }
                if (opcion != 1) { System.out.println("Opcion invalida"); continue; }
                Cliente cliente = identificarCliente();
                if (cliente == null) continue;
                System.out.println("1 Alquilar, 2 Devolver, 3 Historico, 0 Volver");
                switch (leerEntero()) {
                    case 1:
                        Vehiculo v = seleccionarVehiculo(cliente);
                        if (v == null) break;
                        System.out.println("Duracion en dias:");
                        System.out.println("Precio total: " + alquilar(cliente, v, leerEntero()) + " euros");
                        break;
                    case 2:
                        System.out.println("Matricula del vehiculo a devolver:");
                        devolverVehiculo(cliente, leerLinea().trim());
                        System.out.println("Vehiculo devuelto.");
                        break;
                    case 3:
                        for (Alquiler a : obtenerAlquileres(cliente)) System.out.println(a);
                        break;
                    case 0: break;
                    default: System.out.println("Opcion invalida");
                }
            } catch (java.util.NoSuchElementException e) {
                return;
            } catch (IOException | IllegalArgumentException | IllegalStateException | org.json.JSONException e) {
                System.out.println("No se pudo realizar la operacion: " + e.getMessage());
            }
        }
    }

    public void listarAlquileres() {
        for (Alquiler a : obtenerAlquileres()) System.out.println(a);
    }

    public void listarVehiculos() {
        for (Vehiculo v : vehiculos) if (v != null) System.out.println(v);
    }

    public static void main(String[] args) {
        new GestorAlquileres().menuPrincipal();
    }
}
