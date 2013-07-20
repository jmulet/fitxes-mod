/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.dialogs;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.iesapp.clients.iesdigital.actuacions.Actuacio;
import org.iesapp.clients.iesdigital.actuacions.IFormulari;
import org.iesapp.framework.actuacions.BasicForm;
import org.iesapp.modules.fitxes.DesktopManager;
import org.iesapp.modules.fitxescore.util.Cfg;

/**
 *
 * @author Josep
 */
public class PreDocManager {
    
    private final int id_actuacio;
    private BasicForm basicForm1=null;
    public final Actuacio actuacio;
    
    public PreDocManager(Actuacio actuacio, final Cfg cfg)
    {
        this.id_actuacio =  actuacio.id_actuacio;
        this.actuacio = actuacio;
        //Carrega dinamicament el basicForm1ulari custom
        if(actuacio.beanRule.getClassName()!=null)
        {
            try {        
            Class<?> loadClass = Class.forName(actuacio.beanRule.getClassName());
            try {
                Object obj = loadClass.newInstance();
                basicForm1 = (BasicForm) obj;                
                basicForm1.setActuacio(actuacio);
              
            } catch (InstantiationException ex) {
                Logger.getLogger(PreDocManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(PreDocManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            } catch (ClassNotFoundException ex) {
               // Logger.getLogger(PreDocManager.class.getName()).log(Level.SEVERE, null, ex);
                Object[] options = {"D'acord"};
                String missatge = "No es troba la classe de formulari "+actuacio.beanRule.getClassName()+".\nAssegureu-vos que es troba dins el classpath.";

                int n = JOptionPane.showOptionDialog(null,
                missatge, "Error",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);
            }
        }  
        else
        {
            //Utilitza el basicFormulari BASIC
              basicForm1 = new BasicForm(cfg.getCoreCfg()) {};      
              basicForm1.setActuacio(actuacio);
        }
           
    }
    
   public void dispose()
    {
        basicForm1.dispose();
    }
  
   public void show() {
       if(basicForm1==null) {
           return;
       }
       DesktopManager.showFormulari(id_actuacio, basicForm1);
    }

    public HashMap getMap() {
        
        return basicForm1.getMap();

    }

    public byte getExitCode()
    {
        return basicForm1.getExitCode();
    }

    public boolean isLimitador() {
        return basicForm1.isLimitador();
    }
    
    public IFormulari getFormulari()
    {
        return basicForm1;
    }
}
