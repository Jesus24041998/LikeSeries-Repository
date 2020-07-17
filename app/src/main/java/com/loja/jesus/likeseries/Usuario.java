package com.loja.jesus.likeseries;

public class Usuario {
    private String nombre,email,uid;
    private Boolean mensajes;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String uid, Boolean mensajes) {
        this.nombre = nombre;
        this.email = email;
        this.uid = uid;
        this.mensajes = mensajes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return uid;
    }

    public void setToken(String token) {
        this.uid = uid;
    }

    public Boolean getMensajes() {
        return mensajes;
    }

    public void setMensajes(Boolean mensajes) {
        this.mensajes = mensajes;
    }
}
