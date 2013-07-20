/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import org.iesapp.framework.actuacions.BasicForm;
import org.iesapp.framework.pluggable.TopPluginWindow;
import org.iesapp.framework.pluggable.grantsystem.Profile;
import org.iesapp.modules.fitxes.dialogs.AccionsAlumne4;
import org.iesapp.modules.fitxes.dialogs.EntrevistaPares;
import org.iesapp.modules.fitxes.dialogs.FitxaAlumne;
import org.iesapp.modules.fitxescore.util.Cfg;
//import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Josep
 */
public class DesktopManager {
    public static JDesktopPane desktop;
   
    
    public static void setDesktop(JDesktopPane pane)
    {
         desktop = pane;
    }
    
    
    public static void closeAll()
    {
       
            JInternalFrame[] allFrames = desktop.getAllFrames();


            for (JInternalFrame frame: allFrames) {
                if(frame.getLayer()>1) {
                    desktop.remove(frame);
                    frame.dispose();
                }
            }
 
    }
    
    //Administra com mostrar la fitxa de l'alumne...
    public static void showFitxaAlumne(FitxesGUI parent, 
            int numexp, ArrayList<Integer> listexpd, boolean newwin, final Cfg cfg) 
    {

        FitxaAlumne frame = null;

        if (!newwin) {

            JInternalFrame[] allFrames = desktop.getAllFrames();


            for (int i = 0; i < allFrames.length; i++) {
                if (allFrames[i].getClass().equals(FitxaAlumne.class)) {
                    frame = (FitxaAlumne) allFrames[i];
                    break;
                }
            }

        }

        if (frame == null) {
            frame = new FitxaAlumne(parent, true, numexp, listexpd, cfg);
            frame.setSize(frame.getWidth(), desktop.getHeight());
            desktop.add(frame);
        } else {
            frame.setAlumnes(numexp, listexpd);
        }

        frame.setVisible(true);
        frame.setLayer(2);

        try {
            frame.setSelected(true);
            frame.setIcon(false);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DesktopManager.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


        
    //Administra com mostrar accions d'alumne de l'alumne...
    public static void showAccionsAlumne(FitxesGUI parental, int numexp, 
            ArrayList<Integer> listexpd, boolean newwin, final Cfg cfg) 
    {

        AccionsAlumne4 frame = null;

        if (!newwin) {

            JInternalFrame[] allFrames = desktop.getAllFrames();


            for (int i = 0; i < allFrames.length; i++) {
                if (allFrames[i].getClass().equals(AccionsAlumne4.class)) {
                    frame = (AccionsAlumne4) allFrames[i];
                    break;
                }
            }

        }

        if (frame == null) {
            frame = new AccionsAlumne4(parental, true, numexp, listexpd, cfg);
            frame.setSize(frame.getWidth(), desktop.getHeight());
            desktop.add(frame);
        } else {
            frame.setAlumnes(numexp, listexpd);
        }
        
        //frame.setSize( (int) 0.75*desktop.getWidth(), 300);
        frame.setVisible(true);
        frame.setLayer(2);

        try {
            frame.setSelected(true);
            frame.setIcon(false);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DesktopManager.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    
      //Administra com mostrar entrevistaPares
    public static void showEntrevistaPares(FitxesGUI parental, Profile numexp, 
            ArrayList<Profile> listexpd, boolean newwin, final Cfg cfg) 
    {

        EntrevistaPares frame = null;

        if (!newwin) {

            JInternalFrame[] allFrames = desktop.getAllFrames();


            for (int i = 0; i < allFrames.length; i++) {
                if (allFrames[i].getClass().equals(EntrevistaPares.class)) {
                    frame = (EntrevistaPares) allFrames[i];
                    break;
                }
            }

        }

        if (frame == null) {
            frame = new EntrevistaPares(parental, true, numexp, listexpd, cfg);
            frame.setSize(frame.getWidth(), desktop.getHeight());
            desktop.add(frame);
        } else {
            frame.setAlumnes(numexp, listexpd);
        }

        frame.setVisible(true);
        frame.setLayer(2);

        try {
            frame.setMaximum(true);
            frame.setSelected(true);
            frame.setIcon(false);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DesktopManager.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    public static void addReport(Object viewer, String title) {
        
        //Determina quants de documents tenc a la pantalla
           
            int n = getFramesInLayer(3) + 1;
            //           
            JInternalFrame iframe = new JInternalFrame();
            iframe.setTitle("Document "+n+": "+title);
            iframe.add( (Component) viewer);
            iframe.pack();
            iframe.setVisible(true);
            iframe.setMaximizable(true);
            iframe.setIconifiable(true);
            iframe.setClosable(true);
            iframe.setLayer(3);         //3 = layer for documents
            try {
                iframe.setSelected(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(DesktopManager.class.getName()).log(Level.SEVERE, null, ex);
            }
                       
            iframe.setResizable(true);
            desktop.add(iframe);
            
            organitzaDocs();
    }

    public static void mazimitzaCerca()
    {
         for(JInternalFrame frame: desktop.getAllFramesInLayer(1))
         {
            try {
                frame.setIcon(false);
                frame.setMaximum(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(DesktopManager.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
             
    }
     
    public static void tileFinestres()
    {
        JInternalFrame[] allFrames = desktop.getAllFramesInLayer(2);       
        int nlayer2 = allFrames.length;
        int nlayer3 = getFramesInLayer(3);
       
        //tiled windows
        ///Determine the necessary grid size
        if(nlayer2==0) {
            return;
        }
        
        
        int sqrt = (int) Math.sqrt(nlayer2);
        int rows = sqrt;
        int cols = sqrt;
        if (rows * cols < nlayer2) {
          cols++;
          if (rows * cols < nlayer2) {
            rows++;
          }
        }

        // Define some initial values for size & location.
        Dimension size = desktop.getSize();

        int extra = 0;
        if(nlayer3>0) {
            extra = size.width/3;
        }
            
        int w = (size.width-extra) / cols;
        int h = size.height / rows;
        int x = 0;
        int y = 0;

        // Iterate over the frames, deiconifying any iconified frames and then
        // relocating & resizing each.
        int id = 0;
        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols && ((i * cols) + j < nlayer2); j++) {
            JInternalFrame f = allFrames[id];
            if(f.getLayer()==2)
            {

            if (!f.isClosed() && f.isIcon()) {
              try {
                f.setIcon(false);
              } catch (PropertyVetoException ignored) {
              }
            }

            desktop.getDesktopManager().resizeFrame(f, x, y, w, h);
            x += w;
            }
            id +=1 ;
          }
          y += h; // start the next row
          x = 0;
        }
       
    }
    
    public static void organitzaDocs()
    {
        
        JInternalFrame[] allFrames = desktop.getAllFramesInLayer(3);       
        int nlayer3 = allFrames.length;
        if(nlayer3==0) {
            return;
        }
        Dimension size = desktop.getSize();
      
            
        int w = size.width/2;   //abans /3
        int h = size.height / (nlayer3);
        int x = size.width/2;   //abans 2/3
        int y = 0;
        
        for(int i=0; i<nlayer3; i++)
        {
            JInternalFrame f = allFrames[i];
           

            if (!f.isClosed() && f.isIcon()) {
              try {
                f.setIcon(false);
              } catch (PropertyVetoException ignored) {
              }
            
            }
            desktop.getDesktopManager().resizeFrame(f, x, y, w, h);
            y += h;
        }
            
    }
     
 
     
    public static void organitza() {
       
        mazimitzaCerca();
        tileFinestres();
        organitzaDocs();
    }

    public static void showDesktop() {
         JInternalFrame[] allFrames = desktop.getAllFrames();

        
        for (int i = 0; i < allFrames.length; i++) {
            
            //La finestra de cerca maximitzada
            if (allFrames[i].getLayer()>1) 
            {
                try {
                    allFrames[i].setIcon(true);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(DesktopManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Returns the number of frames in a given layer
     * @param layer
     * @return 
     */
    private static int getFramesInLayer(int layer) {
        int n = 0;

        for (JInternalFrame frame : desktop.getAllFrames()) {
            if (frame.getLayer() == layer) {
                n += 1;
            }
        }
        return n;
    }
    
    
     public static void showFormulari(int idActuacio, BasicForm frame) 
     {
        //Comprova si hi ha altres formularis oberts i les tanca
               
        for(Component component: desktop.getComponentsInLayer(4))
        {
           BasicForm frame0 = (BasicForm) component;
           frame0.setVisible(false);
           frame0.dispose();
        }
      
            
        frame.setVisible(true);
        frame.setMaximizable(true);
        frame.setClosable(true);
        frame.setResizable(true);
        frame.setLayer(4);
        frame.setSize((int) Math.round(desktop.getWidth()*0.5), desktop.getHeight());
        desktop.add(frame);
        

        try {
            frame.setSelected(true);
            frame.setIcon(false);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DesktopManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    

    }

    public static void showPlugin(TopPluginWindow plugin) {
        JInternalFrame frame = plugin.asJInternalFrame();
        frame.setLayer(2);
        frame.setClosable(true);
        frame.setResizable(true);
        frame.setIconifiable(true);
        frame.setMaximizable(true);
        frame.setTitle(plugin.getBeanPlugin().getModuleNameBundle().get(Locale.getDefault().getLanguage()));
        frame.setSize(desktop.getWidth()/2, desktop.getHeight());
        frame.setVisible(true);
        desktop.add(frame);
    }

}
