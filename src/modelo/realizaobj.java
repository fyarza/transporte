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
    String fecha,hora,destino,cliente,costo_adicional,tiempo_espera,hora_espera;

    public realizaobj(String fecha, String hora, String destino, String cliente,String costo_adicional,String tiempo_espera,String hora_espera) {
        this.fecha = fecha;
        this.hora = hora;
        this.destino = destino;
        this.cliente = cliente;
        this.costo_adicional = costo_adicional;
        this.tiempo_espera = tiempo_espera;
        this.hora_espera = hora_espera;
    }
    
    public realizaobj(){
        this.fecha = null;
        this.hora = null;
        this.destino = null;
        this.cliente = null;
        this.costo_adicional = null;
        this.hora_espera = null;
        this.tiempo_espera = null;
    }

    public boolean esVacio(){
        return(this.fecha.isEmpty()&&
                this.hora.isEmpty()&&
                this.destino.isEmpty()&&
                this.cliente.isEmpty()&&
                this.costo_adicional.isEmpty()&&
                this.hora_espera.isEmpty()&&
                this.tiempo_espera.isEmpty());
        
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

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getCosto_adicional() {
        return costo_adicional;
    }

    public void setCosto_adicional(String costo_adicional) {
        this.costo_adicional = costo_adicional;
    }

    public String getTiempo_espera() {
        return tiempo_espera;
    }

    public void setTiempo_espera(String tiempo_espera) {
        this.tiempo_espera = tiempo_espera;
    }

    public String getHora_espera() {
        return hora_espera;
    }

    public void setHora_espera(String hora_espera) {
        this.hora_espera = hora_espera;
    }
    
    
}
