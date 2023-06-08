package com.paqta.paqtafood.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String id;
    private String username;
    private String imagen;
    private String email;
    private String password;
    private String rol;
    private List<String> favoritos;
    private List<String> carrito;
    private Boolean estado;
    private Timestamp created_at;
    private Timestamp updated_at;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String imagen, String email, String rol, List<String> favoritos, List<String> carrito) {
        this.username = username;
        this.imagen = imagen;
        this.email = email;
        this.rol = rol;
        this.favoritos = favoritos;
        this.carrito = carrito;
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
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
