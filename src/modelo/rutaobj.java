/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

/**
 *
 * @author Federico Yarza
 */
public class rutaobj {
    String id, origen,destino, precio,tipo;

    public rutaobj(String id, String origen,String destino, String precio,String tipo) {
        this.id = id;
        this.origen=origen;
        this.destino = destino;
        this.precio = precio;
        this.tipo=tipo;
    }
    
    public rutaobj(){
        this.id=null;
        this.destino=null;
        this.precio=null;
        this.origen=null;
        this.tipo=null;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public boolean esVacio(){
        return (this.destino.isEmpty() && this.id.isEmpty() && this.precio.isEmpty()&&this.origen.isEmpty()&&this.tipo.isEmpty());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
    
}
