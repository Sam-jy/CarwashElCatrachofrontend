package com.example.carwashelcatracho.Models;

import com.google.gson.annotations.SerializedName;

public class Vehicle {
    @SerializedName("id")
    private int id;
    
    @SerializedName("usuario_id")
    private int usuarioId;
    
    @SerializedName("marca")
    private String marca;
    
    @SerializedName("modelo")
    private String modelo;
    
    @SerializedName("anio")
    private int anio;
    
    @SerializedName("placa")
    private String placa;
    
    @SerializedName("tipo_aceite")
    private String tipoAceite;
    
    @SerializedName("color")
    private String color;
    
    @SerializedName("activo")
    private int activo;
    
    @SerializedName("fecha_creacion")
    private String fechaCreacion;
    
    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    // Constructors
    public Vehicle() {}

    public Vehicle(String marca, String modelo, int anio, String placa, String tipoAceite, String color) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.placa = placa;
        this.tipoAceite = tipoAceite;
        this.color = color;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getTipoAceite() { return tipoAceite; }
    public void setTipoAceite(String tipoAceite) { this.tipoAceite = tipoAceite; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public boolean isActivo() {
        return activo == 1;
    }

    public String getDescripcionCompleta() {
        return marca + " " + modelo + " (" + anio + ") - " + placa;
    }

    public String getDescripcionCorta() {
        return marca + " " + modelo + " - " + placa;
    }
}
