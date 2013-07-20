/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EntrevistaPares.java
 *
 * Created on 13-jul-2011, 16:31:37
 */

package org.iesapp.modules.fitxes.dialogs;

import com.toedter.calendar.JSpinnerDateEditor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.iesapp.clients.iesdigital.fitxes.BeanDadesPersonals;
import org.iesapp.clients.iesdigital.fitxes.BeanEmergencyInfo;
import org.iesapp.clients.iesdigital.fitxes.BeanEntrevistaPares;
import org.iesapp.clients.iesdigital.fitxes.BeanFitxaCurs;
import org.iesapp.clients.iesdigital.missatgeria.BeanMissatge;
import org.iesapp.clients.sgd7.evaluaciones.EvaluacionesCollection;
import org.iesapp.clients.sgd7.mensajes.Mensajes;
import org.iesapp.clients.sgd7.reports.BeanSGDResumInc;
import org.iesapp.clients.sgd7.reports.InformesSGD;
import org.iesapp.framework.pluggable.grantsystem.Profile;
import org.iesapp.framework.table.MyCheckBoxRenderer;
import org.iesapp.framework.util.AnimatedPanel;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.modules.fitxes.FitxesGUI;
import org.iesapp.modules.fitxes.reports.ReportingClass;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.StringUtils;
/**
 *
 * @author Josep
 */
public class EntrevistaPares extends javax.swing.JInternalFrame {
    private ArrayList<BeanSeleccio> listSelect;
    private DefaultTableModel modelTable1;
    private String nombre;
    private String grupo;
    private String tutor;
    private int expedient;
    private DefaultTableModel modelTable2;
    private String iniSms="";
    private ArrayList<Profile> listProfiles;
    private FitxesGUI parental;
    private final Date avui;
    private boolean isListening = false;
    private final Cfg cfg;
    private Profile profile;
    private final Date iniCurs;

    /** Creates new form EntrevistaPares */
    public EntrevistaPares(FitxesGUI parent, boolean modal, Profile profile, 
            ArrayList<Profile> listProfiles, final Cfg cfg) {
        this.cfg = cfg;
        cfg.getCoreCfg().getMainHelpBroker().enableHelpKey(this, "org-iesapp-modules-fitxes-entrevista", null);
     
        avui = new java.util.Date();
        initComponents();
        parental = parent;
        
        //Query inici de curs desde SGD
        iniCurs = EvaluacionesCollection.getInicioCurso(cfg.getCoreCfg().getSgdClient());
       
         
        if(!FitxesGUI.moduleGrant.isGranted("entrevistaPares_edit",profile))
        {
            jButton4.setEnabled(false);
            jTabbedPane1.setEnabledAt(0,false);
        }
        this.expedient = profile.getNexp();
        this.profile = profile;
        this.listProfiles = listProfiles;
        double ran = Math.random();
        if(ran<0.33)
        {
            jLabel9.setText("Atenció: Els professors proporcionaran la informació a través de programa. No cal que deixeu els fulls als casillers.");
        }
        else if(ran<=0.33 && ran<0.66)
        {
            jLabel9.setText("Suggeriment: Podeu escriure comentaris de la reunió en la columna ``ACORDS PRESOS´´");            
        }
        else
        {
            jLabel9.setText("Suggeriment: Per no saturar l'equip docent amb missatges, utilitzau ``INFORMACIÓ D'EMERGÈNCIA´´");            
        }
        
        
        jTabbedPane1.setSelectedIndex(1);
        
        if(cfg.activaMissatgeria)
        {
            iniSms = "Entrant als programes fitxes o reserves,";
            jCheckBox1.setEnabled(false);
        }
        else
        {
            iniSms = "Per favor,";
            jLabel9.setVisible(false);
        }
                 
         jTable1.setIntercellSpacing( new java.awt.Dimension(2,2) );
         jTable1.setGridColor(java.awt.Color.gray);
         jTable1.setShowGrid(true);
        
        jDateChooser1.setMinSelectableDate(avui); //evita entrevistes en el passat.
        jDateChooser1.setDate(null);
        jDateChooser1.getJCalendar().getDayChooser().addDateEvaluator(new org.iesapp.framework.util.FestiusDateEvaluator(avui, null, cfg.getCoreCfg()));
       
        
        if(cfg.getCoreCfg().getSgd()==null || cfg.getCoreCfg().getSgd().isClosed())
        {
              JOptionPane.showMessageDialog(null,
                    "No hi ha connexió amb el servidor SGD.\nDisculpau les molèsties.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
              jButton2.setEnabled(false);
              jButton3.setEnabled(false);
        }

        startUp();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser(null, null, null, new JSpinnerDateEditor())
        ;
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Entrevista amb Pares");
        setToolTipText("Entrevista amb Pares");

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel2.setName("jPanel2"); // NOI18N

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("S'enviarà aquest missatge a les PDAs dels professors: (podeu editar-lo)");
        jCheckBox1.setName("jCheckBox1"); // NOI18N
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Tria", "Professor", "Matèria"
            }
        );
        jTable1.setModel(modelTable1);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setRowHeight(32);

        JCheckBox checkbox = new JCheckBox("");
        checkbox.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor( checkbox ));
        jTable1.getColumnModel().getColumn(0).setCellRenderer(new MyCheckBoxRenderer());
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(250);
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel2.setText("Sol·licita informació a l'equip docent de l'alumne:");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText("Data de l'entrevista:");
        jLabel3.setName("jLabel3"); // NOI18N

        jDateChooser1.setLocale(new java.util.Locale("ca"));
        jDateChooser1.setDateFormatString("dd-MM-yyyy");
        jDateChooser1.setName("jDateChooser1"); // NOI18N
        jDateChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser1PropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2))
                .addContainerGap(348, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(1, 1, 1))
        );

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane2.setViewportView(jTextArea1);

        jLabel10.setText("Instruccions per al professorat (si ho desitjau)");
        jLabel10.setName("jLabel10"); // NOI18N

        jTextField1.setToolTipText("Informau a l'equip docent que voleu saber");
        jTextField1.setName("jTextField1"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1))
                        .addGap(7, 7, 7))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(jLabel10)
                .addGap(2, 2, 2)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        jTabbedPane1.addTab("Prepara nova entrevista", jPanel2);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 0, 0));
        jLabel9.setText("Atenció: Els professors proporcionaran la informació a través de programa. No cal que deixeu els fulls als casillers.");
        jLabel9.setName("jLabel9"); // NOI18N

        jTabbedPane2.setName("jTabbedPane2"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 18, Short.MAX_VALUE))
                    .addComponent(jTabbedPane2))
                .addGap(1, 1, 1))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        jTabbedPane1.addTab("Consulta historial", jPanel3);

        jPanel4.setName("jPanel4"); // NOI18N

        jLabel5.setText("Agafa informació a l'interval de dates");
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText("Des de");
        jLabel6.setName("jLabel6"); // NOI18N

        jDateChooser2.setName("jDateChooser2"); // NOI18N

        jLabel7.setText("Fins");
        jLabel7.setName("jLabel7"); // NOI18N

        jDateChooser3.setName("jDateChooser3"); // NOI18N

        jCheckBox2.setText(" Mostra activitats malgrat no se'ls hagi donat nota");
        jCheckBox2.setName("jCheckBox2"); // NOI18N

        jLabel8.setText("Opcions:");
        jLabel8.setName("jLabel8"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jTextArea2.setColumns(20);
        jTextArea2.setEditable(false);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText("Atenció: Aquesta la informació d'emergència s'obté a partir de les dades que el professorat hagi pogut introduir a la seva PDA. Teniu present que pot ésser una informació incompleta.");
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setName("jTextArea2"); // NOI18N
        jTextArea2.setOpaque(false);
        jScrollPane4.setViewportView(jTextArea2);

        jPanel7.setName("jPanel7"); // NOI18N

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/genera.gif"))); // NOI18N
        jButton2.setText("Genera informe");
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(150, 41));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton2);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox2)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(65, 65, 65)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(29, 29, 29))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                        .addGap(29, 29, 29))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jDateChooser3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jDateChooser2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                            .addComponent(jLabel6))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jLabel8))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(jCheckBox2))))
                    .addComponent(jLabel7))
                .addGap(46, 46, 46)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Informació d'emergència", jPanel4);

        jLabel4.setBackground(new java.awt.Color(244, 241, 223));
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(" ");
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setOpaque(true);

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 5));

        jButton1.setText("Cancel·la");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton1);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/insert.gif"))); // NOI18N
        jButton4.setText("Nova Entrevista");
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton4);

        jButton3.setText("Accepta");
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton3);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/back.gif"))); // NOI18N
        jButton5.setToolTipText("Alumne anterior");
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/forward.gif"))); // NOI18N
        jButton6.setToolTipText("Alumne següent");
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(5, 5, 5))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jTabbedPane1)
                .addGap(2, 2, 2)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        int tab = jTabbedPane1.getSelectedIndex();
        boolean sms = jCheckBox1.isSelected();
        if(tab==0)
        {
           
                java.util.Date data = jDateChooser1.getDate();
                if(data==null)
                {
                    JOptionPane.showMessageDialog(null, "Cal que trieu una data per l'entrevista.");
                    return;
                }
                
                //Comprova si ja existeix tal entrevista i evita SMS duplicats
                String SQL1 = "SELECT * FROM tuta_entrevistes WHERE exp2='"+expedient+"' and dia='"+
                        new DataCtrl(data).getDataSQL()+"' ";
                
                try {
                    Statement st = cfg.getCoreCfg().getMysql().createStatement();
                    ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
                    if(rs1!=null && rs1.next())
                    {
                        int missatge = rs1.getInt("sms");
                        if(missatge==1)
                        {
                            JOptionPane.showMessageDialog(null, "Ja hi ha una entrevista amb aquesta data.\n"
                                                               +"Els professors han estat avisats per SMS.");
                            jDateChooser1.setDate(null);
                            return;
                        }
                        else
                        {
                            Object[] options = {"No","Sí"};
                            String txt = "Ja hi ha una entrevista amb aquesta data.\nSegur que voleu procedir?";

                            int n = JOptionPane.showOptionDialog(this,
                            txt, "Confirmació",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                            if(n!=1) {
                            return;
                        }
                         }
                    }
                    if(rs1!=null) {
                    rs1.close();
                }
                 } catch (SQLException ex) {
                Logger.getLogger(EntrevistaPares.class.getName()).log(Level.SEVERE, null, ex);
                }

                String dia = new DataCtrl(data).getDiaMesComplet();

                ArrayList<BeanEntrevistaPares> listbean = new ArrayList<BeanEntrevistaPares>();

                ArrayList<String> profes= new ArrayList<String>();;
                for(int i=0; i<jTable1.getRowCount(); i++)
                {                 
                    boolean tria = (Boolean) jTable1.getValueAt(i, 0);
                    BeanSeleccio selection = listSelect.get(i);
                    if(tria)
                    {
                        profes.add(selection.abrev);

                        BeanEntrevistaPares bean = new BeanEntrevistaPares();
                        bean.setMateria(selection.materia);
                        bean.setProfesor(selection.nomProfe);
                        listbean.add(bean);
                    
                    }
                }

                //Afegeix l'entrevista a la base de dades
                SQL1 = "INSERT INTO tuta_entrevistes (exp2, abrev, dia, dataEnviat, sms, para, observacions) VALUES(?,?,?,NOW(),?,?,?)";
                //System.out.println(expedient+ cfg.getCoreCfg().getUserInfo().getAbrev()+ data+ (sms?1:0) );
                int isms = (sms?1:0);
                Object[] values = new Object[]{expedient, cfg.getCoreCfg().getUserInfo().getAbrev(), data, isms, profes.toString(), jTextField1.getText() };
                int idEntrevista = cfg.getCoreCfg().getMysql().preparedUpdateID(SQL1, values);

                //Envia els SMSs als professors implicats
                int idRemite= cfg.getCoreCfg().getUserInfo().getIdSGD();
                if(idRemite>=0)
                {
                        //Envia als professors
                        // i Crea les sol.licituds d'informacio a missatgeria
                        ArrayList<Integer> aProfes = new ArrayList<Integer>();
                        
                        
                        for(int i=0; i<jTable1.getRowCount(); i++)
                        {
                            boolean tria = (Boolean) jTable1.getValueAt(i, 0);
                       
                            if(tria) {
                                aProfes.add(listSelect.get(i).idProfe);
                            }
                        }
                        
                        //Envia els missatges a les pda's
                        Mensajes smssgd = cfg.getCoreCfg().getSgdClient().getMensajes(idRemite, jTextArea1.getText(), aProfes);
                        smssgd.save();

                        //crea les sol.licituds
                        for(int i=0; i<jTable1.getRowCount(); i++)
                        {
                            boolean tria = (Boolean) jTable1.getValueAt(i, 0);
                            BeanSeleccio seleccio = listSelect.get(i);
                   
                            if(tria && cfg.activaMissatgeria)
                            {
                                    int idmensajeProfesor = smssgd.getDestinatarios().get(seleccio.idProfe);
                                    BeanMissatge bean = new BeanMissatge();
                                    bean.setIdEntrevista(idEntrevista);
                                    bean.setDestinatari_abrev(seleccio.abrev);
                                    bean.setIdMateria(seleccio.idGrupAsig);
                                    bean.setMateria(seleccio.materia);
                                    bean.setIdMensajeProfesor(idmensajeProfesor);
                                    int idBean = cfg.getCoreCfg().getIesClient().getMissatgeriaCollection().saveBeanMissatge(bean);
                                    
                            }
                        }
                         
                }

               AnimatedPanel animatedPanel = new AnimatedPanel(true, 1250, AnimatedPanel.LETTER);
               animatedPanel.setVisible(true);
               
               //Genera l'informe antic si la missatgeria està desactivada
                if(!cfg.activaMissatgeria)
                {
                    ReportingClass rc = new ReportingClass(cfg);

                    HashMap map = new HashMap();
                    map.put("alumne", nombre);
                    map.put("grup", grupo);
                    map.put("data", dia);
                    map.put("tutor", tutor);
                    
                    rc.entrevistaPares(listbean, map, false);
                }
              
      
               jTabbedPane1.setSelectedIndex(1);
               jTabbedPane2.setSelectedIndex(0);
           

        }
        else if(tab>=1)
        {
            for(int i=0; i<jTabbedPane2.getTabCount(); i++)
            {
               HistorialEntrevistes historial = (HistorialEntrevistes) jTabbedPane2.getComponentAt(i);
               historial.commitAcords();
            }
            this.dispose();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if(jCheckBox1.isSelected())
        {
            jTextArea1.setVisible(true);
            jCheckBox1.setText("S'enviarà aquest missatge a les PDAs dels professors: (podeu editar-lo)");
        }
        else
        {
            jTextArea1.setVisible(false);
            jCheckBox1.setText("Fes click per enviar un missatge a les PDAs dels professors");
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jDateChooser1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser1PropertyChange
        java.util.Date date = jDateChooser1.getDate();
        if(date==null) {
            return;
        }
        String dia = new DataCtrl(date).getDiaMesComplet();
        jTextArea1.setText(iniSms + " emplenau informació de l'alumne/a "+
                StringUtils.formataNom(nombre)+" de "+grupo+" per entrevista amb pares dia "+dia);
    }//GEN-LAST:event_jDateChooser1PropertyChange

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
       
        int sel = jTabbedPane1.getSelectedIndex();
        if(sel==0)
        {
            jButton3.setText("Accepta");
            jButton4.setVisible(false);
        }
        else
        {
             jButton3.setText("Tanca");
             jButton4.setVisible(true);
        }
        
        fillTable();
    }//GEN-LAST:event_jTabbedPane1StateChanged

   
    /**
     * Informacio d'emergencia
     * @param evt 
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

       
        String condNota="";
        if(!jCheckBox2.isSelected()) 
        {
            condNota = " AND nota>0 ";
        }
        
        String SQL1 = "SELECT "+
               " actividades.descripcion "+
               "  , actividades.fecha "+
               "  , actividadesalumno.nota "+
               "  , actividadesalumno.idAlumnos "+
               "  , asignaturas.descripcion as asig "+
               "  , asignaturas.id as idasig"+
               "  , profesores.nombre as nombre"+
               "   , alumnos.expediente "+
            " FROM "+
              "   actividades "+
              "   INNER JOIN actividadesalumno  "+
              "       ON (actividades.id = actividadesalumno.idActividades) "+
              "  INNER JOIN alumnos "+
              "       ON (actividadesalumno.idAlumnos = alumnos.id) "+
              "   INNER JOIN grupasig  "+
              "       ON (grupasig.id = actividades.idGrupAsig) "+
              "   INNER JOIN profesores  "+
              "       ON (actividades.idProfesores = profesores.id) "+
              "   INNER JOIN asignaturas  "+
              "       ON (grupasig.idAsignaturas = asignaturas.id) "+
            " WHERE (alumnos.expediente ="+expedient+" AND fecha<>'0000-00-00' " + condNota +
            " AND fecha>='"+new DataCtrl(jDateChooser2.getDate()).getDataSQL()+"' AND fecha<='"+new DataCtrl(jDateChooser3.getDate()).getDataSQL()+"') "+
            " ORDER BY asignaturas.descripcion ASC, actividades.fecha ASC";

         //System.out.println(SQL1);
         InformesSGD infsgd = new InformesSGD(cfg.getCoreCfg().getSgdClient()); //StringUtils.anyAcademic_primer()

        
         ArrayList<BeanEmergencyInfo> listbean = new ArrayList<BeanEmergencyInfo>();
         
         int oldidAsig = -1;
         BeanSGDResumInc resumInc = null;
         
         try {
            Statement st = cfg.getCoreCfg().getSgd().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1,st);
            while (rs1!= null && rs1.next()) {

                int idAsig = rs1.getInt("idasig");
                if(oldidAsig !=idAsig)
                {
                   // //System.out.println("nova asig"+idAsig);
                    resumInc = infsgd.getResumIncidenciesByAsig(expedient, jDateChooser2.getDate(), jDateChooser3.getDate(), idAsig);
                  // //System.out.println(resumInc.getRe()+" "+resumInc.getFj()+" "+resumInc.getFa());
                }
                oldidAsig = idAsig;
                
                BeanEmergencyInfo bean = new BeanEmergencyInfo();
                bean.setAsig(rs1.getString("asig"));
                bean.setData(new DataCtrl(rs1.getDate("fecha")).getDiaMesComplet());
                bean.setNota(rs1.getFloat("nota"));
                bean.setDescripcio(rs1.getString("descripcion"));
                bean.setProfesor(rs1.getString("nombre"));
                
                //ara li afegeixo el resum d'amonestacions
                bean.setAg(resumInc.getAg());
                bean.setAl(resumInc.getAl());
                bean.setAlh(resumInc.getAlh());
                bean.setFa(resumInc.getFa());
                bean.setFj(resumInc.getFj());
                bean.setRe(resumInc.getRe());
                bean.setRj(resumInc.getRj());
                bean.setDi(resumInc.getDi());
                bean.setPa(resumInc.getPa());
                bean.setCn(resumInc.getCp());
                bean.setCp(resumInc.getCn());
                
                //System.out.println(bean.getRe()+" "+bean.getFj()+" "+bean.getFa());
                
                listbean.add(bean);
            }
            if(rs1!=null) {
                 rs1.close();
                 st.close();
             }
        } catch (SQLException ex) {
            Logger.getLogger(EntrevistaPares.class.getName()).log(Level.SEVERE, null, ex);
        }


         if(listbean.isEmpty())
         {
             JOptionPane.showMessageDialog(this, "No s'ha trobat informació.");
             return;
         }
         
         HashMap map = new HashMap();
         map.put("alumno", nombre);
         map.put("datainici", new DataCtrl(jDateChooser2.getDate()).getDiaMesComplet());
         map.put("datafi", new DataCtrl(jDateChooser3.getDate()).getDiaMesComplet());
         map.put("SUBREPORT_DIR",CoreCfg.contextRoot+"\\reports\\");
        
         
         ReportingClass rc = new ReportingClass(cfg);
         rc.emergencyInfo(listbean, map);

    }//GEN-LAST:event_jButton2ActionPerformed

   
    
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_jButton4ActionPerformed

    //Anterior en la llista
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int id = listProfiles.indexOf(profile);
        if(id>0) {
            id = id-1;
        }
        else {
            id = listProfiles.size()-1;
        }
        if(id>=0)
        {
            profile = listProfiles.get(id);
            expedient = profile.getNexp();
            startUp();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    //Seguent en la llista
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        //System.out.println("Size listProfiles"+listProfiles.size());
        int id = listProfiles.indexOf(profile);
        //System.out.println("id"+id);
        if(id<listProfiles.size()-1) {
            id = id+1;
        }
        else {
            id = 0;
        }
         //System.out.println("idnow"+id);
        if(id>=0)
        {
            profile = listProfiles.get(id);
            expedient = profile.getNexp();
            startUp();
        }
    }//GEN-LAST:event_jButton6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDateChooser3;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

  
    public void setAlumnes(Profile numexp, ArrayList<Profile> listexpd) {
        this.expedient = numexp.getNexp();
        this.listProfiles = listexpd;
    }

    private void fillTable()
    {
         for(int i=0; i<jTabbedPane2.getTabCount(); i++)
         {
             HistorialEntrevistes historial = (HistorialEntrevistes) jTabbedPane2.getComponentAt(i);
             if(historial.getAny()==cfg.anyAcademicFitxes)
             {
                 historial.fillTable();
                 break;
             }
         }
          
    }

    
    //Method call every time we switch student
    private void startUp() {
        listSelect = new ArrayList<BeanSeleccio>();
        
        while(jTable1.getRowCount()>0)
        {
            modelTable1.removeRow(0);
        }
   
        //abans tenia aa.id as idGrupAsig
        //Aquesta query dona les assignatures dels alumnes (en el curs actual);
        //Es necessita per poder crear una nova entrevista (es podria agafar els historials)
        String SQL1 = "SELECT DISTINCT alumn.expediente, alumn.nombre, g.grupo, a.descripcion, "
                + " a.id as idAsig, ga.id as idGrupAsig, "+
                "p.codigo AS profesor, p.nombre AS NombreProfe "+
               " FROM Asignaturas a "+
                " INNER JOIN ClasesDetalle cd ON 1=1 "+
                " INNER JOIN HorasCentro hc ON 1=1 "+
                " INNER JOIN Horarios h ON 1=1 "+
                " INNER JOIN GrupAsig ga ON 1=1 "+
                " INNER JOIN Grupos g ON 1=1 "+
                " INNER JOIN AsignaturasAlumno aa ON 1=1 "+
                " INNER JOIN alumnos alumn ON alumn.id=aa.idAlumnos "+
                " LEFT OUTER JOIN Aulas au ON h.idAulas=au.id "+
               " LEFT OUTER JOIN Profesores p ON h.idProfesores=p.id "+
               "  WHERE alumn.expediente="+expedient+" AND (a.descripcion NOT LIKE 'Atenció educativa%') AND "+
                " (a.descripcion NOT LIKE 'Tut%') AND (a.descripcion NOT LIKE 'EA%') AND "+
                "  aa.idGrupAsig=ga.id "+
                " AND  (aa.opcion<>'0' AND (cd.opcion='X' OR cd.opcion=aa.opcion)) "+
                " AND  h.idClases=cd.idClases "+
                " AND cd.idGrupAsig=ga.id "+
                " AND  h.idHorasCentro=hc.id "+
                " AND ga.idGrupos=g.id "+
                " AND  ga.idAsignaturas=a.id "+
                " ORDER BY NombreProfe, a.descripcion";

        nombre ="";
        grupo="";


        try {
            Statement st = cfg.getCoreCfg().getSgd().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1,st);
      
            while (rs1 != null && rs1.next()) {

               nombre= rs1.getString("nombre");
               grupo= rs1.getString("grupo");
               
               int idProfe= rs1.getInt("profesor");              
               String abrev = cfg.getCoreCfg().getIesClient().getProfessoratData().getAbrev(idProfe);
                       
               String nombreprofe= rs1.getString("NombreProfe");
               String descripcion= rs1.getString("descripcion");
               int idasig = rs1.getInt("idAsig");
               int idgrupasig = rs1.getInt("idGrupAsig");
               
               listSelect.add( new BeanSeleccio(nombreprofe,abrev,idProfe,descripcion,idasig,idgrupasig) );

               //evita demanar-me informacio a mi mateix
               boolean inclou =true;
               if(abrev.equals(cfg.getCoreCfg().getUserInfo().getAbrev()) || 
                       descripcion.toUpperCase().startsWith("EA")) {
                    inclou=false;
                }
               
               modelTable1.addRow(new Object[]{inclou, nombreprofe, descripcion});
                        
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EntrevistaPares.class.getName()).log(Level.SEVERE, null, ex);
        }

        jLabel4.setText("["+expedient+"] "+nombre+" - "+grupo);
        jTextArea1.setText(iniSms+" emplenau informació de l'alumne/a "+
                StringUtils.formataNom(nombre)+ " de "+grupo+" per entrevista amb pares.");

        BeanFitxaCurs bean = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
        String any = cfg.anyAcademicFitxes+"-"+(cfg.anyAcademicFitxes+1);
        bean.getFromDB(expedient, any);
        tutor = bean.getProfessor();
    
        jDateChooser2.setDate(iniCurs);
        jDateChooser3.setDate(new java.util.Date());
        
        //retrieve all years for expediente alumno
        BeanDadesPersonals bdp = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
        ArrayList<Integer> anys = BeanFitxaCurs.getAnys(expedient, cfg.getCoreCfg().getMysql());
        ArrayList<Integer> allYears = cfg.getCoreCfg().getIesClient().getAllYears();
        
        //Clear historial
        jTabbedPane2.removeAll();
        
        for(int iany: anys)
        {
            if(allYears.contains(iany))
            {
                //System.out.println("any-->"+iany);
                //Create new historial View instance
                HistorialEntrevistes historial = new HistorialEntrevistes(iany, expedient, cfg);
                historial.fillTable();
                ImageIcon icon;
                if(iany==cfg.anyAcademicFitxes)
                {
                    icon = new ImageIcon(EntrevistaPares.class.getResource("/org/iesapp/modules/fitxes/icons/editable.gif"));
                }
                else
                {
                    icon = new ImageIcon(EntrevistaPares.class.getResource("/org/iesapp/modules/fitxes/icons/locked.gif"));
                }
                jTabbedPane2.insertTab(iany+"-"+(iany+1), icon, historial, null, 0);
            }
            if(jTabbedPane2.getTabCount()>0)
            {
                jTabbedPane2.setSelectedIndex(0);
            }
        }
  
    }
}

