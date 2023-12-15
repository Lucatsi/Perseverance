package com.perseverance;

import android.widget.Button;

public class ItemTarea {
    public String nombre;
    public String fecha;
    public Button editar;
    public Button eliminar;

    public ItemTarea(String nombre, String fecha, Button editar, Button eliminar) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.editar = editar;
        this.eliminar = eliminar;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Button getEditar() {
        return editar;
    }

    public void setEditar(Button editar) {
        this.editar = editar;
    }

    public Button getEliminar() {
        return eliminar;
    }

    public void setEliminar(Button eliminar) {
        this.eliminar = eliminar;
    }

}
