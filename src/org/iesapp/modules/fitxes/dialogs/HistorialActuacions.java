/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.dialogs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.iesapp.framework.table.CellDateRenderer;
import org.iesapp.framework.table.CellTableState;
import org.iesapp.framework.table.MyIconButtonRenderer;
import org.iesapp.framework.table.MyIconLabelRenderer;
import org.iesapp.framework.table.TextAreaRenderer;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.IconUtils;
import org.iesapp.modules.fitxescore.util.Cfg;

/**
 *
 * @author Josep
 */
public class HistorialActuacions extends javax.swing.JScrollPane{
    private final Cfg cfg;
    protected final int any;
    private final int expedient;
    //private boolean isListening;
    private final String dbName;
    //private String nombre;
    //private String grupo;
    //private final String dbSgdName;
    private JTable jTable1;
    private DefaultTableModel modelTable1;
    private CellDateRenderer cellDateRenderer1;
    private CellDateRenderer cellDateRenderer2;
    private MyIconLabelRenderer iconLabelRenderer1;
   // private ArrayList<Integer> llistaIdTasksInTable;
   // private ArrayList<Actuacio> listActuacions;

    public HistorialActuacions(int any, int expedient, Cfg cfg)
    {
        this.cfg = cfg;
        this.any = any;
        this.dbName = CoreCfg.core_mysqlDBPrefix+any;
        //this.dbSgdName = (String) CoreCfg.configTableMap.get("sgdDBPrefix")+any;
        this.expedient = expedient;
         //Create a model
        initComponents();
        jTable1.setIntercellSpacing( new java.awt.Dimension(2,2) );
        jTable1.setGridColor(java.awt.Color.gray);
        jTable1.setShowGrid(true);
        fillTable();  
    }

    private void initComponents() {
        jTable1 = new javax.swing.JTable() {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
               return false;
            }
        };
        modelTable1 = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Id", "Data inici", "Acció", "Data fi", "Prefectura" //, "Document"
                });
        jTable1.setModel(modelTable1);
        Icon[] icons = new Icon[]{
            IconUtils.getIconResource(getClass().getClassLoader(), "org/iesapp/modules/fitxes/icons/print2.gif"),
            IconUtils.getBlankIcon(),
            IconUtils.getBlankIcon()
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
        //jTable1.getColumnModel().getColumn(5).setCellRenderer(new MyIconButtonRenderer(icons));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setRowHeight(32);
        jTable1.setName("jTable1"); // NOI18N

        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
 
        this.setViewportView(jTable1);

    }

     protected final void fillTable()
    {
        fillTableBasic();
//        doHightLight();
    }

    private void fillTableBasic() {
        
        //llistaIdTasksInTable = new ArrayList<Integer>();

        while(jTable1.getRowCount()>0) {
            modelTable1.removeRow(0);
        }
      
         String SQL1 = "SELECT treg.*, ta.actuacio FROM "+dbName+".tuta_reg_actuacions AS treg INNER JOIN "+
                 dbName+".tuta_actuacions AS ta ON ta.id=treg.idActuacio WHERE exp2='"+expedient+"' ORDER BY data1";
         try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while (rs1 != null && rs1.next()) {
                int id = rs1.getInt("id");
                int idrule = rs1.getInt("idActuacio");
                
                //CellTableState cts = new CellTableState("",-1,0);
                //cts.setTooltip("Edita i visualitza el document");
                CellTableState cts2 = new CellTableState(rs1.getString("actuacio"), -idrule, 1);
                CellTableState cts0 = new CellTableState("", id, 2);
                
                modelTable1.addRow(new Object[]{cts0, rs1.getDate("data1"), cts2, rs1.getDate("data2"), rs1.getString("resolucio")}); //, cts
            }
            if (rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccionsAlumne4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    


}
