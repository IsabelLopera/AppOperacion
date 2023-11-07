package com.example.appoperaciones.Clases;

public class Tienda {

    public int id;
    public String nombre;

    public Tienda() {
    }

    public Tienda(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
