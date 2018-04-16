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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
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
         poputTable();
         total1=0.0;
        txt_precio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(totalFormat)));
        anadeListenerAlModelo(tabla_ruta);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
       entrada();
       cargarempresas();
       if( cb_empresa.getItemCount() > 0){
          String[] arrayempresa = cb_empresa.getSelectedItem().toString().split("-");
            cargarTablaDestinos (arrayempresa[0]);
           }
    }
    
    private void entrada(){
         txt_origen.setText("Omega C.A");
        txt_origen.setEnabled(false);
    }
    
    private void cargarempresas(){
        ConexionBD con = new ConexionBD();
        try {
            Connection conexion = con.obtConexion();
            Statement st = conexion.createStatement();
            String sql = "SELECT concat(id,'-',nombre) as nombre FROM p.empresa";
            ResultSet rs = st.executeQuery(sql);
            cb_empresa.removeAllItems();
            while(rs.next()){
                cb_empresa.addItem(rs.getString("nombre"));
            }
            rs.close();
            st.close();
            conexion.close();
        } catch (SQLException ex) {
            Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            String id,origen, destino,precio,tipo;
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
            if ( columna == 0) {
                return;
            }
            try {
                origen=modelo.getValueAt(fila, 1).toString();
                destino=modelo.getValueAt(fila, 2).toString();
                precio=modelo.getValueAt(fila, 3).toString();
                tipo=modelo.getValueAt(fila, 4).toString();
                
                
               actualizar(id,origen,destino,precio,tipo); 
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
    
    private boolean actualizar(String id,String origen,String destino, String precio,String tipo){
        boolean resul=false;
        if(!origen.isEmpty()&&!precio.isEmpty()&&!destino.isEmpty()){
        ConexionBD con=new ConexionBD();
        try {
            Connection conexion=con.obtConexion();
            Statement st=conexion.createStatement();
            String SQL ="UPDATE p.ruta set origen='"+origen+"',destino='" + destino + "',precio='" + precio + "',tipo='"+tipo+"'where id='" + id + "'";
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
           if( cb_empresa.getItemCount() > 0){
          String[] arrayempresa = cb_empresa.getSelectedItem().toString().split("-");
            cargarTablaDestinos (arrayempresa[0]);
           }
          cargarempresas();
        });
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        tabla_ruta.setComponentPopupMenu(popupMenu);
    }
    
    private boolean guardar(boolean tipo){
        boolean resul=false;
        ConexionBD con= new ConexionBD();
        try {
            Connection conexion=con.obtConexion();
            Statement st=conexion.createStatement();
            String Sql;
            String[] arrayempresa = cb_empresa.getSelectedItem().toString().split("-");
            if (!tipo){
            Sql="insert into p.ruta (destino,precio,tipo,empresa) values ("
                    + "'"+txt_destino.getText()+"',"
                    + "'"+txt_precio.getText()+"',"
                    + "'"+cb_tipo.getSelectedItem().toString()+"',"
                    + "'"+arrayempresa[0]+"')";
            }else{
             Sql="insert into p.ruta (destino,precio,origen,tipo,empresa) values ("
                     + "'"+txt_destino.getText()+"',"
                     + "'"+txt_precio.getText()+"',"
                     + "'"+txt_origen.getText()+"',"
                     + "'"+cb_tipo.getSelectedItem().toString()+"',"
                     + "'"+arrayempresa[0]+"')";
            }
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
    
    
    private void cargarTablaDestinos (String empresa){
     String sql;
     ConexionBD con =new ConexionBD();
     reiniciarJTable(tabla_ruta);
     modeloruta.setRowCount(0);
     Connection conexion;
        try {
            conexion = con.obtConexion();
            sql= "select id,origen,destino,precio,tipo from p.ruta where empresa = '"+empresa+"'";
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
    private void desbloquear(){
         txt_origen.setText("");
        txt_origen.setEnabled(true);
        txt_origen.requestFocus();
    }
    private void limpiar(){
        txt_destino.setText(null);
        txt_precio.setText(null);
    }
    private boolean validad(){
        boolean resul=true;
        if(txt_origen.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Campo Nombre Vacio", "Error", JOptionPane.ERROR_MESSAGE);
            resul=false;
            txt_origen.requestFocus();
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
        txt_origen = new javax.swing.JTextField();
        txt_precio = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_destino = new javax.swing.JTextField();
        cb_tipo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        buttonTransluceIcon1 = new org.edisoncor.gui.button.ButtonTransluceIcon();
        jLabel5 = new javax.swing.JLabel();
        cb_empresa = new javax.swing.JComboBox<>();
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
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_guardar.setText("Guardar");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });
        jPanel2.add(btn_guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 150, -1, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Empresa:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Precio:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 51, -1));

        txt_origen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_origenKeyTyped(evt);
            }
        });
        jPanel2.add(txt_origen, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, 177, -1));

        txt_precio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txt_precio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_precioKeyTyped(evt);
            }
        });
        jPanel2.add(txt_precio, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 120, 177, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Destino: ");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 80, -1, -1));
        jPanel2.add(txt_destino, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 80, 189, -1));

        cb_tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Regular", "Externo" }));
        jPanel2.add(cb_tipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 110, 189, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Tipo:");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 120, -1, -1));

        buttonTransluceIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icono/Editar3.png"))); // NOI18N
        buttonTransluceIcon1.setText("Cambiar");
        buttonTransluceIcon1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTransluceIcon1ActionPerformed(evt);
            }
        });
        jPanel2.add(buttonTransluceIcon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, 80, 60));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Origen:");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, -1, -1));

        cb_empresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cb_empresa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cb_empresaItemStateChanged(evt);
            }
        });
        jPanel2.add(cb_empresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 270, -1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        tabla_ruta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Origen", "Destino", "Precio", "Tipo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true
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
            tabla_ruta.getColumnModel().getColumn(2).setPreferredWidth(100);
            tabla_ruta.getColumnModel().getColumn(3).setPreferredWidth(30);
            tabla_ruta.getColumnModel().getColumn(4).setMinWidth(80);
            tabla_ruta.getColumnModel().getColumn(4).setPreferredWidth(80);
            tabla_ruta.getColumnModel().getColumn(4).setMaxWidth(80);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelTask1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(labelTask1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
       if(validad()) {
           boolean tipo=false;
           if(cb_tipo.getSelectedItem().toString().equals("Externo")){
            tipo=true;   
           }
            if(guardar(tipo)){
            JOptionPane.showMessageDialog(null, "Registro Guardado Exitosamente","Exito",JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            if( cb_empresa.getItemCount() > 0){
            String[] arrayempresa = cb_empresa.getSelectedItem().toString().split("-");
            cargarTablaDestinos (arrayempresa[0]);
            };
            txt_destino.requestFocus();
            entrada();
            }else{
            JOptionPane.showMessageDialog(null, "Ocurrio un Error en el Guardado","ERROR", JOptionPane.ERROR_MESSAGE);
            }   
       }       
        
       
    }//GEN-LAST:event_btn_guardarActionPerformed

    private void txt_origenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_origenKeyTyped
      if(evt.getKeyChar()==KeyEvent.VK_ENTER){
          txt_precio.requestFocus();
      }
    }//GEN-LAST:event_txt_origenKeyTyped

    private void txt_precioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_precioKeyTyped
        if(evt.getKeyChar()==KeyEvent.VK_ENTER){
        if(validad()) {
           boolean tipo=false;
           if(cb_tipo.getSelectedItem().toString().equals("Externo")){
            tipo=true;   
           }
            if(guardar(tipo)){
            JOptionPane.showMessageDialog(null, "Registro Guardado Exitosamente","Exito",JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            if( cb_empresa.getItemCount() > 0){
                String[] arrayempresa = cb_empresa.getSelectedItem().toString().split("-");
                cargarTablaDestinos (arrayempresa[0]);
             }
            txt_destino.requestFocus();
            entrada();
            }else{
            JOptionPane.showMessageDialog(null, "Ocurrio un Error en el Guardado","ERROR", JOptionPane.ERROR_MESSAGE);
            }   
       }   
      }
    }//GEN-LAST:event_txt_precioKeyTyped

    private void buttonTransluceIcon1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTransluceIcon1ActionPerformed
     desbloquear();
    }//GEN-LAST:event_buttonTransluceIcon1ActionPerformed

    private void cb_empresaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cb_empresaItemStateChanged
         if( cb_empresa.getItemCount() > 0){
                String[] arrayempresa = cb_empresa.getSelectedItem().toString().split("-");
                cargarTablaDestinos (arrayempresa[0]);
             }
    }//GEN-LAST:event_cb_empresaItemStateChanged

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
    private org.edisoncor.gui.button.ButtonTransluceIcon buttonTransluceIcon1;
    private javax.swing.JComboBox<String> cb_empresa;
    private javax.swing.JComboBox<String> cb_tipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private org.edisoncor.gui.label.LabelTask labelTask1;
    private javax.swing.JTable tabla_ruta;
    private javax.swing.JTextField txt_destino;
    private javax.swing.JTextField txt_origen;
    private javax.swing.JFormattedTextField txt_precio;
    // End of variables declaration//GEN-END:variables
}
