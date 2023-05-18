package com.paqta.paqtafood.model;

public class Platillo extends Producto{
    private String tipo;
    private Double precio;

    public Platillo(String tipo, Double precio) {
        this.tipo = tipo;
        this.precio = precio;
    }

    public Platillo(String nombre, String descripcion, String categoria, Integer cantidad, String imagen, String tipo, Double precio) {
        super(nombre, descripcion, categoria, cantidad, imagen);
        this.tipo = tipo;
        this.precio = precio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Platillo{" +
                "tipo='" + tipo + '\'' +
                ", precio=" + precio +
                "} " + super.toString();
    }
}
