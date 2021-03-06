/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JasperComple.java
 *
 * Created on 04-feb-2012, 13:14:27
 */
package org.iesapp.modules.fitxes.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.util.StringUtils;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperCompileManager;
//import net.sf.jasperreports.engine.design.JasperDesign;
//import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author Josep
 */
public class JasperCompile extends javax.swing.JDialog {
    private  Class<?> classJasperCompileManager;
    private  Class<?> classJasperDesign;
    private  Class<?> classJRXmlLoader;

    /** Creates new form JasperComple */
    public JasperCompile(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
       
            initComponents();
        try {     
           // Class.forName("net.sf.jasperreports.engine.JRException");
            classJasperCompileManager = Class.forName("net.sf.jasperreports.engine.JasperCompileManager");
            classJasperDesign = Class.forName("net.sf.jasperreports.engine.design.JasperDesign");
            classJRXmlLoader = Class.forName("net.sf.jasperreports.engine.xml.JRXmlLoader");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JasperCompile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void compile()
    {
        List<File> files = new ArrayList<File>();
        try {
            //Obté una estructura de tots els directoris i fitxers
           //A qualsevol report li pas el directori complet
                java.io.File dir1 = new java.io.File (".");
                String path = "";
                try {
                  //System.out.println ("Current dir : " + dir1.getCanonicalPath());
                 path = dir1.getCanonicalPath()+"\\reports";
                }
                catch(Exception e) {
                            //
                }  
          files = getFileListing( new File(path) );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JasperCompile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(File file: files) { 
            if(file.isFile() && file.toString().contains(".jrxml"))
            {
            try {
                // Recuperamos el fichero fuente
                Object jdInstance = classJRXmlLoader.getMethod("load", java.io.File.class).invoke(null, file);
                //JasperDesign jd = JRXmlLoader.load(file);
                // Compilamos el informe jrxml
                String output = StringUtils.BeforeLast( file.toString(),".")+".jasper";
                classJasperCompileManager.getMethod("compileReportToFile", new Class[]{classJasperDesign, String.class}).invoke(null, new Object[]{jdInstance,output});
                //JasperCompileManager.compileReportToFile(jd, output);
                
            } catch (Exception ex) {
                Logger.getLogger(JasperCompile.class.getName()).log(Level.SEVERE, null, ex);
                jTextArea1.append("\t ERROR:"+ex);
            }
                jTextArea1.append(file.toString()+"\t Compiled! \n");
            }
        }
    }
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Compilador dels reports Jasper");

        jButton1.setText("Compila");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Surt");
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 328, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.compile();
    }//GEN-LAST:event_jButton1ActionPerformed

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables


/**
  * Recursively walk a directory tree and return a List of all
  * Files found; the List is sorted using File.compareTo().
  *
  * @param aStartingDir is a valid directory, which can be read.
  */
  static public List<File> getFileListing(
    File aStartingDir
  ) throws FileNotFoundException {
    validateDirectory(aStartingDir);
    List<File> result = getFileListingNoSort(aStartingDir);
    Collections.sort(result);
    return result;
  }

  // PRIVATE //
  static private List<File> getFileListingNoSort(
    File aStartingDir
  ) throws FileNotFoundException {
    List<File> result = new ArrayList<File>();
    File[] filesAndDirs = aStartingDir.listFiles();
    List<File> filesDirs = Arrays.asList(filesAndDirs);
    for(File file : filesDirs) {
      result.add(file); //always add, even if directory
      if ( ! file.isFile() ) {
        //must be a directory
        //recursive call!
        List<File> deeperList = getFileListingNoSort(file);
        result.addAll(deeperList);
      }
    }
    return result;
  }

  /**
  * Directory is valid if it exists, does not represent a file, and can be read.
  */
  static private void validateDirectory (
    File aDirectory
  ) throws FileNotFoundException {
    if (aDirectory == null) {
      throw new IllegalArgumentException("Directory should not be null.");
    }
    if (!aDirectory.exists()) {
      throw new FileNotFoundException("Directory does not exist: " + aDirectory);
    }
    if (!aDirectory.isDirectory()) {
      throw new IllegalArgumentException("Is not a directory: " + aDirectory);
    }
    if (!aDirectory.canRead()) {
      throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
    }
  }
}
