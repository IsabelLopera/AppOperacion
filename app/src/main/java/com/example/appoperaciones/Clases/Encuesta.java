package com.example.appoperaciones.Clases;


public class Encuesta {

    public String id;
    public String nombre;

    public Encuesta() {
    }

    public Encuesta(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String toString() {
        return nombre;
    }
}
