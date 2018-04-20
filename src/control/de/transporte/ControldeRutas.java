/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control.de.transporte;

import Clases.ConexionBD;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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


public class ControldeRutas extends javax.swing.JFrame {

    private final double costo;
    private double costo_adicional;
     DefaultTableModel modelotablarealiza;
      private NumberFormat totalFormat;
       private double total;
  private int hora;
    public ControldeRutas() {
        initComponents();
        modelotablarealiza=(DefaultTableModel) tabla_realiza.getModel();
         this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         cargarclientes();
         cargardirecciones();
         costo=0.0;
         hora=0;
         costo_adicional=0.0;
         total=0.0;
         entrada();
         cargarTablaRealiza ();
         poputTable();  
         formato();
         
    }
    
    private void calculototal(){
        ConexionBD con = new ConexionBD();
        try {
            Connection conexion = con.obtConexion();
            Statement st = conexion.createStatement();
            String sql="select SUM(a.precio)+ SUM(b.costo_adicional)+ SUM(hora_espera) "
                    + "as total from p.realiza b left join p.ruta a on (b.destino=a.id) where b.factura = 'No Facturado'";
            
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                total=rs.getDouble("total");
            }
            //JOptionPane.showMessageDialog(null, total);
        } catch (SQLException ex) {
            Logger.getLogger(ControldeRutas.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
        
    
    private void formato(){
        totalFormat= NumberFormat.getCurrencyInstance();
        txt_totalpagar.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(totalFormat)));
        txt_totalpagar.setValue(total);
        txt_totalpagar.setColumns(10);
        txt_totalpagar.setEditable(false);
        txt_totalpagar.setForeground(Color.red);
 
    }
    
    
    private void eliminarruta(){
     int fsel = this.tabla_realiza.getSelectedRow();
        if (fsel == -1)
        {
            JOptionPane.showMessageDialog(null, "Debe selecionar la Ruta a Eliminar", "Advertencia", 0);
        }
        else
        {
            
            int resp = JOptionPane.showConfirmDialog(null, "Estas Seguro de Eliminar esta Ruta", "Eliminar", 0);
            if (resp == 0)
            {
                ConexionBD con=new ConexionBD();
                Connection conexion;
                try {
                    conexion = con.obtConexion();
                    Statement st=conexion.createStatement();
                    String sql="DELETE from p.realiza where id='"+tabla_realiza.getValueAt(tabla_realiza.getSelectedRow(), 0).toString()+"'";
                    st.executeUpdate(sql);
                    JOptionPane.showMessageDialog(null, "Eliminado Exitosamente","ELIMINADO", JOptionPane.INFORMATION_MESSAGE);
                    this.modelotablarealiza = ((DefaultTableModel)this.tabla_realiza.getModel());
                this.modelotablarealiza.removeRow(fsel);
                st.close(); 
                conexion.close();
                 entrada();
                } catch (SQLException ex) {
                    Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
      private void poputTable(){
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
          cargarTablaRealiza();
        });
       
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        tabla_realiza.setComponentPopupMenu(popupMenu);
        
    }
    
     
    
    
    
    private void limpiar(){
        txt_costo_primario.setValue(0.0);
        txt_costo_adicional.setValue(0.0);
        txt_motivo.setText(null);
        HH.setValue(7);
        MM.setValue(0);
        HH_E.setValue(0);
        MM_E.setValue(0);
    }
    
    
    private static void reiniciarJTable(javax.swing.JTable Tabla){
        DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
        while(modelo.getRowCount()>0)modelo.removeRow(0);
    }   
    
    private void cargarTablaRealiza (){
     String sql;
     ConexionBD con =new ConexionBD();
     reiniciarJTable(tabla_realiza);
     modelotablarealiza.setRowCount(0);
     Connection conexion;
        try {
            conexion = con.obtConexion();
            /*sql= "select realiza.id,cliente.nombre,realiza.fecha,ruta.destino,realiza.hora_espera,realiza.costo_adicional,realiza.motivo,ruta.precio"
                    + " from p.realiza realiza left join p.cliente cliente on(realiza.cliente = cliente.id)"
                    + "left join p.ruta ruta on (realiza.destino = ruta.id) where realiza.factura = 'No Facturado' order by realiza.fecha DESC";
            //JOptionPane.showMessageDialog(null, sql);*/
            sql="select realiza.id,cliente.nombre,realiza.fecha,\n" +
                "concat(ruta.origen,'-',ruta.destino) as destino,\n" +
                "realiza.hora_espera,realiza.costo_adicional,\n" +
                "realiza.motivo,ruta.precio \n" +
                "from p.realiza realiza \n" +
                "inner join p.ruta on (realiza.destino = ruta.id) \n" +
                "inner join p.cliente cliente on (realiza.cliente = cliente.id)\n" +
               "where realiza.factura = 'No Facturado' "+
                     "order by realiza.id DESC";
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
        modelotablarealiza.addRow(filas);
         tabla_realiza.setModel(modelotablarealiza);
     }
     rset.close();
     conexion.close();   
        } catch (SQLException ex) {
            Logger.getLogger(Transporte.class.getName()).log(Level.SEVERE, null, ex);
        }
     
 }
    
    
    
    private String obt_fecha(com.toedter.calendar.JDateChooser fecha){
        /*jfnac es de tipo JDateChooser
        fnacim es de tipo JTextFiel donde muestro la fecha que seleccione
        */
        String fnacim="";
        try {
        String formato = fecha.getDateFormatString();
        java.util.Date date = fecha.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        fnacim = String.valueOf(sdf.format(date));
        
        } catch (Exception e) {
           // JOptionPane.showMessageDialog(null, "Al menos elija una FECHA DE NACIMIENTO VALIDA ", "Error..!!", JOptionPane.ERROR_MESSAGE);
        }
        return fnacim;
    }
    
    private void entrada(){
       txt_costo_primario.setValue(costo);
       txt_costo_adicional.setValue(costo_adicional);
       calculototal();
       txt_totalpagar.setValue(total);
    }
    
    
    
     private void cargarclientes(){
        ConexionBD con = new ConexionBD();
        Connection conexion;
        try {
            conexion = con.obtConexion();
            Statement st = conexion.createStatement();
            String sql = "Select concat(id,'-',nombre) as nombre from p.cliente";
            ResultSet rs=st.executeQuery(sql);
            cb_clientes.removeAllItems();
            while(rs.next()){
                cb_clientes.addItem(rs.getString("nombre"));
            }
            st.close();
            rs.close();
            conexion.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
     
     
     
      private void cargardirecciones(){
        ConexionBD con = new ConexionBD();
        Connection conexion;
        try {
            conexion = con.obtConexion();
            Statement st = conexion.createStatement();
            String sql = "Select concat(a.id,'-',a.origen,'-',a.destino,'-',b.rif) as nombre from p.ruta a left join p.empresa b on (a.empresa=b.id)";
            //String sql = "Select concat(id,'-',origen,'-',destino) as nombre from p.ruta";
            ResultSet rs=st.executeQuery(sql);
            cb_destinos.removeAllItems();
            while(rs.next()){
                cb_destinos.addItem(rs.getString("nombre"));
            }
            st.close();
            rs.close();
            conexion.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
      
      private void guardarrelacion(){
          ConexionBD con  = new ConexionBD();
      try {
          Connection conexion = con.obtConexion();
          Statement st = conexion.createStatement();
          String[] arraydestino = cb_destinos.getSelectedItem().toString().split("-");
          String[] arraycliente = cb_clientes.getSelectedItem().toString().split("-");
          String Sql="INSERT INTO p.realiza (fecha,hora,destino,cliente,costo_adicional,tiempo_espera,hora_espera,motivo)"
                  + " values ('"+obt_fecha(txt_fecha)+"','"+HH.getValue()+":"+MM.getValue()+" "+TT.getSelectedItem().toString()+"',"
                  + "'"+arraydestino[0]+"','"+arraycliente[0]+"','"+txt_costo_adicional.getValue()+"','"+HH_E.getValue()+":"+MM_E.getValue()+"',"
                  + "'"+txt_costo_primario.getValue()+"','"+txt_motivo.getText()+"')";
          //JOptionPane.showMessageDialog(null, Sql);
          st.executeUpdate(Sql);
          JOptionPane.showMessageDialog(null, "Registro Guardado Exitosamente", "Guardado...", JOptionPane.INFORMATION_MESSAGE);
          st.close();
          conexion.close();
      } catch (SQLException ex) {
        JOptionPane.showMessageDialog (null, "Error:"+ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
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

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        labelTask1 = new org.edisoncor.gui.label.LabelTask();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cb_clientes = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cb_destinos = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txt_fecha = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lb_costo = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        HH_E = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        MM_E = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        HH = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        MM = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        TT = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        txt_costo_adicional = new javax.swing.JFormattedTextField();
        txt_costo_primario = new javax.swing.JFormattedTextField();
        btn_guardar = new org.edisoncor.gui.button.ButtonAction();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_motivo = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_realiza = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        txt_totalpagar = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        labelTask1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTask1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/camionero_bolsa_trabajo_opt.png"))); // NOI18N
        labelTask1.setText("Cliente");
        labelTask1.setCategoryFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        labelTask1.setDescription("Rutas Diarias");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Informacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Cliente: ");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 388, 21));

        jPanel3.add(cb_clientes, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 0, 388, 21));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Destino:");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 36, 388, 21));

        jPanel3.add(cb_destinos, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 36, 388, 21));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Fecha:");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 72, 388, 21));

        txt_fecha.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jPanel3.add(txt_fecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 72, 388, 21));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Hora:");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 108, 388, 21));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Tiempo de Espera:");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 144, 120, 21));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Costo de Tiempo:");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 388, 21));

        lb_costo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lb_costo.setText(" ");
        jPanel3.add(lb_costo, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 180, 388, 21));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Motivo:");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 250, 21));

        HH_E.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        HH_E.setModel(new javax.swing.SpinnerNumberModel(0, 0, 24, 1));
        HH_E.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                HH_EStateChanged(evt);
            }
        });
        jPanel3.add(HH_E, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 150, 70, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("HH");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 150, -1, 20));

        MM_E.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        MM_E.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        jPanel3.add(MM_E, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 150, 60, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("MM");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 150, -1, 20));

        HH.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        HH.setModel(new javax.swing.SpinnerNumberModel(7, 0, 24, 1));
        jPanel3.add(HH, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 110, 70, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("HH");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 110, -1, 20));

        MM.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        MM.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        jPanel3.add(MM, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 110, 60, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("MM");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 110, -1, 20));

        TT.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        TT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PM", "AM" }));
        jPanel3.add(TT, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 110, -1, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Costo:");
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 150, 50, 20));

        txt_costo_adicional.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txt_costo_adicional.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_costo_adicionalKeyTyped(evt);
            }
        });
        jPanel3.add(txt_costo_adicional, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 220, 390, -1));

        txt_costo_primario.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txt_costo_primario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_costo_primarioKeyTyped(evt);
            }
        });
        jPanel3.add(txt_costo_primario, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 150, 120, -1));

        btn_guardar.setText("Guardar");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });
        jPanel3.add(btn_guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 290, -1, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("Costo Adicional:");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 216, 388, 21));

        txt_motivo.setColumns(20);
        txt_motivo.setRows(5);
        jScrollPane2.setViewportView(txt_motivo);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 250, 390, 90));

        tabla_realiza.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Cliente", "Fecha", "Destino", "H/Espera", "Costo A", "Motivo", "Precio"
            }
        ));
        jScrollPane1.setViewportView(tabla_realiza);
        if (tabla_realiza.getColumnModel().getColumnCount() > 0) {
            tabla_realiza.getColumnModel().getColumn(0).setMinWidth(40);
            tabla_realiza.getColumnModel().getColumn(0).setPreferredWidth(40);
            tabla_realiza.getColumnModel().getColumn(0).setMaxWidth(40);
            tabla_realiza.getColumnModel().getColumn(3).setMinWidth(200);
            tabla_realiza.getColumnModel().getColumn(3).setPreferredWidth(200);
            tabla_realiza.getColumnModel().getColumn(3).setMaxWidth(200);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 942, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText("SALDO:");

        txt_totalpagar.setBackground(new java.awt.Color(0, 0, 0));
        txt_totalpagar.setForeground(java.awt.Color.red);
        txt_totalpagar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_totalpagar.setDisabledTextColor(new java.awt.Color(255, 0, 0));
        txt_totalpagar.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("BsF.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(labelTask1, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_totalpagar, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelTask1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(jLabel16))
                        .addComponent(txt_totalpagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
        /*if(validad())
        if(guardar()){
            JOptionPane.showMessageDialog(null, "Registro Guardado Exitosamente","Exito",JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarTablaDestinos();
            txt_nombre.requestFocus();
        }else{
            JOptionPane.showMessageDialog(null, "Ocurrio un Error en el Guardado","ERROR", JOptionPane.ERROR_MESSAGE);
        }*/
        guardarrelacion();
        cargarTablaRealiza ();
        limpiar();
        entrada();
    }//GEN-LAST:event_btn_guardarActionPerformed

    private void txt_costo_adicionalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_costo_adicionalKeyTyped
        /*if(evt.getKeyChar()==KeyEvent.VK_ENTER){
            if(validad()) {
                boolean tipo=false;
                if(cb_tipo.getSelectedItem().toString().equals("Externo")){
                    tipo=true;
                }
                if(guardar(tipo)){
                    JOptionPane.showMessageDialog(null, "Registro Guardado Exitosamente","Exito",JOptionPane.INFORMATION_MESSAGE);
                    limpiar();
                    cargarTablaDestinos();
                    txt_destino.requestFocus();
                    entrada();
                }else{
                    JOptionPane.showMessageDialog(null, "Ocurrio un Error en el Guardado","ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }*/
    }//GEN-LAST:event_txt_costo_adicionalKeyTyped

    private void txt_costo_primarioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_costo_primarioKeyTyped
       /* if(evt.getKeyChar()==KeyEvent.VK_ENTER){
            if(validad()) {
                boolean tipo=false;
                if(cb_tipo.getSelectedItem().toString().equals("Externo")){
                    tipo=true;
                }
                if(guardar(tipo)){
                    JOptionPane.showMessageDialog(null, "Registro Guardado Exitosamente","Exito",JOptionPane.INFORMATION_MESSAGE);
                    limpiar();
                    cargarTablaDestinos();
                    txt_destino.requestFocus();
                    entrada();
                }else{
                    JOptionPane.showMessageDialog(null, "Ocurrio un Error en el Guardado","ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }*/
    }//GEN-LAST:event_txt_costo_primarioKeyTyped

    private void HH_EStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_HH_EStateChanged
        hora=(int) HH_E.getValue();
        hora=hora*60;
        JOptionPane.showMessageDialog(null,hora);
    }//GEN-LAST:event_HH_EStateChanged

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
            java.util.logging.Logger.getLogger(ControldeRutas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ControldeRutas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ControldeRutas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ControldeRutas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControldeRutas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner HH;
    private javax.swing.JSpinner HH_E;
    private javax.swing.JSpinner MM;
    private javax.swing.JSpinner MM_E;
    private javax.swing.JComboBox<String> TT;
    private org.edisoncor.gui.button.ButtonAction btn_guardar;
    private javax.swing.JComboBox<String> cb_clientes;
    private javax.swing.JComboBox<String> cb_destinos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.edisoncor.gui.label.LabelTask labelTask1;
    private javax.swing.JLabel lb_costo;
    private javax.swing.JTable tabla_realiza;
    private javax.swing.JFormattedTextField txt_costo_adicional;
    private javax.swing.JFormattedTextField txt_costo_primario;
    private com.toedter.calendar.JDateChooser txt_fecha;
    private javax.swing.JTextArea txt_motivo;
    private javax.swing.JFormattedTextField txt_totalpagar;
    // End of variables declaration//GEN-END:variables
}
