/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FitxaAlumne.java
 *
 * Created on 07-abr-2011, 17:50:57
 */

package org.iesapp.modules.fitxes.dialogs;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.iesapp.clients.iesdigital.fitxes.BeanDadesPersonals;
import org.iesapp.clients.iesdigital.fitxes.BeanFitxaCurs;
import org.iesapp.clients.iesdigital.fitxes.BeanHistoric;
import org.iesapp.framework.pluggable.grantsystem.Profile;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.modules.fitxes.FitxesGUI;
import org.iesapp.modules.fitxes.dialogs.admin.DlgCreaFitxa;
import org.iesapp.modules.fitxes.forms.FormDadesPersonals;
import org.iesapp.modules.fitxes.forms.FormFitxaCurs;
import org.iesapp.modules.fitxes.reports.ReportingClass;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class FitxaAlumne extends javax.swing.JInternalFrame {
    private final FormDadesPersonals formDadesPersonals1;   
    private FitxesGUI parental;
    private Profile profile;
    private final Cfg cfg;

    /** Creates new form FitxaAlumne */
    public FitxaAlumne(FitxesGUI par, boolean modal, int nexpedient, ArrayList expds, final Cfg cfg) {
        //super(par, modal);
        this.cfg = cfg;
        initComponents();
        parental = par;
       
        formDadesPersonals1 = new FormDadesPersonals(parental);
        jScrollPane1.setViewportView(formDadesPersonals1);

        //this.setIconImage(new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/appIcon.gif")).getImage());
        this.setTitle("Fitxa alumne/a");
      
        nexp = nexpedient;
        listExpds = expds;
      
        startUp();
    }

    private void startUp() {

        //Obté la posició antiga del tab principal
        int oldpos = jTabbedPane1.getSelectedIndex();
        
        //Obté la posició antiga del tab de Dadespersonals
        int oldpos2 = formDadesPersonals1.getSelectedIndex();
   
        
        vectorFitxes = new ArrayList<FormFitxaCurs>();

        while(jTabbedPane1.getTabCount()>1) {
            jTabbedPane1.remove(1);
        }

        BeanDadesPersonals bean1 = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
        bean1.getFromDB(nexp, cfg.anyAcademicFitxes);
        profile = AccionsAlumne4.createProfileFromDP(bean1);
        formDadesPersonals1.setData(bean1,cfg);

        byte[] photo = bean1.getFoto();
        if(photo!=null)
        {
            //make sure you rescale the photo to the suitable size
            mostraImatge(photo);
        }
        else
        {
            if(bean1.getSexe().contains("D"))
            {
               jPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/default2.gif")));
            }
            else 
            {
                jPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/default.gif")));
            }
            
        }


// Descomentat, assegura que es crei la fitxa pel curs academic seleccionat
// Es preferible que es faci de cop ja que així, s'importen el nom de tutors, etc.        
//        String cursActual = anyacademic + "-" + (anyacademic+1);
//        if(!bean1.isFitxaCursCreated(nexp,cursActual))
//                        bean1.createFitxaCurs(nexp,cursActual);



      
// Manage Permisos

        boolean editableDP = false;
        boolean pertany = FitxesGUI.belongs.contains(bean1.getExpedient());//DbUtils.isEditable(cfg.getCoreCfg().getUserInfo().getAbrev(), bean1.getExpedient());

        //boto de canvi def fitxa a accions

       
        formDadesPersonals1.startUp(pertany);
        editableDP = FitxesGUI.moduleGrant.isGranted("dadesPersonals_edit", profile);
        formDadesPersonals1.setEditable(editableDP, pertany);



        jButton2.setEnabled( FitxesGUI.moduleGrant.isGranted("fitxesCtrl_crear", profile) );
        jButton3.setEnabled( FitxesGUI.moduleGrant.isGranted("fitxesCtrl_esborrar", profile) );
        jButton7.setEnabled( FitxesGUI.moduleGrant.isGranted("dadesPrimaria_view", profile) );
        jButton4.setEnabled( FitxesGUI.moduleGrant.isGranted("informeFitxaTutoria_gen", profile) );
    

//comprova si hi ha l'informe de primaria disponible
         String path = CoreCfg.coreDB_FitxesDataPath + "/"+ StringUtils.AddZeros(nexp) + "_primaria.pdf";
         java.io.File file = new java.io.File(path);
         if (!file.exists()){
                jButton7.setEnabled(false);
         }
         file=null;


//comprova per quants d'anys hi ha fitxa?
        ArrayList<String> years = bean1.getYears(nexp);

        //Ara crea tabs per tots els anys que existeixen
          
         for(String element: years) {
          
            FormFitxaCurs fitxa = new FormFitxaCurs();

            boolean editable=false;
            //Manage permisos
            String fullAnyAcademic = cfg.anyAcademicFitxes+"-"+(cfg.anyAcademicFitxes+1);
            if(element.equals(fullAnyAcademic))
            {
                 editable = FitxesGUI.moduleGrant.isGranted("fitxaActual_edit", profile);
            }
            else
            {
                 editable = FitxesGUI.moduleGrant.isGranted("fitxaAnterior_edit", profile);
            }

        

            vectorFitxes.add(fitxa);

            BeanFitxaCurs bean2 = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
            bean2.getFromDB(nexp, element);
            
            //comprova si el nom del tutor està en blanc i si es així utilitza
            //la informacio de l'historic a l'any pertinent
            if(bean2.getProfessor().trim().isEmpty())
            {
                String nomtutor = new BeanHistoric(bean2.getExp_FK_ID(), bean2.getIdCurs_FK_ID(), cfg.getCoreCfg().getIesClient()).getProfTutor();
                bean2.setProfessor(nomtutor);
            }
            int lastelement = vectorFitxes.size()-1;
            vectorFitxes.get(lastelement).setData(bean2, cfg);
            vectorFitxes.get(lastelement).setEditable(editable, profile);


            if(!cfg.getCoreCfg().getUserInfo().getAbrev().equals("GUARD"))
            {
                    JScrollPane jScrollPane = new JScrollPane();
                    jScrollPane.setViewportView(vectorFitxes.get(lastelement));
                    jScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                    jScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
                    jTabbedPane1.addTab(element, null, jScrollPane);
                    if(editable)
                    {
                       jTabbedPane1.setIconAt(jTabbedPane1.getTabCount()-1, new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/editable.gif")));
                    }
                    else
                    {
                        jTabbedPane1.setIconAt(jTabbedPane1.getTabCount()-1, new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/locked.gif")));
                    }
            }
        }

        if(editableDP)
        {
             jTabbedPane1.setIconAt(0, new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/editable.gif")));
        }
        else
        {
            jButton2.setEnabled(false);
            jButton3.setEnabled(false);
            jTabbedPane1.setIconAt(0, new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/locked.gif")));
        }

        
        //Restaura les posicions dels elements
        
         if(oldpos==0) {
            jTabbedPane1.setSelectedIndex(0);
        }
         else {
            jTabbedPane1.setSelectedIndex(jTabbedPane1.getTabCount()-1);
        }

         formDadesPersonals1.setSelectedIndex(0);
       
         
         String titol="";

         int lastelement = vectorFitxes.size()-1;
         //BeanFitxaCurs bean2 = vectorFitxes.get(lastelement).getData();

         titol = "["+bean1.getExpedient()+"] "+
                  bean1.getLlinatge1()+" "+bean1.getLlinatge2()+", "+bean1.getNom1()+
                 "  -  "+ bean1.getEstudis() +" " + bean1.getGrupLletra();

         jLabel1.setText(titol);



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
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButton3 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jLabel3 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jButton7 = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jButton1 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jPhoto = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Fitxa de l'alumne");

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jTabbedPane1.addTab("Dades Personals", jScrollPane1);

        jPanel2.setName("jPanel2"); // NOI18N

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/insert.gif"))); // NOI18N
        jButton2.setText("  Nou   ");
        jButton2.setToolTipText("Crea una fitxa per un nou curs");
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jToolBar1.add(jSeparator5);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/delete.gif"))); // NOI18N
        jButton3.setText("  Esborra  ");
        jButton3.setToolTipText("Esborra la fitxa del curs seleccionat");
        jButton3.setFocusable(false);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar1.add(jSeparator3);

        jLabel3.setText("         ");
        jLabel3.setName("jLabel3"); // NOI18N
        jToolBar1.add(jLabel3);

        jSeparator8.setName("jSeparator8"); // NOI18N
        jToolBar1.add(jSeparator8);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/primaria.gif"))); // NOI18N
        jButton7.setText("  Primària  ");
        jButton7.setToolTipText("Mostra informació informes de primària");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton7);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar1.add(jSeparator6);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/pdf.gif"))); // NOI18N
        jButton4.setText("  Informe  ");
        jButton4.setToolTipText("Genera la fitxa de tutoria");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jToolBar1.add(jSeparator4);

        jLabel2.setText("        ");
        jLabel2.setName("jLabel2"); // NOI18N
        jToolBar1.add(jLabel2);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jToolBar1.add(jSeparator7);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/exit.gif"))); // NOI18N
        jButton1.setText("Desa  i Tanca");
        jButton1.setToolTipText("Aplica els canvis i tanca la finestra");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jToolBar1.add(jSeparator2);

        jPhoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPhoto.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPhoto.setName("jPhoto"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setBackground(new java.awt.Color(244, 241, 223));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("GRUP: ..........");
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setOpaque(true);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/forward.gif"))); // NOI18N
        jButton6.setToolTipText("Alumne següent");
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/back.gif"))); // NOI18N
        jButton5.setToolTipText("Alumne anterior");
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addGap(2, 2, 2))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
            .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 183, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Dades personals");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Aplica i tanca
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         
         this.actualitzaDB();
        
         this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       //obri un dialeg que et demana el curs
        DlgCreaFitxa dlg = new DlgCreaFitxa(null,true);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);

        if(dlg.crea)
        {
             String any = dlg.getSelection().trim();

             for(int i=1; i<jTabbedPane1.getTabCount(); i++)
             {
                 if(jTabbedPane1.getTitleAt(i).equals(any)) {
                    return;
                }
             }

             BeanDadesPersonals bean1 = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
             bean1.getFromDB(nexp, cfg.anyAcademicFitxes);
             if(!bean1.isFitxaCursCreated(nexp,any)){
                        bean1.createFitxaCurs(nexp,any);
             }

             FormFitxaCurs fitxa = new FormFitxaCurs();
             vectorFitxes.add(fitxa);
             BeanFitxaCurs bean2 = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
             bean2.getFromDB(nexp, any);
             int lastelement = vectorFitxes.size()-1;
             vectorFitxes.get(lastelement).setData(bean2, cfg);
           
             jTabbedPane1.insertTab(any, null, vectorFitxes.get(lastelement),"", 1);


        }
        dlg.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    //Esborra una fitxa
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        int sel = jTabbedPane1.getSelectedIndex();
        if(sel<1) {
            return;
        } //can't delete personal info

        //ask confirmation
        //Custom button text
        Object[] options = {"Cancel·la", "Si"};
        int n = JOptionPane.showOptionDialog(this,
                "La fitxa seleccionada serà\n"
              + "esborrada. Voleu continuar?",
                "Confima esborrar",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if(n==0) {
            return;
        }

        FormFitxaCurs fitxa = vectorFitxes.get(sel-1);
        fitxa.deleteFromDB();
        jTabbedPane1.remove(sel);
        vectorFitxes.remove(sel-1);
    }//GEN-LAST:event_jButton3ActionPerformed

    //Anterior
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        this.actualitzaDB();
        int id = listExpds.indexOf(nexp);
        if(id>0) { 
            id = id-1;
        }
        else {
            id = listExpds.size()-1;
        }
        nexp = listExpds.get(id);
        startUp();
    }//GEN-LAST:event_jButton5ActionPerformed

    //Següent
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        this.actualitzaDB();
        int id = listExpds.indexOf(nexp);
        if(id<listExpds.size()-1) {
            id = id+1;
        }
        else {
            id = 0;
        }
        nexp = listExpds.get(id);
       startUp();
    }//GEN-LAST:event_jButton6ActionPerformed

    //Genera el PDF
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
            ReportingClass rp = new ReportingClass(cfg);
            rp.exportFitxaCompleta(nexp);
        
        
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String path = CoreCfg.coreDB_FitxesDataPath + "/"+ StringUtils.AddZeros(nexp) + "_primaria.pdf";
        try {
          Desktop.getDesktop().open(new java.io.File(path));
        } catch (IOException ex) {
                //
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jButton7ActionPerformed

      
      private void actualitzaDB() {
         
         //Actualitza les dades personals (nomes administrador i prefectura)
         BeanDadesPersonals bean1 = formDadesPersonals1.getData();
         if( FitxesGUI.moduleGrant.isGranted("dadesPersonals_edit", profile) ){
              bean1.commitToDB(nexp);
         }
                           
         //Actualitza totes les fitxes dels cursos; en tot cas
         for(int i=0; i<vectorFitxes.size(); i++)
         {
              FormFitxaCurs fitxa = vectorFitxes.get(i);
              BeanFitxaCurs bean2 = fitxa.getData();
              //if(fitxa.isEditable())
                        bean2.commitToDB(nexp);
         }
    }

    private void mostraImatge(byte[] foto)
    {
        int heigth2 = 85;
        Image image = Toolkit.getDefaultToolkit().createImage(foto);
        Image scaledInstance = image.getScaledInstance(-1, heigth2, Image.SCALE_SMOOTH);

        jPhoto.setIcon( new ImageIcon(scaledInstance) ); // NOI18N
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jPhoto;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
   
    private int nexp;
    private ArrayList<FormFitxaCurs> vectorFitxes;
    private ArrayList<Integer> listExpds;

    public void setAlumnes(int numexp, ArrayList<Integer> listexpd) {
        this.nexp = numexp;
        this.listExpds = listexpd;
        startUp();
        
    }

   

}
