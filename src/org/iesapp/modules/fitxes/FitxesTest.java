/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes;

/**
 *
 * @author Josep
 */
public class FitxesTest extends org.iesapp.framework.pluggable.DockingFrameworkApp {

    /**
     * Creates new form FitxesApp
     */
    public FitxesTest(String[] args) {
        super(args);
        this.appNameId="fitxes";
        this.appDisplayName="Fitxes-Tutoria";
        this.appDescription="Programa de fitxes-tutoria";
        initComponents();
        this.appClass = getClass();
        this.requiredJar = "org-iesapp-modules-fitxes.jar";
        this.requiredModuleName = "/org/iesapp/modules/fitxes/FitxesGUI";
        this.initializeFramework();
     }
 
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


     public static void main(String[] args) {
         
         FitxesTest fitxesApp = new FitxesTest(args);
         fitxesApp.setVisible(true);

        
    }
}
