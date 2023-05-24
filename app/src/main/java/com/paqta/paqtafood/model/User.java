package com.paqta.paqtafood.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String name;
    private String image;
    private String email;
    private String token;
    private List<String> favoritos;
    private List<String> carrito;

    public User() {
    }

    public User(String name, String image, String email, String token, List<String> favoritos, List<String> carrito) {
        this.name = name;
        this.image = image;
        this.email = email;
        this.token = token;
        this.favoritos = favoritos;
        this.carrito = carrito;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
