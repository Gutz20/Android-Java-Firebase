package com.paqta.paqtafood.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String username;
    private String imagen;
    private String email;
    private String rol;
    private List<String> favoritos;
    private List<String> carrito;

    public User() {
    }

    public User(String username, String imagen, String email, String rol, List<String> favoritos, List<String> carrito) {
        this.username = username;
        this.imagen = imagen;
        this.email = email;
        this.rol = rol;
        this.favoritos = favoritos;
        this.carrito = carrito;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<String> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<String> favoritos) {
        this.favoritos = favoritos;
    }

    public List<String> getCarrito() {
        return carrito;
    }

    public void setCarrito(List<String> carrito) {
        this.carrito = carrito;
    }
}
