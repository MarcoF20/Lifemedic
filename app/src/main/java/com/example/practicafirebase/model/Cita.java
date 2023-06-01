package com.example.practicafirebase.model;

public class Cita {
    private String id;
    private String fecha;
    private String nombreMedico;
    private String especialidad;
    private String userId;

    public Cita(String id, String nombreMedico, String especialidad, String fecha, String userId) {
        this.id = id;
        this.nombreMedico = nombreMedico;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}
