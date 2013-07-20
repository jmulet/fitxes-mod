/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * InformesSGD.java
 *
 * Created on 13-jul-2011, 8:56:45
 */

package org.iesapp.modules.fitxes.dialogs;

import com.toedter.calendar.JSpinnerDateEditor;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.iesapp.clients.iesdigital.actuacions.FactoryRules;
import org.iesapp.clients.iesdigital.fitxes.BeanReportActuacions;
import org.iesapp.framework.pluggable.grantsystem.GrantBean;
import org.iesapp.framework.pluggable.grantsystem.Profile;
import org.iesapp.framework.table.MyCheckBoxRenderer;
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
public class InformesActuacions extends javax.swing.JDialog {
    private final DefaultComboBoxModel modelComboBox1;
    private final DefaultComboBoxModel modelComboBox2;
    private DefaultTableModel modelTable1;
    private final ArrayList<Profile> listexpds;
    private final ArrayList<Integer> listCondSms;
    private final ArrayList<Integer> listCondCarta;
    private ArrayList<Integer> listCondConvivencia;
    private ArrayList<Integer> listCondAssistencia;
    private final Cfg cfg;

    /** Creates new form InformesSGD */
    public InformesActuacions(java.awt.Frame parent, boolean modal, ArrayList<Profile> nexpds, 
            ArrayList<String> noms, final Cfg cfg) {
        super(parent, modal);
        this.cfg = cfg;
        initComponents();

        listexpds = nexpds;
        listCondConvivencia = new ArrayList<Integer>();
        listCondAssistencia = new ArrayList<Integer>();
        
       //Determina quines actuacions són amb SMS o carta
        listCondSms = cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getListConditionEnviament(FactoryRules.SMS);
        listCondCarta = cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getListConditionEnviament(FactoryRules.CARTA);

        
        modelComboBox1 = new DefaultComboBoxModel();
        modelComboBox2 = new DefaultComboBoxModel();

        for(int i=0; i<noms.size(); i++)
        {
            modelComboBox1.addElement(noms.get(i));
            modelComboBox2.addElement(noms.get(i));
        }

        jComboBox2.setModel(modelComboBox1);
        jComboBox3.setModel(modelComboBox2);

        jComboBox3.setSelectedIndex(modelComboBox2.getSize()-1);

        //Query SGD for inici curs

        //jDateChooser1.setDate(informes.datesAvaluacions.get("1aIni"));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cfg.anyAcademicFitxes);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH,1);
                
        jDateChooser1.setDate(cal.getTime());
        jDateChooser2.setDate( cfg.getCoreCfg().getMysql().getServerDate());
        

        String SQL1 = "Select * from tuta_actuacions";
         
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
              
            while(rs1!=null && rs1.next()){
                 int id = rs1.getInt("id");
                 String act = rs1.getString("actuacio");
                 String tipus = rs1.getString("tipus");
                 boolean inc = false;
                 if(tipus.equalsIgnoreCase("ASSISTENCIA"))
                 {
                     listCondAssistencia.add(id);
                 }
                 else if(tipus.equalsIgnoreCase("CONVIVENCIA"))
                 {
                     listCondConvivencia.add(id);
                 }
                 modelTable1.addRow(new Object[]{id, inc, act, tipus});
            }
            rs1.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(InformesActuacions.class.getName()).log(Level.SEVERE, null, ex);
        }
         
             
        marcaTaula(jAsistencia.isSelected(), listCondAssistencia);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser(null, null, null, new JSpinnerDateEditor());
        jLabel3 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser(null, null, null, new JSpinnerDateEditor());
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                if(colIndex==1)
                return true;
                else
                return false;
            }
        }
        ;
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jConvivencia = new javax.swing.JCheckBox();
        jSms = new javax.swing.JCheckBox();
        jAsistencia = new javax.swing.JCheckBox();
        jCarta = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTancades = new javax.swing.JCheckBox();
        jObertes = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Informes d'Actuacions de Tutoria");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel2.setText("Data  des de");
        jLabel2.setName("jLabel2"); // NOI18N

        jDateChooser1.setLocale(new java.util.Locale("ca"));
        jDateChooser1.setDateFormatString("dd-MM-yyyy");
        jDateChooser1.setName("jDateChooser1"); // NOI18N

        jLabel3.setText("Fins");
        jLabel3.setName("jLabel3"); // NOI18N

        jDateChooser2.setLocale(new java.util.Locale("ca"));
        jDateChooser2.setDateFormatString("dd-MM-yyyy");
        jDateChooser2.setName("jDateChooser2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Id", "Seleccionat", "Incidència", "Abrev"
            }
        );
        jTable1.setModel(modelTable1);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new MyCheckBoxRenderer());

        JCheckBox checkbox = new JCheckBox("");
        checkbox.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor( checkbox ));

        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setRowHeight(32);
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        jLabel4.setText("Mostrar actuacions");
        jLabel4.setName("jLabel4"); // NOI18N

        jButton1.setText("Genera");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel·la");
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel5.setText("Alumne des de");
        jLabel5.setName("jLabel5"); // NOI18N

        jComboBox2.setName("jComboBox2"); // NOI18N

        jLabel6.setText("Fins");
        jLabel6.setName("jLabel6"); // NOI18N

        jComboBox3.setName("jComboBox3"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jConvivencia.setText("Convivència");
        jConvivencia.setName("jConvivencia"); // NOI18N
        jConvivencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jConvivenciaActionPerformed(evt);
            }
        });

        jSms.setText(" SMS");
        jSms.setName("jSms"); // NOI18N
        jSms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSmsActionPerformed(evt);
            }
        });

        jAsistencia.setSelected(true);
        jAsistencia.setText("Assistència");
        jAsistencia.setName("jAsistencia"); // NOI18N
        jAsistencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAsistenciaActionPerformed(evt);
            }
        });

        jCarta.setText("Carta");
        jCarta.setName("jCarta"); // NOI18N
        jCarta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCartaActionPerformed(evt);
            }
        });

        jLabel1.setText("Tipus:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel7.setText("Enviament");
        jLabel7.setName("jLabel7"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSms, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jAsistencia)
                            .addGap(10, 10, 10))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jConvivencia)
                            .addContainerGap()))
                    .addComponent(jLabel1)
                    .addComponent(jLabel7)
                    .addComponent(jCarta, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAsistencia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jConvivencia)
                .addGap(24, 24, 24)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSms)
                .addGap(2, 2, 2)
                .addComponent(jCarta)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTancades.setSelected(true);
        jTancades.setText("Tancades");
        jTancades.setName("jTancades"); // NOI18N

        jObertes.setSelected(true);
        jObertes.setText("Obertes");
        jObertes.setName("jObertes"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jObertes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTancades))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox2, 0, 202, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox3, 0, 238, Short.MAX_VALUE)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(17, 17, 17))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jObertes)
                    .addComponent(jTancades))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, 0, 328, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        doLlistatInforme();

        
    }//GEN-LAST:event_jButton1ActionPerformed

    public void doLlistatInforme()
    {
        //crea una llista dels alumnes que realment s'han de generar informe

        int index1 = jComboBox2.getSelectedIndex();
        int index2 = jComboBox3.getSelectedIndex();

        if(index2<index1)
        {
            int aux=index1;
            index1 = index2;
            index2 = aux;
        }

       ArrayList<Integer> filteredListExpd = new ArrayList<Integer>();


      GrantBean gb = FitxesGUI.moduleGrant.get("informeAccions");
      for(int i=index1; i<=index2; i++)
      {
          //comprova si l'alumne pertany a l'usuari, sino no pot obtenir info
          int exp2 = listexpds.get(i).getNexp();
          if( FitxesGUI.moduleGrant.isGranted(gb, listexpds.get(i)) )
          {
              filteredListExpd.add(exp2);
          }
      }



        //condicio sobre tipus accio
        ArrayList<Integer> filteredListInc = new ArrayList<Integer>();

        for(int i=0; i<jTable1.getRowCount(); i++)
        {
            boolean sel = (Boolean) jTable1.getValueAt(i, 1);
            int idTask = ((Number) jTable1.getValueAt(i, 0)).intValue();
            if(sel)
            {
                filteredListInc.add(idTask);
            }
        }

        if(filteredListExpd.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "No hi ha cap alumne de la seva tutoria\nper mostrar informació.");
            return;
        }


        if(filteredListInc.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "No heu triat cap actuació per mostrar.");
        }
        else
        {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //genera la llista
            List listbean = new ArrayList<BeanReportActuacions>();
            
            String txt1 = "(";
            for (Integer s : filteredListInc)
            {
                txt1 +=""+s+",";
            }
            String txt2 = "(";
            for (Integer s : filteredListExpd)
            {
                txt2 +=""+s+",";
            }
            txt1 = StringUtils.BeforeLast(txt1, ",") +")"; 
            txt2 = StringUtils.BeforeLast(txt2, ",") +")";

            
            
            String ini = " (data2 <= '"+new DataCtrl(jDateChooser2.getDate()).getDataSQL()+"' ";
            String condicio = ini + " OR data2 IS NULL)   ";
                  
            if(jTancades.isSelected() && !jObertes.isSelected())
            {
                condicio = ini + " AND data2 IS NOT NULL)   ";
            }
            else if(!jTancades.isSelected() && jObertes.isSelected())
            {
                condicio = "(data2 IS NULL)   ";
            }
            else if(jTancades.isSelected() && jObertes.isSelected())
            {
                condicio = ini+" OR data2 IS NULL)   ";    
            }
                
            
            //Genera la Query
            String SQL1 = " SELECT  "+
                          " CONCAT(xh.Estudis, ' ', xh.Grup) AS grup,  "+
                          " tut.exp2,  "+
                         " CONCAT(  "+
                          "   xes.Llinatge1,  "+
                          "   ' ',  "+
                         "    xes.Llinatge2,  "+
                         "    ', ',  "+
                         "    xes.Nom1  "+
                         "  ) AS alumne,  "+
                         "  CASE WHEN (ISNULL(prof.nombre))   "+
                         "  THEN tut.iniciatper   "+
                         "  ELSE prof.nombre END  "+
                         "  AS propietari,  "+
                         "  act.actuacio,  "+
                         "  tut.data1,  "+
                         "  tut.data2,  "+
                         "  tut.resolucio   "+
                        " FROM  "+
                          " tuta_reg_actuacions AS tut   "+
                         "  LEFT JOIN  "+  
                         "  sig_professorat AS prof   "+  
                         "  ON prof.abrev = tut.iniciatper   "+  
                         "  INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes   "+  
                         "  ON xes.Exp2 = tut.exp2   "+
                         "  INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh "+
                         "  ON xes.Exp2 = xh.Exp2 AND AnyAcademic='"+cfg.anyAcademicFitxes+"'"+
                         "  INNER JOIN  "+  
                         "  tuta_actuacions AS act   "+  
                         "  ON act.id = tut.idActuacio   "+  
                        " WHERE tut.idActuacio IN  "+ txt1 +  
                        "   AND tut.exp2 IN  "+ txt2 +  
                        "   AND data1 >= '"+new DataCtrl(jDateChooser1.getDate()).getDataSQL()+"'   "+  
                        "   AND " + condicio + 
                        "   ORDER BY grup, alumne";

            //System.out.println(SQL1);
            
             try {
                Statement st = cfg.getCoreCfg().getMysql().createStatement();
                ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
              
                while(rs1!=null && rs1.next())
                {
                    BeanReportActuacions bra = new BeanReportActuacions();
                    bra.setActuacio(rs1.getString("actuacio"));
                    bra.setAlumne(rs1.getString("alumne"));
                    bra.setExpd(rs1.getString("exp2"));
                    java.sql.Date fi = rs1.getDate("data2");
                    String fitxt = "";
                    if(fi!=null) {
                        fitxt = new DataCtrl(fi).getDiaMesComplet();
                    }
                    bra.setFi(fitxt);
                    bra.setInici(new DataCtrl(rs1.getDate("data1")).getDiaMesComplet());
                    bra.setPrefectura(rs1.getString("resolucio"));
                    bra.setPropietari(rs1.getString("propietari"));
                    bra.setGrup(rs1.getString("grup"));
                    
                    listbean.add(bra);
                }
                rs1.close();
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(InformesActuacions.class.getName()).log(Level.SEVERE, null, ex);
            }
           

            HashMap map = new HashMap();
            map.put("datainici", new DataCtrl(jDateChooser1.getDate()).getDiaMesComplet());
            map.put("datafinal", new DataCtrl(jDateChooser2.getDate()).getDiaMesComplet());

            //genera el report
            ReportingClass rc = new ReportingClass(cfg);
            rc.sgdReport3(listbean, map);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

    }

  

    //Tick convivència
    private void jConvivenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jConvivenciaActionPerformed
        marcaTaula(jConvivencia.isSelected(), listCondConvivencia);
    }//GEN-LAST:event_jConvivenciaActionPerformed

    //Tick Assistència
    private void jAsistenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAsistenciaActionPerformed
        marcaTaula(jAsistencia.isSelected(), listCondAssistencia);
    }//GEN-LAST:event_jAsistenciaActionPerformed

    private void jSmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSmsActionPerformed
       marcaTaula(jSms.isSelected(), listCondSms);
    }//GEN-LAST:event_jSmsActionPerformed

    private void jCartaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCartaActionPerformed
        marcaTaula(jCarta.isSelected(), listCondCarta);
    }//GEN-LAST:event_jCartaActionPerformed

     private void marcaTaula(boolean selected, ArrayList<Integer> llista) {

         for(int i=0; i<jTable1.getRowCount(); i++)
         {
             int codig = ((Number) jTable1.getValueAt(i, 0)).intValue();
             if(llista.contains(codig)) {
                jTable1.setValueAt(selected, i, 1);
            }
         }
    }

      @Override
    protected JRootPane createRootPane() {
    JRootPane rootPane2 = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
    Action actionListener = new AbstractAction() {
      public void actionPerformed(ActionEvent actionEvent) {
        setVisible(false);
      }
    } ;
    InputMap inputMap = rootPane2.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(stroke, "ESCAPE");
    rootPane2.getActionMap().put("ESCAPE", actionListener);

    return rootPane2;
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jAsistencia;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCarta;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JCheckBox jConvivencia;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JCheckBox jObertes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JCheckBox jSms;
    private javax.swing.JTable jTable1;
    private javax.swing.JCheckBox jTancades;
    // End of variables declaration//GEN-END:variables



}
