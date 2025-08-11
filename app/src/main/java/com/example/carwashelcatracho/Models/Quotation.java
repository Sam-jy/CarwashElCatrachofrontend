package com.example.carwashelcatracho.Models;

import com.google.gson.annotations.SerializedName;

public class Quotation {
    @SerializedName("id")
    private int id;
    
    @SerializedName("usuario_id")
    private int usuarioId;
    
    @SerializedName("vehiculo_id")
    private int vehiculoId;
    
    @SerializedName("servicio_id")
    private int servicioId;
    
    @SerializedName("tipo_ubicacion")
    private String tipoUbicacion;
    
    @SerializedName("direccion_servicio")
    private String direccionServicio;
    
    @SerializedName("latitud")
    private Double latitud;
    
    @SerializedName("longitud")
    private Double longitud;
    
    @SerializedName("fecha_servicio")
    private String fechaServicio;
    
    @SerializedName("hora_servicio")
    private String horaServicio;
    
    @SerializedName("precio_cotizado")
    private double precioCotizado;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("notas_cliente")
    private String notasCliente;
    
    @SerializedName("notas_admin")
    private String notasAdmin;
    
    @SerializedName("fecha_respuesta")
    private String fechaRespuesta;
    
    @SerializedName("fecha_creacion")
    private String fechaCreacion;
    
    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;
    
    // Additional fields from joins
    @SerializedName("servicio_nombre")
    private String servicioNombre;
    
    @SerializedName("marca")
    private String marca;
    
    @SerializedName("modelo")
    private String modelo;
    
    @SerializedName("placa")
    private String placa;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("apellido")
    private String apellido;
    
    @SerializedName("telefono")
    private String telefono;
    
    @SerializedName("email")
    private String email;

    @SerializedName("precio_final")
    private String precioFinal; 

    // Constructors
    public Quotation() {}

    public Quotation(int vehiculoId, int servicioId, String tipoUbicacion, String fechaServicio, String horaServicio) {
        this.vehiculoId = vehiculoId;
        this.servicioId = servicioId;
        this.tipoUbicacion = tipoUbicacion;
        this.fechaServicio = fechaServicio;
        this.horaServicio = horaServicio;
    }

    public String getPrecioFinal() {
    return precioFinal;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(int vehiculoId) { this.vehiculoId = vehiculoId; }

    public int getServicioId() { return servicioId; }
    public void setServicioId(int servicioId) { this.servicioId = servicioId; }

    public String getTipoUbicacion() { return tipoUbicacion; }
    public void setTipoUbicacion(String tipoUbicacion) { this.tipoUbicacion = tipoUbicacion; }

    public String getDireccionServicio() { return direccionServicio; }
    public void setDireccionServicio(String direccionServicio) { this.direccionServicio = direccionServicio; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getFechaServicio() { return fechaServicio; }
    public void setFechaServicio(String fechaServicio) { this.fechaServicio = fechaServicio; }

    public String getHoraServicio() { return horaServicio; }
    public void setHoraServicio(String horaServicio) { this.horaServicio = horaServicio; }

    public double getPrecioCotizado() { return precioCotizado; }
    public void setPrecioCotizado(double precioCotizado) { this.precioCotizado = precioCotizado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNotasCliente() { return notasCliente; }
    public void setNotasCliente(String notasCliente) { this.notasCliente = notasCliente; }

    public String getNotasAdmin() { return notasAdmin; }
    public void setNotasAdmin(String notasAdmin) { this.notasAdmin = notasAdmin; }

    public String getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(String fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Helper methods
    public String getPrecioFormateado() {
        return "L. " + String.format("%.2f", precioCotizado);
    }

    public String getPrecioFinalFormateado() {
    try {
        double precio = Double.parseDouble(precioFinal);
        return "L. " + String.format("%.2f", precio);
    } catch (Exception e) {
        return "L. 0.00";
    }
}

    public String getClienteNombre() {
        return nombre + " " + apellido;
    }

    public String getVehiculoDescripcion() {
        return marca + " " + modelo + " - " + placa;
    }

    public boolean isPendiente() {
        return "pendiente".equals(estado);
    }

    public boolean isEnviada() {
        return "enviada".equals(estado);
    }

    public boolean isAceptada() {
        return "aceptada".equals(estado);
    }

    public boolean isCompletada() {
        return "completada".equals(estado);
    }

    public boolean isRechazada() {
        return "rechazada".equals(estado);
    }

    public boolean isCancelada() {
        return "cancelada".equals(estado);
    }

    public String getEstadoDisplay() {
        switch (estado) {
            case "pendiente": return "Pendiente";
            case "enviada": return "Enviada";
            case "aceptada": return "Aceptada";
            case "completada": return "Completada";
            case "rechazada": return "Rechazada";
            case "cancelada": return "Cancelada";
            default: return estado;
        }
    }

    public boolean isDomicilio() {
        return "domicilio".equals(tipoUbicacion);
    }

    public boolean isCentro() {
        return "centro".equals(tipoUbicacion);
    }
}
