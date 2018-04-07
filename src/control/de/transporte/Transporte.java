/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control.de.transporte;

import Clases.ConexionBD;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Federico Yarza
 */
public class Transporte extends javax.swing.JFrame {

    /**
     * Creates new form Transporte
     */
    DefaultTableModel modeloruta;
    private DecimalFormat totalFormat;
    private double total1;
    public Transporte() {
        initComponents();
        modeloruta=(DefaultTableModel)tabla_ruta.getModel();
        cargarTablaDestinos ();
         poputTable();
         total1=0.0;
        txt_precio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(totalFormat)));
        anadeListenerAlModelo(tabla_ruta);
        //txt_precio.setValue(total1);
    }
    
    
    
     private void anadeListenerAlModelo(JTable tabla) {
        tabla.getModel().addTableModelListener((TableModelEvent evento) -> {
            actualizartabla(evento);
        });
    }
      protected void actualizartabla(TableModelEvent evento) {
        // Solo se trata el evento UPDATE, correspondiente al cambio de valor
        // de una celda.
        if (evento.getType() == TableModelEvent.UPDATE) {
            String id,nombre,precio;
            // Se obtiene el modelo de la tabla y la fila/columna que han cambiado.
            TableModel modelo = ((TableModel) (evento.getSource()));
            int fila = evento.getFirstRow();
            int columna = evento.getColumn();
            
            id=modelo.getValueAt(fila, 0).toString();
            
            // Los cambios en la ultima fila y columna se ignoran.
            // Este return es necesario porque cuando nuestro codigo modifique
            // los valores de las sumas en esta fila y columna, saltara nuevamente
            // el evento, metiendonos en un bucle recursivo de llamadas a este
            // metodo.
            if (fila == 0 || columna == 0) {
                return;
            }
            try {
                nombre=modelo.getValueAt(fila, 1).toString();
                precio=modelo.getValueAt(fila, 2).toString();
               actualizar(id,nombre,precio); 
            } catch (NullPointerException e) {
                // La celda que ha cambiado esta vacia.
            }
        }

    }
    
    
    
    private void eliminarruta(){
     int fsel = this.tabla_ruta.getSelectedRow();
        if (fsel == -1)
        {
            JOptionPane.showMessageDialog(null, "Debe selecionar la materia a eliminar", "Advertencia", 0);
        }
        else
        {
            
            int resp = JOptionPane.showConfirmDialog(null, "Estas Seguro de Eliminar esta materia", "Eliminar", 0);
            if (resp == 0)
            {
                ConexionBD con=new ConexionBD();
                Connection conexion;
                try {
                    conexion = con.obtConexion();
                    Statement st=conexion.createStatement();
                    String sql="DELETE from p.ruta where id='"+tabla_ruta.getValueAt(tabla_ruta.getSelectedRow(), 0).toString()+"'";
                    st.executeUpdate(sql);
                    JOptionPane.showMessageDialog(null, "Eliminado Exitosamente","ELIMINADO", JOptionPane.INFORMATION_MESSAGE);
                    this.modeloruta = ((DefaultTableModel)this.tabla_ruta.getModel());
                this.modeloruta.removeRow(fsel);
                st.close(); 
                conexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private boolean actualizar(String id,String nombre, String precio){
        boolean resul=false;
        if(!nombre.isEmpty()&&!precio.isEmpty()){
        ConexionBD con=new ConexionBD();
        try {
            Connection conexion=con.obtConexion();
            Statement st=conexion.createStatement();
            String SQL ="UPDATE p.ruta set nombre='" + nombre + "',precio='" + precio + "'where id='" + id + "'";
            st.executeUpdate(SQL);
            st.close();
            conexion.close();
            resul=true;
        } catch (SQLException ex) {
            Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        return resul;
    }
    
    
     public void poputTable(){
        JPopupMenu popupMenu =new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Eliminar",new ImageIcon(getClass().getResource("/Icono/Borrar3_opt.png")));
        menuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               eliminarruta();
            }
        });
        JMenuItem menuItem2=new JMenuItem("Actualizar",new ImageIcon(getClass().getResource("/Icono/refresh_256_opt.png")));
      menuItem2.addActionListener((ActionEvent ae) -> {
          cargarTablaDestinos ();
        });
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        tabla_ruta.setComponentPopupMenu(popupMenu);
    }
    
    private boolean guardar(){
        boolean resul=false;
        ConexionBD con= new ConexionBD();
        try {
            Connection conexion=con.obtConexion();
            Statement st=conexion.createStatement();
            String Sql="insert into p.ruta (nombre,precio) values ('"+txt_nombre.getText()+"','"+txt_precio.getText()+"')";
            st.executeUpdate(Sql);
            st.close();
            resul=true;
            conexion.close();
        } catch (SQLException ex) {
            Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resul;
    }
    
    
    private static void reiniciarJTable(javax.swing.JTable Tabla){
        DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
        while(modelo.getRowCount()>0)modelo.removeRow(0);
    }   
    
    
    private void cargarTablaDestinos (){
     String sql;
     ConexionBD con =new ConexionBD();
     reiniciarJTable(tabla_ruta);
     modeloruta.setRowCount(0);
     Connection conexion;
        try {
            conexion = con.obtConexion();
            sql= "select * from p.ruta";
            PreparedStatement pstm = conexion.prepareCall(sql);
            ResultSet rset = pstm.executeQuery();
            ResultSetMetaData rsmd=rset.getMetaData();
            int col= rsmd.getColumnCount();
            while (rset.next())
            {
                String filas[]=new String[col];
                for (int j=0;j<col;j++){
                    filas[j]=rset.getString(j+1);
         }
        modeloruta.addRow(filas);
         tabla_ruta.setModel(modeloruta);
     }
     rset.close();
     conexion.close();   
        } catch (SQLException ex) {
            Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
        }
     
 }
    private void limpiar(){
        txt_nombre.setText(null);
        txt_precio.setText(null);
    }
    private boolean validad(){
        boolean resul=true;
        if(txt_nombre.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Campo Nombre Vacio", "Error", JOptionPane.ERROR_MESSAGE);
            resul=false;
            txt_nombre.requestFocus();
        }else if(txt_precio.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Campo Precio Vacio", "Error", JOptionPane.ERROR_MESSAGE);
            resul=false;
            txt_precio.requestFocus();
        }
        return resul;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        labelTask1 = new org.edisoncor.gui.label.LabelTask();
        jPanel2 = new javax.swing.JPanel();
        btn_guardar = new org.edisoncor.gui.button.ButtonAction();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_nombre = new javax.swing.JTextField();
        txt_precio = new javax.swing.JFormattedTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_ruta = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        labelTask1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTask1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/camionero_bolsa_trabajo_opt.png"))); // NOI18N
        labelTask1.setText("TRANSPORTE");
        labelTask1.setCategoryFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        labelTask1.setDescription("Servicio");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153), 2), "Informacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        btn_guardar.setText("Guardar");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Nombre:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Precio:");

        txt_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nombreKeyTyped(evt);
            }
        });

        txt_precio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txt_precio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_precioKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(247, 247, 247)
                        .addComponent(btn_guardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(166, 166, 166)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(44, 44, 44)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_precio, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_precio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_guardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        tabla_ruta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "nombre", "precio"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tabla_ruta);
        if (tabla_ruta.getColumnModel().getColumnCount() > 0) {
            tabla_ruta.getColumnModel().getColumn(0).setResizable(false);
            tabla_ruta.getColumnModel().getColumn(0).setPreferredWidth(20);
            tabla_ruta.getColumnModel().getColumn(1).setPreferredWidth(100);
            tabla_ruta.getColumnModel().getColumn(2).setPreferredWidth(30);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelTask1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(labelTask1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_guardarActionPerformed
       if(validad())        
        if(guardar()){
            JOptionPane.showMessageDialog(null, "Registro Guardado Exitosamente","Exito",JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarTablaDestinos();
            txt_nombre.requestFocus();
        }else{
            JOptionPane.showMessageDialog(null, "Ocurrio un Error en el Guardado","ERROR", JOptionPane.ERROR_MESSAGE);
        }
       
    }//GEN-LAST:event_btn_guardarActionPerformed

    private void txt_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nombreKeyTyped
      if(evt.getKeyChar()==KeyEvent.VK_ENTER){
          txt_precio.requestFocus();
      }
    }//GEN-LAST:event_txt_nombreKeyTyped

    private void txt_precioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_precioKeyTyped
        if(evt.getKeyChar()==KeyEvent.VK_ENTER){
         if(validad())        
        if(guardar()){
            JOptionPane.showMessageDialog(null, "Registro Guardado Exitosamente","Exito",JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarTablaDestinos();
            txt_nombre.requestFocus();
        }else{
            JOptionPane.showMessageDialog(null, "Ocurrio un Error en el Guardado","ERROR", JOptionPane.ERROR_MESSAGE);
        }
      }
    }//GEN-LAST:event_txt_precioKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Transporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Transporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Transporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Transporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Transporte().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.edisoncor.gui.button.ButtonAction btn_guardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private org.edisoncor.gui.label.LabelTask labelTask1;
    private javax.swing.JTable tabla_ruta;
    private javax.swing.JTextField txt_nombre;
    private javax.swing.JFormattedTextField txt_precio;
    // End of variables declaration//GEN-END:variables
}
