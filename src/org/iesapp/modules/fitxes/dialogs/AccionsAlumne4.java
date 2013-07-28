/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AccionsAlumne.java
 *
 * Created on 08-jul-2011, 8:59:57
 */

package org.iesapp.modules.fitxes.dialogs;


import com.l2fprod.common.swing.JLinkButton;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.iesapp.clients.iesdigital.actuacions.Actuacio;
import org.iesapp.clients.iesdigital.actuacions.BeanRules;
import org.iesapp.clients.iesdigital.alumnat.Grup;
import org.iesapp.clients.iesdigital.fitxes.BeanDadesPersonals;
import org.iesapp.clients.iesdigital.fitxes.BeanFitxaCurs;
import org.iesapp.clients.iesdigital.fitxes.PareTutor;
import org.iesapp.clients.iesdigital.fitxes.SGDImporter;
import org.iesapp.clients.sgd7.evaluaciones.EvaluacionesCollection;
import org.iesapp.clients.sgd7.reports.InformesSGD;
import org.iesapp.framework.data.User;
import org.iesapp.framework.pluggable.deamons.TopModuleDeamon;
import org.iesapp.framework.pluggable.grantsystem.Profile;
import org.iesapp.framework.pluggable.modulesAPI.ErrorDisplay;
import org.iesapp.framework.table.*;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.IconUtils;
import org.iesapp.modules.fitxes.FitxesDeamon;
import org.iesapp.modules.fitxes.FitxesGUI;
import org.iesapp.modules.fitxes.reports.ReportingClass;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.JSEngine;
import org.iesapp.util.StringUtils;


/**
 *
 * @author Josep
 */
public class AccionsAlumne4 extends javax.swing.JInternalFrame {
    private final int timerInterval=100;  //1000 ms = 1 second
    private DefaultComboBoxModel comboModel1total;
    private DefaultComboBoxModel comboModel2total;
    private ArrayList<Integer> listIds1total;
    private ArrayList<Integer> listIds2total;
    private ArrayList<Integer> llistaIdTasksInTable;
    public  static Grup grup;
    private boolean listening=false;
    private boolean locked;
    private boolean admin;
    private final boolean tutor;
    protected final Timer timer;
    private HashMap<String,Object> resourceMap; 
    private CellDateRenderer cellDateRenderer1;
    private CellDateRenderer cellDateRenderer2;
    private final Color HIGHLIGHTCOLOR=Color.ORANGE;
    private final Color HIGHLIGHTCOLOR2=new Color(120,255,100);
    private final Color HIGHLIGHTCOLOR3=new Color(255,100,120);
    private MyIconLabelRenderer iconLabelRenderer1;
    private ArrayList<Actuacio> listActuacions;
    private Actuacio currentActuacio;
    private final FitxesGUI parental;
    private Profile profile;
    private final Cfg cfg;
    private final JSEngine jsEngine;
  
    
      private void tasksTimer() {
       if(FitxesGUI.pend!=null && !FitxesGUI.pend.isRunning())
       {
           timer.stop();
           startUp();
       }
   }
    
    public void setAlumnes(int nexp, ArrayList<Integer> expds)
    {
        this.expedient = nexp;
        this.listExpds = expds;
        listening = false;
        startUp();
        fillTable();
        listening = true;
    }
      
    /** Creates new form AccionsAlumne */
    public AccionsAlumne4(FitxesGUI parent, boolean modal, int nexp, ArrayList<Integer> expds, Cfg cfg) {
        this.cfg = cfg;      
        initComponents();
        jTabbedPane1.setTitleAt(0,cfg.anyAcademicFitxes+"-"+(cfg.anyAcademicFitxes+1));
        jTabbedPane1.setIconAt(0, new ImageIcon(AccionsAlumne4.class.getResource("/org/iesapp/modules/fitxes/icons/editable.gif")));
      
        cfg.getCoreCfg().getMainHelpBroker().enableHelpKey(this, "org-iesapp-modules-fitxes-actuacions", null);
     
        
        //Scripting engine for this class
        jsEngine = JSEngine.getJSEngine(getClass(), CoreCfg.contextRoot);
        
        this.parental = (FitxesGUI) parent;
        
        ButtonGroup group1 = new ButtonGroup();
        group1.add(jObertes);
        group1.add(jSms);
        group1.add(jCarta);
        
        
        timer = new Timer(timerInterval, new ActionListener ()
        {
            @Override
                public void actionPerformed(ActionEvent e)
                {
                    tasksTimer();
                }

         });
        
    

        
        jTable1.setIntercellSpacing( new java.awt.Dimension(2,2) );
        jTable1.setGridColor(java.awt.Color.gray);
        jTable1.setShowGrid(true);
        
        expedient = nexp;
        listExpds = expds;

        //Condicio d'aministrador
        admin = (cfg.getCoreCfg().getUserInfo().getGrant() == User.ADMIN)
                || (cfg.getCoreCfg().getUserInfo().getGrant() == User.PREF)
                || FitxesGUI.moduleGrant.get("accions_fullEdit").isAll();
        
        //Condicio de tutor
        tutor = !FitxesGUI.moduleGrant.get("accions_fullEdit").isNone();
        startUp();
        
        

        //he canviat l'ordre de aquestes dues linies 12/9/11
         fillTable();
         listening = true;
    }

    
    
    
    
    private void startUp() {
        //Update fitxa d'aquest alumne
//        new UpdateFromSGD(CoreCfg.coreDB_sgdDB, ""+cfg.anyAcademicFitxes, cfg.getCoreCfg().getUserInfo().getAbrev()
//                    ,expedient, cfg.getCoreCfg()).doUpdate();
        
        dp = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
        dp.getFromDB(expedient, cfg.anyAcademicFitxes);
        profile = createProfileFromDP(dp);
       //DbUtils.isEditable(cfg.getCoreCfg().getUserInfo().getAbrev(), bean1.getExpedient());
        grup = new Grup(Grup.XESTIB, dp.getEnsenyament(), dp.getEstudis(), dp.getGrupLletra(), cfg.getCoreCfg().getIesClient());
        
        updatePendents();
     
        //si no estic a l'any que toca no permet editar res
        //tampoc deixa editar si s'ha desabilitat l'edició del programa
        //tampoc si es tracta d'un alumne d'ensenyament GM o GS (11/10/2011)
       
        locked = !StringUtils.anyAcademic_primer().equals(""+cfg.anyAcademicFitxes) ||
           (CoreCfg.coreDB_EditFitxes==0 && cfg.getCoreCfg().getUserInfo().getGrant()!=User.ADMIN  && cfg.getCoreCfg().getUserInfo().getGrant()!=User.PREF ) ||
                !FitxesGUI.moduleGrant.isGranted("accions_fullEdit", profile);
      
         jComboBox1.setEnabled(!locked);
         jComboBox2.setEnabled(!locked);
        
        byte[] photo = dp.getFoto();
        if(photo!=null)
        {
            //make sure you rescale the photo to the suitable size
            mostraImatge(photo);
        }
        else
        {
             jPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/default.gif")));
        }
          
        
        bean1 = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
        bean1.getFromDB(expedient, cfg.anyAcademicFitxes+"-"+(cfg.anyAcademicFitxes+1));

        faltesnj = bean1.getImportacioSGD().get("FA").getNTotal();
        faltes = bean1.getImportacioSGD().get("FJ").getNTotal();
        jProgressBar1.setValue(faltesnj);
        jProgressBar1.setString(""+faltesnj);
        jProgressBar3.setValue(faltes);
        jProgressBar3.setString(""+faltes);

        int greus0 = 0;
        if(bean1.getImportacioSGD().containsKey("AG"))
        {
            greus0 = bean1.getImportacioSGD().get("AG").getNTotal();
        }
        int lleus0 = 0;
        if(bean1.getImportacioSGD().containsKey("AL"))
        {
            lleus0 = bean1.getImportacioSGD().get("AL").getNTotal();
        }
        greus = greus0 + lleus0/5.0;
                
        jProgressBar2.setValue(greus0);
        jProgressBar2.setString(""+greus0);

        jProgressBar5.setValue(lleus0);
        jProgressBar5.setString(""+lleus0);
        
        jLabel12.setText("Equivalent greus "+greus);

        String message = "["+expedient+"] "+ dp.getLlinatge1()+" "+dp.getLlinatge2()+", "+dp.getNom1() 
                + "  -  "+dp.getEstudis() + " "+dp.getGrupLletra();
        jLabel1.setText(message);

        nret = 0;
        if(bean1.getImportacioSGD().containsKey("RE"))
        {
            nret = bean1.getImportacioSGD().get("RE").getNTotal();
        }
        jProgressBar4.setValue(nret);
        jProgressBar4.setString(""+nret);
        
        
        //Carrega els comboBox
        comboModel1total = new DefaultComboBoxModel();
        comboModel2total = new DefaultComboBoxModel();
        comboModel1 = new DefaultComboBoxModel();
        comboModel2 = new DefaultComboBoxModel();

        listIds1total=new ArrayList<Integer>();
        listIds2total=new ArrayList<Integer>();
        listIds1=new ArrayList<Integer>();
        listIds2=new ArrayList<Integer>();
        listCond1=new ArrayList<Integer>();
        listCond2=new ArrayList<Integer>();
      
        comboModel1.addElement("Triau una opció");
        comboModel1total.addElement("Triau una opció");
        comboModel2.addElement("Triau una opció");
        comboModel2total.addElement("Triau una opció");
        
        listIds1total.add(-1);
        listIds2total.add(-1);
        listIds1.add(-1);
        listIds2.add(-1);
        listCond1.add(-1);
        listCond2.add(-1);
        
        cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getAllRules(comboModel1total, listIds1total, comboModel1, listIds1, "ASSISTENCIA", dp.getEnsenyament(), dp.getEstudis(), admin, tutor);
        cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getAllRules(comboModel2total, listIds2total, comboModel2, listIds2, "CONVIVENCIA", dp.getEnsenyament(), dp.getEstudis(), admin, tutor);
        jComboBox1.setModel(comboModel1);
        jComboBox2.setModel(comboModel2);

        createResourceMap();

        fillTable();
        //Fill old tables
        while(jTabbedPane1.getTabCount()>1)
        {
            jTabbedPane1.remove(1);
        }
        Icon icon = new ImageIcon(AccionsAlumne4.class.getResource("/org/iesapp/modules/fitxes/icons/locked.gif"));
        ArrayList<Integer> allYears = cfg.getCoreCfg().getIesClient().getAllYears();
        for(Integer year: BeanFitxaCurs.getAnys(expedient, cfg.getCoreCfg().getMysql()))
        {
            if(allYears.contains(year) && year!=cfg.anyAcademicFitxes)
            {
                HistorialActuacions ha = new HistorialActuacions(year, expedient, cfg);
                jTabbedPane1.insertTab(year+"-"+(year+1), icon, ha, null, 1);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jProgressBar2 = new javax.swing.JProgressBar();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jProgressBar5 = new javax.swing.JProgressBar();
        jButton3 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jProgressBar3 = new javax.swing.JProgressBar();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jProgressBar4 = new javax.swing.JProgressBar();
        jButton2 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPhoto = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean q= false;
                if(locked) return false;

                //if(FitxesGUI.userInfo.getGrant()==User.ADMIN || FitxesGUI.userInfo.getAbrev().equals("PREF"))
                if(admin)
                q= true;   //Disallow the editing of any cell
                else
                q= false;

                if(colIndex!=4) q=false;

                return q;
            }
        }
        ;
        jPanel5 = new javax.swing.JPanel();
        jCarta = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jObertes = new javax.swing.JCheckBox();
        jSms = new javax.swing.JCheckBox();
        jButton10 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Actuacions de tutoria");
        setToolTipText("");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setBackground(new java.awt.Color(244, 241, 223));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(" ");
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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton5)
                .addGap(3, 3, 3)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addComponent(jButton6)
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel7.setName("jPanel7"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName("jPanel3"); // NOI18N

        jProgressBar2.setMaximum(30);
        jProgressBar2.setName("jProgressBar2"); // NOI18N
        jProgressBar2.setString("0");
        jProgressBar2.setStringPainted(true);

        jLabel3.setText("Acumulat AG:");
        jLabel3.setName("jLabel3"); // NOI18N

        jComboBox2.setName("jComboBox2"); // NOI18N
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel8.setText("Accions:  ");
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel7.setText("Acumulat AL:");
        jLabel7.setName("jLabel7"); // NOI18N

        jProgressBar5.setMaximum(10);
        jProgressBar5.setName("jProgressBar5"); // NOI18N
        jProgressBar5.setString("0");
        jProgressBar5.setStringPainted(true);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iesapp/modules/fitxes/icons/view.gif"))); // NOI18N
        jButton3.setText("Detalls");
        jButton3.setToolTipText("Mostra un informe de sancions acumulades");
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText(" ");
        jLabel12.setName("jLabel12"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(47, 47, 47)
                        .addComponent(jComboBox2, 0, 142, Short.MAX_VALUE))
                    .addComponent(jButton3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(jProgressBar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jProgressBar5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N

        jProgressBar1.setMaximum(50);
        jProgressBar1.setName("jProgressBar1"); // NOI18N
        jProgressBar1.setString("0");
        jProgressBar1.setStringPainted(true);

        jLabel2.setText("Acumulat FNJ");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel4.setText("Acumulat FJ");
        jLabel4.setName("jLabel4"); // NOI18N

        jProgressBar3.setName("jProgressBar3"); // NOI18N
        jProgressBar3.setString("0");
        jProgressBar3.setStringPainted(true);

        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel5.setText("Accions:  ");
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText("Acumulat Retards:");
        jLabel6.setName("jLabel6"); // NOI18N

        jProgressBar4.setName("jProgressBar4"); // NOI18N
        jProgressBar4.setString("0");
        jProgressBar4.setStringPainted(true);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iesapp/modules/fitxes/icons/view.gif"))); // NOI18N
        jButton2.setText("Detalls");
        jButton2.setToolTipText("Mostra un informe d'assistència i puntualitat");
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                            .addComponent(jProgressBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jProgressBar4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jButton2))
                .addGap(2, 2, 2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jProgressBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jProgressBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.setName("jPanel8"); // NOI18N

        jPhoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPhoto.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPhoto.setName("jPhoto"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPhoto, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        jPanel4.setName("jPanel4"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Data inici", "Acció", "Data fi", "Prefectura", "Document"
            }
        );
        jTable1.setModel(modelTable1);
        Icon[] icons = new Icon[] {
            IconUtils.getIconResource(getClass().getClassLoader(),"iesapp/modules/fitxes/icons/print2.gif"),
            IconUtils.getBlankIcon(),
            IconUtils.getDeleteIcon()
        };

        jTable1.setModel(modelTable1);

        cellDateRenderer1 = new CellDateRenderer();
        cellDateRenderer2 = new CellDateRenderer();
        iconLabelRenderer1 = new MyIconLabelRenderer(icons);

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(new MyIconButtonRenderer(icons));
        jTable1.getColumnModel().getColumn(1).setCellRenderer(cellDateRenderer1);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(cellDateRenderer2);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(iconLabelRenderer1);
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new TextAreaRenderer());
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new MyIconButtonRenderer(icons));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setRowHeight(32);
        jTable1.setName("jTable1"); // NOI18N
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTable1PropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.addTab("tab1", jScrollPane1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
        );

        jPanel5.setName("jPanel5"); // NOI18N

        jCarta.setText("Carta");
        jCarta.setToolTipText("S'ha d'enviar una carta");
        jCarta.setName("jCarta"); // NOI18N
        jCarta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCartaActionPerformed(evt);
            }
        });

        jLabel11.setText("Resalta actuacions:");
        jLabel11.setName("jLabel11"); // NOI18N

        jObertes.setSelected(true);
        jObertes.setText("Obertes");
        jObertes.setToolTipText("Resalta totes les actuacions obertes");
        jObertes.setName("jObertes"); // NOI18N
        jObertes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jObertesActionPerformed(evt);
            }
        });

        jSms.setText("SMS");
        jSms.setToolTipText("S'ha d'enviar un SMS");
        jSms.setName("jSms"); // NOI18N
        jSms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSmsActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iesapp/modules/fitxes/icons/exit.gif"))); // NOI18N
        jButton10.setText("Tanca");
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(14, 14, 14)
                .addComponent(jObertes)
                .addGap(10, 10, 10)
                .addComponent(jSms)
                .addGap(10, 10, 10)
                .addComponent(jCarta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 280, Short.MAX_VALUE)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCarta)
                    .addComponent(jLabel11)
                    .addComponent(jObertes)
                    .addComponent(jSms)
                    .addComponent(jButton10)))
        );

        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setPreferredSize(new java.awt.Dimension(100, 110));

        jPanel6.setBackground(new java.awt.Color(255, 204, 204));
        jPanel6.setMinimumSize(new java.awt.Dimension(100, 48));
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(34, 44));
        jPanel6.setLayout(new com.l2fprod.common.swing.PercentLayout());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iesapp/modules/fitxes/icons/flag_red.gif"))); // NOI18N
        jLabel9.setText("Pendents:");
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(90, 24));
        jPanel6.add(jLabel9);

        jScrollPane2.setViewportView(jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Seguent alumne de la llista
     * @param evt 
     */
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
         int id = listExpds.indexOf(expedient);
        if(id<listExpds.size()-1) {
            id = id+1;
        }
        else {
            id = 0;
        }
        expedient = (listExpds.get(id)).intValue();
        startUp();
        //fillTable();
}//GEN-LAST:event_jButton6ActionPerformed

    /**
     * Anterior alumne de la llista
     * @param evt 
     */
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int id = listExpds.indexOf(expedient);
        if(id>0) {
            id = id-1;
        }
        else {
            id = listExpds.size()-1;
        }
        expedient = (listExpds.get(id)).intValue();
        startUp();
        //fillTable();
}//GEN-LAST:event_jButton5ActionPerformed

    /**
     * Tanca la finestra d'actuacions
     * @param evt 
     */
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        listening = false;
        TopModuleDeamon deamon = TopModuleDeamon.getActiveDeamons().get(FitxesDeamon.class.getName()+"@"+cfg.getCoreCfg().getUserInfo().getAbrev());
        if(deamon!=null)
        {
            deamon.checkStatus();
        }
        //System.out.println("calling filltable of parental ");
        parental.fillTable();
        this.dispose();
    }//GEN-LAST:event_jButton10ActionPerformed

    /***************************************************************************
        crea una nova acció de tipus assistència
    ***************************************************************************/
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        
        
        int index = jComboBox1.getSelectedIndex();
        jComboBox1.setSelectedIndex(0);
        if(index<1) {
            return;
        }
        jComboBox1.hidePopup();
         
        //Crea la rule 
        int idRule = listIds1.get(index);
        onNovaAccio(idRule, 0);

    }//GEN-LAST:event_jComboBox1ActionPerformed

    
    /***
     *  CREA UNA NOVA ACTUACIO DE CONVIVENCIA
     ***/
    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
         int index = jComboBox2.getSelectedIndex();
         jComboBox2.setSelectedIndex(0);
         if(index<1) {
            return;
        }
         jComboBox2.hidePopup();
                  
       //Crea la rule 
        int idRule = listIds2.get(index);
        onNovaAccio(idRule, 0); 
    }//GEN-LAST:event_jComboBox2ActionPerformed

    
    private void esborraActuacio(int row)
    {
        if(locked) {
            return;
        }

//        
//        CellTableState cts2 = (CellTableState) jTable1.getValueAt(row, 2);
//        int idRule = Math.abs(cts2.getCode());
//        
       
        if(!FitxesGUI.moduleGrant.isGranted("accions_fullEdit",profile) && listActuacions.get(row).beanRule.isNomesAdmin())
        {
               return; 
                                                               
        }
        
        if(FitxesGUI.moduleGrant.isGranted("accions_esborrar",profile))
        {
            Object[] options = {"No","Sí"};
            String missatge = "Voleu esborrar aquesta actuació?";

            int n = JOptionPane.showOptionDialog(this,
            missatge, "Confirmació",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

            if(n!=1) {
                return;
            }
            
            String creador = "";
            int id = ((CellTableState) jTable1.getValueAt(row,0)).getCode();
            String SQL1 = "SELECT iniciatper FROM tuta_reg_actuacions WHERE id="+id;
            try {
                Statement st = cfg.getCoreCfg().getMysql().createStatement();
                ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
                while(rs1!=null && rs1.next())
                {
                    creador = rs1.getString("iniciatper");
                }
                if(rs1!=null) {
                    rs1.close();
                    st.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AccionsAlumne4.class.getName()).log(Level.SEVERE, null, ex);
            }
            
             //També és imprescindible esborrar les entrades de l'sgd que hi pugui haver-hi associades
             //System.out.println("Estic a punt d'esborrar l'actuacio n. "+id);
             org.iesapp.framework.actuacions.IncidenciesSGD1.clearAll(id,cfg.getCoreCfg().getMysql());
            
            //Farem un safe delete que consisteix en moure l'actuacio dins d'una taula "TRASH"
            SQL1 = "UPDATE tuta_reg_actuacions SET iniciatper='" + creador + "; "+cfg.getCoreCfg().getUserInfo().getAbrev() + "' WHERE id="+id;
            int nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
            
            SQL1 = "INSERT INTO tuta_reg_actuacions_deleted (SELECT * FROM tuta_reg_actuacions WHERE id="+id+")";
            nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);

            SQL1 = "DELETE FROM tuta_reg_actuacions WHERE id="+id;
            nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
            
            //Pel cas dels expedients tambe cal esborrar els instructors associats amb aquest cas
             SQL1 = "DELETE FROM tuta_instructors WHERE idCas="+id;
             nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
            
            //En qualssevol cas cal esborrar el registre de dies de càstig: expulsio i/o dimecres
                SQL1 = "DELETE FROM tuta_dies_sancions WHERE idActuacio="+id;
                nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
           
            //Actualitza la base de dades de l'alumne
            //Actualitza l'alumne segons el seu calendari d'avaluacions
            new SGDImporter(CoreCfg.coreDB_sgdDB, ""+cfg.anyAcademicFitxes, 
                             cfg.getCoreCfg().getUserInfo().getAbrev(),
                             expedient, null, cfg.getCoreCfg().getIesClient()).start();
               
            //Actualitza les tasques pendents    
            FitxesGUI.pend.checkTasquesPendents(expedient);  
            timer.start();
            fillTable();
            
            updateFitxaAlumne();
        }
    }
    
    
    
    private void tancarActuacio(int row)
    {
           
            if(locked || row<0) {
            return;
           }
        
            Actuacio actuacio = listActuacions.get(row);
            
            if( FitxesGUI.moduleGrant.isGranted("accions_tancar",profile))
            {
              
                if(!FitxesGUI.moduleGrant.isGranted("accions_fullEdit",profile))
                {
                    if( actuacio.beanRule.isNomesAdmin() ) {
                        return;
                    }
                }
                 
                Object[] options = {"No","Sí"};
                String missatge = "Voleu donar aquesta actuació per tancada?";

                int n = JOptionPane.showOptionDialog(this,
                missatge, "Confirmació",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

                if(n!=1) {
                    return;
                }
                      
                
                String txt = (String) jTable1.getValueAt(row, 4);
                
                if(actuacio.beanRule.getEnviamentSMS().equalsIgnoreCase("S") ||
                  (actuacio.beanRule.getEnviamentSMS().startsWith("$") && actuacio.map.get(actuacio.beanRule.getEnviamentSMS()).equals("X")))
                {
                    txt = "SMS Enviat; " + txt;
                    jTable1.setValueAt(txt, row, 4);
                }
                else if(actuacio.beanRule.getEnviamentCarta().equalsIgnoreCase("S") ||
                       (actuacio.beanRule.getEnviamentCarta().startsWith("$") && actuacio.map.get(actuacio.beanRule.getEnviamentCarta()).equals("X")))
                {
                    txt = "Carta Enviada; " + txt;
                    jTable1.setValueAt(txt, row, 4);
                }
                
                
                String SQL1 = "UPDATE tuta_reg_actuacions SET resolucio=?, data2=CURRENT_DATE() WHERE id=?";
                Object[] obj = new Object[]{txt, actuacio.id_actuacio};
                int nup = cfg.getCoreCfg().getMysql().preparedUpdate(SQL1, obj);
            }
                    
            fillTable();
            
             //comprova si queden actuacions sense tancar
                boolean tottancat = true;
                for(int i=0; i<jTable1.getRowCount(); i++)
                {
                    java.util.Date data2 = (java.util.Date) jTable1.getValueAt(i,3);
                    if(data2==null) 
                    {
                        tottancat=false;
                        break;
                    }
                }
                if(tottancat)
                {
                    FitxesGUI.pend.removeOberta(expedient);
                }
    }
    
    //Mostra el document,, Genera un informe; esborra; etc...
    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int shiftPressed = (evt.getModifiers() & InputEvent.SHIFT_MASK);
        
        int col = jTable1.getSelectedColumn();
        int row = jTable1.getSelectedRow();
        if(col<0 || row<0) {
            return;
        }
        java.util.Date data22 = (java.util.Date) jTable1.getValueAt(row, 3);
        
         if(evt.getClickCount()<2 && col==3) {
            return;
        }
             
        //esborra
        if(col==0)
        {
            if(data22==null || (shiftPressed==1 && admin) )  
            {
                esborraActuacio(row);
            }
            else
            {
               JOptionPane.showMessageDialog(this, "Només l'administrador pot esborrar\nactuacions finalitzades.");
            }
        }
        else if(col==3)
        {
            if(data22==null)
            {
                tancarActuacio(row);
            }
            else if(data22!=null &&  (shiftPressed==1 && admin)  )
            {
                //obrir de nou l'actuació
                //desa a la base de dades
                CellTableState cts0 = (CellTableState) jTable1.getValueAt(row, 0);
                int id = cts0.getCode();
                String SQL1 = "UPDATE tuta_reg_actuacions SET data2=NULL WHERE id='"+id+"'";
                int nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
                fillTable();
                
            }
        }
        else if(col==5)
        {
            onShowAction(row);
        }

        
    }//GEN-LAST:event_jTable1MouseClicked

    
    //listen to property changes in the table
    private void jTable1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTable1PropertyChange
        if(!listening) {
            return;
        }
        int row=jTable1.getSelectedRow();
        int col=jTable1.getSelectedColumn();
        if(col==4 && row>=0)
        {
            //hi ha un canvi en els comentatis de prefectura
            int id = ((CellTableState) jTable1.getValueAt(row, 0)).getCode();
            String comment = (String) jTable1.getValueAt(row, 4);

            String SQL1 = "UPDATE tuta_reg_actuacions SET resolucio=? where id=?";
            Object[] obj = new Object[]{comment,id};
            int nup = cfg.getCoreCfg().getMysql().preparedUpdate(SQL1, obj);
        }
    }//GEN-LAST:event_jTable1PropertyChange

    //Treu un informe d'assitencia i puntualitat
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
     
        InformesSGD informes = new InformesSGD(cfg.getCoreCfg().getSgdClient());
     
          //condicio sobre tipus d'incidencia
        ArrayList<String> filteredListInc = new ArrayList<String>();
        filteredListInc.add("FA");
        filteredListInc.add("F");
        filteredListInc.add("FJ");
        filteredListInc.add("RE");
        filteredListInc.add("R");
        filteredListInc.add("RJ");
     
        ArrayList<Integer> filteredListExpd = new ArrayList<Integer>();
        filteredListExpd.add(expedient);
       
        //this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
       //genera la llista
        List listbean = informes.getListIncidencies(filteredListExpd, filteredListInc, 0);

        HashMap map = new HashMap();
        map.put("datainici", new DataCtrl(EvaluacionesCollection.getInicioCurso(cfg.getCoreCfg().getSgdClient())).getDiaMesComplet());
        map.put("datafinal", new DataCtrl(new java.util.Date()).getDiaMesComplet());

        //genera el report
        ReportingClass rc = new ReportingClass(cfg);
        rc.sgdReport1(listbean, map);
        //this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
       

    }//GEN-LAST:event_jButton2ActionPerformed

    //Treu un informe de sancions
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        InformesSGD informes = new InformesSGD(cfg.getCoreCfg().getSgdClient());
     
          //condicio sobre tipus d'incidencia
        ArrayList<String> filteredListInc = new ArrayList<String>();
        filteredListInc.add("AL");
        filteredListInc.add("AG");
        filteredListInc.add("ALH");
        
        ArrayList<Integer> filteredListExpd = new ArrayList<Integer>();
        filteredListExpd.add(expedient);
       
        //this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
       //genera la llista
        List listbean = informes.getListIncidencies(filteredListExpd, filteredListInc, 0);

        HashMap map = new HashMap();
        map.put("datainici", new DataCtrl(EvaluacionesCollection.getInicioCurso(cfg.getCoreCfg().getSgdClient())).getDiaMesComplet());
        map.put("datafinal", new DataCtrl(new java.util.Date()).getDiaMesComplet());

        //genera el report
        ReportingClass rc = new ReportingClass(cfg);
        rc.sgdReport1(listbean, map);
       // this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jCartaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCartaActionPerformed
       fillTable();
    }//GEN-LAST:event_jCartaActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        //System.out.println("calling filltable of parental ");
        //parentalcc.fillTable();
    }//GEN-LAST:event_formInternalFrameClosing

    private void jObertesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jObertesActionPerformed
        fillTable();
    }//GEN-LAST:event_jObertesActionPerformed

    private void jSmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSmsActionPerformed
       fillTable();
    }//GEN-LAST:event_jSmsActionPerformed

    protected void fillTable()
    {
        fillTableBasic();
        doHightLight();
    }

    private void fillTableBasic() {
        
        llistaIdTasksInTable = new ArrayList<Integer>();

        while(jTable1.getRowCount()>0) {
            modelTable1.removeRow(0);
        }
        
        listActuacions = new ArrayList<Actuacio>();

        String SQL1 = "SELECT * FROM tuta_reg_actuacions WHERE exp2='"+expedient+"' ORDER BY data1";
         try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while (rs1 != null && rs1.next()) {
                int id = rs1.getInt("id");
                int idrule = rs1.getInt("idActuacio");
                
                Actuacio act = new Actuacio(cfg.getCoreCfg().getUserInfo().getAbrev(), null, true, expedient, id, idrule,
                    false, admin, dp.getEnsenyament(), dp.getEstudis(), resourceMap, cfg.getCoreCfg().getIesClient());
 
                listActuacions.add(act);
                
                String creator = "";
                String creatorAbrev = act.creador;
                if(creatorAbrev.equals("ADMIN"))
                {
                    creator = "Administrador";
                }
                else if(creatorAbrev.equals(User.PREF))
                {
                    creator = "Prefectura";
                }
                else
                {
                    creator = cfg.abrev2prof.get(creatorAbrev);
                }
                CellTableState cts = new CellTableState("",-1,0);
                cts.setTooltip("Edita i visualitza el document");
                CellTableState cts2 = new CellTableState(act.beanRule.getDescripcio(), -act.beanRule.getIdRule(), 1);
                CellTableState cts0 = new CellTableState("", id, 2);
                cts0.setTooltip("Creador: "+creator);

                modelTable1.addRow(new Object[]{cts0, act.data1, cts2, act.data2, act.resolucio, cts});
            }
            if (rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccionsAlumne4.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }


    private void updatePendents() {

        //Comprova Tasques pendents
        if(!FitxesGUI.pend.jobs.containsKey(expedient))
        {
            jScrollPane2.setVisible(false);
        }
        else
        {
            jScrollPane2.setVisible(true);
            jPanel6.removeAll();
            jPanel6.add(jLabel9);
            //Crea una llista avançada de tasques pendents
            int i=0;
            for(int idRule: FitxesGUI.pend.jobs.get(expedient).idTasks)
            {                
                JLinkButton jLinkButton1 = new JLinkButton();
                jLinkButton1.setActionCommand(idRule+"");
                jLinkButton1.setText(" ["+FitxesGUI.pend.jobs.get(expedient).detallTasks.get(i)+"] ");
                //Crea la rule 
                jLinkButton1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int idRule = Integer.parseInt( e.getActionCommand() );
                        onNovaAccio(idRule, 0); 
                        
                    }
                });
                i += 1;
        
                 jPanel6.add(jLinkButton1);
            }
            jPanel6.repaint();
            
            
//            String tip = FitxesGUI.pend.jobs.get(expedient).detallTasks.toString();
//            String tooltip = tip;
//            En cas de longitud massa gran la retallam
//            if(tip.length()>120) tip = tip.substring(0,120)+" ···  | més >>";
//            
//            jLabel10.setText(tip);
//            if(tip.length()!=tooltip.length()) jLabel10.setToolTipText(tooltip);
//            
//            if(dp.getEnsenyament().equals("GM") || dp.getEnsenyament().equals("GS"))
//            {
//                jLabel10.setText("Atenció: Funcionalitat deshabilitada per a estudis de GM/GS");
//                jLabel10.setToolTipText(null);
//            }

        }
    }
 
    
    
    private void mostraImatge(byte[] foto)
    {
        int heigth2 = jPhoto.getHeight()>0?jPhoto.getHeight():100;
        Image image = Toolkit.getDefaultToolkit().createImage(foto);
        Image scaledInstance = image.getScaledInstance(-1, heigth2, Image.SCALE_SMOOTH);

        jPhoto.setIcon( new ImageIcon(scaledInstance) ); // NOI18N
    }


    
    //modification: 9-10-11 
    //Retorna 0: si no hi es 
    //Retorna 1: si n'hi ha alguna i totes estan tancades
    //Retorna 2: si n'hi ha alguna pero s'en troben d'obertes
    
    private int isTaskAlreadyInTable(int idTask) {
        int isthere = 0;
        boolean isthereOpen = false;
        
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
             CellTableState cts2 = (CellTableState) jTable1.getValueAt(i, 2);
             java.util.Date fin = (java.util.Date) jTable1.getValueAt(i, 3);
             int myid = Math.abs(cts2.getCode());
             if(myid==idTask)
             {
                 isthere = 1;
                 if(fin==null) {
                     isthereOpen=true;
                 }
             }
        }
        
        if(isthereOpen) {
            isthere = 2;
        }
        
        return isthere;
    }
    
     /**
     * Registra a la fitxa de l'alumne totes aquelles actuacions que son registrables
     */
      protected void updateFitxaAlumne() {
         
          String msg = "";
          for (Actuacio act : listActuacions) {
              String data = new DataCtrl(act.data1).getDiaMesComplet();
              if (act.beanRule.isRegisterInFitxaAlumne()) {
                  msg += data + ": " + act.beanRule.getDescripcioLlarga() + "\n";
              }

          }
          
          if(!msg.isEmpty())
          {
              BeanFitxaCurs bfc = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
              bfc.getFromDB(expedient, cfg.anyAcademicFitxes + "-" + (cfg.anyAcademicFitxes + 1));
              String sancions = bfc.getSancions();
              String sancions0 = StringUtils.BeforeFirst(sancions, "<<");
              String sancions1 = StringUtils.AfterLast(sancions, ">>");
              sancions = sancions0 + "<<\n" + msg + ">>" + sancions1;
              bfc.setSancions(sancions);
              bfc.commitToDB(expedient);
          }
    }

       
    
    
@Override

    protected JRootPane createRootPane() {
    JRootPane rootPane2 = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
    Action actionListener = new AbstractAction() {
        @Override
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
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBox jCarta;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JCheckBox jObertes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel jPhoto;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JProgressBar jProgressBar3;
    private javax.swing.JProgressBar jProgressBar4;
    private javax.swing.JProgressBar jProgressBar5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox jSms;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables


    private int expedient;
    private ArrayList<Integer> listExpds;
    private DefaultComboBoxModel comboModel1;
    private DefaultComboBoxModel comboModel2;
    private DefaultTableModel modelTable1;

    private BeanDadesPersonals dp;
    private BeanFitxaCurs bean1;

    private ArrayList<Integer> listIds1;
    private ArrayList<Integer> listIds2;

    private ArrayList<Integer> listCond1;
    private ArrayList<Integer> listCond2;

    private int faltes=0;
    private int faltesnj=0;
    private double greus=0;
    private int nret = 0;

    private void doHightLight() {
        if(jObertes.isSelected())
        {
            ArrayList<Integer> rows = new ArrayList<Integer>();
            for(int i=0; i<jTable1.getRowCount(); i++)
            {
                
                if(jTable1.getValueAt(i, 3)==null) {
                    rows.add(i);
                }
            }
            
            cellDateRenderer1.setHighLightRows(rows);
            cellDateRenderer2.setHighLightRows(rows);
            iconLabelRenderer1.setRows(rows);
            iconLabelRenderer1.setHighLightColor(HIGHLIGHTCOLOR);
            cellDateRenderer1.setHighLightColor(HIGHLIGHTCOLOR);
            cellDateRenderer2.setHighLightColor(HIGHLIGHTCOLOR);            
        }
        else if(jSms.isSelected())
        {
            ArrayList<Integer> rows = new ArrayList<Integer>();
            for(int i=0; i<jTable1.getRowCount(); i++)
            {
                Actuacio actuacio = listActuacions.get(i);
                String enviament = actuacio.beanRule.getEnviamentSMS();
                boolean shouldSendSms = false;
                if(enviament.equalsIgnoreCase("S"))
                {
                    shouldSendSms = true;
                }
                else if(!enviament.equalsIgnoreCase("N") && !enviament.equalsIgnoreCase("S") && !enviament.isEmpty())
                {
                    if(actuacio.map.containsKey(enviament) && !actuacio.map.get(enviament).equals(""))
                    {
                        shouldSendSms = true;
                    }
                }
                if(jTable1.getValueAt(i, 3)==null && shouldSendSms) {
                    rows.add(i);
                }
            }
            
            cellDateRenderer1.setHighLightRows(rows);
            cellDateRenderer2.setHighLightRows(rows);
            iconLabelRenderer1.setRows(rows);
            iconLabelRenderer1.setHighLightColor(HIGHLIGHTCOLOR2);
            cellDateRenderer1.setHighLightColor(HIGHLIGHTCOLOR2);
            cellDateRenderer2.setHighLightColor(HIGHLIGHTCOLOR2);
            
        }
        else if(jCarta.isSelected())
        {
            ArrayList<Integer> rows = new ArrayList<Integer>();
            for(int i=0; i<jTable1.getRowCount(); i++)
            {
                Actuacio actuacio = listActuacions.get(i);
                String enviament = actuacio.beanRule.getEnviamentCarta();
                boolean shouldSendCarta = false;
                if(enviament.equalsIgnoreCase("S"))
                {
                    shouldSendCarta = true;
                }
                else if(!enviament.equalsIgnoreCase("N") && !enviament.equalsIgnoreCase("S") && !enviament.isEmpty())
                {
                    if(actuacio.map.containsKey(enviament) && !actuacio.map.get(enviament).equals(""))
                    {
                        shouldSendCarta = true;
                    }
                }
                if(jTable1.getValueAt(i, 3)==null && shouldSendCarta) {
                    rows.add(i);
                }
            }
            
            cellDateRenderer1.setHighLightRows(rows);
            cellDateRenderer2.setHighLightRows(rows);
            iconLabelRenderer1.setRows(rows);
            iconLabelRenderer1.setHighLightColor(HIGHLIGHTCOLOR3);
            cellDateRenderer1.setHighLightColor(HIGHLIGHTCOLOR3);
            cellDateRenderer2.setHighLightColor(HIGHLIGHTCOLOR3);
            
        }
        else
        {
            cellDateRenderer1.setHighLightColor(null);
            cellDateRenderer2.setHighLightColor(null);
            iconLabelRenderer1.setHighLightColor(null);
        }
        fillTableBasic();  
    }

    /**
     * Mostra una actuacio ja feta
     * @param row 
     */
    private void onShowAction(int row) {
        
            currentActuacio = listActuacions.get(row);
            currentActuacio.locked = locked || (currentActuacio.data2!=null); //!admin && 

            java.awt.Point p = this.getLocation();
            p.x = p.x + this.getSize().width;
            p.y = p.y + 5;
            
            currentActuacio.position = p;


            displayActuacio(currentActuacio);
     }

    /**
     * Crea el mapa de recursos per esser utilitzat per l'alumne actual
     * fieldIni              
        $ADRECA     
        $ANYACADEMIC      
        $EXPEDIENT            
        $HORARI_VISITA   
        $PARENT_NAMES      
        $STUDENT_BIRTHDATE  
        $STUDENT_DNI        
        $STUDENT_GRUP         
        $STUDENT_ISREPETIDOR  
        $STUDENT_NAME         
        $TELEFON              
        $TODAY_TXT            
        $TUTOR_NAME           
     */
    private void createResourceMap() {
        resourceMap = new HashMap<String, Object>();
        Calendar cal1 = Calendar.getInstance();

        ArrayList<PareTutor> pt = dp.getTutorsInfo();
        String pares = "";
        for (int i = 0; i < pt.size(); i++) {
            pares += pt.get(i).getNom() + ", ";
        }
        pares = StringUtils.BeforeLast(pares, ",");

        
        Calendar cal0 = Calendar.getInstance();
        cal0.setTime(dp.getDataNaixament());
        float millisInYear = 1000f*60f*60f*24f*365f;                
        float anys = (cal1.getTimeInMillis() - cal0.getTimeInMillis())/millisInYear;       
        cal0.add(Calendar.YEAR, 18); //Aixo es la data que fa ver els 18
        boolean majorEdat = cal0.compareTo(cal1)<=0;
        
        String nomcomplet = dp.getLlinatge1()+" "+ dp.getLlinatge2()+", "+dp.getNom1();
        
        //Instructors
        HashMap<String,String> profes = new HashMap<String,String>();
//        String SQL1 = "SELECT DISTINCT prof,nombre FROM sig_horaris AS hor INNER JOIN sig_professorat "
//                    + " AS profes ON hor.prof = profes.abrev WHERE curso = '"+AccionsAlumne4.grup.getKCursInt()+"' "
//                    + "  AND nivel = '"+AccionsAlumne4.grup.getKNivell()+"' AND grupo = '"+AccionsAlumne4.grup.getKGrup()+"' AND torn = 0 ";

        //Per seguretat, mostra tots els professors com a instructors
         String SQL1 = "SELECT DISTINCT abrev, nombre FROM sig_professorat order by nombre ASC";
             try {
                Statement st = cfg.getCoreCfg().getMysql().createStatement();
                ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
                while (rs1 != null && rs1.next()) {
                    String abrev = rs1.getString("abrev");
                    profes.put(abrev, rs1.getString("nombre")+" ["+abrev+"]");
                }
                if(rs1!=null) {
                    rs1.close();
                    st.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AccionsAlumne4.class.getName()).log(Level.SEVERE, null, ex);
            }

        //crea els possibles instructors per aquest alumne
        //Aquest mapa ha d'estar ordenat
        profes =  StringUtils.getSortedMap(profes);
         
        ArrayList<String> instructors = new ArrayList<String>();
        HashMap<String, Integer> mapaPics = new HashMap<String, Integer>();

        SQL1 ="SELECT idInstructor, COUNT(inst.id) AS pics FROM tuta_instructors AS inst INNER JOIN "
                    + " tuta_reg_actuacions AS treg ON treg.id=inst.idCas "
                    + " WHERE treg.data1>='"+cfg.anyAcademicFitxes+"-09-01' AND treg.data1<='"+(cfg.anyAcademicFitxes+1)+"-09-31' "
                    + " GROUP BY idInstructor";
          
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
                while (rs1 != null && rs1.next()) {
                    String abrev = rs1.getString("idInstructor");
                    int pics = rs1.getInt("pics");

                    mapaPics.put(abrev,pics);
                }
                if(rs1!=null) {
                rs1.close();
                st.close();
            }
            } catch (SQLException ex) {
                Logger.getLogger(AccionsAlumne4.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(String abrev: profes.keySet())
            {
                int pics = 0;
                if(mapaPics.containsKey(abrev)) {
                    pics=mapaPics.get(abrev);
                }
                instructors.add("<"+pics+"> "+ profes.get(abrev));
            }
            mapaPics.clear();
            profes.clear();
           
           
            
            
        Date now = new java.util.Date();
        
        //determina l'horari de visita del tutor
        
        ArrayList<String> listabrev = dp.getProfPermisos();
        String horariVisita="";
        if(!listabrev.isEmpty()) {
            horariVisita = cfg.getCoreCfg().getIesClient().getFitxesClient().getFitxesUtils().getDataVisita(listabrev.get(0));
        }
               
        resourceMap.put("$TODAY_TXT", new DataCtrl().getLongData());
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");  
        SimpleDateFormat df2 = new SimpleDateFormat("H:m:s");  
        resourceMap.put("$TODAY", df.format(now));
        resourceMap.put("$HORA", df2.format(now));
        resourceMap.put("$STUDENT_NAME", StringUtils.formataNom(nomcomplet));
        resourceMap.put("$STUDENT_GRUP", dp.getEstudis() + " " + dp.getGrupLletra());
        resourceMap.put("$STUDENT_DNI", dp.getDni());
        resourceMap.put("$STUDENT_BIRTHDATE", df.format(dp.getDataNaixament()));       
        resourceMap.put("$STUDENT_ISREPETIDOR", dp.isRepetidor()?"X":"");
        resourceMap.put("$PARENT_NAMES", pares);
        resourceMap.put("$TUTOR_NAME", bean1.getProfessor());
        resourceMap.put("$HORARI_VISITA", horariVisita);
        resourceMap.put("$EXPEDIENT", "" + expedient);
        resourceMap.put("$MAJOREDAT", majorEdat?"S":"N");
        resourceMap.put("$ANYSDECIMAL", anys+"");
        resourceMap.put("$LIST_OF_INSTRUCTORS", instructors);
        resourceMap.put("$ANYACADEMIC", cfg.anyAcademicFitxes+"-"+(cfg.anyAcademicFitxes+1));
        resourceMap.put("$ADRECA", dp.getAdreca());
        resourceMap.put("$CITY", dp.getLocalitat());
        
        String telefons = "";
        for(PareTutor paretutor: dp.getTutorsInfo()){
            telefons += paretutor.getTelefons()+", ";
        }
    
        //dp.getTelefonsUrgencia().toString().replace("[", "").replace("]","")
        resourceMap.put("$TELEFON", telefons);
        resourceMap.put("$ANYS_DECIMAL", dp.getAnysDecimal());
        
        resourceMap.put("$CURSACADEMIC", "01/09/"+cfg.anyAcademicFitxes+" a 31/06/"+(cfg.anyAcademicFitxes+1));       
        resourceMap.put("$AVALUACIO", ""); //TODO
        
        //Further extend the resouce map with js
          
            try {
                Bindings bindings = jsEngine.getBindings();
                bindings.put("resourceMap", resourceMap);
                bindings.put("dp", dp);
                bindings.put("coreCfg", cfg.getCoreCfg());
                jsEngine.evalBindings(bindings);
                //System.out.println("resourceMap before:"+resourceMap);
                jsEngine.invokeFunction("extendResourceMap");
                //System.out.println("resourceMap after:"+resourceMap);     
            } catch (Exception ex) {
                ErrorDisplay.showMsg(ex.toString());
            }
         
    }
    
    /*
     * Comprova si l'actuacio esta dins dels rangs d'edat correctes.
     */

    private boolean checkEdatRanges(final Actuacio actuacio) {
        boolean checkMin;
        boolean checkMax;
        Calendar calnow= Calendar.getInstance();
       
        if (actuacio.beanRule.getMinAge() <= 0) {
            checkMin = true;
        } else {
            Calendar cal0 = Calendar.getInstance();
            cal0.setTime(dp.getDataNaixament());
            cal0.add(Calendar.YEAR, actuacio.beanRule.getMinAge());
            checkMin = cal0.compareTo(calnow) <= 0;
        }

        if (actuacio.beanRule.getMaxAge() >= 100) {
            checkMax = true;
        } else {
            Calendar cal0 = Calendar.getInstance();
            cal0.setTime(dp.getDataNaixament());
            cal0.add(Calendar.YEAR, actuacio.beanRule.getMaxAge());
            checkMax = cal0.compareTo(calnow) >= 0;    
        }

            
            
         if(!checkMin || !checkMax )
         {
             
             String motiuerror = "L'alumne ha de tenir ";
             if(!checkMin && checkMax)
             {
                 motiuerror += " més de "+actuacio.beanRule.getMinAge()+ " anys";
             }
             else if(checkMin && !checkMax)
             {
                 motiuerror += " menys de "+actuacio.beanRule.getMaxAge()+ " anys";
             }
             else if(!checkMin && !checkMax)
             {
                 motiuerror += "una edat compresa entre "+actuacio.beanRule.getMinAge()+" i "+actuacio.beanRule.getMaxAge()+" anys";
             }
              JOptionPane.showMessageDialog(this, motiuerror +"\nper poder aplicar aquesta mesura.");
              return false; 
         }
         return true;
    }

    /**
     * comprova segons la politica si ja existeix l'actuacio
     * @param actuacio
     * @return 
     */
    private boolean checkInstancesPolicy(Actuacio actuacio) {
        
        int existeix = isTaskAlreadyInTable(actuacio.id_rule);
        if(actuacio.beanRule.getInstancesPolicy().equals(BeanRules.POLICY_SINGLE))
        {
            if(existeix>0)
            {
              JOptionPane.showMessageDialog(this, "La politica actual impedeix crear múltiples\nactuacions del tipus "+actuacio.beanRule.getDescripcio());                
              return false;
            }
        }
        else
        {
            if (actuacio.beanRule.getInstancesPolicy().equals(BeanRules.POLICY_MULTIPLE_WAIT)) 
            {
                if(existeix==2) //existeix l'accio i es troba oberta
                {   
                    JOptionPane.showMessageDialog(this, "Ja existeix un procediment similar OBERT.\nHeu d'esperar que es finalitzi o be\ncomunicar-ho a prefectura.");
                    return false;
                }
        
            } 
            else if (actuacio.beanRule.getInstancesPolicy().equals(BeanRules.POLICY_MULTIPLE_WARNING) && existeix>0) {
                 
                JOptionPane.showMessageDialog(this, "Atenció, ja hi ha creat ''"+actuacio.beanRule.getDescripcio()+"''"); 
            
            }
        }
        
        
        //Ara comprova si aquesta accio requeix que altres estiguin tancades per poder obrir-se
        if(!admin)
        {
        String quines = "";
        ArrayList<Integer> obertes = new ArrayList<Integer>();
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
            if(jTable1.getValueAt(i, 3)==null)
            {
                CellTableState cts2 = (CellTableState) jTable1.getValueAt(i, 2);
                int id = Math.abs(cts2.getCode());
                if(!obertes.contains(id))
                {
                    obertes.add(id);
                    if( actuacio.beanRule.getRequiredClosed()!=null && actuacio.beanRule.getRequiredClosed().contains(id))
                    {
                        quines += " - " + ((CellTableState) jTable1.getValueAt(i, 2)).getText()+"\n";
                    }
                }
                
            }
        }
        
        obertes.clear();
        obertes = null;
        if(!quines.isEmpty())
        {
                JOptionPane.showMessageDialog(this, "Per poder dur a terme aquesta actuació\ncal que hagin finalitzat les actuacions:\n"+quines);
                return false;
        }  
        
        ////Ara comprova si aquesta accio requeix que altres s'hagin creat previament
        if (actuacio.beanRule.getRequiredCreated() != null && !actuacio.beanRule.getRequiredCreated().isEmpty()) {
       
                ArrayList<Integer> created = new ArrayList<Integer>();
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    CellTableState cts2 = (CellTableState) jTable1.getValueAt(i, 2);
                    int id = Math.abs(cts2.getCode());
                    created.add(id);
                }

                quines = "";
                for(Integer idRequired: actuacio.beanRule.getRequiredCreated())
                {
            
                    if(!created.contains(idRequired))
                    {
                        quines += idRuleToString(idRequired)+"\n";
                    }
                }
                
                created.clear();
                created = null;
                if (!quines.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Per poder dur a terme aquesta actuació\ncal que s'hagin creat les actuacions:\n" + quines);
                    return false;
                }
            }
        }
        
        return true;
    
    }

    private void onNovaAccio(int idRule, int idActuacio) {
        
        jTabbedPane1.setSelectedIndex(0);
        //Check if the current user is allowed to create this action
        boolean contains = listIds1.contains(idRule) || listIds2.contains(idRule);
        //System.out.println("contains-->"+contains);
        if(!contains && !admin)
        {
            JOptionPane.showMessageDialog(this, "Només l'administrador pot iniciar aquesta acció.");
            return;
        }
          java.awt.Point p = this.getLocation();
          p.x = p.x + this.getSize().width;
          p.y = p.y + 5;
          
            
         Actuacio actuacio = new Actuacio(cfg.getCoreCfg().getUserInfo().getAbrev(), p, true, expedient, idActuacio, idRule, locked, admin,
                dp.getEnsenyament(), dp.getEstudis(), resourceMap, cfg.getCoreCfg().getIesClient());
        
    
        //Fa les comprovacions pertinents
        //Ara s'ha de generalitzar al simbol de qualsevol importacio
        String simbol = actuacio.beanRule.getSimbol();
        int total = 0;
        if(bean1.getImportacioSGD().containsKey(simbol))
        {
            total = bean1.getImportacioSGD().get(simbol).getNTotal();
        }
        if(!simbol.isEmpty() && total<actuacio.beanRule.getThreshold())
        {
            String missatge = "L'alumne/a no té incidències ("+simbol+") suficients\n per poder aplicar aquesta mesura.";
            JOptionPane.showMessageDialog(this, missatge);
            return;
        }
       
        
                
        if(!checkEdatRanges(actuacio))
        {
            return;
        }
        
        if(!checkInstancesPolicy(actuacio))
        {
            return;
        }
      
   
       displayActuacio(actuacio);
    }

    private void displayActuacio(final Actuacio actuacio) {
        
        //Obri l'editor de propietats
        final PreDocManager dlg = new PreDocManager(actuacio, cfg);
        ActionListener listener = new ReportListener(expedient, dlg, dp, this, cfg);
        dlg.getFormulari().addListenerDocButton(listener);
        //This is the action for cancellation
        dlg.getFormulari().addListenerCloseButtons(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.getFormulari().setVisible(false);
                dlg.dispose();
            }
        });

        dlg.getFormulari().addListenerAcceptButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String doCheck = dlg.getFormulari().doCheck();
                if (doCheck != null && !doCheck.isEmpty()) {
                    return;
                }
                dlg.getFormulari().updateDocDatabase();
                dlg.getFormulari().setVisible(false);
                dlg.getFormulari().dispose();

                //Comprova si ha de modificar la fitxa personal de l'alumne
                updateFitxaAlumne();
  
               //Fa un update de les tasques pendents
               FitxesGUI.pend.checkTasquesPendents(expedient);
               parental.timer.start();
               timer.start();
               if(actuacio.data2==null)
               {
                    FitxesGUI.pend.addOberta(expedient);
               }
               //actualitza l'entorn
               //startUp();
               fillTable();
            }
        });

       dlg.show();
    
    
    }

    public static Profile createProfileFromDP(BeanDadesPersonals dp) {
        Profile profile = new Profile();
        profile.setNexp(dp.getExpedient());
        profile.setNese(dp.isAnee());
        profile.setBelongs(FitxesGUI.belongs.contains(dp.getExpedient()));
        profile.setRepetidor(dp.isRepetidor());
        return profile;
    }

    private String idRuleToString(int idRequired) {
        String str = "id="+idRequired;
        try {
            String SQL1 = "SELECT actuacio FROM tuta_actuacions WHERE id="+idRequired;
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                str = rs1.getString(1);                
            }
            rs1.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(AccionsAlumne4.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str;
    }

   

  
}
