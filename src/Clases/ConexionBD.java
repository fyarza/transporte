/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

/**
 *
 * @author federico
 */

 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.realizaobj;
import modelo.rutaobj;

   
public class ConexionBD {
    
    @SuppressWarnings("empty-statement")
    
    String bd="transporte";
    String usuario="postgres";
    String contrasena="freidnea";
    
    public Connection obtConexion() throws SQLException {
        try {
            String driverName = "org.postgresql.Driver";
            String url = "jdbc:postgresql://localhost:5432/"+bd;
            Class.forName(driverName);
            Connection conn = DriverManager.getConnection(url, usuario, contrasena);
            if (conn != null)
                //JOptionPane.showMessageDialog(null, "Conexión establecida con éxito!!", "Suceso", JOptionPane.INFORMATION_MESSAGE);
               System.out.print("\nConexion Establecida con Exito");
            return conn;    
            
        } catch (ClassNotFoundException exc) {
            JOptionPane.showMessageDialog (null, "No se encontró el Driver de la BD"+exc.getMessage(), "Error Conexion", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (SQLException e) {
            //JOptionPane.showMessageDialog(null, "No pudo establecerse la Conexión.", "Error", JOptionPane.ERROR_MESSAGE);
            //JOptionPane.showMessageDialog (null, "Error: "+e.getMessage(), "Error Conexion", JOptionPane.ERROR_MESSAGE);
            return null;
        }
 }
}

