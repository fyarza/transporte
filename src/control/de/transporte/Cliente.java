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
public class Cliente extends javax.swing.JFrame {

    DefaultTableModel modelotablacliente;
    public Cliente() {
        initComponents();
        modelotablacliente=(DefaultTableModel) tabla_cliente.getModel();
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cargarTablaDestinos();
        anadeListenerAlModelo(tabla_cliente);
        poputTable();
        cargardirecciones();
    }
    
    private void cargardirecciones(){
        ConexionBD con = new ConexionBD();
        Connection conexion;
        try {
            conexion = con.obtConexion();
            Statement st = conexion.createStatement();
            String sql = "Select concat(id,'-',nombre) as nombre from p.ruta";
            ResultSet rs=st.executeQuery(sql);
            cb_direcciones.removeAllItems();
            while(rs.next()){
                cb_direcciones.addItem(rs.getString("nombre"));
            }
            st.close();
            rs.close();
            conexion.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
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
            String id,nombre,direccion;
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
                nombre=modelo.getValueAt(fila, 1).toString();
                direccion=modelo.getValueAt(fila, 2).toString();
               actualizar(id,nombre,direccion); 
            } catch (NullPointerException e) {
                // La celda que ha cambiado esta vacia.
            }
        }

    }
    private void eliminarruta(){
     int fsel = this.tabla_cliente.getSelectedRow();
        if (fsel == -1)
        {
            JOptionPane.showMessageDialog(null, "Debe selecionar el Cliente a Eliminar", "Advertencia", 0);
        }
        else
        {
            
            int resp = JOptionPane.showConfirmDialog(null, "Estas Seguro de Eliminar este cliente", "Eliminar", 0);
            if (resp == 0)
            {
                ConexionBD con=new ConexionBD();
                Connection conexion;
                try {
                    conexion = con.obtConexion();
                    Statement st=conexion.createStatement();
                    String sql="DELETE from p.cliente where id='"+tabla_cliente.getValueAt(tabla_cliente.getSelectedRow(), 0).toString()+"'";
                    st.executeUpdate(sql);
                    JOptionPane.showMessageDialog(null, "Eliminado Exitosamente","ELIMINADO", JOptionPane.INFORMATION_MESSAGE);
                    this.modelotablacliente = ((DefaultTableModel)this.tabla_cliente.getModel());
                this.modelotablacliente.removeRow(fsel);
                st.close(); 
                conexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private boolean actualizar(String id,String nombre, String direccion){
        boolean resul=false;
        if(!nombre.isEmpty()&&!direccion.isEmpty()){
        ConexionBD con=new ConexionBD();
        try {
            Connection conexion=con.obtConexion();
            Statement st=conexion.createStatement();
            String SQL ="UPDATE p.cliente set nombre='" + nombre + "',direccion='" + direccion + "'where id='" + id + "'";
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
        JMenuItem menuItem3 = new JMenuItem("Actualizar ruta", new ImageIcon(getClass().getResource("/Icono/refresh_256_opt.png")));
        menuItem3.addActionListener((ActionEvent ae)->{
            cargardirecciones();
        });
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        popupMenu.add(menuItem3);
        tabla_cliente.setComponentPopupMenu(popupMenu);
        jPanel1.setComponentPopupMenu(popupMenu);
    }
    
    private boolean guardar(){
        boolean resul=false;
        ConexionBD con= new ConexionBD();
        try {
            Connection conexion=con.obtConexion();
            Statement st=conexion.createStatement();
            String[] arrayruta = cb_direcciones.getSelectedItem().toString().split("-");
            String Sql="insert into p.cliente (nombre,direccion) values ('"+txt_nombre.getText()+"','"+arrayruta[0]+"')";
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
     reiniciarJTable(tabla_cliente);
     modelotablacliente.setRowCount(0);
     Connection conexion;
        try {
            conexion = con.obtConexion();
            sql= "select a.id,a.nombre,b.nombre from p.ruta b left join p.cliente a on (b.id=a.direccion)";
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
        modelotablacliente.addRow(filas);
         tabla_cliente.setModel(modelotablacliente);
     }
     rset.close();
     conexion.close();   
        } catch (SQLException ex) {
            Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
        }
     
 }
    private void limpiar(){
        txt_nombre.setText(null);
        
    }
    private boolean validad(){
        boolean resul=true;
        if(txt_nombre.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Campo Nombre Vacio", "Error", JOptionPane.ERROR_MESSAGE);
            resul=false;
            txt_nombre.requestFocus();
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

        jPanel1 = new javax.swing.JPanel();
        labelTask1 = new org.edisoncor.gui.label.LabelTask();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_cliente = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btn_guardar = new org.edisoncor.gui.button.ButtonAction();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cb_direcciones = new javax.swing.JComboBox<>();
        txt_nombre = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        labelTask1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTask1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/camionero_bolsa_trabajo_opt.png"))); // NOI18N
        labelTask1.setText("Cliente");
        labelTask1.setCategoryFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        labelTask1.setDescription("Servicio");

        tabla_cliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "nombre", "Direccion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tabla_cliente);
        if (tabla_cliente.getColumnModel().getColumnCount() > 0) {
            tabla_cliente.getColumnModel().getColumn(0).setMinWidth(50);
            tabla_cliente.getColumnModel().getColumn(0).setPreferredWidth(50);
            tabla_cliente.getColumnModel().getColumn(0).setMaxWidth(50);
            tabla_cliente.getColumnModel().getColumn(1).setMinWidth(200);
            tabla_cliente.getColumnModel().getColumn(1).setPreferredWidth(200);
            tabla_cliente.getColumnModel().getColumn(1).setMaxWidth(200);
            tabla_cliente.getColumnModel().getColumn(2).setResizable(false);
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
        jPanel2.add(btn_guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 120, -1, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Nombre:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Direccion:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 77, -1));

        jPanel2.add(cb_direcciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 70, 260, -1));

        txt_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nombreKeyTyped(evt);
            }
        });
        jPanel2.add(txt_nombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 260, -1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelTask1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 568, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 568, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(21, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(labelTask1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(70, 70, 70)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(181, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cliente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.edisoncor.gui.button.ButtonAction btn_guardar;
    private javax.swing.JComboBox<String> cb_direcciones;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private org.edisoncor.gui.label.LabelTask labelTask1;
    private javax.swing.JTable tabla_cliente;
    private javax.swing.JTextField txt_nombre;
    // End of variables declaration//GEN-END:variables
}
