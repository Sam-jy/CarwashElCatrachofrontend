package com.example.carwashelcatracho.Models;

import com.google.gson.annotations.SerializedName;

public class Service {
    @SerializedName("id")
    private int id;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("descripcion")
    private String descripcion;
    
    @SerializedName("precio_base")
    private double precioBase;
    
    @SerializedName("precio_domicilio")
    private double precioDomicilio;
    
    @SerializedName("disponible_domicilio")
    private int disponibleDomicilio;
    
    @SerializedName("disponible_centro")
    private int disponibleCentro;
    
    @SerializedName("tiempo_estimado")
    private int tiempoEstimado;
    
    @SerializedName("activo")
    private int activo;
    
    @SerializedName("fecha_creacion")
    private String fechaCreacion;
    
    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    // Constructors
    public Service() {}

    public Service(String nombre, String descripcion, double precioBase) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }

    public double getPrecioDomicilio() { return precioDomicilio; }
    public void setPrecioDomicilio(double precioDomicilio) { this.precioDomicilio = precioDomicilio; }

    public int getDisponibleDomicilio() { return disponibleDomicilio; }
    public void setDisponibleDomicilio(int disponibleDomicilio) { this.disponibleDomicilio = disponibleDomicilio; }

    public int getDisponibleCentro() { return disponibleCentro; }
    public void setDisponibleCentro(int disponibleCentro) { this.disponibleCentro = disponibleCentro; }

    public int getTiempoEstimado() { return tiempoEstimado; }
    public void setTiempoEstimado(int tiempoEstimado) { this.tiempoEstimado = tiempoEstimado; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public boolean isDisponibleDomicilio() {
        return disponibleDomicilio == 1;
    }

    public boolean isDisponibleCentro() {
        return disponibleCentro == 1;
    }

    public boolean isActivo() {
        return activo == 1;
    }

    public double getPrecioParaUbicacion(String tipoUbicacion) {
        if ("domicilio".equals(tipoUbicacion) && isDisponibleDomicilio()) {
            return precioBase + precioDomicilio;
        }
        return precioBase;
    }

    public String getPrecioFormateado(String tipoUbicacion) {
        return "L. " + String.format("%.2f", getPrecioParaUbicacion(tipoUbicacion));
    }

    public boolean isCambioAceite() {
        return nombre != null && nombre.toLowerCase().contains("aceite");
    }
}
