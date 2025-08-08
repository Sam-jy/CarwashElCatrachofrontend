package com.example.carwashelcatracho.Models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("apellido")
    private String apellido;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("telefono")
    private String telefono;
    
    @SerializedName("pais")
    private String pais;
    
    @SerializedName("foto_perfil")
    private String fotoPerfil;
    
    @SerializedName("tipo_usuario")
    private String tipoUsuario;
    
    @SerializedName("email_verificado")
    private int emailVerificado;
    
    @SerializedName("fecha_creacion")
    private String fechaCreacion;
    
    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;
    
    @SerializedName("activo")
    private int activo;

    // Constructors
    public User() {}

    public User(String nombre, String apellido, String email, String telefono, String pais) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.pais = pais;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public int getEmailVerificado() { return emailVerificado; }
    public void setEmailVerificado(int emailVerificado) { this.emailVerificado = emailVerificado; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean isEmailVerificado() {
        return emailVerificado == 1;
    }

    public boolean isActivo() {
        return activo == 1;
    }

    public boolean isAdmin() {
        return "admin".equals(tipoUsuario);
    }
}
