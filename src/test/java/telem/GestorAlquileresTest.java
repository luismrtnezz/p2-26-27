package telem;

import static org.junit.Assert.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GestorAlquileresTest {
    @Rule public TemporaryFolder carpeta = new TemporaryFolder();

    @Test public void filtraTipoYCategoria() {
        GestorAlquileres g = new GestorAlquileres();
        assertEquals(3,g.obtenerVehiculos("Turismo").length);
        assertEquals(1,g.obtenerVehiculos("VehiculoIndustrial",Categoria.ESPECIAL).length);
        assertEquals("CEA2025",g.obtenerVehiculos("VehiculoIndustrial",Categoria.ESPECIAL)[0].getMatricula());
        assertEquals(0,g.obtenerVehiculos("Avion").length);
    }

    @Test public void registraDevuelveYConservaHistorico() {
        GestorAlquileres g = new GestorAlquileres();
        Cliente c = g.buscarCliente("001");
        Vehiculo v = g.buscarVehiculo("TNA2020");
        assertEquals(150,g.alquilar(c,v,3),0.000001);
        assertFalse(v.isDisponible());
        assertEquals(1,g.obtenerAlquileres(new Cliente("Otro nombre","001")).length);
        assertEquals(0,g.obtenerAlquileres(g.buscarCliente("002")).length);
        g.devolverVehiculo(c,v.getMatricula());
        assertTrue(v.isDisponible());
        assertEquals(1,g.obtenerAlquileres().length);
    }

    @Test public void frecuenteSoloTrasCuatroAlquileresPrevios() {
        GestorAlquileres g = new GestorAlquileres();
        Cliente c = g.buscarCliente("001");
        Vehiculo v = g.buscarVehiculo("TNA2020");
        for (int i=0;i<4;i++) {
            assertEquals(200,g.alquilar(c,v,4),0.000001);
            g.devolverVehiculo(c,v.getMatricula());
        }
        assertEquals(150,g.alquilar(c,v,4),0.000001);
    }

    @Test(expected=IllegalStateException.class) public void impideAlquilarVehiculoOcupado() {
        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(g.buscarCliente("001"),g.buscarVehiculo("TNA2020"),1);
        g.alquilar(g.buscarCliente("002"),g.buscarVehiculo("TNA2020"),1);
    }

    @Test(expected=IllegalArgumentException.class) public void impideDevolucionDeOtroCliente() {
        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(g.buscarCliente("001"),g.buscarVehiculo("TNA2020"),1);
        g.devolverVehiculo(g.buscarCliente("002"),"TNA2020");
    }

    @Test(expected=IllegalArgumentException.class) public void rechazaDuracionCero() {
        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(g.buscarCliente("001"),g.buscarVehiculo("TNA2020"),0);
    }

    @Test public void guardaRecuperaYReenlazaReferenciasSinDuplicar() throws Exception {
        GestorAlquileres g = new GestorAlquileres();
        Cliente c = g.buscarCliente("001");
        g.alquilar(c,g.buscarVehiculo("TNA2020"),2);
        g.devolverVehiculo(c,"TNA2020");
        g.alquilar(g.buscarCliente("002"),g.buscarVehiculo("TNA2020"),3);
        g.alquilar(c,g.buscarVehiculo("MNA2021"),1);
        File archivo = carpeta.newFile("datos.json");
        g.guardarDatos(archivo.getPath());
        GestorAlquileres copia = new GestorAlquileres();
        copia.cargarDatos(archivo.getPath());
        copia.cargarDatos(archivo.getPath());
        assertEquals(3,copia.obtenerAlquileres().length);
        assertEquals("Ana Lítica",copia.buscarCliente("005").getNombre());
        assertSame(copia.buscarVehiculo("TNA2020"),copia.obtenerAlquileres()[1].getVehiculo());
        assertSame(copia.buscarCliente("002"),copia.obtenerAlquileres()[1].getCliente());
        assertFalse(copia.buscarVehiculo("TNA2020").isDisponible());
        try { copia.devolverVehiculo(copia.buscarCliente("001"),"TNA2020"); fail(); }
        catch (IllegalArgumentException esperado) { }
        copia.devolverVehiculo(copia.buscarCliente("002"),"TNA2020");
        assertTrue(copia.buscarVehiculo("TNA2020").isDisponible());
    }

    @Test public void cargaInvalidaNoModificaEstado() throws Exception {
        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(g.buscarCliente("001"),g.buscarVehiculo("TNA2020"),1);
        File archivo = carpeta.newFile("invalido.json");
        g.guardarDatos(archivo.getPath());
        JSONObject raiz = new JSONObject(new String(Files.readAllBytes(archivo.toPath()),StandardCharsets.UTF_8));
        raiz.getJSONArray("alquileres").getJSONObject(0).getJSONObject("cliente").put("dni","no-existe");
        Files.write(archivo.toPath(),raiz.toString().getBytes(StandardCharsets.UTF_8));
        try { g.cargarDatos(archivo.getPath()); fail(); }
        catch (IllegalArgumentException esperado) { }
        assertEquals(1,g.obtenerAlquileres().length);
        assertFalse(g.buscarVehiculo("TNA2020").isDisponible());
    }

    @Test public void guardaYRecuperaSinAlquileres() throws Exception {
        GestorAlquileres g = new GestorAlquileres();
        File archivo = carpeta.newFile("vacio.json");
        g.guardarDatos(archivo.getPath());
        g.cargarDatos(archivo.getPath());
        assertEquals(0,g.obtenerAlquileres().length);
        assertEquals(3,g.obtenerVehiculos("Motocicleta").length);
        assertTrue(g.buscarVehiculo("TNA2020").isDisponible());
    }
}
