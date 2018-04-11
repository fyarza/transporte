
package modelo;


public class clienteobj {
String id, nombre, direccion;

    public clienteobj(String id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public clienteobj(){
        this.id=null;
        this.nombre=null;
        this.direccion=null;
    }
    
    public boolean esVacio(){
        return(this.id.isEmpty()&&this.nombre.isEmpty()&&this.direccion.isEmpty());
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
