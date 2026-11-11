package telem;

import static org.junit.Assert.*;
import java.time.LocalDate;
import org.json.JSONObject;
import org.junit.Test;

public class FactoriasTest {
    @Test public void reconstruyeVehiculosConTodosSusCampos() {
        Vehiculo[] vehiculos = {new Turismo("T",Categoria.ESPECIAL,2026,7,450.5),
                new Motocicleta("M",Categoria.SUPERIOR,2021,500),
                new VehiculoIndustrial("I",Categoria.NORMAL,2022,1400,3)};
        for (Vehiculo v : vehiculos) {
            v.setDisponible(false);
            Vehiculo copia = FactoriaVehiculos.getVehiculo(v.toString());
            assertNotSame(v,copia);
            assertEquals(v.getClass(),copia.getClass());
            assertTrue(new JSONObject(v.toString()).similar(new JSONObject(copia.toString())));
        }
    }

    @Test public void reconstruyeClienteYAlquilerAnidado() {
        Cliente c = new Cliente("Ana Lítica","005");
        Cliente copia = FactoriaClientes.getCliente(c.toString());
        assertEquals(c.getNombre(),copia.getNombre());
        assertEquals(c.getDni(),copia.getDni());
        LocalDate fecha = LocalDate.of(2026,11,11);
        Alquiler a = new Alquiler(new Motocicleta("M",Categoria.NORMAL,2021,200),c,fecha,fecha.plusDays(2));
        Alquiler b = FactoriaAlquileres.getAlquiler(a.toString());
        assertTrue(b.getVehiculo() instanceof Motocicleta);
        assertTrue(new JSONObject(a.toString()).similar(new JSONObject(b.toString())));
        assertEquals(90,b.calcularPrecio(new PoliticaNormal()),0.000001);
    }

    @Test(expected=IllegalArgumentException.class) public void rechazaTipoDesconocido() {
        FactoriaVehiculos.getVehiculo("{\"tipo\":\"Avion\"}");
    }

}
