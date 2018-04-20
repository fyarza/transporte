
package instancia;

import control.de.transporte.Empresa;
import java.awt.Toolkit;
import javax.swing.JFrame;


public class InstanciaEmpresa {
     private static JFrame jf;

    public InstanciaEmpresa() {
    }
    private static void ubicacionVentana(JFrame jf,int tamanox,int tamanoy) { 
            int tamanioX = jf.getWidth(); 
            int tamanioY = jf.getHeight(); 
            int maxX = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(); 
            int maxY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(); 
             System.out.println(maxX+" "+maxY);
             System.out.println(tamanioX+" "+tamanioY);
            // ubicacion de la ventana 
            //JOptionPane.showMessageDialog(this, "Este el Valor de maxX= "+ maxY+"\n"+" Este es el valor maxY= "+ maxY+"\n"+"tamanio X= "+tamanioX+"\n"+"tamanio Y= "+tamanioY);
            jf.setLocation((maxX - tamanioX)-tamanox/2, ((maxY - tamanioY)-tamanoy)/2); 
        }
    
    public static JFrame getInstance(int tamanox,int tamanoy){
        
        if(jf==null){
            jf=new Empresa();
          InstanciaEmpresa.ubicacionVentana(jf,tamanox,tamanoy);
       }
            
        return jf;
    }
}
