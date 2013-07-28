/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes;

import com.l2fprod.common.swing.StatusBar;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.iesapp.clients.iesdigital.actuacions.FactoryRules;
import org.iesapp.clients.iesdigital.alumnat.Grup;
import org.iesapp.clients.iesdigital.fitxes.BeanActuacionsPendents;
import org.iesapp.clients.iesdigital.fitxes.BeanExpulsats;
import org.iesapp.clients.iesdigital.fitxes.BeanMedicamentsAutoritzats;
import org.iesapp.clients.iesdigital.fitxes.BeanMedicamentsResum;
import org.iesapp.clients.iesdigital.fitxes.BeanMedicamentsSubministrats;
import org.iesapp.clients.iesdigital.fitxes.BeanPerduaAC;
import org.iesapp.clients.iesdigital.fitxes.BeanStatPares;
import org.iesapp.clients.iesdigital.fitxes.BeanStatSolicituds;
import org.iesapp.clients.iesdigital.fitxes.SGDImporter;
import org.iesapp.clients.iesdigital.fitxes.TasquesPendents;
import org.iesapp.framework.admin.AssignaRoles;
import org.iesapp.framework.admin.GenContrassenyes;
import org.iesapp.framework.admin.ImportarFotos;
import org.iesapp.framework.admin.ImportarFotosJpg;
import org.iesapp.framework.admin.ImportarNotes;
import org.iesapp.framework.admin.UpdateFromSGDdlg;
import org.iesapp.framework.admin.cfg.ChangePwd;
import org.iesapp.framework.data.User;
import org.iesapp.framework.dialogs.PeriodSelect;
import org.iesapp.framework.dialogs.ReportFactory;
import org.iesapp.framework.pluggable.StatusBarZone;
import org.iesapp.framework.pluggable.TopModuleWindow;
import org.iesapp.framework.pluggable.TopPluginWindow;
import org.iesapp.framework.pluggable.grantsystem.GrantBean;
import org.iesapp.framework.pluggable.grantsystem.GrantModule;
import org.iesapp.framework.pluggable.grantsystem.GrantSystem;
import org.iesapp.framework.pluggable.grantsystem.Profile;
import org.iesapp.framework.table.CellTableState;
import org.iesapp.framework.table.MyIconButtonRenderer;
import org.iesapp.framework.table.MyIconLabelRenderer;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.IconUtils;
import org.iesapp.modules.fitxes.dialogs.CanviaGrup;
import org.iesapp.modules.fitxes.dialogs.Cleaner;
import org.iesapp.modules.fitxes.dialogs.DlgCastigDimecres;
import org.iesapp.modules.fitxes.dialogs.ExportUsers;
import org.iesapp.modules.fitxes.dialogs.InformesActuacions;
import org.iesapp.modules.fitxes.dialogs.InformesSGDdlg;
import org.iesapp.modules.fitxes.dialogs.Permisos;
import org.iesapp.modules.fitxes.dialogs.admin.CreaFitxes;
import org.iesapp.modules.fitxes.dialogs.admin.ImportarAlumnes;
import org.iesapp.modules.fitxes.forms.PickDate;
import org.iesapp.modules.fitxes.reports.ReportingClass;
import org.iesapp.modules.fitxes.reports.Statistics;
import org.iesapp.modules.fitxescore.lookups.TableSelection;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.MySorter;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
//@org.openide.util.lookup.ServiceProvider(service=TopModuleWindow.class, path="modules")

    public class FitxesGUI extends TopModuleWindow {

    private int timerInterval=100;  //1000 ms = 1 second
    public Timer timer;
    private MySorter msort;
    private boolean doInformeTP=false;
    private ArrayList<Integer> listRepetidors;
    private int rowHeight = 45;
    private ActionListener fillTableListener;
    private Finder finder1;
    private boolean isListening=false;
    
    public static ArrayList<Number> pos2exp;    
    private DefaultTableModel modelTable1;
    
    private DefaultComboBoxModel modelComboNivells;    
    private DefaultComboBoxModel modelComboCursos;
    private DefaultComboBoxModel modelComboGrups;
    public static DefaultComboBoxModel modelComboAnys;
    public static TasquesPendents pend;
    public static ArrayList<Integer> belongs;
    private int nalumnes=0;
    private JDesktopPane jDesktopPane1;
    private ArrayList<Integer> currentListAll;
    private ArrayList<Integer> currentListBelongs;
    public static GrantModule moduleGrant;
    private Cfg cfg;
    private boolean isTableFilling;
    private SwingWorker<Void, Void> fillTableWorker;
    private boolean cacelFilling;
  
    /**
     * Creates new form FitxesModule
     */
    
    
    public FitxesGUI() {
        this.moduleDisplayName = "Fitxes-Tutoria";
        this.moduleName = "fitxes";
        this.moduleDescription = "A module for tutorial administration";
        this.multipleInstance = false;
    }
    
  
    @Override
    public void postInitialize() {
    
        cfg = new Cfg(coreCfg);
        coreCfg.getMainHelpBroker().enableHelpKey(this, "org-iesapp-modules-fitxes", null);
     
        this.initializationObject = cfg;
        moduleGrant = GrantSystem.getInstance(moduleName, coreCfg);
        moduleGrant.loadGrantForRole(cfg.getCoreCfg().getUserInfo().getRole(), Cfg.defaultsGrant);     
   
     
        initComponents();
        jSharedDoc1.startUp(cfg);
        
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                     TableSelection tableSelection = new TableSelection(getCurrentSelection(), currentListAll, currentListBelongs, cfg.anyAcademicFitxes);
                     content.set(Collections.singleton(tableSelection),null);                         
            }
        });
        
         jSharedDoc1.setJTabbedPane(jTabbedPane2);
         finder1 = new Finder(cfg);
        
         //Crea dinàmicament els menus de registres a iesDigital (afegits a jMenu7)
         //Aquestes són accions de tutoria que queden enregistrades per tipus
         ArrayList<String> list1 = coreCfg.getIesClient().getFitxesClient().getFactoryRules().getIesDigitalRegisterNames();
         for(String s: list1)
         {
             JMenuItem jmenuItemReg1 = new JMenuItem(s);
             jmenuItemReg1.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/informe.gif")));
             jmenuItemReg1.setActionCommand(s);
             jmenuItemReg1.addActionListener(new ActionListener() {

                 @Override
                 public void actionPerformed(ActionEvent e) {
                     String regType = e.getActionCommand();
                     doInformeRegistres(regType);
                 }
             });
             jMenu7.add(jmenuItemReg1);
         }
         
         
         
         //Tots els components que escolten generar la taula de nou
         fillTableListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //UPDATE UI
                jComboBox5.setEnabled(jAssistencia1.isSelected());
                jComboBox6.setEnabled(jConvivencia1.isSelected());
                jComboAccions.setEnabled(jAssistencia.isSelected());
                jComboAccions1.setEnabled(jConvivencia.isSelected());
                FitxesGUI.this.fillTable();
            }
         };
        
         jAssistencia1.addActionListener(fillTableListener);
         jConvivencia1.addActionListener(fillTableListener);
         ButtonGroup buttongroup1 = new ButtonGroup();
         buttongroup1.add(jAssistencia1);
         buttongroup1.add(jConvivencia1);
         buttongroup1.add(jSMSaEnviar);
         buttongroup1.add(jSMSenviat);
         buttongroup1.add(jCartaEnviar);
         buttongroup1.add(jCartaEnviada);
         jCartaEnviar.setSelected(true);
         
         ButtonGroup buttongroup2 = new ButtonGroup();
         buttongroup2.add(jAssistencia);
         buttongroup2.add(jConvivencia);
         jAssistencia.setSelected(true);
         jComboBox5.setEnabled(jAssistencia1.isSelected());
         jComboBox6.setEnabled(jConvivencia1.isSelected());
         jComboAccions.setEnabled(jAssistencia.isSelected());
         jComboAccions1.setEnabled(jConvivencia.isSelected());
         
         jCheckBox1.addActionListener(fillTableListener);
         jComboBox5.addActionListener(fillTableListener);
         jComboBox6.addActionListener(fillTableListener);
         
         jDesktopPane1 = new JDesktopPane();
         this.setLayout(new BorderLayout());
         this.add(jDesktopPane1);
         DesktopManager.setDesktop(jDesktopPane1);
         jNumExp.requestFocus();
         
         
         jDesktopPane1.add(jInternalFrame1);
        
         //Maximize search frame
         try {
            jInternalFrame1.setMaximum(true);
         } catch (PropertyVetoException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
         }
         jInternalFrame1.setLayer(0);
         
      
         jTable1.setIntercellSpacing( new java.awt.Dimension(2,2) );
         jTable1.setGridColor(java.awt.Color.gray);
         jTable1.setShowGrid(true);
         
         JTableHeader header = jTable1.getTableHeader();
         header.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                        JTable table = ((JTableHeader) evt.getSource()).getTable();
                        TableColumnModel colModel = table.getColumnModel();

                        int index = colModel.getColumnIndexAtX(evt.getX());

                        if (index ==0) //ordena per expedient
                        {
                            msort.setFirst("Exp2");
                            fillTable();
                        }
                        else if (index ==1) //ordena per grup
                        {
                            msort.setFirst("Grup");
                            msort.setFirst("Estudis");
                            fillTable();
                        }
                        else if (index ==2) //ordena per llinatge1
                        {
                            msort.setFirst("Llinatge1");
                            fillTable();
                        }                  
                        else
                        {
                            return;
                        }

                        java.awt.Rectangle headerRect = table.getTableHeader().getHeaderRect(index);
                        if (index == 0) {
                          headerRect.width -= 10;
                        } else {
                          headerRect.grow(-10, 0);
                        }
                        if (!headerRect.contains(evt.getX(), evt.getY())) {
                          int vLeftColIndex = index;
                          if (evt.getX() < headerRect.x) {
                            vLeftColIndex--;
                          }
                        }
            }
         });
         
         msort = new MySorter(new String[]{"Estudis", "Grup", "Llinatge1", "Exp2"});

         
         if(cfg.getCoreCfg().getMysql().isClosed())
         {
            Object[] options = {"D'acord"};

            int n = JOptionPane.showOptionDialog(null,
            "El programa fitxes no pot engegar-se perquè\nno hi ha connexió amb la base de dades.\nConsultau a l'administrador.", "Error",
            JOptionPane.ERROR_MESSAGE,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            options[0]);
            cfg.getCoreCfg().close();
            System.exit(1);
         }

     
         modelComboNivells = new DefaultComboBoxModel();
         modelComboCursos = new DefaultComboBoxModel();
         modelComboGrups = new DefaultComboBoxModel();
         modelComboAnys = new DefaultComboBoxModel();

         checkAnys();
         
          //Inicia arrays de repetidors
         listRepetidors = getListRepetidors();
         

         //obté el nombre màxim d'alumnes
         String SQL2 = "SELECT COUNT(xh.Exp2) AS nalumnes FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic "
                 + " AS xh INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne as xes ON xh.Exp2=xes.Exp2 WHERE xh.AnyAcademic='"+cfg.anyAcademicFitxes+"'";
         
         try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs2 = cfg.getCoreCfg().getMysql().getResultSet(SQL2,st);
            while (rs2 != null && rs2.next()) {
                nalumnes = rs2.getInt("nalumnes");
            }
            if(rs2!=null) {
                 rs2.close();
                 st.close();
             }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
      

//        Cfg.createComboModels(true);
//        Cfg.createComboModels(false);

        //comprova les tasques pendents de l'alumnat
        int any = getSelectedCurs_Primer();
        pend = new TasquesPendents(cfg.getCoreCfg().getIesClient());
        pend.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                fromTableUpdateFlags();
            }
        });
        pend.checkTasquesPendents();

        belongs = new ArrayList<Integer>();
 

        timer = new Timer(timerInterval, new ActionListener ()
        {
            @Override
                public void actionPerformed(ActionEvent e)
                {
                    tasksTimer();
                }

         });
        
        ///// MOVED FROM INITIALIZE
        
        //Nomes si accedeix un tutor faig un update del recompte de faltes
        if(cfg.getCoreCfg().getUserInfo().getGrant()==User.TUTOR || cfg.getCoreCfg().getUserInfo().getGrant()==User.PREF)
        {
            ArrayList<String> list = new ArrayList<String>();
            list.add("FA");
            list.add("FJ");
            SGDImporter task = new SGDImporter(CoreCfg.coreDB_sgdDB, coreCfg.anyAcademic+"", 
                             cfg.getCoreCfg().getUserInfo().getAbrev(),
                             -1, list, cfg.getCoreCfg().getIesClient());
            task.start();
            task.addPropertyChangeListener(new PropertyChangeListener(){

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                      pend.checkTasquesPendents();
                }
                
            });
        }
        
    }
/**
     * Timer control in this module
     */
   private void tasksTimer() {
       if(pend!=null && !pend.isRunning())
       {
           if(doInformeTP)
           {
               //In this type of report, we should pass only the current
               //selection
               doInformeTasquesPendents();
           }
           
           doInformeTP = false;
           timer.stop();
           
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

        jToolBarTutoria = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jToolBarInformes = new javax.swing.JToolBar();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jMenuEines = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem43 = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItem32 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuInformes = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem18 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenuItem31 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem26 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenu11 = new javax.swing.JMenu();
        jMenuItem41 = new javax.swing.JMenuItem();
        jMenuItem42 = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItem34 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        jMenuTutoria = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuFinestres = new javax.swing.JMenu();
        jMenuItem39 = new javax.swing.JMenuItem();
        jMenuItem40 = new javax.swing.JMenuItem();
        jMenuLandF = new javax.swing.JMenu();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jCercaAvancada = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jNumExp = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPrimerLlinatge = new javax.swing.JTextField();
        jSegonLlinatge = new javax.swing.JTextField();
        jNom = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jSMSaEnviar = new javax.swing.JCheckBox();
        jSMSenviat = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        jCartaEnviar = new javax.swing.JCheckBox();
        jCartaEnviada = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jAssistencia1 = new javax.swing.JRadioButton();
        jConvivencia1 = new javax.swing.JRadioButton();
        jComboBox5 = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jComboBox6 = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jAssistencia = new javax.swing.JCheckBox();
        jConvivencia = new javax.swing.JCheckBox();
        jComboAccions = new javax.swing.JComboBox();
        jComboAccions1 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox();
        jNomesTuta = new javax.swing.JCheckBox();
        jLinkButton1 = new com.l2fprod.common.swing.JLinkButton();
        jRepetidor = new javax.swing.JCheckBox();
        jAnee = new javax.swing.JCheckBox();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;   //Disallow the editing of any cell
            }
        };
        jSharedDoc1 = new org.iesapp.modules.fitxes.util.JSharedDoc();
        jStatusBarResults = new javax.swing.JLabel();

        jToolBarTutoria.setFloatable(false);
        jToolBarTutoria.setBorderPainted(false);
        jToolBarTutoria.setName("jToolBarTutoria"); // NOI18N

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/pares.gif"))); // NOI18N
        jButton1.setToolTipText("Entrevista amb pares");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBarTutoria.add(jButton1);

        jToolBarInformes.setFloatable(false);
        jToolBarInformes.setBorderPainted(false);
        jToolBarInformes.setName("jToolBarInformes"); // NOI18N

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/keys.gif"))); // NOI18N
        jButton7.setToolTipText("Llista de contrasenyes");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jToolBarInformes.add(jButton7);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/orles.gif"))); // NOI18N
        jButton8.setToolTipText("Orles de grup");
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jToolBarInformes.add(jButton8);

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/resumFitxes.gif"))); // NOI18N
        jButton10.setToolTipText("Resum Fitxes Tutoria");
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jToolBarInformes.add(jButton10);

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/sgd.gif"))); // NOI18N
        jButton9.setToolTipText("Informes SGD");
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jToolBarInformes.add(jButton9);

        jMenuEines.setText("Eines");
        jMenuEines.setName("jMenuEines"); // NOI18N

        jMenuItem5.setText("Importa nous alumnes");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem5);

        jMenuItem7.setText("Importa fotos des de xestib .html");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem7);

        jMenuItem12.setText("Importa fotos des d'imatges .jpg");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem12);

        jMenuItem16.setText("Importa notes d'avaluació Juny");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem16);
        jMenuEines.add(jSeparator1);

        jMenuItem3.setText("Genera contrasenyes d'alumnes");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem3);

        jMenuItem6.setText("Actualitza des de SGD");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem6);
        jMenuEines.add(jSeparator2);

        jMenuItem8.setText("Control de fitxes creades");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem8);
        jMenuEines.add(jSeparator4);

        jMenuItem43.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem43.setText("Exportació d'alumnes");
        jMenuItem43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem43ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem43);
        jMenuEines.add(jSeparator11);

        jMenuItem32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/clean.gif"))); // NOI18N
        jMenuItem32.setText("Netejador");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem32);

        jMenuItem14.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        jMenuItem14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/switch.gif"))); // NOI18N
        jMenuItem14.setText("Canvia l'alumne de grup");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem14);

        jMenuItem30.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/close.gif"))); // NOI18N
        jMenuItem30.setText("Dóna de baixa l'alumne");
        jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem30ActionPerformed(evt);
            }
        });
        jMenuEines.add(jMenuItem30);

        jMenu2.setText("Administració");

        jMenuItem2.setText("Canvia contrasenya administrador");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);
        jMenu2.add(jSeparator3);

        jCheckBoxMenuItem1.setText("Permet l'edició de fitxes");
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jCheckBoxMenuItem1);

        jMenuItem11.setText("Carrega permisos");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem11);

        jMenuItem15.setText("Edita grups de permisos");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem15);
        jMenu2.add(jSeparator5);

        jMenuItem17.setText("Assigna 'roles' al professorat");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem17);
        jMenu2.add(jSeparator13);

        jMenuItem25.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem25.setText("Control dels plugins");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem25);

        jMenuEines.add(jMenu2);

        jMenuInformes.setText("Informes");
        jMenuInformes.setName("jMenuInformes"); // NOI18N
        jMenuInformes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuInformesActionPerformed(evt);
            }
        });

        jMenuItem9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/keys.gif"))); // NOI18N
        jMenuItem9.setText("Llista de contrasenyes");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenuInformes.add(jMenuItem9);

        jMenuItem20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/orles.gif"))); // NOI18N
        jMenuItem20.setText("Orles de grup");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenuInformes.add(jMenuItem20);

        jMenuItem10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/resumFitxes.gif"))); // NOI18N
        jMenuItem10.setText("Resum fitxes tutoria");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenuInformes.add(jMenuItem10);
        jMenuInformes.add(jSeparator8);

        jMenuItem18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/sgd.gif"))); // NOI18N
        jMenuItem18.setText("Informes SGD");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenuInformes.add(jMenuItem18);
        jMenuInformes.add(jSeparator6);

        jMenu8.setText("Informes d'Actuacions");

        jMenuItem22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/informe.gif"))); // NOI18N
        jMenuItem22.setText("Actuacions realitzades");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem22);

        jMenuItem24.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/informe.gif"))); // NOI18N
        jMenuItem24.setText("Actuacions pendents");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem24);

        jMenuInformes.add(jMenu8);

        jMenu9.setText("Informes de Tutoria");

        jMenuItem29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/informe.gif"))); // NOI18N
        jMenuItem29.setText("Sol·licituds d'informació tutors");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem29);

        jMenuItem31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/informe.gif"))); // NOI18N
        jMenuItem31.setText("Visites de pares");
        jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem31ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem31);

        jMenuInformes.add(jMenu9);

        jMenu7.setText("Informes de Sancions");

        jMenuItem26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/informe.gif"))); // NOI18N
        jMenuItem26.setText("Càstig de Dimecres");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem26);
        jMenu7.add(jSeparator9);

        jMenuInformes.add(jMenu7);

        jMenu11.setText("Informes de Medicaments");

        jMenuItem41.setText("Llista d'autoritzats");
        jMenuItem41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem41ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem41);

        jMenuItem42.setText("Llista de subministraments");
        jMenuItem42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem42ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem42);

        jMenuInformes.add(jMenu11);
        jMenuInformes.add(jSeparator12);

        jMenuItem34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/informe.gif"))); // NOI18N
        jMenuItem34.setText("Pèrdua d'avaluació contínua");
        jMenuItem34.setToolTipText("resolució del Consell Escolar");
        jMenuItem34.setEnabled(false);
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenuInformes.add(jMenuItem34);

        jMenuItem33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/statistics.gif"))); // NOI18N
        jMenuItem33.setText("Estadístiques");
        jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem33ActionPerformed(evt);
            }
        });
        jMenuInformes.add(jMenuItem33);

        jMenuTutoria.setText("Tutoria");
        jMenuTutoria.setName("jMenuTutoria"); // NOI18N

        jMenuItem19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/toolbar/pares.gif"))); // NOI18N
        jMenuItem19.setText("Entrevista amb pares");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenuTutoria.add(jMenuItem19);
        jMenuTutoria.add(jSeparator10);

        jMenuFinestres.setText("Finestres");
        jMenuFinestres.setName("jMenuFinestres"); // NOI18N

        jMenuItem39.setText("Organitza");
        jMenuItem39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem39ActionPerformed(evt);
            }
        });
        jMenuFinestres.add(jMenuItem39);

        jMenuItem40.setText("Minimitza tot");
        jMenuItem40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem40ActionPerformed(evt);
            }
        });
        jMenuFinestres.add(jMenuItem40);

        jMenuLandF.setText("Aparença");
        jMenuLandF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuLandFActionPerformed(evt);
            }
        });
        jMenuFinestres.add(jMenuLandF);

        jInternalFrame1.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        jInternalFrame1.setTitle("Cerca d'alumnes");
        jInternalFrame1.setVisible(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Condicions de cerca"));

        jLabel2.setText("Estudis");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tots", "ESO", "BATX", "MITJA" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Nivell");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tots" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Grup");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tots" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jButton2.setText("Cercar");
        jButton2.setToolTipText("Cerca & Refresca");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jCercaAvancada.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jLabel1.setText("Nº d'Expedient");

        jNumExp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jNumExpKeyPressed(evt);
            }
        });

        jLabel8.setText("Segon Llinatge");

        jPrimerLlinatge.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPrimerLlinatgeKeyPressed(evt);
            }
        });

        jSegonLlinatge.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jSegonLlinatgeKeyPressed(evt);
            }
        });

        jNom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jNomKeyPressed(evt);
            }
        });

        jLabel6.setText("Primer Llinatge");

        jLabel9.setText("Nom");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNumExp, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPrimerLlinatge, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addGap(2, 2, 2)
                .addComponent(jSegonLlinatge, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNom, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jNumExp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jPrimerLlinatge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSegonLlinatge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Cerca per dades personals", jPanel6);

        jPanel10.setBackground(new java.awt.Color(236, 222, 222));
        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel10.setToolTipText("SMS per 14 Faltes o 8 retard ESO");

        jSMSaEnviar.setSelected(true);
        jSMSaEnviar.setText(" Se'ls ha d'enviar SMS");
        jSMSaEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSMSaEnviarActionPerformed(evt);
            }
        });

        jSMSenviat.setText("Tenen SMS enviat");
        jSMSenviat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSMSenviatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSMSaEnviar)
                    .addComponent(jSMSenviat))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jSMSaEnviar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSMSenviat)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(236, 222, 222));
        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel11.setToolTipText("Carta per 14, 28 o 42 FNJ");

        jCartaEnviar.setText(" Se'ls ha d'enviar Carta");
        jCartaEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCartaEnviarActionPerformed(evt);
            }
        });

        jCartaEnviada.setText("Tenen Carta enviada");
        jCartaEnviada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCartaEnviadaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCartaEnviar)
                    .addComponent(jCartaEnviada))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jCartaEnviar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCartaEnviada)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jAssistencia1.setText("Assistència i puntualitat");

        jConvivencia1.setText("Convivència");

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jCheckBox1.setText("Amaga finalitzades");

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAssistencia1)
                    .addComponent(jConvivencia1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox6, 0, 326, Short.MAX_VALUE)
                    .addComponent(jComboBox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAssistencia1)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jConvivencia1)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        jTabbedPane1.addTab("Actuacions iniciades", jPanel4);

        jAssistencia.setSelected(true);
        jAssistencia.setText("Assistència i puntualitat");

        jConvivencia.setText("Convivència");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAssistencia)
                    .addComponent(jConvivencia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboAccions1, 0, 327, Short.MAX_VALUE)
                    .addComponent(jComboAccions, 0, 179, Short.MAX_VALUE))
                .addGap(412, 412, 412))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAssistencia)
                    .addComponent(jComboAccions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jConvivencia)
                    .addComponent(jComboAccions1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Actuacions pendents", jPanel5);

        javax.swing.GroupLayout jCercaAvancadaLayout = new javax.swing.GroupLayout(jCercaAvancada);
        jCercaAvancada.setLayout(jCercaAvancadaLayout);
        jCercaAvancadaLayout.setHorizontalGroup(
            jCercaAvancadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jCercaAvancadaLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        jCercaAvancadaLayout.setVerticalGroup(
            jCercaAvancadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jCercaAvancadaLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        jLabel5.setText("Curs Acadèmic");

        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jNomesTuta.setText("Mostra només alumnes de la tutoria");
        jNomesTuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomesTutaActionPerformed(evt);
            }
        });

        jLinkButton1.setBackground(new java.awt.Color(204, 204, 204));
        jLinkButton1.setForeground(new java.awt.Color(0, 0, 204));
        jLinkButton1.setText("Cerca Avançada");
        jLinkButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLinkButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jLinkButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLinkButton1ActionPerformed(evt);
            }
        });

        jRepetidor.setText("Repetidors");
        jRepetidor.setToolTipText("Filtra alumnes repetidors del curs actual");
        jRepetidor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRepetidorActionPerformed(evt);
            }
        });

        jAnee.setText("NESE");
        jAnee.setToolTipText("Filtra alumnes NESE");
        jAnee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAneeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCercaAvancada, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jNomesTuta)
                                .addGap(18, 18, 18)
                                .addComponent(jRepetidor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jAnee))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLinkButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(9, 9, 9))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLinkButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jNomesTuta)
                    .addComponent(jAnee)
                    .addComponent(jRepetidor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCercaAvancada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Expedient", "Grup", "Alumne/a", "Repeteix", "NESE"
            }
        );
        jTable1.setModel(modelTable1);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane2.addTab("Resultats de la Cerca", jScrollPane1);
        jTabbedPane2.addTab("Documents", jSharedDoc1);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane2)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );

        jInternalFrame1.getAccessibleContext().setAccessibleParent(jAnee);

        jStatusBarResults.setName("jStatusBarResults"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentContainer());
        getContentContainer().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 857, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // checkNivells();
        checkCursos();
        checkLletres();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        checkLletres();
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        //   fillTable();
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        listRepetidors = getListRepetidors();
        msort.reset();
        pend.checkTasquesPendentsForeground(); //actualitza les tasques pendents
        fillTable();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jNumExpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jNumExpKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            fillTable();
        }
    }//GEN-LAST:event_jNumExpKeyPressed

    private void jPrimerLlinatgeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPrimerLlinatgeKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            fillTable();
        }
    }//GEN-LAST:event_jPrimerLlinatgeKeyPressed

    private void jSegonLlinatgeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jSegonLlinatgeKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            fillTable();
        }
    }//GEN-LAST:event_jSegonLlinatgeKeyPressed

    private void jNomKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jNomKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            fillTable();
        }
    }//GEN-LAST:event_jNomKeyPressed

    private void jSMSaEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSMSaEnviarActionPerformed
        fillTable();
    }//GEN-LAST:event_jSMSaEnviarActionPerformed

    private void jSMSenviatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSMSenviatActionPerformed
        fillTable();
    }//GEN-LAST:event_jSMSenviatActionPerformed

    private void jCartaEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCartaEnviarActionPerformed
        fillTable();
    }//GEN-LAST:event_jCartaEnviarActionPerformed

    private void jCartaEnviadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCartaEnviadaActionPerformed
        fillTable();
    }//GEN-LAST:event_jCartaEnviadaActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if(!isListening) {
            return;
        }
        fillTable();
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        if(!isListening) {
            return;
        }

        cfg.anyAcademicFitxes = getSelectedCurs_Primer();

        //Canvia transitoriament de base de dades
        switchDatabases(cfg.anyAcademicFitxes);

        //Comprova si l'any academic seleccionat correpon a l'any actual
        if(coreCfg.anyAcademic==cfg.anyAcademicFitxes)
        {
            jPanel1.setBackground(new java.awt.Color(224,223,227));
            //System.out.println("ANY 1");
        }
        else
        {
            jPanel1.setBackground(new java.awt.Color(255,100,50));
            // //System.out.println("ANY 2");
        }

        //cerca possibilitats pels comboBox
        //       Cfg.createComboModels(false);
        checkNivells();
        checkCursos();
        checkLletres();

        //obté el nombre màxim d'alumnes
        String SQL1 = "SELECT COUNT(xh.Exp2) AS nalumnes FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic "
        + " AS xh INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne as xes ON xh.Exp2=xes.Exp2 WHERE xh.AnyAcademic='"+cfg.anyAcademicFitxes+"'";
        
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while (rs1 != null && rs1.next()) {
                nalumnes = rs1.getInt("nalumnes");
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        //actualitza la pertanença
        //crea una llista de pertanença
        belongs = cfg.getCoreCfg().getIesClient().getFitxesClient().getFitxesUtils().belongsList(cfg.getCoreCfg().getUserInfo().getAbrev(), cfg.anyAcademicFitxes);
        
        //actualitza la taula
        fillTable();

        //Cada pic que canviam d'any cal checkejar les tasques pendents

        pend.checkTasquesPendents();
        

    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jNomesTutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomesTutaActionPerformed
        fillTable();
    }//GEN-LAST:event_jNomesTutaActionPerformed

    private void jLinkButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLinkButton1ActionPerformed
        boolean q = jCercaAvancada.isVisible();
        jCercaAvancada.setVisible(!q);
        if (q) {
            jLinkButton1.setText("Cerca avançada");
        } else {
            jLinkButton1.setText("Cerca simple");
        }

        fillTable();
    }//GEN-LAST:event_jLinkButton1ActionPerformed

    private void jRepetidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRepetidorActionPerformed
        fillTable();
    }//GEN-LAST:event_jRepetidorActionPerformed

    private void jAneeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAneeActionPerformed
        fillTable();
    }//GEN-LAST:event_jAneeActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        //        //event que surt de pitjar el boto de d'alt de la columna
        //        TableColumnModel columnModel = jTable1.getColumnModel();
        //        int viewColumn = columnModel.getColumnIndexAtX(evt.getX());
        //        int column = jTable1.convertColumnIndexToModel(viewColumn);
        //        if (evt.getClickCount() == 1 && column != -1) {
            //          //System.out.println("Sorting ...");
            //          int shiftPressed = (evt.getModifiers() & InputEvent.SHIFT_MASK);
            //          boolean ascending = (shiftPressed == 0);
            //          if(ascending) //System.out.println("shift pressed");
            //          //sorter.sortByColumn(column, ascending);
            //        }

        int col = jTable1.getSelectedColumn();
        int row = jTable1.getSelectedRow();
        if(col<0 || row<0) {
            return;
        }

        CellTableState cts0 = (CellTableState) jTable1.getValueAt(row, 0);
        int numexp =  cts0.getCode();
        String nomAlumne = ((JLabel) jTable1.getValueAt(row, 2)).getText();

       // int n = jSharedDoc1.setDocuments(numexp, nomAlumne);
        //jTabbedPane2.setIconAt(1, n>0?new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/print2.gif")):null);

        ArrayList<Integer> llista = new ArrayList<Integer>();
        for(int i=0; i<jTable1.getRowCount(); i++) {
            CellTableState cts2 = (CellTableState) jTable1.getValueAt(i, 0);
            int expedient =  cts2.getCode();
            llista.add(expedient);
        }

        if(col==0) {
            CellTableState valueAt = (CellTableState) jTable1.getValueAt(row, col);
            if(valueAt.getState()<=1) {
                obrirFitxa();
            } else {
                actionEntrevistaPares();
            }
        }

        if(col>0 && col<5) {
            if(evt.getClickCount()==2) {
                obrirFitxa();
            }

        }

        if(cfg.getCoreCfg().getUserInfo().getGrant()==User.ADMIN) {
            if(col==5) {

                stamper.addAction(moduleName,"EditorPermisos");
                Permisos dlg = new Permisos(javar.JRDialog.getActiveFrame(), true, (String) jTable1.getValueAt(row, 5), cfg);
                dlg.setLocationRelativeTo(null);
                dlg.setVisible(true);
                String perm = dlg.output;
                dlg.dispose();

                //now update table from database
                CellTableState cts = (CellTableState) jTable1.getValueAt(row, 0);
                String nexp = ""+cts.getCode();
                String SQL1 = "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne SET Permisos='"+perm+"' where Exp2='"+nexp+"'";

                int nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
                ////////System.out.println(nup+" :: "+SQL1);
                //now update grid
                if(nup>0) {
                    jTable1.setValueAt(perm,row,5);
                }
        } else if(col==6) {
                CellTableState cts = (CellTableState) jTable1.getValueAt(row, 0);
                int nexp = cts.getCode();

                stamper.addAction(moduleName,"AccionsAlumne");
                DesktopManager.showAccionsAlumne(this, nexp, llista, false, cfg);

            }
        } else if(cfg.getCoreCfg().getUserInfo().getGrant()!=User.ADMIN) {
            if(col==5) {
                Profile profile = getStudentProfileFromLine(row);
               
                if( moduleGrant.isGranted("accions_view", profile) ) {
                    stamper.addAction(moduleName,"AccionsAlumne");
                    DesktopManager.showAccionsAlumne(this, profile.getNexp(), llista, false, cfg);
                }
            }
        }

    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        if(evt.getKeyCode()== KeyEvent.VK_DOWN    ||
            evt.getKeyCode()== KeyEvent.VK_KP_DOWN ||
            evt.getKeyCode()== KeyEvent.VK_UP      ||
            evt.getKeyCode()== KeyEvent.VK_KP_UP  )
        {
            int col = jTable1.getSelectedColumn();
            int row = jTable1.getSelectedRow();
            if(col<0 || row<0) {
                return;
            }

            CellTableState cts0 = (CellTableState) jTable1.getValueAt(row, 0);
            int numexp =  cts0.getCode();

            String nomAlumne = ((JLabel) jTable1.getValueAt(row, 2)).getText();

            int n = jSharedDoc1.setDocuments(numexp, nomAlumne);
            jTabbedPane2.setIconAt(1, n>0?new ImageIcon(getClass().getResource("/org/iesapp/modules/fitxes/icons/print2.gif")):null);
        }
    }//GEN-LAST:event_jTable1KeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        actionEntrevistaPares();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        actionContrasenyes();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        actionOrles();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        genResumFitxes();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        actionSGD();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        stamper.addAction(moduleName,"ImportarAlumnes");
        //importaFromTable();
        ImportarAlumnes dlg = new ImportarAlumnes(javar.JRDialog.getActiveFrame(), true, getSelectedCurs_Primer(), cfg);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        stamper.addAction(moduleName,"ImportarFotos");
        ImportarFotos dlg = new ImportarFotos(javar.JRDialog.getActiveFrame(),true,cfg.getCoreCfg());
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        stamper.addAction(moduleName,"ImportarFotosJpg");
        ImportarFotosJpg dlg = new ImportarFotosJpg(javar.JRDialog.getActiveFrame(),true,cfg.getCoreCfg());
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        stamper.addAction(moduleName,"ImportarNotes");
        ImportarNotes dlg = new ImportarNotes(javar.JRDialog.getActiveFrame(), true,cfg.getCoreCfg());
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        stamper.addAction(moduleName,"GenContrassenyes");
        GenContrassenyes dlg = new GenContrassenyes(javar.JRDialog.getActiveFrame(), true, ""+cfg.anyAcademicFitxes,cfg.getCoreCfg());
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);

    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        stamper.addAction(moduleName,"UpdateFromSGDdlg");
        UpdateFromSGDdlg dlg = new UpdateFromSGDdlg(javar.JRDialog.getActiveFrame(), true, cfg.getCoreCfg().getUserInfo().getAbrev(),cfg.getCoreCfg());
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        stamper.addAction(moduleName,"CreaFitxes");
        CreaFitxes dlg = new CreaFitxes(javar.JRDialog.getActiveFrame(), true, cfg);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem43ActionPerformed

        ExportUsers dlg = new ExportUsers(this.getExpdsInView(), cfg);
        dlg.setLocationRelativeTo(null);
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem43ActionPerformed

    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
        Cleaner dlg = new Cleaner(javar.JRDialog.getActiveFrame(), false, cfg);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem32ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        stamper.addAction(moduleName,"CanviaGrup");
        int id = jTable1.getSelectedRow();
        if(id>=0)
        {
            CellTableState cts2 = (CellTableState) jTable1.getValueAt(id, 0);
            int expedient =  cts2.getCode();

            isListening = false;

            //obre una pantalla dialeg
            CanviaGrup dlg = new CanviaGrup(javar.JRDialog.getActiveFrame(), true, expedient, cfg);
            dlg.setLocationRelativeTo(null);
            dlg.setVisible(true);

            fillTable();

            //cerca l'alumne a la nova taula
            for(int i=0; i<jTable1.getRowCount(); i++)
            {
                CellTableState cts3 = (CellTableState) jTable1.getValueAt(i, 0);
                int expedient2 =  cts3.getCode();

                if(expedient==expedient2)
                {
                    jTable1.setRowSelectionInterval(i, i);
                    java.awt.Rectangle rect = jTable1.getCellRect(i, 0, true);
                    jTable1.scrollRectToVisible(rect);
                    jTable1.setRowSelectionInterval(i, i);
                    break;
                }
            }

            isListening = true;
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Triau primer l'alumne de la llista\nque voleu canviar de grup.");
        }

    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem30ActionPerformed
        int id = jTable1.getSelectedRow();
        if(id>=0)
        {
            CellTableState cts2 = (CellTableState) jTable1.getValueAt(id, 0);
            int expedient =  cts2.getCode();
            cfg.donaBaixa(expedient);
            fillTable();
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Triau primer l'alumne de la llista\nque voleu canviar de grup.");
        }

    }//GEN-LAST:event_jMenuItem30ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        stamper.addAction(moduleName,"ChangePwd");
        ChangePwd dlg = new ChangePwd(javar.JRDialog.getActiveFrame(), true,cfg.getCoreCfg());
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        CoreCfg.coreDB_EditFitxes = jCheckBoxMenuItem1.isSelected()? 1 : 0;
        cfg.getCoreCfg().updateDatabaseCfg();
    }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        cfg.readPermisos(cfg.getCoreCfg().getUserInfo().getAbrev());
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        stamper.addAction(moduleName,"EditorGrupsUsuaris");
        //TODO
        //            DBEditor dlg = new DBEditor(this, true, "Editor de grups d'usuaris", cfg.getCoreCfg().getMysql(), "fitxa_permisos");
        //            dlg.setLocationRelativeTo(null);
        //            dlg.setVisible(true);

    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        stamper.addAction(moduleName,"AssignaRoles");
        AssignaRoles dlg = new AssignaRoles(javar.JRDialog.getActiveFrame(), true, cfg.getCoreCfg());
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
       
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        actionContrasenyes();
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        actionOrles();
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        genResumFitxes();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        actionSGD();
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        stamper.addAction(moduleName,"InformesActuacions");
        //Crea dues llistes amb els expedients i amb noms d'alumnes
        ArrayList<Profile> listexpd = new ArrayList<Profile>();
        ArrayList<String> listnoms = new ArrayList<String>();

        for(int i=0; i<jTable1.getRowCount(); i++)
        {
            Profile profile = this.getStudentProfileFromLine(i);
            listexpd.add(profile);
            //            String nom = (String) jTable1.getValueAt(i, 2)+" ";
            //            nom +=(String) jTable1.getValueAt(i, 3)+", ";
            //            nom +=(String) jTable1.getValueAt(i, 4);
            String nom = ((JLabel) jTable1.getValueAt(i,2)).getText();
            listnoms.add(nom);
        }

        InformesActuacions dlg = new InformesActuacions(javar.JRDialog.getActiveFrame(), false, listexpd, listnoms, cfg);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed

        //Abans de dur a terme l'informe cas assegurar-se que
        //s'han actualizat les tasques pendents, per si un altre
        //usuari remot ha fet canvis recentment.
        pend.checkTasquesPendents();
        doInformeTP = true;
        timer.start();
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed

        ArrayList<BeanStatSolicituds> list = new ArrayList<BeanStatSolicituds>();

        String SQL1 = "SELECT prof.nombre, "
        + " SUM( IF(mis.dataContestat IS NULL AND te.dia<CURRENT_DATE(),1,0)) AS caducats,  "
        + " SUM( IF(mis.dataContestat IS NULL AND te.dia>=CURRENT_DATE(),1,0)) AS pendents,  "
        + " SUM( IF(mis.dataContestat IS NOT NULL AND mis.dataContestat<=te.dia,1,0)) AS contestatentermini, "
        + " SUM( IF(mis.dataContestat IS NOT NULL AND mis.dataContestat>te.dia,1,0)) AS contestaforatermini "
        + " FROM sig_missatgeria AS mis INNER JOIN tuta_entrevistes as te ON te.id=mis.idEntrevista "
        + " INNER JOIN sig_professorat AS prof ON prof.abrev=mis.destinatari  "
        + " GROUP BY mis.destinatari ORDER BY caducats DESC, pendents DESC, contestatentermini, nombre ";

        
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            
            while (rs1 != null && rs1.next()) {
                BeanStatSolicituds bean = new BeanStatSolicituds();
                bean.setProfe(rs1.getString("nombre"));
                bean.setCaducats(rs1.getInt("caducats"));
                bean.setPendent(rs1.getInt("pendents"));
                bean.setContestatDins(rs1.getInt("contestatentermini"));
                bean.setContestatFora(rs1.getInt("contestaforatermini"));
                list.add(bean);
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        ReportingClass rc = new ReportingClass(cfg);
        rc.statisticsSolicituds( list, new HashMap());
    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void jMenuItem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem31ActionPerformed
        ArrayList<BeanStatPares> list = new ArrayList<BeanStatPares>();
        String SQL1 = "SELECT "
        + " prof.nombre AS tutor, "
        + " CONCAT(xh.Estudis, ' ', xh.Grup) AS grup, "
        + " SUM(IF(dia IS NULL, 1, 1)) AS totalAlumnes, "
        + " SUM(IF(dia IS NOT NULL, 1, 0)) AS totalEntrevistes, "
        + " COUNT( DISTINCT  tenv.exp2) AS totalEntrevistats "
        + " FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes  "
        + " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh "
        + " ON (xh.AnyAcademic='" + cfg.anyAcademicFitxes + "' AND xh.Exp2=xes.Exp2) "
        + " LEFT JOIN "
        + " tuta_entrevistes AS tenv  "
        + " ON tenv.exp2 = xes.Exp2  "
        + " INNER JOIN "
        + " sig_professorat AS prof  "
        + " ON prof.abrev = xes.permisos  "
        + " GROUP BY permisos  "
        + " ORDER BY grup";

        
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            
            while (rs1 != null && rs1.next()) {
                BeanStatPares bean = new BeanStatPares();
                bean.setTutor(rs1.getString("tutor"));
                bean.setGrup(rs1.getString("grup"));

                int na = rs1.getInt("totalAlumnes");
                int nentrevistes = rs1.getInt("totalEntrevistes");
                int nentrevistats = rs1.getInt("totalEntrevistats");

                String tpc = ""+ (100*nentrevistats/(1.0*na));
                if(tpc.length()>4) {
                    tpc = tpc.substring(0, 3);
                }

                bean.setNalumnes(na);
                bean.setEntrevistes( nentrevistes +"");
                bean.setEntrevistats( nentrevistats + " ("+ tpc +"%)");

                list.add(bean);
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        ReportingClass rc = new ReportingClass(cfg);
        rc.statisticsVisitaPares(list, new HashMap());
    }//GEN-LAST:event_jMenuItem31ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        //mostrara en principi els expulsats de la setmana a no ser que ho modifiquem
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int dia = cal.get(Calendar.DAY_OF_WEEK)-2;
        if (dia<0) {
            dia=6;
        }
        //System.out.println(dia);
        cal.add(Calendar.DAY_OF_MONTH, -dia);
        java.util.Date date1 = cal.getTime();
        //System.out.println(date1);
        cal.add(Calendar.DAY_OF_MONTH, 6);
        java.util.Date date2 = cal.getTime();
        //System.out.println(date2);

        DlgCastigDimecres ps = new DlgCastigDimecres(javar.JRDialog.getActiveFrame(),true, cfg);
        ps.setIniDate(date1);
        ps.setEndDate(date2);
        ps.setLocationRelativeTo(null);
        ps.setVisible(true);

        //        PeriodSelect ps = new PeriodSelect(this, true, PeriodSelect.SINGLE_INTERVAL, -1,-1,"DIMECRES");
        //        ps.setDate1(date1);
        //        ps.setDate2(date2);
        //        ps.showEsborraDies(false);
        //        ps.setLocationRelativeTo(null);
        //        ps.setVisible(true);

        if(ps.accept)
        {
            if(ps.getDatabase()==DlgCastigDimecres.IESDIGITALDB) {
                doInformeSancionats(ps.getIniDate(), ps.getEndDate(), "DIMECRES");
            }
            else
            {
                //todo
                doInformeDimecresSGD(ps.getIniDate(), ps.getEndDate());
            }
        }
        ps.dispose();
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem41ActionPerformed
        ArrayList<BeanMedicamentsAutoritzats> list = cfg.getCoreCfg().getIesClient().getFitxesClient().getMedicaments().listInformeAutoritzats();
        ReportingClass rc = new ReportingClass(cfg);
        rc.customReport(list, new HashMap(), "alumnat/medicamentsAutoritzats");
    }//GEN-LAST:event_jMenuItem41ActionPerformed

    private void jMenuItem42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem42ActionPerformed
        ArrayList<BeanMedicamentsSubministrats> list = cfg.getCoreCfg().getIesClient().getFitxesClient().getMedicaments().listSubministraments();
        ArrayList<BeanMedicamentsResum> list2 = cfg.getCoreCfg().getIesClient().getFitxesClient().getMedicaments().listSubministramentsResum();
      
        ReportingClass rc = new ReportingClass(cfg);
        HashMap map = new HashMap();
        //USE REFLECTION
        Object db2 = ReportFactory.createJRBeanCollectionDataSource(list2);       
        map.put("SUBREPORT", db2);
        rc.customReport(list, map, "alumnat/medicamentsSubministrats");

    }//GEN-LAST:event_jMenuItem42ActionPerformed

    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        stamper.addAction(moduleName,"informePAC");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ArrayList<BeanPerduaAC> list = new ArrayList<BeanPerduaAC>();

        String SQL1 = "SELECT "
        + " CONCAT(xh.Estudis,  ' ' ,xh.Grup) AS grupo, "
        + " CONCAT(xes.Llinatge1, ' ', xes.Llinatge2, ', ', xes.Nom1) AS nombre, "
        + " treg.document "
        + " FROM "
        + " tuta_reg_actuacions AS treg  "
        + " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes  "
        + " ON xes.Exp2 = treg.exp2  "
        + " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh  "
        + " ON (xh.Exp2 = xes.Exp2 AND xh.AnyAcademic='" + cfg.anyAcademicFitxes + "') "
        + " WHERE document LIKE '%perdAC={X%'  "
        + " ORDER BY xh.Estudis, xh.Grup, xes.Llinatge1, xes.Llinatge2, xes.Nom1, treg.data1";

        

        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                BeanPerduaAC bean = new BeanPerduaAC();
                bean.setNomAlumne(rs1.getString("nombre"));
                bean.setGrup(rs1.getString("grupo"));
                bean.setMotiu("");
                HashMap mapdoc = StringUtils.StringToHash(rs1.getString("document"), ";");
                bean.setData((String) mapdoc.get("dataConsell"));
                list.add(bean);
            }
            if(rs1!=null) {
                rs1.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        ReportingClass rc = new ReportingClass(cfg);
        rc.customReport(list, new HashMap(), "asistencia/perduaAvalCont");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jMenuItem34ActionPerformed

    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem33ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Statistics stat = new Statistics(cfg);
        stat.doTask();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jMenuItem33ActionPerformed

    private void jMenuInformesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuInformesActionPerformed

    }//GEN-LAST:event_jMenuInformesActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        actionEntrevistaPares();
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem39ActionPerformed
        DesktopManager.organitza();
    }//GEN-LAST:event_jMenuItem39ActionPerformed

    private void jMenuItem40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem40ActionPerformed
        DesktopManager.showDesktop();
    }//GEN-LAST:event_jMenuItem40ActionPerformed

    private void jMenuLandFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuLandFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuLandFActionPerformed

    @Override
    public void refreshUI()
    {
         //Crea models pel combo D'ACTUACIONS
        boolean admin = cfg.getCoreCfg().getUserInfo().getGrant()==User.ADMIN || cfg.getCoreCfg().getUserInfo().getGrant()==User.PREF;
        jComboBox5.setModel(cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getComboModel(FactoryRules.ASSISTENCIA, admin, FactoryRules.ALL));
        jComboBox6.setModel(cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getComboModel(FactoryRules.CONVIVENCIA, admin, FactoryRules.ALL));
        jComboAccions.setModel(cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getComboModel(FactoryRules.ASSISTENCIA, admin, FactoryRules.PENDENTS));
        jComboAccions1.setModel(cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getComboModel(FactoryRules.CONVIVENCIA, admin, FactoryRules.PENDENTS));
        jAssistencia.addActionListener(fillTableListener);
        jConvivencia.addActionListener(fillTableListener);
        jComboAccions.addActionListener(fillTableListener);
        jComboAccions1.addActionListener(fillTableListener);
        
            //Torna a possar tots els menus visible
            //Eines:
            jMenuItem5.setVisible(true);
            jMenuItem7.setVisible(true);
            jMenuItem12.setVisible(true);
            jMenuItem16.setVisible(true);
            jMenuItem3.setVisible(true);
            jMenuItem6.setVisible(true);
            jMenuItem9.setVisible(!moduleGrant.get("informePasswords_gen").isNone());
            jMenuItem14.setVisible(true);
            jSeparator1.setVisible(true);
            jSeparator2.setVisible(true);
            jSeparator4.setVisible(true);
             
            //Informes:
            jMenuItem20.setVisible(true);
            jMenuItem10.setVisible(!moduleGrant.get("informeResumFitxa_gen").isNone());
            jMenuItem18.setVisible(!moduleGrant.get("informeSGD_gen").isNone());
            jMenuItem22.setVisible(true);
            jMenuItem24.setVisible(true);
            jMenu7.setVisible(!moduleGrant.get("informeSancions").isNone());
            jMenu8.setVisible(!moduleGrant.get("informeAccions").isNone());
            jMenu9.setVisible(true);
            jMenuItem33.setVisible(true);
            jMenuItem29.setVisible(true);
            jMenuItem31.setVisible(true);
        
        
            jMenuItem30.setVisible(true);
             
            //crea una llista de pertanença
            belongs = cfg.getCoreCfg().getIesClient().getFitxesClient().getFitxesUtils().belongsList(cfg.getCoreCfg().getUserInfo().getAbrev(), cfg.anyAcademicFitxes);
            ////////System.out.println("quants pertanyen="+belongs.size());

            Icon[] icons = new Icon[] {
                 IconUtils.getIconResource(getClass().getClassLoader(),"org/iesapp/modules/fitxes/icons/locked.gif"),
                 IconUtils.getIconResource(getClass().getClassLoader(),"org/iesapp/modules/fitxes/icons/editable.gif"),
                 IconUtils.getIconResource(getClass().getClassLoader(),"org/iesapp/modules/fitxes/icons/toolbar/pares.gif")
                };

             Icon[] icons2 = new Icon[] {
                 IconUtils.getIconResource(getClass().getClassLoader(),"org/iesapp/modules/fitxes/icons/flag_green.gif"),
                 IconUtils.getIconResource(getClass().getClassLoader(),"org/iesapp/modules/fitxes/icons/flag_red.gif"),
                 IconUtils.getIconResource(getClass().getClassLoader(),"org/iesapp/modules/fitxes/icons/flag_orange.gif"),
                 IconUtils.getBlankIcon()
                };

                
            if(cfg.getCoreCfg().getUserInfo().getGrant()!=User.ADMIN && cfg.getCoreCfg().getUserInfo().getGrant()!=User.PREF)
            {
                jComboBox4.setEnabled(false); //combo dels anys
                jMenu2.setVisible(false);
                jMenuEines.setVisible(false);
                jMenu9.setVisible(false);
                jMenuTutoria.setEnabled(true);
                 
                jMenuItem33.setVisible(false);
                jMenuItem29.setVisible(false);
                jMenuItem31.setVisible(false);
                jMenuItem32.setVisible(false);
              
                jButton1.setEnabled(true);
                jMenuItem30.setVisible(false);
             
                   
           
            }
            else
            {
                jComboBox4.setEnabled(true);
                jMenu2.setVisible(true);
                jMenuEines.setVisible(true);
                jMenuItem29.setVisible(true);
                jMenuItem31.setVisible(true);
                jButton1.setEnabled(true);
            
                jMenuItem32.setVisible(true);
                
               
       
                    
            }
            

            if(cfg.getCoreCfg().getUserInfo().getGrant()==User.PREF )
            {
                    jMenuItem5.setVisible(false);
                    jMenuItem7.setVisible(false);
                    jMenuItem12.setVisible(false);
                    jMenuItem16.setVisible(false);
                    jMenuItem3.setVisible(false);
                    //no desabilitar tutoria
                    jMenuItem6.setVisible(false);
                    
                    jMenuItem8.setVisible(false);
                    jSeparator1.setVisible(false);
                    jSeparator2.setVisible(false);
                    jSeparator4.setVisible(false);
                    jMenu2.setVisible(false);
            }
            
             if( moduleGrant.get("cerca_mostraAvancada").isNone() )
             {
                    jCercaAvancada.setVisible(false);
                    jLinkButton1.setText("Cerca avançada");
             }
             else
             {
                    jCercaAvancada.setVisible(true);
                    jLinkButton1.setText("Cerca simple");
             }
             
              jMenuItem22.setVisible(!moduleGrant.get("informeAccions").isNone());
              jMenuItem24.setVisible(!moduleGrant.get("informeAccions").isNone());
              jMenu11.setVisible(!moduleGrant.get("informeMedicaments").isNone());
             
         
              //identifica els nivells que existeixen
              checkNivells();


                if(cfg.getCoreCfg().getUserInfo().getGrant()!=User.ADMIN && !moduleGrant.get("accions_view").isNone())
                {

                    modelTable1 = new javax.swing.table.DefaultTableModel(
                                new Object [][] {

                                },
                                new String [] {
                                    "Expedient", "Grup", "Alumne/a", "Repeteix", "NESE",
                                    "Accions"
                                }
                            );

                    jTable1.setModel(modelTable1);
                    jTable1.getColumnModel().getColumn(5).setCellRenderer(new MyIconButtonRenderer(icons2));
                    jTable1.getTableHeader().setReorderingAllowed(false);
                    jTable1.setRowHeight(rowHeight);
                    jTable1.setName("jTable1"); // NOI18N

                   
                  
                }


                //Si l'usuari és admin ha de canviar el format
                if(cfg.getCoreCfg().getUserInfo().getGrant()==User.ADMIN)
                {
                jMenuItem30.setVisible(true);
                      
                modelTable1 = new javax.swing.table.DefaultTableModel(
                            new Object [][] {

                            },
                            new String [] {
                                "Expedient", "Grup", "Alumne/a", "Repeteix", "NESE",
                                "Assignat a", "Accions"
                            }
                        );


                jTable1.setModel(modelTable1);
                jTable1.getColumnModel().getColumn(6).setCellRenderer(new MyIconButtonRenderer(icons2));
                jTable1.getTableHeader().setReorderingAllowed(false);
                jTable1.setName("jTable1"); // NOI18N
                }

               //Per a tots els casos
               jTable1.getColumnModel().getColumn(0).setCellRenderer(new MyIconButtonRenderer(icons));
               jTable1.getColumnModel().getColumn(2).setPreferredWidth(250);
               jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
               jTable1.getColumnModel().getColumn(2).setCellRenderer(new MyIconLabelRenderer());
               
               jTable1.setRowHeight(rowHeight);
               
               jButton1.setEnabled(cfg.getCoreCfg().getUserInfo().getIdSGD()>0 && !moduleGrant.get("entrevistaPares_view").isNone()); 
               
              if(cfg.getCoreCfg().getUserInfo().getGrant()==User.TUTOR && cfg.getCoreCfg().getUserInfo().getGrant()!=User.PREF )
              {
                    jNomesTuta.setEnabled(true);
                    jNomesTuta.setSelected(true);
                    jMenuItem19.setVisible(true);
                    jMenuItem9.setVisible(true); 
              }
               else
              {
                   jNomesTuta.setSelected(false);
                   jNomesTuta.setEnabled(false);
                   
              }

              if(cfg.getCoreCfg().getUserInfo().getGrant()==User.NOTUTOR)
              {
                  jMenuItem19.setVisible(false);
                
              }
              else
              {
                  jMenuItem10.setVisible(true);                  
              }

              
              jTabbedPane1.setEnabledAt(1, !moduleGrant.get("cerca_permetAccions").isNone() );
              jTabbedPane1.setEnabledAt(2, !moduleGrant.get("cerca_permetAccions").isNone() );

             
              fillTable();
       
    }


    private void checkNivells()
    {
        
        modelComboNivells.removeAllElements();
        modelComboNivells.addElement("Tots");

        String SQL1 = "Select distinct Ensenyament from `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic where AnyAcademic='"+cfg.anyAcademicFitxes+"' order by Ensenyament ";
               
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            
            while (rs1 != null && rs1.next()) {
                String nivel = rs1.getString("Ensenyament");
                modelComboNivells.addElement(nivel);
            }
             if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        jComboBox1.setModel(modelComboNivells);

    }

     private void checkCursos()
    {

        modelComboCursos.removeAllElements();
        modelComboCursos.addElement("Tots");


        String condicio = "WHERE ";
        String and = "";
        if(jComboBox1.getSelectedIndex()>0)
        {
            condicio += " Ensenyament='"+jComboBox1.getSelectedItem()+"' ";
            and = " AND ";
        }
        condicio += and+" AnyAcademic='"+cfg.anyAcademicFitxes+"' ";
        
        String SQL1 = "Select distinct Estudis from `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic " + condicio+  " order by Estudis";
        //System.out.println(SQL1);

        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);

            while (rs1 != null && rs1.next()) {
                String nivel = rs1.getString("Estudis");
                modelComboCursos.addElement(nivel);
            }
             if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        jComboBox2.setModel(modelComboCursos);

    }

     private void checkLletres()
    {

        modelComboGrups.removeAllElements();
        modelComboGrups.addElement("Tots");


        String condicio = "";
        String and=" ";
        if(jComboBox1.getSelectedIndex()>0)
        {
            condicio += and + " Ensenyament='"+jComboBox1.getSelectedItem()+"' ";
            and = " AND ";
        }
        if(jComboBox2.getSelectedIndex()>0)
        {
            String curs = (String) jComboBox2.getSelectedItem();
            curs = curs.substring(0,1);
            condicio += and + " Estudis LIKE '"+curs+"%' ";
            and = " AND ";
        }
        condicio += and + " AnyAcademic='"+cfg.anyAcademicFitxes+"' ";

        if(!condicio.isEmpty()) {
            condicio = " WHERE " + condicio;
        }


        String SQL1 = "Select distinct Grup from `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic " + condicio+ " order by Grup";
        ////////System.out.println(SQL1);

        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);

            while (rs1 != null && rs1.next()) {
                String nivel = rs1.getString("Grup");
                modelComboGrups.addElement(nivel);
            }
             if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        jComboBox3.setModel(modelComboGrups);

    }


    private void checkAnys()
    {

        modelComboAnys.removeAllElements();
        //22-2-13: The only allowed years are those that we have PREFIXDB+YEAR in database
        
       
        try {
            Statement st;
            st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet("SHOW DATABASES", st);
            while (rs1 != null && rs1.next()) {
                String db = rs1.getString(1);
                if(db.startsWith(CoreCfg.core_mysqlDBPrefix))
                {
                  String nivel = StringUtils.AfterFirst(db, CoreCfg.core_mysqlDBPrefix);
                  if(!nivel.trim().isEmpty())
                  {
                      int any = (int) Double.parseDouble(nivel);
                      nivel = any+"-"+(any+1);
                      modelComboAnys.addElement(nivel);
                  }
                }
            }
            rs1.close();
            st.close();

        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
//        
//        String SQL1 = "Select distinct AnyAcademic from `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic ORDER BY AnyAcademic DESC";
//        ////////System.out.println(SQL1);
//        
//        
//        try {
//            Statement st = cfg.getCoreCfg().getMysql().createStatement();
//            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
//            while (rs1 != null && rs1.next()) {
//                String nivel = rs1.getString("AnyAcademic");
//                int any = (int) Double.parseDouble(nivel);
//                nivel = any+"-"+(any+1);
//                modelComboAnys.addElement(nivel);
//            }
//             if(rs1!=null) {
//                rs1.close();
//                st.close();
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
//        }

        jComboBox4.setModel(modelComboAnys);
        String anyEnDB = cfg.anyAcademicFitxes+"-"+(cfg.anyAcademicFitxes+1);
        jComboBox4.setSelectedItem(anyEnDB);
        cfg.anyAcademicFitxes = getSelectedCurs_Primer();
    }

    
    //Genera un resum de totes les fitxes que estan actualment en la taula seleccionades
    private void genResumFitxes()
    {
        stamper.addAction(moduleName,"exportResumFitxes");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        ArrayList expds = new ArrayList();
        GrantBean gb = moduleGrant.get("informeResumFitxa_gen");
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
            Profile profile = this.getStudentProfileFromLine(i);
           
            //comprova si pertany
            if(moduleGrant.isGranted(gb, profile)) {
                expds.add(profile.getNexp());
            }
        }

        if(expds.isEmpty())
        {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this, "No hi ha informació a mostrar ja que\nno teniu cap alumne assignat.");
            return;
        }
         ReportingClass rp = new ReportingClass(cfg);
         rp.exportResumFitxes(expds);

        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }


     
    private void actionContrasenyes()
    {
        stamper.addAction(moduleName,"exportUsuaris");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ArrayList expds = new ArrayList();
        GrantBean gb = moduleGrant.get("informePasswords_gen");
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
            Profile profile = this.getStudentProfileFromLine(i);
            //comprova si pertany
            if(moduleGrant.isGranted(gb,profile)) {
                expds.add(profile.getNexp());
            }
        }

         if(expds.isEmpty())
        {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this, "No hi ha informació a mostrar ja que\nno teniu cap alumne assignat.");
            return;
        }

        
         java.util.Date desde=null;
         byte mode = 0;
         if(cfg.getCoreCfg().getUserInfo().getGrant()==User.PREF  || cfg.getCoreCfg().getUserInfo().getGrant()==User.ADMIN )
         {
             PickDate dlg = new PickDate(javar.JRDialog.getActiveFrame(), true, cfg);
             dlg.setTitle("Constrasenyes d'alumnes");
             dlg.setLocationRelativeTo(null);
             dlg.setVisible(true);
             if(!dlg.accept) {
                 dlg.dispose();
                 return;
             }
             desde = dlg.date;
             mode = dlg.mode;
             dlg.dispose();
             
             //Obtenim un llistat d'expedients segons
             if(mode!=PickDate.EXPORTFROMDATE) 
             {
                 desde =null;
             }
             else
             {
                 expds =null;
             }
                 
         
             if(mode==PickDate.EXPORTALL)
             {
                 expds = null;   //Llista amb tots els usuaris
              }
         
         }
         ReportingClass rp = new ReportingClass(cfg);
         rp.exportUsuaris(expds, desde);
        
            
       this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
                     

    private void actionSGD()
    {
        stamper.addAction(moduleName,"InformesSGDdlg");
        //Crea dues llistes amb els expedients i amb noms d'alumnes
        ArrayList<Profile> listexpd = new ArrayList<Profile>();
        ArrayList<String> listnoms = new ArrayList<String>();
        
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
            Profile profile = this.getStudentProfileFromLine(i);
            listexpd.add(profile);
            String nom = ((JLabel) jTable1.getValueAt(i, 2)).getText();
            listnoms.add(nom);
        }
        
        InformesSGDdlg dlg = new InformesSGDdlg(javar.JRDialog.getActiveFrame(), false, listexpd, listnoms, cfg);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }
    
                                           
    private void actionEntrevistaPares()
    {
         int row = jTable1.getSelectedRow();

          stamper.addAction(moduleName,"EntrevistaPares");
          GrantBean gb = moduleGrant.get("entrevistaPares_view");
         
          Profile profile = null;
          ArrayList<Profile> list = new ArrayList<Profile>();
          
         for(int i=0; i<jTable1.getRowCount(); i++)
         {
             Profile profile2 = getStudentProfileFromLine(i);
             
             //Modificat per treballar amb permisos
             if(moduleGrant.isGranted(gb, profile2)){
                  list.add(profile2);
                  if(i==row)
                  {
                     profile = profile2;
                  }
             }
          }
         if(list.isEmpty())
         {
             return;
         }
         if(profile==null)
         {
             profile = list.get(0);
         }
         DesktopManager.showEntrevistaPares(this, profile, list, false, cfg);
         
       
    }
                                           

    private void actionOrles()
    {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ArrayList expds = new ArrayList();
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
            CellTableState cts = (CellTableState) jTable1.getValueAt(i, 0);
            expds.add(cts.getCode());
        }

         if(expds.isEmpty())
        {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this, "No hi ha informació a mostrar ja que\nno hi ha cap alumne seleccionat.");
            return;
        }

        stamper.addAction(moduleName,"exportOrles");
        ReportingClass rp = new ReportingClass(cfg);
        rp.exportOrles(expds, getSelectedCurs());

       this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));    
    }
    
//  
//    public void dispose()
//    {
//       Cfg.outStamp();
//       CoreCfg.close();
//       Runtime.getRuntime().removeShutdownHook(Fitxes.shutdownHook);
//       this.dispose();
//       System.exit(0);
//    }
    
//    //Mostra l'ajuda
//    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {                                            
//        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        String path ="doc/fitxeshelp.pdf";
//        try {
//          Desktop.getDesktop().open(new java.io.File(path));
//        } catch (IOException ex) {
//                //
//        }
//        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//    }                                           
 
                               
    private void doInformeTasquesPendents()
    {
        int oldexp2 = -1;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
         
        ArrayList<BeanActuacionsPendents> list = new ArrayList<BeanActuacionsPendents>();
        
        String currentSelection = ""; 
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
            CellTableState cts = (CellTableState) jTable1.getValueAt(i, 0);
            currentSelection += cts.getCode()+",";
        }
        currentSelection = StringUtils.BeforeLast(currentSelection, ",");
        
        String SQL1 = "SELECT "
                + " xh.Exp2, "
                + " CONCAT( "
                + "   xes.Nom1, "
                + "  ' ', "
                + "   xes.Llinatge1, "
                + "   ' ', "
                + "  xes.Llinatge2 "
                + " ) AS nom, "
                + " CONCAT(xh.Estudis,' ',xh.Grup) AS grupo, "
                + " act.actuacio, tuta.data1, "
                + " tuta.id, "
                + " tuta.idActuacio, "
                + " tuta.data2, "
                + " tuta.document "
                + " FROM `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne_historic AS xh  "
                + "  INNER JOIN `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne AS xes  "
                + "  ON xh.Exp2 = xes.Exp2  "
                + "  LEFT JOIN "
                + "   tuta_reg_actuacions AS tuta  "
                + "  ON xh.Exp2 = tuta.exp2  "
                + " LEFT JOIN "
                + "  tuta_actuacions AS act "
                + "  ON tuta.idActuacio = act.id "
                + " WHERE xh.AnyAcademic = '" + cfg.anyAcademicFitxes + "'  "
                + " AND xh.Exp2 IN ("+currentSelection+") "  
                + " ORDER BY estudis, "
                + "   grup, "
                + "   llinatge1, "
                + "   llinatge2, "
                + "   nom1  ";
            
            
            
            try
            {
             Statement st = cfg.getCoreCfg().getMysql().createStatement();
             ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                int exp2 = rs1.getInt("Exp2");
                int id = rs1.getInt("id");
                int idActuacio = rs1.getInt("idActuacio"); //idRule
                String nom = rs1.getString("nom");
                String grupo = rs1.getString("grupo");
                String act = rs1.getString("actuacio");
                //String document = rs1.getString("document");
                java.sql.Date data1= rs1.getDate("data1"); 
                java.sql.Date data2= rs1.getDate("data2"); 
                
                //Primer inclou a l'informe les actuacions pendents
                if(oldexp2==-1 || oldexp2 !=exp2) {
                    if(pend.jobs.containsKey(exp2))
                    {
                        ArrayList<String> lstr = pend.jobs.get(exp2).detallTasks;
                        for(int i=0; i<lstr.size(); i++)
                        {
                             BeanActuacionsPendents ba = new BeanActuacionsPendents();
                             ba.setExpd(""+exp2);
                             ba.setActuacio(lstr.get(i));
                             ba.setAlumne(nom);
                             ba.setGrup(grupo);
                             ba.setObrir("X");
                             list.add(ba);
                        }
                    }
                }
               
                oldexp2 = exp2;
                
                //Despres inclou les actuacions que no han estat tancades
                 if(act!=null && data2 ==null)
                 {
                     BeanActuacionsPendents ba = new BeanActuacionsPendents();
                     ba.setExpd(""+exp2);
                     String descripcio = act+" (Iniciada:"+new DataCtrl(data1).getDiaMesComplet()+") ";
                     
                     //La mesura i el dia de la mesura ara la cercam en la taula
                     //de registres tuta_dies_sancions
                     String extraInfo = "";
                     String SQL2 = "SELECT GROUP_CONCAT( IF(desde!=fins, CONCAT(DATE_FORMAT(desde,'%d/%m/%y'),' a ',"
                             + "DATE_FORMAT(fins,'%d/%m/%y')),DATE_FORMAT(desde,'%d/%m/%y') ) ) AS dies, tipus FROM "
                             + "tuta_dies_sancions WHERE exp2="+exp2+" AND idActuacio="+id+" GROUP BY tipus";
                     Statement st2 = cfg.getCoreCfg().getMysql().createStatement();
                     ResultSet rs2 = cfg.getCoreCfg().getMysql().getResultSet(SQL2,st2);
                     while(rs2!=null && rs2.next())
                     {
                         extraInfo += rs2.getString("tipus")+": "+ rs2.getString("dies") +"  ";
                     }
                     if(rs2!=null){
                         rs2.close();
                         st2.close();
                     }
// HashMap<String, String> docmap = StringUtils.StringToHash(document, ";");
//                     switch(idActuacio)
//                     {
//                         case 102: {
//                             String str = StringUtils.noNull( docmap.get("sancioDimecres"));
//                             if(str.contains("X"))
//                             {
//                                 mesura = " Dies triats: ";
//                                 diaMesura = docmap.get("quinDimecres");
//                             }
//                             break;
//                         }
//                         case 105: {
//                             String str = StringUtils.noNull( docmap.get("sancio_castig_Dimecres"));
//                             if(str.contains("X"))
//                             {
//                                 mesura = " Dimecres triat: ";
//                                 diaMesura = docmap.get("quinDimecres");
//                             }
//                             break;
//                         }
//                         case 106: {
//                             String str = StringUtils.noNull( docmap.get("sancio_castig_Dimecres"));
//                             String str2 = StringUtils.noNull( docmap.get("sancio_castig_Expulsio"));
//                             if(str.contains("X"))
//                             {
//                                 mesura = " Dimecres triat: ";
//                                 diaMesura = docmap.get("quinDimecres");
//                             }
//                             else if(str2.contains("X"))
//                             {
//                                 mesura = " Expulsió triat: ";
//                                 diaMesura = docmap.get("quinDiaExpulsio");
//                             }
//                             
//                             break;
//                         }
//                         case 107: {
//                             mesura = " Expulsió triat: ";
//                             diaMesura = docmap.get("quinDiaExpulsio");
//                             break;
//                         }
//                         case 1:{
//                             mesura = " Expulsió triat: ";
//                             diaMesura = docmap.get("dia_Expulsio");
//                             break;
//                         }
//                         default:
//                         {
//                             if(idActuacio>1 && idActuacio<100)
//                             {
//                                  mesura = " Expulsió triats: ";
//                                  diaMesura = docmap.get("dies_Expulsio");
//                                  break; 
//                             }
//                         }
//                     }
                     
                     ba.setActuacio(descripcio+" "+extraInfo);
                     ba.setAlumne(nom);
                     ba.setGrup(grupo);
                     ba.setTancar("X");
                     list.add(ba);
                 }
                
            }
            if(rs1!=null) {
                    rs1.close();
                    st.close();
                }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
               
        ReportingClass rc = new ReportingClass(cfg);
        rc.sgdReport4(list, new HashMap());
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
    }
    
   
    private void obrirFitxa()
    {
           int row = jTable1.getSelectedRow();
           if(row<0) {
            return;
        }

           CellTableState cts = (CellTableState) jTable1.getValueAt(row, 0);
           int nexpd = cts.getCode();

//           String nom = (String) jTable1.getValueAt(row, 1);
//           nom += " " + (String) jTable1.getValueAt(row, 2);
//           nom += ", " + (String) jTable1.getValueAt(row, 3);

           ArrayList<Integer> llista = new ArrayList<Integer>();
           for(int i=0; i<jTable1.getRowCount(); i++)
           {
                 CellTableState cts2 = (CellTableState) jTable1.getValueAt(i, 0);
                 int expedient =  cts2.getCode();
                 llista.add(expedient);
           }

           stamper.addAction(moduleName,"FitxaAlumne");
           DesktopManager.showFitxaAlumne(this, nexpd, llista, false, cfg);
           //Refresh
           //this.fillTable();
    }

    
    /**
     * Fill table is done in background
     */
    public void fillTable()
    {
        fillTableMethod();
    }
    
    public void fillTableMethod()
    {
        isListening = false;
        isTableFilling = true;
        
        int row = jTable1.getSelectedRow();
        int expSelected = -1;
        if (row >= 0) {

            CellTableState cts = ((CellTableState) jTable1.getValueAt(row, 0));
            expSelected = cts.getCode();
        }

        jTabbedPane2.setSelectedIndex(0);
        int any = getSelectedCurs_Primer();

        int whichSelect = 0;
     
        //esborra la taula
        while(jTable1.getRowCount()>0) {
            modelTable1.removeRow(0);
        }

        String condicions = "";
        String nexp = jNumExp.getText().trim();
        String cogn1 = jPrimerLlinatge.getText().trim();
        String cogn2 = jSegonLlinatge.getText().trim();
        String nombre = jNom.getText().trim();
        String and="";
        
        finder1.clearAll();
        finder1.setEnsenyament( jComboBox1.getSelectedIndex()>0? (String) jComboBox1.getSelectedItem(): null );
        finder1.setEstudis( jComboBox2.getSelectedIndex()>0? (String) jComboBox2.getSelectedItem(): null );
        finder1.setGrup( jComboBox3.getSelectedIndex()>0? (String) jComboBox3.getSelectedItem(): null );
        finder1.setRepetidor( jRepetidor.isSelected() );
        finder1.setAnee( jAnee.isSelected() );
        
        int tabsel = jTabbedPane1.getSelectedIndex();
        
        if(jCercaAvancada.isVisible())
        {
            
            switch(tabsel)
            {
                case 0: //Mostra el resultat de la cerca simple
                        finder1.setExpd(!nexp.isEmpty()? nexp : null);
                        finder1.setLlinatge1(!cogn1.isEmpty()? cogn1 : null);
                        finder1.setLlinatge2(!cogn2.isEmpty()? cogn2 : null);
                        finder1.setNom1(!nombre.isEmpty()? nombre : null);
                        break;
                case 1: //Mostra per actuacions iniciades
                        whichSelect = 1;
                        finder1.setAssistenciaIniciades(jAssistencia1.isSelected()?jComboBox5.getSelectedItem():"");
                        finder1.setConvivenciaIniciades(jConvivencia1.isSelected()?jComboBox6.getSelectedItem():"");
                        finder1.setAmagaFinalitzades(jCheckBox1.isSelected());
                        finder1.setEnviamentSMS(jSMSaEnviar.isSelected(), jSMSenviat.isSelected());
                        finder1.setEnviamentCarta(jCartaEnviar.isSelected(), jCartaEnviada.isSelected());
                        break;
//                case 2:  //Mostra per actuacions pendents
//                       break;
            }
        }
         
      
        
//        if(jComboBox1.getSelectedIndex()>0)
//        {
//            String ensenyament = (String) jComboBox1.getSelectedItem();
//            condicions +=  and + " Ensenyament='"+ensenyament+"' ";
//            and = " AND ";
//        }
//
//        if(jComboBox2.getSelectedIndex()>0)
//        {
//            String estudis = (String) jComboBox2.getSelectedItem();
//           
//            condicions += and + " Estudis = '"+ estudis +"'  ";
//            and = " AND ";
//
//        }
//
//        if(jComboBox3.getSelectedIndex()>0)
//        {
//            String grup = (String) jComboBox3.getSelectedItem();
//
//            condicions += and + " Grup='"+grup+"'  ";
//            and = " AND ";
//        }
//        
//        if(jRepetidor.isSelected()) //Filra nomes els alumnes repetidors en el curs actual
//        {
//            condicions += and + " ( ( estudis IN (SELECT estudis FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh2 WHERE xh.Exp2=xh2.Exp2 AND xh2.AnyAcademic<"
//                    +cfg.anyAcademicFitxes+" AND xh2.AnyAcademic>0) ) OR xes.Repetidor>0 )" ;
//            and = " AND ";
//        }
//        
//         if(jAnee.isSelected()) //Filtra nomes els alumnes Anees
//        {
//            condicions += and + " ( xes.anee<>'' and xes.anee is not null ) " ;
//            and = " AND ";
//        }
//        
//        
//        if(jCercaAvancada.isVisible())
//        {
//            
//            if(tabsel == 0)
//            {
//                if(!nexp.equals(""))
//                {
//                    condicions += and + " xes.Exp2 LIKE '%" + nexp + "%' ";
//                    and = " AND ";
//                }
//                if(!cogn1.equals(""))
//                {
//                    condicions += and + " Llinatge1 LIKE '%" + cogn1 + "%' ";
//                    and = " AND ";
//                }
//                if(!cogn2.equals(""))
//                {
//                    condicions += and + " Llinatge2 LIKE '%" + cogn2 + "%' ";
//                    and = " AND ";
//                }
//                if(!nombre.equals(""))
//                {
//                    condicions += and + " Nom1 LIKE '%" + nombre + "%' ";
//                    and = " AND ";
//                }    
//            }
//            else if(tabsel == 1)
//            {
//            //Condicions que provenen del Tab d'accions de tutoria ja iniciades pel tutor
//            //Si canviam d'any cal afegir la condicio que nomes cercam accions dins l'any
//            
//                    
//                    whichSelect = 1;
//                    //Enviament de SMSs
//                    if(jSMSaEnviar.isSelected() && !jSMSenviat.isSelected() )
//                    {
//                    
//                        condicions += and + " ((idActuacio='100' OR idActuacio='105') AND data2 IS NULL) ";
//                        and = " AND ";
//                    }
//                    if(!jSMSaEnviar.isSelected() && jSMSenviat.isSelected())
//                    {
//                   
//                        condicions += and + " ((idActuacio='100' OR idActuacio='105') AND data2 IS NOT NULL) ";
//                        and = " AND ";
//                    }
//                    if(jSMSaEnviar.isSelected() && jSMSenviat.isSelected())
//                    {
//                  
//                        condicions += and + " (idActuacio='100' OR idActuacio='105') ";
//                        and = " AND ";
//                    }
//                    
//                    //Enviament de cartes
//                    if(jCartaEnviar.isSelected() && !jCartaEnviada.isSelected() )
//                    {
//                    
//                        condicions += and + " ((idActuacio>='101' AND idActuacio<='103') AND data2 IS NULL) ";
//                        and = " AND ";
//                    }
//                    if(!jCartaEnviar.isSelected() && jCartaEnviada.isSelected())
//                    {
//                   
//                        condicions += and + " ((idActuacio>='101' AND idActuacio<='103') AND data2 IS NOT NULL) ";
//                        and = " AND ";
//                    }
//                    if(jCartaEnviar.isSelected() && jCartaEnviada.isSelected())
//                    {
//                  
//                        condicions += and + " (idActuacio>='101' AND idActuacio<='103') ";
//                        and = " AND ";
//                    }
//                    
//                   //mostra nomes les actuacions de l'any academic
//                    String condAnys = "data1>='"+any+"-09-01' AND data1<='"+(any+1)+"-09-31' ";
//                    condicions  += and + " "+condAnys;
//                    and =" AND ";
//
//
//            }
//            else if(tabsel==2)
//            {
//                int index = jComboAccions.getSelectedIndex();
//                int min = 0;
//                int max = 0;
//
//                String restriction = "";
//                 
//                if(jConvivencia.isSelected())
//                {
//                 String inicondition = and + " (xh.Exp2 IN ( "+
//                                      "  SELECT tmp.Exp2 FROM "+
//                                      "  ( "+
//                                      "    SELECT "+
//                                      "      Exp_FK_ID AS Exp2, "+
//                                      "      ( "+
//                                      "       (NumAL_1rTRI + NumAL_2nTRI + NumAL_3rTRI)/5+ "+
//                                      "        NumAG_1rTRI + NumAG_2nTRI + NumAG_3rTRI "+
//                                      "      ) AS amon "+
//                                      "    FROM `"+CoreCfg.core_mysqlDBPrefix+"`.fitxa_alumne_curs WHERE IdCurs_FK_ID='"+any+"' ";
//                                      
//                                    and = " AND ";
//
//                       
//                        boolean q = false; // jAmagaEnTramit.isSelected();
//
//                        switch(index)
//                        {
//                            case (0):   if(q)  restriction =" AND xh.Exp2 NOT IN (SELECT exp2 AS Exp2 FROM tuta_reg_actuacions WHERE idActuacio=1 ) ";
//                                        condicions +=  inicondition + " HAVING (amon >= 4 AND amon<8) "+
//                                          "  ) AS tmp ) "+restriction+" )"; break;
//                            case (1):   if(q)  restriction =" AND xh.Exp2 NOT IN (SELECT exp2 AS Exp2 FROM tuta_reg_actuacions WHERE idActuacio=2 ) ";
//                                        condicions +=  inicondition + " HAVING (amon >= 8 AND amon<12) "+
//                                          "  ) AS tmp ) "+restriction+" )"; break;
//                            case (2):   if(q)  restriction =" AND xh.Exp2 NOT IN (SELECT exp2 AS Exp2 FROM tuta_reg_actuacions WHERE idActuacio=3 ) ";
//                                        condicions +=  inicondition + " HAVING (amon >= 12 AND amon<17) "+
//                                           "  ) AS tmp ) "+restriction+" )"; break;
//                            case (3):   if(q)  restriction =" AND xh.Exp2 NOT IN (SELECT exp2 AS Exp2 FROM tuta_reg_actuacions WHERE idActuacio=4 ) ";
//                                        condicions +=  inicondition + " HAVING (amon >= 17 AND amon<22) "+
//                                           "  ) AS tmp ) "+restriction+" )"; break;
//                            case (4):   if(q)  restriction =" AND xh.Exp2 NOT IN (SELECT exp2 AS Exp2 FROM tuta_reg_actuacions WHERE idActuacio=5 ) ";
//                                        condicions +=  inicondition + " HAVING (amon >= 22 AND amon<27) "+
//                                           "  ) AS tmp ) "+restriction+" )"; break;
//                            case (5):   condicions +=  inicondition + " HAVING amon >= 27 "+
//                                           "  ) AS tmp ) "+restriction+" )"; break;
//                        }
//
//                }
//                else
//                {
//                     String inicondition = and + " (xh.Exp2 IN ( "+
//                                      "  SELECT tmp.Exp2 FROM "+
//                                      "  ( "+
//                                      "    SELECT "+
//                                      "      Exp_FK_ID AS Exp2, "+
//                                      "      ( "+
//                                      "       F_1rTRI+F_2nTRI+F_3rTRI "+
//                                      "      ) AS falt "+
//                                      "    FROM `"+CoreCfg.core_mysqlDBPrefix+"`.fitxa_alumne_curs WHERE IdCurs_FK_ID='"+any+"' ";
//
//                                    and = " AND ";
//
//                            
//                            boolean q =  true ; //jAmagaEnTramit.isSelected();
//
//                            switch(index)
//                            {
//                                case (0):   if(q)  restriction =" AND xh.Exp2 NOT IN (SELECT exp2 AS Exp2 FROM tuta_reg_actuacions WHERE idActuacio=100 OR idActuacio=101 ) ";
//                                            condicions +=  inicondition + " HAVING (falt >= 14 AND falt<28) "+
//                                               "  ) AS tmp ) "+restriction+" )"; break;
//                                case (1):   if(q)  restriction =" AND xh.Exp2 NOT IN (SELECT exp2 AS Exp2 FROM tuta_reg_actuacions WHERE idActuacio=102 ) ";
//                                             condicions +=  inicondition + " HAVING (falt >= 28 AND falt<42) "+
//                                               "  ) AS tmp ) "+restriction+" )"; break;
//                                case (2):   if(q)  restriction =" AND xh.Exp2 NOT IN (SELECT exp2 AS Exp2 FROM tuta_reg_actuacions WHERE idActuacio=103 OR idActuacio=104 ) ";
//                                            condicions +=  inicondition + " HAVING (falt >= 42) "+
//                                              "  ) AS tmp ) "+restriction+" )"; break;
//                            }
//                            
//                           
//                           
//                }
//                }//fins aqui tabsel 2
//        }
//
//
//
//
        if(jNomesTuta.isSelected() && cfg.getCoreCfg().getUserInfo().getGrant()==User.TUTOR)
        {
           
            Grup mg = cfg.getCoreCfg().getUserInfo().getGrup();
            String extracondition =  " (  (xes.Permisos='' AND (xh.Ensenyament='"+mg.getXEnsenyament()+
                    "' AND xh.Estudis='"+mg.getXEstudis()+"' AND xh.Grup='"+mg.getXGrup()+"'))" +
                         "       OR (xes.Permisos LIKE '%"+cfg.getCoreCfg().getUserInfo().getAbrev()+"%') )";
            condicions += and + extracondition;
            finder1.appendExtraConditions(extracondition);
            //and = " AND ";
        }
//       
//        condicions += and +  " xh.AnyAcademic='"+any+"' ";
//
//        if(!condicions.equals("")) {
//            condicions = " WHERE " + condicions;
//        }


        
      
//        String SQL1 = "";
//        if(whichSelect==0)
//        {
//            //10-10-2012 xes.Permisos -> xh.Permisos
//             SQL1 = "SELECT DISTINCT xes.Permisos, xes.Llinatge1, xes.Llinatge2, xes.Nom1, xes.Exp2, xes.sexe, xes.anee, xd.Foto, "
//                    + " xh.Ensenyament, xh.Estudis, xh.Grup,  IF(MAX(tenv.dia)>=CURRENT_DATE(), MAX(tenv.dia), NULL) AS entrevistes  FROM `"
//                    +  CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes ON xes.Exp2=xh.Exp2 "
//                    + " LEFT JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_detall AS xd on xd.Exp_FK_ID = xes.Exp2 "
//                    + " LEFT JOIN tuta_entrevistes AS tenv ON tenv.exp2=xh.Exp2 "
//                    + condicions 
//                    + " GROUP BY xh.Exp2 "
//                    + order;
//             
//             //System.out.println("type 0 :: "+SQL1);
//        }
//        else
//        {
//            SQL1 = "SELECT DISTINCT xes.Permisos, xes.Llinatge1, xes.Llinatge2, xes.Nom1, xes.Exp2, xes.sexe, xes.anee, xd.Foto, "
//                    + " xh.Ensenyament, xh.Estudis, xh.Grup,  IF(MAX(tenv.dia)>=CURRENT_DATE(), MAX(tenv.dia), NULL) AS entrevistes FROM `"+
//                      CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic as xh INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes ON xh.Exp2=xes.Exp2 "
//                    + " LEFT JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_detall AS xd on xd.Exp_FK_ID = xes.Exp2 "
//                    + " INNER JOIN tuta_reg_actuacions AS tuta "
//                    + " ON xes.Exp2 = tuta.exp2 "
//                    + " LEFT JOIN tuta_entrevistes AS tenv ON tenv.exp2=xh.Exp2 "
//                    + condicions 
//                    + " GROUP BY xh.Exp2 "
//                    + order;
//            
//            //System.out.println("type 1 :: "+SQL1);
//        }
       
        String order = msort.getMysqlOrder();
        finder1.setOrderQuery(order);
        finder1.setWhichSelect(whichSelect);
        try {
            
            ResultSet rs1 = null;
            FinderSet set = null;
            if(tabsel<2) {
                set = finder1.getResultSet();  ////cfg.getCoreCfg().getMysql().getResultSet(SQL1);
                rs1 = set.getResultSet();
            }
            else {
                String actuacioTriada = "";
                if(jAssistencia.isSelected())
                {
                    actuacioTriada = (String) jComboAccions.getSelectedItem();
                }
                else if(jConvivencia.isSelected())
                {
                    actuacioTriada = (String) jComboAccions1.getSelectedItem();           
                }
                set = finder1.getResultSetPendents(actuacioTriada);
                rs1 = set.getResultSet();
            }
            
            GrantBean gb_accionsview = moduleGrant.get("accions_view");
            
            while (rs1 != null && rs1.next()) {
                Profile profile = new Profile();
                int exp = rs1.getInt("Exp2");
                profile.setNexp(exp);
                
                String llinatge1 = rs1.getString("Llinatge1");
                String llinatge2 = rs1.getString("Llinatge2");
                
                String repetidor = "";
                if(listRepetidors.contains(exp)) {
                    repetidor ="      Sí";
                    profile.setRepetidor(true);
                }
                
                String tmp = StringUtils.noNull( rs1.getString("anee"));
                String anee = "";
                if(!tmp.isEmpty()) {
                    anee ="      Sí";
                    profile.setNese(true);
                }
                
                String nom1 = rs1.getString("Nom1");
                
                String nomComplet = llinatge1 + " " + llinatge2 +", "+nom1; 
                javax.swing.JLabel labelNom = new javax.swing.JLabel();
                labelNom.setText(nomComplet);
                javax.swing.Icon photo = createIcon(rs1.getBytes("Foto"), rowHeight);
                
                if(photo==null)
                {
                    String resource = "/org/iesapp/modules/fitxes/icons/default32x40.png";
                    if(rs1.getString("sexe").equalsIgnoreCase("D")){
                        resource = "/org/iesapp/modules/fitxes/icons/default2_32x40.png";
                    }
                    photo = new javax.swing.ImageIcon(getClass().getResource(resource));
                }
                labelNom.setIcon(photo);
                

                String grupo = rs1.getString("Estudis")+ " " + rs1.getString("Grup");
                String editable = rs1.getString("Permisos");
                java.sql.Date entrevistes = rs1.getDate("entrevistes");

                //Comprova si pertany al seu grup:
                boolean pertany = belongs.contains(exp);
                profile.setBelongs(pertany);
                
                boolean condPertaneca = moduleGrant.isGranted(gb_accionsview, profile);
                CellTableState cts = new CellTableState("",exp,pertany?1:0);
                if(cfg.getCoreCfg().getUserInfo().getGrant()!=User.ADMIN && cfg.getCoreCfg().getUserInfo().getGrant()!=User.PREF && pertany && entrevistes!=null)
                {
                    cts = new CellTableState("",exp,2); //mostra dibuixet pares
                    cts.setTooltip("Entrevista pendent dia "+new DataCtrl(entrevistes).getDiaMesComplet());
                }
 
                CellTableState cts2;
                cts2 = new CellTableState("",-1,3); //==blank; no mostra perquè no es de la tutoria
               
                if(pend.jobs.containsKey(exp))
                {
                    if(condPertaneca)
                    {
                        cts2 = new CellTableState("",-1,1); //te tasques pendents
                        cts2.setTooltip(pend.jobs.get(exp).detallTasks.toString());
                    }
                }
                else
                {
                    //no te tasques pendents
                    if(pend.oberts.contains(exp))
                    {
                        if(condPertaneca)
                        {
                            cts2 = new CellTableState("",-1,2);  //té actuacions obertes (taronja)
                            cts2.setTooltip("Té actuacions sense tancar");
                        }
                    }
                    else
                    {
                        if(condPertaneca)
                        {
                            cts2 = new CellTableState("",-1,0); //no té actuacions obertes (verd)
                        }
                    }
                }


                if(cfg.getCoreCfg().getUserInfo().getGrant()==User.ADMIN)
                {
                   modelTable1.addRow(new Object[]{cts, grupo, labelNom, repetidor, anee, editable, cts2});
                }
                else if(cfg.getCoreCfg().getUserInfo().getGrant()!=User.ADMIN && !gb_accionsview.isNone())
                {
                   modelTable1.addRow(new Object[]{cts, grupo, labelNom, repetidor, anee, cts2});
                }
                else
                {
                   modelTable1.addRow(new Object[]{cts, grupo, labelNom, repetidor, anee});
                }
            }
             
            set.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        jStatusBarResults.setText("S'han trobat "+jTable1.getRowCount()+" resultats de "+nalumnes);

        //torna a seleccionar
        if(expSelected>0)
        {
            for(int i=0; i<jTable1.getRowCount(); i++)
            {
                 CellTableState cts = ((CellTableState) jTable1.getValueAt(i,0)) ;
                 int exp2 = cts.getCode();
                 if(exp2 == expSelected)
                 {
                     jTable1.setRowSelectionInterval(i, i);
                     
                     break;
                 }
            }
        }
        //Ensure that selection is visible
        
        int sel = jTable1.getSelectedRow();
        if(sel>=0)
        {
            Rectangle rect = jTable1.getCellRect(sel, 0, true);
            jTable1.scrollRectToVisible(rect);
        }
        //Register selection to moduleLookup
        currentListAll = getCurrentList(false);
        currentListBelongs = getCurrentList(true);
        TableSelection tableSelection = new TableSelection(getCurrentSelection(), currentListAll, currentListBelongs, cfg.anyAcademicFitxes);
        content.set(Collections.singleton(tableSelection),null);
                
        
        isListening = true;
        isTableFilling = false;
    }


    // Metodes que permeten triar la seleccio del cursos academics
    public String getSelectedCurs()
    {
        return (String) jComboBox4.getSelectedItem();
    }
    
    private int getSelectedCurs_Primer()
    {
        String txt = StringUtils.BeforeLast(this.getSelectedCurs(), "-");
        return Integer.parseInt(txt);
    }
    
    private void doInformeRegistres(String tipus)
    {
        //Mostra el dialeg
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cfg.anyAcademicFitxes);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        java.util.Date dateDesde = cal.getTime();
        
       
        java.util.Date dateFins = new java.util.Date();
        
        org.iesapp.framework.dialogs.PeriodSelect dlg = new org.iesapp.framework.dialogs.PeriodSelect(javar.JRDialog.getActiveFrame(), true, PeriodSelect.SINGLE_INTERVAL, -1, -1, "", cfg.getCoreCfg());
        dlg.setDate1(dateDesde);
        dlg.setDate2(dateFins);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
        
        if(dlg.accept)
        {
            dateDesde = dlg.getDate1(); 
            dateFins = dlg.getDate2();
        }
        dlg.dispose();    
        DataCtrl ctrl1 = new DataCtrl(dateDesde);
        DataCtrl ctrl2 = new DataCtrl(dateFins);
        String desde_sql = ctrl1.getDataSQL();
        String fins_sql = ctrl2.getDataSQL();
        String desde_txt = ctrl1.getDiaMesComplet();
        String fins_txt = ctrl2.getDiaMesComplet();
        
//        String SQL1 = "SELECT CONCAT(llinatge1,' ',llinatge2,', ',nom1) AS nom, "
//                + " CONCAT(xh.Estudis,' ',xh.Grup) AS grup, tuta.data1, "
//                + " act.actuacio FROM tuta_reg_actuacions AS tuta "
//                + " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes ON tuta.exp2=xes.Exp2 "
//                + " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh ON xh.Exp2=tuta.exp2 AND xh.AnyAcademic='"+cfg.anyAcademicFitxes+"'"
//                + " INNER JOIN tuta_actuacions AS act ON act.id=tuta.idActuacio"
//                + "  WHERE document LIKE '%Extraescolars={X%' AND tuta.data1>='"+desde_sql+"' "
//                + "  AND tuta.data1<='"+fins_sql+"' ";
//        
        String SQL1 = "SELECT tds.exp2, tds.desde, tds.fins, ta.actuacio, "
                + " CONCAT(xes.Llinatge1,' ', xes.Llinatge2,', ',xes.Nom1) AS nom, "
                + " CONCAT(xh.Estudis, ' ',xh.Grup) AS grup FROM tuta_dies_sancions AS tds"
                + " INNER JOIN tuta_reg_actuacions AS trg ON tds.idActuacio=trg.id"
                + " INNER JOIN tuta_actuacions AS ta ON ta.id=trg.idActuacio"
                + " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes ON xes.Exp2=tds.exp2 "
                + " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh ON "
                + " (xes.Exp2=xh.Exp2 AND xh.AnyAcademic='"+cfg.anyAcademicFitxes+"') WHERE tds.tipus='"+tipus+"'"
                + " AND trg.data1>='"+desde_sql+"' AND trg.data1<='"+fins_sql+"' ORDER BY tds.desde, grup, nom, actuacio";
        
          ArrayList<BeanExpulsats> list = new ArrayList<BeanExpulsats>();
          
          
          try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                String nom = rs1.getString("nom");
                String grup = rs1.getString("grup");
                String desde= new DataCtrl(rs1.getDate("desde")).getDiaMesComplet();
                String fins = new DataCtrl(rs1.getDate("fins")).getDiaMesComplet();
                String actuacio = rs1.getString("actuacio");
                
                BeanExpulsats bean = new BeanExpulsats();
                bean.setActuacio(actuacio);
                bean.setDesde(desde);
                bean.setFins(fins);
                bean.setGrup(grup);
                bean.setNom(nom);
                list.add(bean);
            }
            if(rs1!=null)
            {
                rs1.close();
                st.close();
            }
          } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
         }
        
          if(list.size()>0)
          {
            ReportingClass rc = new ReportingClass(cfg);
            HashMap map = new HashMap();
            map.put("desde", desde_txt);
            map.put("fins", fins_txt);
            map.put("titol", "Registre de sancions -"+tipus+"-");
                    
            rc.customReport(list, map, "convivencia/reportExpulsats");
          }
          else
          {
               JOptionPane.showMessageDialog(this, "No s'ha trobat registres tipus: "+tipus);
          }
    }
    
    private void doInformeSancionats(Date date1, Date date2, String tipus) {

          String sdate1 = new DataCtrl(date1).getDiaMesComplet();
          String sdate2 = new DataCtrl(date2).getDiaMesComplet();
        
          String SQL1 = " SELECT  "+
           " CONCAT(  "+
             " xes.Llinatge1,  "+
             " ' ',  "+
             " xes.Llinatge2,  "+
             " ', ',  "+
             " xes.Nom1  "+
           " ) AS nom,  "+
           " CONCAT(xh.Estudis, ' ', xh.Grup) AS grup,  "+
           " dies.desde,  "+
           " dies.fins,  "+
           " act.actuacio   "+
         " FROM  "+
           " tuta_dies_sancions AS dies   "+
           " INNER JOIN  "+
           " tuta_reg_actuacions AS tuta   "+
           " ON tuta.id = dies.idActuacio   "+
           " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh   "+
           " ON xh.Exp2 = dies.exp2   "+
           " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes   "+
           " ON xh.Exp2 = xes.Exp2   "+
           " LEFT JOIN  "+
           " tuta_actuacions AS act  "+ 
         " ON act.id = tuta.idActuacio   "+
           " WHERE xh.AnyAcademic = '"+cfg.anyAcademicFitxes+"'   "+
           " AND dies.tipus = '"+tipus+"'   "+
           " AND dies.desde >= '"+ new DataCtrl(date1).getDataSQL() +"'   "+
           " AND dies.fins <= '"+ new DataCtrl(date2).getDataSQL() +"' "+
           " ORDER BY dies.desde, nom, grup";
            
          
          //System.out.println(SQL1);
          
          ArrayList<BeanExpulsats> list = new ArrayList<BeanExpulsats>();
          
          
          try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);   
            while(rs1!=null && rs1.next())
            {
                String nom = rs1.getString("nom");
                String grup = rs1.getString("grup");
                String desde= new DataCtrl(rs1.getDate("desde")).getDiaMesComplet();
                String fins = new DataCtrl(rs1.getDate("fins")).getDiaMesComplet();
                String actuacio = rs1.getString("actuacio");
                
                BeanExpulsats bean = new BeanExpulsats();
                bean.setActuacio(actuacio);
                bean.setDesde(desde);
                bean.setFins(fins);
                bean.setGrup(grup);
                bean.setNom(nom);
                list.add(bean);
            }
            if(rs1!=null)
            {
                rs1.close();
                st.close();
            }
          } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
         }
        
          if(list.size()>0)
          {
            ReportingClass rc = new ReportingClass(cfg);
            HashMap map = new HashMap();
            map.put("desde", sdate1);
            map.put("fins", sdate2);
            if(tipus.equals("EXPULSIO")) {
                  map.put("titol", "Informe d'alumnes Expulsats");
              }
            else if(tipus.equals("DIMECRES")) {
                  map.put("titol", "Informe d'alumnes amb càstig de dimecres");
              }
            
            rc.customReport(list, map, "convivencia/reportExpulsats");
          }
          else
          {
               JOptionPane.showMessageDialog(this, "No s'ha trobat cap alumne/a expulsat.");
          }
    }
 

    private javax.swing.Icon createIcon(byte[] foto, int heigth2)
    {
        if(foto==null) {
            return null;
        }
        
        java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(foto);
        java.awt.Image scaledInstance = image.getScaledInstance(-1, heigth2, java.awt.Image.SCALE_DEFAULT);
                
        return new ImageIcon(scaledInstance) ; 
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jAnee;
    private javax.swing.JCheckBox jAssistencia;
    private javax.swing.JRadioButton jAssistencia1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCartaEnviada;
    private javax.swing.JCheckBox jCartaEnviar;
    private javax.swing.JPanel jCercaAvancada;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JComboBox jComboAccions;
    private javax.swing.JComboBox jComboAccions1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JCheckBox jConvivencia;
    private javax.swing.JRadioButton jConvivencia1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private com.l2fprod.common.swing.JLinkButton jLinkButton1;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenu jMenuEines;
    private javax.swing.JMenu jMenuFinestres;
    private javax.swing.JMenu jMenuInformes;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem39;
    private javax.swing.JMenuItem jMenuItem40;
    private javax.swing.JMenuItem jMenuItem41;
    private javax.swing.JMenuItem jMenuItem42;
    private javax.swing.JMenuItem jMenuItem43;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenu jMenuLandF;
    private javax.swing.JMenu jMenuTutoria;
    private javax.swing.JTextField jNom;
    private javax.swing.JCheckBox jNomesTuta;
    private javax.swing.JTextField jNumExp;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField jPrimerLlinatge;
    private javax.swing.JCheckBox jRepetidor;
    private javax.swing.JCheckBox jSMSaEnviar;
    private javax.swing.JCheckBox jSMSenviat;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jSegonLlinatge;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private org.iesapp.modules.fitxes.util.JSharedDoc jSharedDoc1;
    private javax.swing.JLabel jStatusBarResults;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBarInformes;
    private javax.swing.JToolBar jToolBarTutoria;
    // End of variables declaration//GEN-END:variables


    @Override
    public ImageIcon getModuleIcon() {
        return null;
    }

    @Override
    public boolean isMultipleInstance() {
        return false;
    }

   
    @Override
    public void setMenus(JMenuBar jMenuBar1, JToolBar jToolbar1, StatusBar jStatusBar1) {
      jMenuBar1.add(jMenuInformes,2);
      jMenuBar1.add(jMenuTutoria,3);
      jMenuBar1.add(jMenuEines,4);
      
      jToolbar1.add(jToolBarTutoria);
      jToolbar1.add(jToolBarInformes);
      
      ((StatusBarZone) jStatusBar1.getZone("third")).addComponent(jStatusBarResults);
    }
  
  //
  // Obté la llista d'alumnes repetidors
  //  

    private ArrayList<Integer> getListRepetidors() {
        ArrayList list = new ArrayList<Integer>();
        
        
        String SQL1  ="SELECT xh.Exp2 FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes ON xes.Exp2=xh.Exp2 WHERE "+
                      "   (estudis IN (SELECT estudis FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh2 WHERE xh.Exp2=xh2.Exp2 AND AnyAcademic<"+
                      cfg.anyAcademicFitxes+" AND AnyAcademic>0) AND AnyAcademic="+cfg.anyAcademicFitxes+") OR xes.Repetidor>0 ";
        
        
        try {
             Statement st = cfg.getCoreCfg().getMysql().createStatement();
             ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                    list.add(rs1.getInt("Exp2"));
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    private boolean switchDatabases(int any) {
        
        boolean success = false;
        //Intenta efectuar els canvis de bases de dades
        int up1 = cfg.getCoreCfg().getMysql().setCatalog(CoreCfg.core_mysqlDBPrefix+any);
        int up2 = cfg.getCoreCfg().getSgd().setCatalog((String) CoreCfg.configTableMap.get("sgdDBPrefix")+any);
        
        
        if(up1>0 && up2>0)
        {
            listRepetidors = getListRepetidors();
            pend = new TasquesPendents(any+"", cfg.getCoreCfg().getIesClient());
            pend.checkTasquesPendents();
            
            success = true;
        }
        else
        {
            //ERROR: no s'han trobat les bases per l'any demanat i Torna a l'any actual (retorna success=false)
             cfg.getCoreCfg().getMysql().setCatalog(CoreCfg.core_mysqlDBPrefix+cfg.anyAcademicFitxes);
             cfg.getCoreCfg().getSgd().setCatalog((String) CoreCfg.configTableMap.get("sgdDBPrefix")+cfg.anyAcademicFitxes);
        }
                
        return success;
    }
    
    //Retorna un array d'integers corresponent a la llista d'expedients dels alumnes que hi ha en la seleccio
    //parametres:
   
    private ArrayList<Integer> getExpdsInView()
    {
        ArrayList expds = new ArrayList();
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
            CellTableState cts = (CellTableState) jTable1.getValueAt(i, 0);           
            expds.add(cts.getCode());
        }
        return expds;
    }
    
    
//Aquest informe es per a tots els castigats de dimecres sigui o no per accio de fitxes
//Llegeix de sgd
    private void doInformeDimecresSGD(Date date1, Date date2) {
         String sdate1 = new DataCtrl(date1).getDataSQL();
         String sdate2 = new DataCtrl(date2).getDataSQL();
        
         String simbolDimecres = (String) CoreCfg.configTableMap.get("simbolCastigDimecres");
         if(simbolDimecres==null || simbolDimecres.isEmpty()) {
            simbolDimecres = "CD";
         }
         int idDimecres = cfg.getCoreCfg().getSgdClient().getTipoIncidencias().getMapaInc2Id().get(simbolDimecres);
        
          
         String SQL1 = " SELECT al.nombre, g.descripcion FROM faltasalumnos AS fa "
                  + " INNER JOIN alumnos AS al ON fa.idAlumnos=al.id "
                  + " INNER JOIN gruposalumno AS ga ON ga.idAlumnos=al.id INNER JOIN grupos AS g "
                  + " ON g.grupoGestion=ga.grupoGestion WHERE idTipoIncidencias="+idDimecres+" AND "
                  + "dia>='"+sdate1+"' AND dia<='"+sdate2+"' ORDER BY descripcion, nombre";
            
          
          //System.out.println(SQL1);
          
          ArrayList<BeanExpulsats> list = new ArrayList<BeanExpulsats>();
          
          
          try {
            Statement st = cfg.getCoreCfg().getSgd().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1,st);    
            while(rs1!=null && rs1.next())
            {
                String nom = rs1.getString(1);
                String grup = rs1.getString(2);
                
                BeanExpulsats bean = new BeanExpulsats();
                bean.setGrup(grup);
                bean.setNom(nom);
                list.add(bean);
            }
            if(rs1!=null)
            {
                rs1.close();
                st.close();
            }
          } catch (SQLException ex) {
            Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
         }
        
          if(list.size()>0)
          {
            ReportingClass rc = new ReportingClass(cfg);
            HashMap map = new HashMap();
            map.put("desde", new DataCtrl(date1).getDiaMesComplet());
            map.put("fins", new DataCtrl(date2).getDiaMesComplet());
            map.put("titol", "LLISTAT D'ALUMNES SANCIONATS");
            
            rc.customReport(list, map, "convivencia/reportDimecresSGD");
          }
          else
          {
               JOptionPane.showMessageDialog(this, "No s'ha trobat cap alumne/a expulsat.");
          }
    }
    
    
    /**
     * Gets expd of the currently selected student
     * @return 
     */
    private int getCurrentSelection()
    {
        int expd = -1;
        int row = jTable1.getSelectedRow();
        if(row>=0)
        {
            CellTableState cts = (CellTableState) jTable1.getValueAt(row, 0);
            expd = cts.getCode();
        }
            
        return expd;
    }
    
    /**
     * Gets the whole list of expd - students in search
     * if pertany is true, only includes students that belong to the current user
     * @param pertany
     * @return 
     */
    private ArrayList<Integer> getCurrentList(boolean pertany)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
          
         for(int i=0; i<jTable1.getRowCount(); i++)
         {
            CellTableState cts = (CellTableState) jTable1.getValueAt(i, 0);
            int expd = cts.getCode();
            
            //Modificat per treballar amb permisos
            if(!pertany || (pertany && belongs.contains(expd)))
            {
                list.add(expd);
            }
          }
         return list;
    }

//    private void loadPlugins() {    
//        //Carrega els plugins i acaba de crear el gui
//        loadInstalledPlugins = PluginFactory.loadInstalledPlugins(PluginFactory.PLUGINS_FITXES);
//        //System.out.println("loadedPlugins: "+loadInstalledPlugins.size());
//        if(loadInstalledPlugins.isEmpty())
//        {
//            return;
//        }
//       ActionListener pluginListener =  new ActionListener(){
//
//                        public void actionPerformed(ActionEvent e) {
//                            String className = e.getActionCommand();
//                           
//                try {
//                    org.iesapp.framework.util.JarClassLoader.getInstance().addDirToClasspath(new File(CoreCfg.contextRoot + "/lib/plugins"));                    
//                    Class<?> pluginClass = Class.forName(className);
//                    PluginInterface plugin = (PluginInterface) pluginClass.newInstance();
//                    plugin.setParameters(createPluginParametersMap());
//                    plugin.initialize();
//                    DesktopManager.showPlugin(plugin);
//                } catch (Exception ex) {
//                    Logger.getLogger(FitxesGUI.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                
//            }
//            
//            private HashMap<String, Object> createPluginParametersMap() {
//                HashMap<String, Object> map = new HashMap<String, Object>();
//                map.put("currentExpd", getCurrentSelection());
//                map.put("listExpds", getCurrentList(true));
//                return map;
//            }
//        };
//        
//        
//        
//        
//        for (BeanInstalledPlugin bean : loadInstalledPlugins) {
//            //System.out.println("\t"+bean.getClassName());
//            if(bean.isEnabled() && (bean.getVisibility().equals("*") || bean.getVisibility().contains(cfg.getCoreCfg().getUserInfo().getRole())))
//            {
//            for(BeanAnchorPoint anchor: bean.getListAnchors())
//            {
//                String sfile = "lib/plugins/resources/"+bean.getClassName().replaceAll("\\.", "_")+".gif";
//                File iconFile = new File(sfile);
//                ImageIcon icon  = null;
//                if(iconFile.exists())
//                {
//                    icon = new ImageIcon(iconFile.getAbsolutePath());
//                }
//                else
//                {
//                     icon = new ImageIcon(FitxesGUI.class.getResource("/org/iesapp/modules/fitxes/icons/plugin.gif"));
//                }
//                
//                if(anchor.getLocation().equalsIgnoreCase("menu"))
//                {
//                    JMenu menu = findMenuComponentByName(anchor.getParentId());
//                    JMenuItem item = new JMenuItem(bean.getDisplayName());
//                    item.setActionCommand(bean.getClassName());
//                    item.addActionListener(pluginListener);
//                    item.setIcon(icon);
//                    menu.add(item);
//                    installedPluginComponents.add(item);
//                    installedPluginParent.add(menu);
//                    
//                }
//                else if(anchor.getLocation().equalsIgnoreCase("toolbar"))
//                {
//                    JToolBar menu = findToolBarComponentByName(anchor.getParentId());
//                    JButton newButton = new JButton();
//                    newButton.setActionCommand(bean.getClassName());
//                    newButton.setToolTipText(bean.getDisplayName());
//                    newButton.setIcon(icon);
//                    menu.add(newButton);
//                    newButton.addActionListener(pluginListener); 
//                    installedPluginComponents.add(newButton);
//                    installedPluginParent.add(menu);
//                }
//            }
//            }
//        }
//       
//    }
//
//    private JMenu findMenuComponentByName(String componentName) {
//        Component componente = null;
//        for(Component comp: jMenuBar1.getComponents())
//        {
//            if(comp!=null && comp.getName()!=null && comp.getName().equals(componentName))
//            {
//                componente = comp;
//                break;
//            }
//        }
//        if(componente==null)
//        {
//             JMenu menu = new JMenu("Plugins");
//             jMenuBar1.add(menu);
//             menu.setName(componentName);
//             componente = menu;
//        }
//        
//        return (JMenu) componente;
//    }
//
//    private JToolBar findToolBarComponentByName(String componentName) {
//        
//        if(componentName.isEmpty())
//        {
//            return jToolBar3;
//        }
//        Component componente = null;
//        for(Component comp: jToolBar3.getComponents())
//        {
//            if(comp!=null && comp.getName()!=null && comp.getName().equals(componentName))
//            {
//                componente = comp;
//                break;
//            }
//        }
//        if(componente==null)
//        {
//             JToolBar menu = new JToolBar("Plugins");
//             jToolBar3.add(menu);
//             menu.setName(componentName);
//             componente = menu;
//        }
//        
//        return (JToolBar) componente;
//    }

    @Override
    public void displayPluginTopWindow(TopPluginWindow plgWin) {
        DesktopManager.showPlugin(plgWin);
    }
 
    private Profile getStudentProfileFromLine(int row) {
         CellTableState cts = (CellTableState) jTable1.getValueAt(row, 0);
         Profile profile = new Profile();
         profile.setNexp( cts.getCode() );
         profile.setBelongs( belongs.contains(cts.getCode()) ); //cts.getState()>=1
         profile.setRepetidor( !((String) jTable1.getValueAt(row,3)).isEmpty() );
         profile.setNese( !((String) jTable1.getValueAt(row,4)).isEmpty() );
         return profile;
    }
    
        // This should only update the status in the table, it is not
        // required to create the table with fillTable()
        //
        private void fromTableUpdateFlags() {


        //which is the last column?
        int col = jTable1.getColumnCount() - 1;
        GrantBean gb_accionsview = moduleGrant.get("accions_view");


        for (int i = 0; i < jTable1.getRowCount(); i++) {
            CellTableState cts0 = (CellTableState) jTable1.getValueAt(i, 0);
            //CellTableState cts = (CellTableState) jTable1.getValueAt(i, col);

            int exp = cts0.getCode();
            Profile profile = new Profile();
            profile.setNexp(col);

            if (listRepetidors.contains(exp)) {
                profile.setRepetidor(true);
            }

            //Comprova si pertany al seu grup:
            boolean pertany = belongs.contains(exp);
            profile.setBelongs(pertany);

            boolean condPertaneca = moduleGrant.isGranted(gb_accionsview, profile);


            CellTableState cts2 = new CellTableState("", -1, 3); //==blank; no mostra perquè no es de la tutoria

           //System.out.println(exp+" is in pend jobs? "+pend.jobs.containsKey(exp));
           //System.out.println(exp+" is in oberts jobs? "+pend.oberts.contains(exp));
                    
            if (pend.jobs.containsKey(exp)) {
                if (condPertaneca) {
                    cts2 = new CellTableState("", -1, 1); //te tasques pendents
                    cts2.setTooltip(pend.jobs.get(exp).detallTasks.toString());
                }
            } else {
                //no te tasques pendents
                if (pend.oberts.contains(exp)) {
                    if (condPertaneca) {
                        cts2 = new CellTableState("", -1, 2);  //té actuacions obertes (taronja)
                        cts2.setTooltip("Té actuacions sense tancar");
                    }
                } else {
                    if (condPertaneca) {
                        cts2 = new CellTableState("", -1, 0); //no té actuacions obertes (verd)
                    }
                }
            }

            jTable1.setValueAt(cts2, i, col);
        }


    }
}
