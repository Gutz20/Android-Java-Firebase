package com.paqta.paqtafood.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class User {

    private String id;
    private String username;
    private String imagen;
    private String email;
    private String password;
    private String rol;
    private boolean disabled;

    private List<String> favoritos;
    private List<String> carrito;
    private Timestamp created_at;
    private Timestamp updated_at;

    public User() {
    }

    public User(String id, String username, String imagen, String email, String password, String rol, boolean disabled, List<String> favoritos, List<String> carrito, Timestamp created_at, Timestamp updated_at) {
        this.id = id;
        this.username = username;
        this.imagen = imagen;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.disabled = disabled;
        this.favoritos = favoritos;
        this.carrito = carrito;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }
}
