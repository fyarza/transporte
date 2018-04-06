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
public class realizaobj {
    String fecha,hora,destino;

    public realizaobj(String fecha, String hora, String destino) {
        this.fecha = fecha;
        this.hora = hora;
        this.destino = destino;
    }
    
    public realizaobj(){
        this.fecha=null;
        this.hora=null;
        this.destino=null;
    }

    public boolean esVacio(){
        return(this.fecha==null&&this.hora==null&&this.destino==null);
    }
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
    
    
}
