/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JSharedDoc.java
 *
 * Created on 14-mar-2012, 6:22:21
 */
package org.iesapp.modules.fitxes.util;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.style.INotificationStyle;
import ch.swingfx.twinkle.style.theme.DarkDefaultNotification;
import ch.swingfx.twinkle.window.Positions;
import com.l2fprod.common.swing.JLinkButton;
import com.l2fprod.common.swing.JTaskPaneGroup;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class JSharedDoc extends javax.swing.JPanel {
    private ArrayList<File> findURLDocs;
    private JTabbedPane jTabbedPane1;
    private Cfg cfg;

    /** Creates new form JSharedDoc */
    public JSharedDoc() {
        initComponents();
        
        jFileChooser1.setVisible(false);
        jFileChooser1.addActionListener(new ActionListener() {

            @Override
        public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("ApproveSelection"))
                {
                    if(jFileChooser1.getSelectedFile()!=null)
                    {

                INotificationStyle style = new DarkDefaultNotification().withWindowCornerRadius(8).withWidth(300).withAlpha(0.86f);

                // Now lets build the notification
                 new NotificationBuilder().withStyle(style) // Required. here we set the previously set style
                    .withTitle("Obrint...") // Required.
                    .withMessage(jFileChooser1.getSelectedFile().getName()) // Optional
                    .withIcon(new ImageIcon(NotificationBuilder.ICON_WRITER)) // Optional. You could also use a String path
                    .withDisplayTime(5000) // Optional
                    .withPosition(Positions.SOUTH_EAST) // Optional. Show it at the center of the screen
                    .showNotification(); 
                    
                     launchFile(jFileChooser1.getSelectedFile().getAbsolutePath());
                    }
                }
                else
                {
                    if(jTabbedPane1!=null) {
                        jTabbedPane1.setSelectedIndex(0);
                    }
                }
                
            }
        });
    }
    
    public void startUp(Cfg cfg)
    {
        this.cfg = cfg;
    }
    
    public void setJTabbedPane(JTabbedPane jTabbedPane1)
    {
        this.jTabbedPane1 = jTabbedPane1;
    }
    
    private void clear()
    {
        jTaskPaneGroup1.removeAll();
    }
    
    public int setDocuments(int exp, String nomAlumne)
    {
        clear();
        jTaskPaneGroup1.setTitle("Documents associats a l'alumne/a: "+nomAlumne);
        
        SharedDocs sharedDocs1 = new SharedDocs(exp, cfg);
        findURLDocs = sharedDocs1.findURLDocs();
        int ndoc = findURLDocs.size(); 
    
        
        if(sharedDocs1.getStudentDirectory()!=null)
        {
            jFileChooser1.setVisible(true);
            jFileChooser1.setControlButtonsAreShown(true);
            jFileChooser1.setApproveButtonText("Obrir");
            jFileChooser1.setMultiSelectionEnabled(false);
            jFileChooser1.setCurrentDirectory(sharedDocs1.getStudentDirectory());
            jFileChooser1.setDialogType(JFileChooser.OPEN_DIALOG);
            jTaskPaneGroup1.setCollapsable(false);
            jTaskPaneGroup1.setExpanded(false);
            jTaskPaneGroup1.setCollapsable(false);
            jTaskPaneGroup1.setExpanded(false);
        }
        else
        {
            jFileChooser1.setVisible(false);
            jTaskPaneGroup1.setCollapsable(true);
            jTaskPaneGroup1.setExpanded(true);
        }
        
        //Segons la ubicació del fitxer s'organitzen en taskpanegroups
        JTaskPaneGroup tpg = new JTaskPaneGroup();
        HashMap<String,JTaskPaneGroup> map = new HashMap<String,JTaskPaneGroup>();
        
        for(int i=0; i<ndoc; i++)
        {
            java.io.File doc = findURLDocs.get(i);
            String parent = doc.getParent();
            String write = doc.canWrite()? "":" (només lectura)";
            JLinkButton jlinkbutton1 = new JLinkButton(doc.getName()+write+" - modificat "+new DataCtrl(new java.util.Date(doc.lastModified())).getDiaMesComplet());
            jlinkbutton1.setActionCommand(""+i);
            jlinkbutton1.setFont(new java.awt.Font("Tahoma", 0, 14));
            jlinkbutton1.setIcon(getIconByExtension(doc.getAbsolutePath())); 
            jlinkbutton1.setToolTipText("[click] obrir fitxer; [SHIFT+click] obrir ubicació");
                                      
            if(i==0)
            {
                tpg.setTitle(parent);
                map.put(parent, tpg);
                jTaskPaneGroup1.add(tpg); 
            }
            else
            {
                if(map.containsKey(parent))
                {
                    tpg = map.get(parent);
                }
                else
                {
                     tpg = new JTaskPaneGroup();
                     tpg.setTitle(parent);
                     map.put(parent, tpg);
                     jTaskPaneGroup1.add(tpg);
                }
            }
                    
            
            jlinkbutton1.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    java.io.File file= null;
                    try {
                        int shiftPressed = (evt.getModifiers() & java.awt.event.InputEvent.SHIFT_MASK);
                        int id = (int) Double.parseDouble(evt.getActionCommand());
                        
                       
                        if(shiftPressed>0)
                        {
                            file = findURLDocs.get(id).getParentFile();
                        }
                        else
                        {
                           file = findURLDocs.get(id);
                        }
                        
                        Desktop.getDesktop().open(file);

                    } catch (MalformedURLException ex) {
                        Logger.getLogger(JSharedDoc.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (IOException ex) {
                       
                        //this is sometimes necessary with files on other servers ie \\xxx\xxx.xls
                        launchFile(file.getPath());
                    }            
                }
            });
            tpg.add(jlinkbutton1);
        }
        
        if(ndoc==0) {
            jTaskPaneGroup1.add(new javax.swing.JLabel("  No s'han trobat documents."));
        }
        
        return ndoc;
    }


  
  //this can launch both local and remote files
  public void launchFile(String filePath)
  {
    if(filePath == null || filePath.trim().length() == 0) {
            return;
        }
    if(!Desktop.isDesktopSupported()) {
            return;
        }
    Desktop dt = Desktop.getDesktop();
    try
    {      
       dt.browse(getFileURI(filePath));
    } catch (Exception ex)
    {
       Logger.getLogger(JSharedDoc.class.getName()).log(Level.SEVERE, null, ex);
     }
   }

  //generate uri according to the filePath
  private URI getFileURI(String filePath)
  {
    
    URI uri = null;
    filePath = filePath.trim();
    if(filePath.indexOf("http") == 0 || filePath.indexOf('\\') == 0)
    {
      if(filePath.indexOf('\\') == 0) {
                filePath = "file:" + filePath;
            }
      try
      {
        filePath = filePath.replaceAll(" ", "%20");
        URL url = new URL(filePath);
        uri = url.toURI();
      } catch (MalformedURLException ex)
      {
         Logger.getLogger(JSharedDoc.class.getName()).log(Level.SEVERE, null, ex);
      }
      catch (URISyntaxException ex)
      {
         Logger.getLogger(JSharedDoc.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    else
    {
      File file = new File(filePath);
      uri = file.toURI();
    }
     
    return uri;
  }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTaskPaneGroup1 = new com.l2fprod.common.swing.JTaskPaneGroup();
        jFileChooser1 = new javax.swing.JFileChooser();
        jLabel1 = new javax.swing.JLabel();

        jTaskPaneGroup1.setCollapsable(false);
        jTaskPaneGroup1.setTitle("Documents associats a l'alumne/a:");
        jTaskPaneGroup1.setToolTipText("");
        jTaskPaneGroup1.setName("jTaskPaneGroup1"); // NOI18N
        com.l2fprod.common.swing.PercentLayout percentLayout1 = new com.l2fprod.common.swing.PercentLayout();
        percentLayout1.setGap(2);
        percentLayout1.setOrientation(1);
        jTaskPaneGroup1.getContentPane().setLayout(percentLayout1);

        jFileChooser1.setName("jFileChooser1");

        jLabel1.setBackground(new java.awt.Color(255, 204, 204));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Podeu editar aquests documents però mai canvieu-los el nom o la ubicació.");
        jLabel1.setName("jLabel1");
        jLabel1.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTaskPaneGroup1, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jFileChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTaskPaneGroup1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFileChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private com.l2fprod.common.swing.JTaskPaneGroup jTaskPaneGroup1;
    // End of variables declaration//GEN-END:variables

    private Icon getIconByExtension(String path) {
        Icon icon= null;
        String extension = StringUtils.AfterLast(path, ".").trim();
        if(extension.equalsIgnoreCase("pdf"))
        {
            icon = new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/pdf.gif"));
        }
        else if(extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx") )
        {
            icon = new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/doc.gif"));
        }
        else if(extension.equalsIgnoreCase("txt") )
        {
            icon = new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/txt.gif"));
        }
        return icon;
    }
}
