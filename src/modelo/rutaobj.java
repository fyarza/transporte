/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import sun.invoke.empty.Empty;

/**
 *
 * @author Federico Yarza
 */
public class rutaobj {
    String id, nombre, precio;

    public rutaobj(String id, String nombre, String precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }
    
    public rutaobj(){
        this.id=null;
        this.nombre=null;
        this.precio=null;
    }
    
    public boolean esVacio(){
        return (this.nombre == null && this.id == null && this.precio == null);
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

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
    
}
