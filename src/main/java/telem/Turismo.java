package telem;

public class Turismo extends Vehiculo {

    public Turismo(String jsonString) {
        super(jsonString);
        org.json.JSONObject json = new org.json.JSONObject(jsonString);
        this.plazas = json.getInt("plazas");
        this.capacidadMaletero = json.getDouble("capacidadMaletero");
    }

    int plazas;
    double capacidadMaletero;

    public Turismo(String matricula, Categoria categoria, int annio,
        int plazas, double capacidadMaletero
    ) {
        super(matricula, categoria, annio);
        this.plazas = plazas;
        this.capacidadMaletero = capacidadMaletero;
    }

    public int getPlazas() {
        return plazas;
    }

    public double getCapacidadMaletero() {
        return capacidadMaletero;
    }

    @Override
    public String getTipo() {
        return "Turismo";
    }

    @Override
    public int getPrecioBase() {
        int precioBase = 0;
        switch (getCategoria()) {
            case NORMAL:
                precioBase = 50;
                break;
            case ESPECIAL:
                precioBase = 80;
                break;
            case SUPERIOR:
                precioBase = 100;
                break;
        }
        return precioBase;
    }
}
