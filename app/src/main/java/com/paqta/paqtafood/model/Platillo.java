package com.paqta.paqtafood.model;

import java.util.List;

public class Platillo extends Producto{
    private String tipo;
    private List<String> contenido;

    public Platillo() {
    }

    public Platillo(String nombre, String descripcion, String categoria, Integer cantidad, String imagen, Double precio, String tipo, List<String> contenido) {
        super(nombre, descripcion, categoria, cantidad, imagen, precio);
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<String> getContenido() {
        return contenido;
    }

    public void setContenido(List<String> contenido) {
        this.contenido = contenido;
    }
}
