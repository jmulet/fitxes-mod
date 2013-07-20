/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.dialogs;

import com.toedter.calendar.JSpinnerDateEditor;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.iesapp.clients.iesdigital.fitxes.BeanDadesPersonals;
import org.iesapp.framework.pluggable.modulesAPI.ErrorDisplay;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.JSEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Josep
 */
public class ExportUsers extends javax.swing.JDialog {
    private final DefaultListModel modelList1;
    private final DefaultComboBoxModel modelCombo1;
    private final DefaultComboBoxModel modelCombo2;
    private final HashMap<String,String> mapDefaults = new HashMap<String,String>();
    private final static String FILENAME = "config/exportacions.xml";
    private final static String CONTEXT = "alumnat";
    private final ArrayList<Integer> expds;
    private final Cfg cfg;
    private final JSEngine jsEngine;


    /**
     * Creates new form ExportUsers
     */
    public ExportUsers(ArrayList<Integer> expdsInView, final Cfg cfg) {
        super((java.awt.Frame) null,false);
        this.cfg = cfg;
        initComponents();
        expds = expdsInView;
        
        //Scripting engine
        jsEngine = JSEngine.getJSEngine(getClass(),CoreCfg.contextRoot);
        
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);
        buttonGroup1.add(jRadioButton3);
        
        altesPanel1.startUp(cfg);
       
        mapDefaults.put("$ESTUDIS", "1R BATX");
        mapDefaults.put("$ENSENYAMENT", "BATX");
        mapDefaults.put("$EXPEDIENT","1234");
        mapDefaults.put("$CONTRASENYA","11111111111");
        mapDefaults.put("$LLINATGE1", "LLABRES");
        mapDefaults.put("$LLINATGE2", "COLL");
        mapDefaults.put("$NOM", "JUAN");
        mapDefaults.put("$GRUP", "A");
        mapDefaults.put("$NUMEROCURS", "1");
        mapDefaults.put("$DNI","78772211");
        mapDefaults.put("$NIF","78772211X");
        
        try{
        Bindings bindings = jsEngine.getBindings();
        bindings.put("mapDefaults", mapDefaults);
        jsEngine.evalBindings(bindings);
        jsEngine.invokeFunction("extendMapDefaults");
        }
        catch(Exception ex)
        {
             ErrorDisplay.showMsg(ex.toString());
        }
       
        
        modelList1 = new DefaultListModel();
        jList1.setModel(modelList1);
       
        
        modelCombo2 =  new DefaultComboBoxModel();
        jComboBox2.setModel(modelCombo2);
        modelCombo1 = new DefaultComboBoxModel();
        jComboBox1.setModel(modelCombo1);
        modelCombo1.addElement("Triau un camp");
        for(String ky: mapDefaults.keySet())
        {
            modelCombo1.addElement(ky);
        }
       
       //Hem de fer un parse del document xml
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
	DocumentBuilder b;
        try {
            b = f.newDocumentBuilder();
            Document doc = b.parse(new File(CoreCfg.contextRoot+File.separator+FILENAME));
            NodeList nodelist = doc.getElementsByTagName("export");
            
            for (int i = 0; i < nodelist.getLength(); i++) {
                Element root = (Element) nodelist.item(i);
                String title = root.getAttribute("name");
                String context = root.getAttribute("context");
                if(context.equalsIgnoreCase(CONTEXT)) {
                    modelCombo2.addElement(title);
                }
            }

            if(nodelist.getLength()>0) {
                loadExportacio(0);
            }
        } catch (SAXException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton5 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser(null, null, null, new JSpinnerDateEditor());
        altesPanel1 = new org.iesapp.modules.fitxes.dialogs.AltesPanel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Exportació d'alumnes");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel4.setText("Expotacions ");

        jButton4.setText("Desa");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel2.setText("Camps");

        jLabel6.setText("Estructura d'exportació");

        jButton9.setText("Amunt");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel5.setText("Text");

        jButton8.setText(" ");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel1.setText("Preview");

        jTextField1.setEditable(false);

        jButton2.setText(";");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("-");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton1.setText(",");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel7.setText("Text encapçalament");

        jButton6.setText("Treu");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        jLabel3.setText("Separadors");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton5.setText(">");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton10.setText("Avall");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel5))
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField3)
                            .addComponent(jTextField1)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField2)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton5))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel6))
                        .addGap(21, 21, 21)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jButton1)
                        .addComponent(jButton2)
                        .addComponent(jButton3)
                        .addComponent(jButton8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addGap(55, 55, 55)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10))
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Paràmetres", jPanel1);

        jRadioButton1.setText("Tots els alumnes en aquest curs");

        jRadioButton2.setSelected(true);
        jRadioButton2.setText("Només els alumnes de la cerca");

        jRadioButton3.setText("Alumnes amb data d'alta a partir de dia");

        jDateChooser1.setLocale(new java.util.Locale("ca"));
        jDateChooser1.setDateFormatString("dd-MM-yyyy");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jRadioButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(189, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(altesPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton3)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(altesPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Selecció", jPanel2);

        jButton11.setText("Nova");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Esborra");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton7.setText("Genera");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jButton4)
                    .addComponent(jButton11)
                    .addComponent(jButton12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int i=jList1.getSelectedIndex();
        if(i>=0) {
            modelList1.add(i+1, ",");
        }
        else {
            modelList1.addElement(",");
        }
        refreshPreview();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
         int i=jList1.getSelectedIndex();
        if(i>=0) {
            modelList1.add(i+1, ";");
        }
        else {
            modelList1.addElement(";");
        }
        refreshPreview();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         int i=jList1.getSelectedIndex();
         if(i>=0) {
            modelList1.add(i+1, "-");
        }
         else {
            modelList1.addElement("-");
        }
         refreshPreview();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int i=jList1.getSelectedIndex();
        if(i>=0) {
            modelList1.add(i+1, " ");
        }
        else {
            modelList1.addElement(" ");
        }
        refreshPreview();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        modelList1.addElement(jTextField2.getText());
        jTextField2.setText("");
        refreshPreview();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
       if(jComboBox1.getSelectedIndex()>0)
       {
           
           
            int i=jList1.getSelectedIndex();
        if(i>=0) {
                modelList1.add(i+1, jComboBox1.getSelectedItem());
            }
        else {
                modelList1.addElement(jComboBox1.getSelectedItem());
            }
           jComboBox1.setSelectedIndex(0);
       }
       refreshPreview();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
       int pos = jList1.getSelectedIndex();
       if(pos>=0)
       {
           modelList1.removeElementAt(pos);         
       }
       if(pos<modelList1.getSize()) {
            jList1.setSelectedIndex(pos);
        }
       refreshPreview();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        int nsel = jComboBox2.getSelectedIndex();
        if(nsel>=0) {
            loadExportacio(nsel);
        }
        refreshPreview();
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // mou amunt
        int i0 = jList1.getSelectedIndex();
        if(i0>0)
        {
            modelList1.add(i0-1, jList1.getSelectedValue());
            modelList1.remove(i0+1);
            jList1.setSelectedIndex(i0-1);
        }
        refreshPreview();
       
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
      // mou avall
        int i0 = jList1.getSelectedIndex();
        if(i0<modelList1.getSize()-1)
        {
            modelList1.add(i0+2, jList1.getSelectedValue());
            modelList1.remove(i0);
            jList1.setSelectedIndex(i0+1);
        }
        refreshPreview();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // genera
        if(jRadioButton1.isSelected())
        {
            generateAll();
        }
        else if(jRadioButton2.isSelected())
        {
            generateSelection();
        }
        else if(jRadioButton3.isSelected())
        {
            generateFromDate();
        }
        
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // Save the exportation as xml
        saveXml();
    }//GEN-LAST:event_jButton4ActionPerformed

    //create new empty
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        String name = JOptionPane.showInputDialog(this, "Nom de la nova exportació", "Nova exportació");
        name = name.trim();
        if(name.isEmpty()) {
            return;
        }
        if(modelCombo2.getIndexOf(name)>=0)
        {
            JOptionPane.showMessageDialog(this, "Ja existeix una importació amb aquest nom.");
            return;
        }
        modelCombo2.addElement(name);
        jComboBox2.setSelectedItem(name);
        jTextField2.setText("");
        jTextField3.setText("");
        modelList1.removeAllElements();
        refreshPreview();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        //Esborra la exportacio actual
        
        
        int id = jComboBox2.getSelectedIndex();
        if(id>=0)
        {
            
           int sol = JOptionPane.showConfirmDialog(this, "Segur que voleu eliminar aquest patró d'exportació?");
           if(sol!=JOptionPane.YES_OPTION) {
                return;
            }
           
           String name = (String) jComboBox2.getSelectedItem();
            deleteFromXml(name);
            modelCombo2.removeElementAt(id);
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void generateAll()
    {
        File file = new File( System.getProperty("user.home")+"/users4"+jComboBox2.getSelectedItem()+".txt"  );
        FileWriter outFile;
        try {
           outFile = new FileWriter(file);
           PrintWriter out = new PrintWriter(outFile);
           String txt = jTextField3.getText();
           if(!txt.isEmpty()) {
               out.println(txt);
           }
            
           String SQL1 = "SELECT * "+
                         " FROM `"+CoreCfg.core_mysqlDBPrefix+"`.`xes_alumne_historic` AS xh INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.`xes_alumne` AS xes ON "+ 
                         " xes.Exp2=xh.Exp2 AND xh.AnyAcademic="+cfg.anyAcademicFitxes+" ORDER BY concat(xh.Estudis,xh.grup) , llinatge2, llinatge1, nom1";
           
           Statement st = cfg.getCoreCfg().getMysql().createStatement();
           ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
              
            while (rs1 != null && rs1.next()) {
                HashMap map = (HashMap) this.mapDefaults.clone();
                int exp2 = rs1.getInt("exp2");
                String estudis = rs1.getString("estudis");
                map.put("$ESTUDIS", estudis);
                map.put("$ENSENYAMENT", rs1.getString("ensenyament"));
                map.put("$EXPEDIENT", exp2+"");
                map.put("$CONTRASENYA", rs1.getString("pwd"));
                map.put("$LLINATGE1", rs1.getString("llinatge1"));
                map.put("$LLINATGE2", rs1.getString("llinatge2"));
                map.put("$NOM", rs1.getString("nom1"));
                map.put("$GRUP", rs1.getString("grup"));
                map.put("$NUMEROCURS", ""+ estudis.charAt(0));
                String nif = rs1.getString("dni");
                map.put("$NIF", nif);
                map.put("$DNI", nif2dni(nif));
               
                try {
                    Bindings bindings = jsEngine.getBindings();
                    bindings.put("map", map);
                    bindings.put("rs1", rs1);
                    BeanDadesPersonals dp = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
                    dp.getFromDB(exp2, cfg.anyAcademicFitxes);
                    bindings.put("dp", dp);
                    bindings.put("coreCfg", cfg.getCoreCfg());
                    jsEngine.evalBindings(bindings);
                    jsEngine.invokeFunction("extendMap");
                } catch (Exception ex) {
                    ErrorDisplay.showMsg(ex.toString());
                }
                out.println(constructLine(map));
               
           }
           if(rs1!=null) {
                rs1.close();
                st.close();
            }
       
           
           //Afegeix els professors
           
           
            out.close();
            outFile.close();
            Desktop.getDesktop().open(file);
        
        } catch (SQLException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void generateSelection()
    {
        File file = new File( System.getProperty("user.home")+"/users4"+jComboBox2.getSelectedItem()+".txt"  );
        FileWriter outFile;
        try {
           outFile = new FileWriter(file);
           PrintWriter out = new PrintWriter(outFile);
           String txt = jTextField3.getText();
           if(!txt.isEmpty()) {
                out.println(txt);
            }
            
           String SQL1 = "SELECT * "+
                         " FROM `"+CoreCfg.core_mysqlDBPrefix+"`.`xes_alumne_historic` AS xh INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.`xes_alumne` AS xes ON "+ 
                         " xes.Exp2=xh.Exp2 AND xh.AnyAcademic="+cfg.anyAcademicFitxes+" ORDER BY concat(xh.Estudis,xh.grup) , llinatge2, llinatge1, nom1";
           HashMap map = new HashMap();
           
         
             Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
              
            while (rs1 != null && rs1.next()) {

                int expd = rs1.getInt("exp2");
                if(expds.contains(expd))
                {
                    String estudis = rs1.getString("estudis");
                    map.put("$ESTUDIS", estudis);
                    map.put("$ENSENYAMENT", rs1.getString("ensenyament"));
                    map.put("$EXPEDIENT", expd+"");
                    map.put("$CONTRASENYA", rs1.getString("pwd"));
                    map.put("$LLINATGE1", rs1.getString("llinatge1"));
                    map.put("$LLINATGE2", rs1.getString("llinatge2"));
                    map.put("$NOM", rs1.getString("nom1"));
                    map.put("$GRUP", rs1.getString("grup"));
                    map.put("$NUMEROCURS", ""+estudis.charAt(0));
                    String nif = rs1.getString("dni");
                
                map.put("$NIF", nif);
                map.put("$DNI", nif2dni(nif));
                
            try {
                    Bindings bindings = jsEngine.getBindings();
                    bindings.put("map", map);
                    bindings.put("rs1", rs1);
                    BeanDadesPersonals dp = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
                    dp.getFromDB(expd, cfg.anyAcademicFitxes);
                    bindings.put("dp", dp);
                    bindings.put("coreCfg", cfg.getCoreCfg());
                    jsEngine.evalBindings(bindings);
                    jsEngine.invokeFunction("extendMap");
                } catch (Exception ex) {
                    ErrorDisplay.showMsg(ex.toString());
                }
                    out.println(constructLine(map));
                 }
           }
           if(rs1!=null) {
                rs1.close();
                st.close();
            }
       
           
           //Afegeix els professors
           
           
            out.close();
            outFile.close();
            Desktop.getDesktop().open(file);
        
        } catch (SQLException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
    
     private void generateFromDate()
     {
        java.util.Date date = jDateChooser1.getDate();
        if(date==null) {
            return;
        }
        
        File file = new File( System.getProperty("user.home")+"/users4"+jComboBox2.getSelectedItem()+".txt"  );
        FileWriter outFile;
        try {
           outFile = new FileWriter(file);
           PrintWriter out = new PrintWriter(outFile);
           String txt = jTextField3.getText();
           if(!txt.isEmpty()) {
                out.println(txt);
            }
            
           String SQL1 = "SELECT * "+
                         " FROM `"+CoreCfg.core_mysqlDBPrefix+"`.`xes_alumne_historic` AS xh INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.`xes_alumne` AS xes ON "+ 
                         " xes.Exp2=xh.Exp2 AND xh.AnyAcademic="+cfg.anyAcademicFitxes+" INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alta as xal on xal.exp2=xh.exp2 "
                   + " where dataAlta>='"+new DataCtrl(date).getDataSQL()+"' ORDER BY concat(xh.Estudis,xh.grup), llinatge2, llinatge1, nom1";
           
            
         
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
              
            while (rs1 != null && rs1.next()) {

                String estudis = rs1.getString("estudis");
                HashMap map = (HashMap) this.mapDefaults.clone();
                int exp2 = rs1.getInt("exp2");
                map.put("$ESTUDIS", estudis);
                map.put("$ENSENYAMENT", rs1.getString("ensenyament"));
                map.put("$EXPEDIENT", exp2+"");
                map.put("$CONTRASENYA", rs1.getString("pwd"));
                map.put("$LLINATGE1", rs1.getString("llinatge1"));
                map.put("$LLINATGE2", rs1.getString("llinatge2"));
                map.put("$NOM", rs1.getString("nom1"));
                map.put("$GRUP", rs1.getString("grup"));
                map.put("$NUMEROCURS", ""+ estudis.charAt(0));
                String nif = rs1.getString("dni");
                
                map.put("$NIF", nif);
                map.put("$DNI", nif2dni(nif));
 
                
                //Redefine this map via js
               try {
                    Bindings bindings = jsEngine.getBindings();
                    bindings.put("map", map);
                    bindings.put("rs1", rs1);
                    BeanDadesPersonals dp = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
                    dp.getFromDB(exp2, cfg.anyAcademicFitxes);
                    bindings.put("dp", dp);
                    bindings.put("coreCfg", cfg.getCoreCfg());
                    jsEngine.evalBindings(bindings);
                    jsEngine.invokeFunction("extendMap");
                } catch (Exception ex) {
                    ErrorDisplay.showMsg(ex.toString());
                }                
                //Comprova si es troba a la llista en pantalla
                out.println(constructLine(map));
                
           }
           if(rs1!=null) {
                rs1.close();
                st.close();
            }
       
           
           //Afegeix els professors
           
           
            out.close();
            outFile.close();
            Desktop.getDesktop().open(file);
        
        } catch (SQLException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
            
    private void refreshPreview()
    {
       
        jTextField1.setText(constructLine(mapDefaults));
    }
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.iesapp.modules.fitxes.dialogs.AltesPanel altesPanel1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables

    private void loadExportacio(int nexport) {
                       
        String nameExport = (String) jComboBox2.getItemAt(nexport);
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
	DocumentBuilder b;
        try {
            b = f.newDocumentBuilder();
            Document doc = b.parse(new File(CoreCfg.contextRoot+File.separator+FILENAME));
            NodeList nodelist = doc.getElementsByTagName("export");
            modelList1.clear();
            for (int i = 0; i < nodelist.getLength(); i++) {
                Element root = (Element) nodelist.item(i);
                String title = root.getAttribute("name");
                String context = root.getAttribute("context");
                if(title.equals(nameExport) && context.equalsIgnoreCase(CONTEXT))
                {
                    NodeList nodelist2 = root.getElementsByTagName("header");
                    for (int j = 0; j < nodelist2.getLength(); j++) {
                        Element element = (Element) nodelist2.item(j);
                        String header = element.getAttribute("value");                     
                        jTextField3.setText(header);
                    }
                    
                    NodeList nodelist3 = root.getElementsByTagName("field");
                    for (int j = 0; j < nodelist3.getLength(); j++) {
                        Element element = (Element) nodelist3.item(j);
                        String value = element.getAttribute("value");                     
                        modelList1.addElement(value);
                    }
                    
          
                }
                    
            }

           
        } catch (SAXException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
             
        refreshPreview();
    }

    private String constructLine(HashMap<String, String> map) {
        
        StringBuilder output = new StringBuilder();
        
        for(int i=0; i<modelList1.size();i++)
        {
            String value = (String) modelList1.get(i);
            if(value.startsWith("$"))
            {
                String get = map.get(value);
                if(get!=null){
                    output.append(get);
                }
                else
                {
                    output.append(value);
                }                      
            }
            else
            {
                output.append(value);
            }
        }
        
        return output.toString();
    }

    private void saveXml() {
        String nameExport = (String) jComboBox2.getSelectedItem();
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
	DocumentBuilder b;
        try {
            b = f.newDocumentBuilder();
            Document doc = b.parse(new File(FILENAME));           
            Node root = doc.getChildNodes().item(0);
            NodeList nodelist = doc.getElementsByTagName("export");
            
            Node existing = null;
            //First remove the exportation from file if it exists
            for (int i = 0; i < nodelist.getLength(); i++) {
                Element element = (Element) nodelist.item(i);
                String title = element.getAttribute("name");
                if(title.equals(nameExport))
                {                    
                   existing = nodelist.item(i);
                   break;
                }                    
            }
            //Create the exportation
            Element exportation = doc.createElement("export");
            exportation.setAttribute("name", nameExport);
            exportation.setAttribute("context", CONTEXT);
            
            Element header = doc.createElement("header");
            header.setAttribute("value", jTextField3.getText());
            exportation.appendChild(header);
            
            for(int i=0; i<modelList1.getSize();i++)
            {
                String fieldtxt = (String) modelList1.get(i);
                Element field = doc.createElement("field");
                field.setAttribute("value", fieldtxt);
                exportation.appendChild(field);
            }
                
            
            if(existing==null)
            {
                 root.appendChild(exportation);
            }
            else
            {
                root.replaceChild(exportation, existing);
            }
            
           
             // Use a Transformer for output
            TransformerFactory tFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");       
          
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FILENAME));
            transformer.transform(source, result);
           
        } catch (TransformerException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
             
    }

    private void deleteFromXml(String nameExport) {
      
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
	DocumentBuilder b;
        try {
            b = f.newDocumentBuilder();
            Document doc = b.parse(new File(FILENAME));           
            Node root = doc.getChildNodes().item(0);
            NodeList nodelist = doc.getElementsByTagName("export");
            
            //First remove the exportation from file if it exists
            for (int i = 0; i < nodelist.getLength(); i++) {
                Element element = (Element) nodelist.item(i);
                String title = element.getAttribute("name");
                if(title.equals(nameExport))
                {                    
                   root.removeChild(element);              
                }                    
            }
           
           
             // Use a Transformer for output
            TransformerFactory tFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");       
          
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("config/exportacions.xml"));
            transformer.transform(source, result);
           
        } catch (TransformerException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ExportUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Elimina la lletra final (en cas que en tingui)
    private String nif2dni(String nif) {
        String dni = nif;
        int len = nif.length();
        if(len<2) {
            return nif;
        }
        
        String last = ""+nif.charAt(len-1);
        if(!last.equals("0") && !last.equals("1")&& !last.equals("2") 
                && !last.equals("3") && !last.equals("4") 
                && !last.equals("5") && !last.equals("6")
                && !last.equals("7") && !last.equals("8") && !last.equals("9"))
        {
            dni = nif.substring(0,len-1);
        }
        
        return dni;
    }
}
