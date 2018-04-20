/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control.de.transporte;

import Clases.ConexionBD;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
public class Empresa extends javax.swing.JFrame {

    /**
     * Creates new form Empresa
     */
    DefaultTableModel modelotablaempresa;
    public Empresa() {
        initComponents();
        txt_rif.requestFocus();
         this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        modelotablaempresa=(DefaultTableModel) tabla_empresa.getModel();
        cargarTablaEmpresa();
         anadeListenerAlModelo(tabla_empresa);
        poputTable();
        
    }
    private void poputTable(){
        JPopupMenu popupMenu =new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Eliminar",new ImageIcon(getClass().getResource("/Icono/Borrar3_opt.png")));
        menuItem1.addActionListener((ActionEvent ae) -> {
             eliminarempresa();
        });
        JMenuItem menuItem2=new JMenuItem("Actualizar",new ImageIcon(getClass().getResource("/Icono/refresh_256_opt.png")));
      menuItem2.addActionListener((ActionEvent ae) -> {
          cargarTablaEmpresa ();
        });
       
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
       
        tabla_empresa.setComponentPopupMenu(popupMenu);
        jPanel1.setComponentPopupMenu(popupMenu);
    }
    
    
     private void eliminarempresa(){
     int fsel = this.tabla_empresa.getSelectedRow();
        if (fsel == -1)
        {
            JOptionPane.showMessageDialog(null, "Debe selecionar la Empresa a Eliminar", "Advertencia", 0);
        }
        else
        {
            
            int resp = JOptionPane.showConfirmDialog(null, "Estas Seguro de Eliminar este Empresa", "Eliminar", 0);
            if (resp == 0)
            {
                ConexionBD con=new ConexionBD();
                Connection conexion;
                try {
                    conexion = con.obtConexion();
                    Statement st=conexion.createStatement();
                    String sql="DELETE from p.empresa where id='"+tabla_empresa.getValueAt(tabla_empresa.getSelectedRow(), 0).toString()+"'";
                    st.executeUpdate(sql);
                    JOptionPane.showMessageDialog(null, "Eliminado Exitosamente","ELIMINADO", JOptionPane.INFORMATION_MESSAGE);
                    this.modelotablaempresa = ((DefaultTableModel)this.tabla_empresa.getModel());
                this.modelotablaempresa.removeRow(fsel);
                st.close(); 
                conexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
            String id, rif,nombre,direccion;
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
            if (columna == 0) {
                return;
            }
            try {
                rif = modelo.getValueAt(fila, 1).toString();
                nombre=modelo.getValueAt(fila, 2).toString();
                direccion=modelo.getValueAt(fila, 3).toString();
               actualizar(id,rif,nombre,direccion); 
            } catch (NullPointerException e) {
                // La celda que ha cambiado esta vacia.
            }
        }

    }
     
     private boolean actualizar(String id,String rif,String nombre, String direccion){
        boolean resul=false;
        if(!rif.isEmpty()&&!nombre.isEmpty()&&!direccion.isEmpty()){
        ConexionBD con=new ConexionBD();
        try {
            Connection conexion=con.obtConexion();
            Statement st=conexion.createStatement();
            String SQL ="UPDATE p.empresa set rif = '"+rif+"',nombre='" + nombre + "',direccion='" + direccion + "'where id='" + id + "'";
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
     
    private boolean validad(){
        boolean resul = true;
        if(txt_rif.getText().isEmpty()){
            resul = false;
         JOptionPane.showMessageDialog(null, "El campo rif no puede ser vacio", "Error", JOptionPane.ERROR_MESSAGE);
        }else if (txt_nombre.getText().isEmpty()){
            resul = false;
            JOptionPane.showMessageDialog(null, "El campo nombre no puede ser vacio", "Error", JOptionPane.ERROR_MESSAGE);
        }else if (txt_direccion.getText().isEmpty()){
            resul = false;
            JOptionPane.showMessageDialog(null, "El campo direccion no puede ser vacio", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return resul;
    }
    
    private void limpiar(){
        txt_rif.setText(null);
        txt_nombre.setText(null);
        txt_direccion.setText(null);
    }
    
    private boolean guardar(){
        boolean save = false;
        ConexionBD con = new ConexionBD();
        try {
            Connection conexion = con.obtConexion();
            Statement st = conexion.createStatement();
            String sql = " INSERT INTO p.empresa (rif,nombre,direccion) VALUES ("
                    + "'"+cb_rif.getSelectedItem().toString()+txt_rif.getText()+"','"+txt_nombre.getText()+"','"+txt_direccion.getText()+"')";
            st.executeUpdate(sql);
            st.close();
            conexion.close();
            save = true;
        } catch (SQLException e) {
        }
        return save;
    }
     private static void reiniciarJTable(javax.swing.JTable Tabla){
        DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
        while(modelo.getRowCount()>0)modelo.removeRow(0);
    }   
    
  private void cargarTablaEmpresa (){
     String sql;
     ConexionBD con =new ConexionBD();
     reiniciarJTable(tabla_empresa);
     modelotablaempresa.setRowCount(0);
     Connection conexion;
        try {
            conexion = con.obtConexion();
            sql= "select id,rif,nombre,direccion from p.empresa";
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
        modelotablaempresa.addRow(filas);
         tabla_empresa.setModel(modelotablaempresa);
     }
     rset.close();
     conexion.close();   
        } catch (SQLException ex) {
            Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
        }
     
 }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        labelTask1 = new org.edisoncor.gui.label.LabelTask();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_empresa = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btn_guardar = new org.edisoncor.gui.button.ButtonAction();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_nombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_direccion = new javax.swing.JTextArea();
        cb_rif = new javax.swing.JComboBox<>();
        txt_rif = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        labelTask1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTask1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/camionero_bolsa_trabajo_opt.png"))); // NOI18N
        labelTask1.setText("EMPRESA");
        labelTask1.setCategoryFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        labelTask1.setDescription("Servicio");

        tabla_empresa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Rif", "nombre", "Direccion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tabla_empresa);
        if (tabla_empresa.getColumnModel().getColumnCount() > 0) {
            tabla_empresa.getColumnModel().getColumn(0).setMinWidth(30);
            tabla_empresa.getColumnModel().getColumn(0).setPreferredWidth(30);
            tabla_empresa.getColumnModel().getColumn(0).setMaxWidth(30);
            tabla_empresa.getColumnModel().getColumn(1).setMinWidth(80);
            tabla_empresa.getColumnModel().getColumn(1).setPreferredWidth(80);
            tabla_empresa.getColumnModel().getColumn(1).setMaxWidth(80);
        }

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153), 2), "Informacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_guardar.setText("Guardar");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });
        jPanel2.add(btn_guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 160, -1, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Nombre:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Rif:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 30, -1));

        txt_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nombreKeyTyped(evt);
            }
        });
        jPanel2.add(txt_nombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 60, 260, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Direccion:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 77, -1));

        txt_direccion.setColumns(20);
        txt_direccion.setRows(5);
        jScrollPane1.setViewportView(txt_direccion);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 260, 100));

        cb_rif.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cb_rif.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "J-", "V-" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelTask1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(190, 190, 190)
                        .addComponent(cb_rif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_rif, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(24, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(labelTask1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cb_rif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_rif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(70, 70, 70)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(183, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_guardarActionPerformed
        if(validad())
        if(guardar()){
            JOptionPane.showMessageDialog(null, "Registro Guardado Exitosamente","Exito",JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarTablaEmpresa();
            txt_rif.requestFocus();
        }else{
            JOptionPane.showMessageDialog(null, "Ocurrio un Error en el Guardado","ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_guardarActionPerformed

    private void txt_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nombreKeyTyped

    }//GEN-LAST:event_txt_nombreKeyTyped

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
            java.util.logging.Logger.getLogger(Empresa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Empresa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Empresa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Empresa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Empresa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.edisoncor.gui.button.ButtonAction btn_guardar;
    private javax.swing.JComboBox<String> cb_rif;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.edisoncor.gui.label.LabelTask labelTask1;
    private javax.swing.JTable tabla_empresa;
    private javax.swing.JTextArea txt_direccion;
    private javax.swing.JTextField txt_nombre;
    private javax.swing.JFormattedTextField txt_rif;
    // End of variables declaration//GEN-END:variables
}
