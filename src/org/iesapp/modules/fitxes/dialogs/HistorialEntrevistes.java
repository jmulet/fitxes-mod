/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.dialogs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.iesapp.clients.iesdigital.fitxes.BeanEntrevistaPares;
import org.iesapp.clients.iesdigital.fitxes.BeanReportActuacions;
import org.iesapp.clients.iesdigital.missatgeria.BeanMissatge;
import org.iesapp.clients.sgd7.evaluaciones.EvaluacionesCollection;
import org.iesapp.clients.sgd7.reports.BeanSGDResumInc;
import org.iesapp.clients.sgd7.reports.InformesSGD;
import org.iesapp.database.MyDatabase;
import org.iesapp.framework.dialogs.ReportFactory;
import org.iesapp.framework.table.CellDateEditor;
import org.iesapp.framework.table.CellDateRenderer;
import org.iesapp.framework.table.CellTableState;
import org.iesapp.framework.table.MyCheckBoxRenderer;
import org.iesapp.framework.table.MyIconButtonRenderer;
import org.iesapp.framework.table.MyIconLabelRenderer;
import org.iesapp.framework.table.TextAreaEditor;
import org.iesapp.framework.table.TextAreaRenderer;
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
public class HistorialEntrevistes extends javax.swing.JScrollPane{
    private final Cfg cfg;
    private JTable jTable2;
    private final Date avui;
    private DefaultTableModel modelTable2;
    protected final int any;
    private final int expedient;
    private boolean isListening;
    private final String dbName;
    private String nombre;
    private String grupo;
    private final String dbSgdName;
    private ArrayList<BeanSeleccio> listSelect;
    private final String tutor;
    
    public HistorialEntrevistes(int any, int expedient, Cfg cfg)
    {
        this.cfg = cfg;
        this.any = any;
        this.dbName = CoreCfg.core_mysqlDBPrefix+any;
        this.dbSgdName = (String) CoreCfg.configTableMap.get("sgdDBPrefix")+any;
        this.expedient = expedient;
        this.tutor = "";  //TODO-NEEDS TO BE IMPLEMENTED
        avui = new java.util.Date();
        //Create a model
        initComponents();
        generateList();
    }

    private void initComponents() {

        jTable2 = new javax.swing.JTable() {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean editable = false;
                if (colIndex == 5) {
                    return (getAny()==cfg.anyAcademicFitxes);  //Editabilitat de la columna acords-presos
                } else if (colIndex == 1) {
                    java.util.Date date = (java.util.Date) jTable2.getValueAt(rowIndex, 1);
                    return date.after(avui) && (getAny()==cfg.anyAcademicFitxes);
                }
                return editable;
            }
        };
        modelTable2 = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "id", "Data entrevista", "Sol·licita informació a", "SMS", "Document", "Acords presos"
                });
        jTable2.setModel(modelTable2);
        String[] icons2 = new String[]{
            "/org/iesapp/modules/fitxes/icons/delete.gif", "/org/iesapp/modules/fitxes/icons/blank.gif"
        };

        String[] icons = new String[]{"/org/iesapp/modules/fitxes/icons/print.gif"};

        jTable2.getColumnModel().getColumn(0).setCellRenderer(new MyIconButtonRenderer(icons2));

        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.setRowHeight(32);

        jTable2.getColumnModel().getColumn(1).setCellRenderer(new CellDateRenderer());
        jTable2.getColumnModel().getColumn(2).setCellRenderer(new TextAreaRenderer());
        jTable2.getColumnModel().getColumn(3).setCellRenderer(new MyCheckBoxRenderer());
        jTable2.getColumnModel().getColumn(4).setCellRenderer(new MyIconLabelRenderer(icons));
        jTable2.getColumnModel().getColumn(5).setCellRenderer(new TextAreaRenderer());
        jTable2.getColumnModel().getColumn(5).setCellEditor(new TextAreaEditor());
        jTable2.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTable2.getColumnModel().getColumn(1).setCellEditor(new CellDateEditor());
        jTable2.getColumnModel().getColumn(1).setCellRenderer(new CellDateRenderer());
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(245);
        jTable2.getColumnModel().getColumn(3).setPreferredWidth(30);
        jTable2.getColumnModel().getColumn(4).setPreferredWidth(120);
        jTable2.getColumnModel().getColumn(5).setPreferredWidth(200);
        jTable2.setName("jTable2"); // NOI18N

        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }

        });

         this.setViewportView(jTable2);
        
         jTable2.setIntercellSpacing( new java.awt.Dimension(2,2) );
         jTable2.setGridColor(java.awt.Color.gray);
         jTable2.setShowGrid(true);
         jTable2.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if(!isListening) {
                    return;
                }
                int col = e.getColumn();
                int row = jTable2.getSelectedRow();
                if(col==1)   //canvi de data
                {
                    
                    CellTableState cts = (CellTableState) jTable2.getValueAt(row, 0);
                    java.util.Date data = (java.util.Date) jTable2.getValueAt(row, 1);
                    String SQL1 = "UPDATE "+dbName+".tuta_entrevistes set dia='"+new DataCtrl(data).getDataSQL()+"' where id="+cts.getCode();
                  //  //System.out.println("SQL! "+SQL1);
                    cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
                }
                else if(col==5) //canvi de acords
                {
                    
                }
            }
        });
        

    }
    
    public void jTable2MouseClicked(java.awt.event.MouseEvent evt){
    
    int col = jTable2.getSelectedColumn();
    int row = jTable2.getSelectedRow();
      
        if(col==0)
        {
            CellTableState cts = (CellTableState) jTable2.getValueAt(row, 0);
             if(cts.getState()==1)
             {
                    return;
             }
            //Esborra l'entrevista
            int n = JOptionPane.showConfirmDialog(this, "Es perdran totes les dades associades a aquest entrevista.\n"
                    + "Segur que la voleu esborrar?", "Confirmació", JOptionPane.YES_NO_OPTION);
            if(n==JOptionPane.YES_OPTION)
            {
                
                //System.out.println("Esborrant "+cts.getCode());
                
                int id = cts.getCode();
                String SQL1 = "DELETE FROM "+dbName+".tuta_entrevistes WHERE id='"+id+"' LIMIT 1";
                int nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
                //Els missatges a les PDAs no les esborra simplement les marca com a llegits
                SQL1 = "SELECT id, idMensajeProfesor FROM "+dbName+".sig_missatgeria WHERE idEntrevista="+id;
                 try {
                    Statement st = cfg.getCoreCfg().getMysql().createStatement();
                    ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
                    while(rs1!=null && rs1.next())
                    {
                        int idmissatgeria = rs1.getInt(1);
                        int idsms = rs1.getInt(2);
                        if(idsms>0 && getAny()==cfg.anyAcademicFitxes)
                        {
                            org.iesapp.clients.sgd7.mensajes.MensajesProfesores mp = cfg.getCoreCfg().getSgdClient().getMensajesProfesores(idsms);
                            mp.setBorradoUp(true);
                            mp.save();
                        }
                        String SQL2 = "DELETE FROM "+dbName+".sig_missatgeria WHERE id='"+idmissatgeria+"' and idEntrevista='"+id+"' LIMIT 1";
                        nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL2);                        
                    }
                    if(rs1!=null)
                    {
                        rs1.close();
                        st.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(EntrevistaPares.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                fillTable();
            }
        }
        if(col==1)
        {
            //Modifica la data de l'entrevista        
        }
        if(col==4)
        {
            generaInforme(row);
        }
      
    }                                    

    
    /**
     * On entrevista click, should generate a report
     * @param row 
     */
    private void generaInforme(int row)
    {
          //Primera passa és determinar si es tracta d'una sol.licitud antiga
        //(no s'envia res a sig_missatgeria) o es nova (hi ha les sol.licitus a missatgeria)
        CellTableState cts0 = (CellTableState) jTable2.getValueAt(row, 0);
        int idEntrevista = cts0.getCode();        
        boolean modelNou = false;
        String SQL1 = "SELECT * FROM "+dbName+".sig_missatgeria WHERE idEntrevista='" + idEntrevista+ "'";
        try{
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            if(rs1!=null && rs1.next())
            {
              modelNou = true;
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(EntrevistaPares.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("modelNou?="+modelNou);
        
        ArrayList<BeanEntrevistaPares> listbean = new ArrayList<BeanEntrevistaPares>();
        
        /**
         * Model Nou
         */
        if(modelNou)
        {
                SQL1 = "SELECT * FROM "+dbName+".sig_missatgeria WHERE idEntrevista='" + idEntrevista+ "' ORDER by materia";
                //System.out.println(SQL1);
                try{
                    Statement st = cfg.getCoreCfg().getMysql().createStatement();
                    ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
                    while(rs1 != null && rs1.next()) {
                        
                       //Problema si dos professors imparteixen la mateixa matèria
                       //per exemple agrupaments flexibles
                       int idGrupAsig = rs1.getInt("idMateria");
                       String abrevProfe = rs1.getString("destinatari");
                       
                      //cerca la informacio de la materia
                       BeanSeleccio seleccio = null;
                       //System.out.println("listSelect is length -->"+listSelect.size());
                       for(int j=0; j<listSelect.size(); j++)
                       {
                          if(listSelect.get(j).idGrupAsig == idGrupAsig && listSelect.get(j).abrev.equals(abrevProfe))
                          {
                            //System.out.println("found materia->"+listSelect.get(j).idGrupAsig );
                            seleccio = listSelect.get(j);
                            break;
                          }
                        }
                
                    if(seleccio!=null)
                    {
                        //System.out.println("adding seleccio" );
                        BeanEntrevistaPares bean = new BeanEntrevistaPares();
                        bean.setMateria(seleccio.materia);
                        bean.setProfesor(seleccio.nomProfe);
                        bean.setActitud(StringUtils.noNull(rs1.getString("actitud")));
                        bean.setFeina(StringUtils.noNull(rs1.getString("feina")));
                        bean.setNotes(StringUtils.noNull(rs1.getString("notes")));
                        bean.setObservacions(StringUtils.noNull(rs1.getString("comentaris")));
                        if (rs1.getDate("dataContestat") != null) {
                            bean.setContestat("Sí");
                        }
                        else
                        {
                            bean.setContestat("No");
                        }

                        //Si l'informe és tipus nou, comprova si el professor ha contestat o no
                        //Si no ha contestat proporciona informacio d'emergència si existeix
                        if (bean.getContestat().equalsIgnoreCase("No")) {
                        BeanMissatge auto = getAutoMissatgeria( cfg.getCoreCfg().getSgd(), expedient, idGrupAsig, seleccio.idAsig, seleccio.idProfe);
                        bean.setActitud(auto.getActitud());
                        bean.setFeina(auto.getFeina());
                        bean.setObservacions(auto.getComentari());
                        bean.setNotes(auto.getNotes());
                    }
                        listbean.add(bean);
                    }  
                    }
                    if (rs1 != null) {
                            rs1.close();
                            st.close();
                        }
                } catch (SQLException ex) {
                    Logger.getLogger(EntrevistaPares.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
        }
        else  //MODEL ANTIC
        {
            String aqui = (String) jTable2.getValueAt(row, 2);
            if (aqui == null){
                aqui = "";
            }
            
            ArrayList<String> list = StringUtils.parseStringToArray(aqui, ";", 0);


            for (int i = 0; i < listSelect.size(); i++) {
                BeanSeleccio seleccio = listSelect.get(i);
                String profe = seleccio.nomProfe;
                String materia = seleccio.materia;

                if (list.contains(profe)) {
                    BeanEntrevistaPares bean = new BeanEntrevistaPares();
                    bean.setMateria(materia);
                    bean.setProfesor(profe);
                    bean.setFeina("");
                    bean.setNotes("");
                    bean.setObservacions("");
                    bean.setContestat("No");
                    listbean.add(bean);
                }
         }
        }
            
      
        
        //Genera l'informe
        
         java.util.Date data = (java.util.Date) jTable2.getValueAt(row, 1);
         String dia = new DataCtrl(data).getDiaMesComplet();
        
         ReportingClass rc = new ReportingClass(cfg);

         HashMap map = new HashMap();
         map.put("alumne", nombre);
         map.put("grup", grupo);
         map.put("data", dia);
         map.put("tutor", tutor);
         map.put("acords", (String) jTable2.getValueAt(row, 5));
         
         String tp = "";
         //Inclou les actuacions pendents
         if(getAny()==cfg.anyAcademicFitxes)
         {
            tp = "No hi ha actuacions pendents.";
            if (FitxesGUI.pend.jobs.containsKey(expedient)) {
                tp = FitxesGUI.pend.jobs.get(expedient).detallTasks.toString();
            }           
         }
         map.put("actuacionsPendents", tp);
         //inclou dates
         String datainici = "01/09/"+getAny();
         String datafinal = new DataCtrl().getDiaMesComplet();
         String iniSQL = getAny()+"-09-01";
         String fiSQL = new DataCtrl().getDataSQL();
         
         map.put("datainici", datainici);
         map.put("datafinal", datafinal);
         
         //Genera l'historial d'actuacions realitzades
         if(modelNou)
         {          
             ArrayList<BeanReportActuacions> list = new ArrayList<BeanReportActuacions>();

             SQL1 = " SELECT  "
                     + " CONCAT(xh.Estudis, ' ', xh.Grup) AS grup,  "
                     + " tut.exp2,  "
                     + " CONCAT(  "
                     + "   xes.Llinatge1,  "
                     + "   ' ',  "
                     + "    xes.Llinatge2,  "
                     + "    ', ',  "
                     + "    xes.Nom1  "
                     + "  ) AS alumne,  "
                     + "  CASE WHEN (ISNULL(prof.nombre))   "
                     + "  THEN tut.iniciatper   "
                     + "  ELSE prof.nombre END  "
                     + "  AS propietari,  "
                     + "  act.actuacio,  "
                     + "  tut.data1,  "
                     + "  tut.data2,  "
                     + "  tut.resolucio   "
                     + " FROM  "
                     + dbName+".tuta_reg_actuacions AS tut   "
                     + "  LEFT JOIN  "
                     + dbName+".sig_professorat AS prof   "
                     + "  ON prof.abrev = tut.iniciatper   "
                     + "  INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes   "
                     + "  ON xes.Exp2 = tut.exp2   "
                     + "  INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh "
                     + "  ON xes.Exp2 = xh.Exp2 AND AnyAcademic='" + getAny() + "'"
                     + "  INNER JOIN  "
                     + dbName+".tuta_actuacions AS act   "
                     + "  ON act.id = tut.idActuacio   "
                     + "  AND tut.exp2="+expedient
                     + "  AND tut.data1>='"+iniSQL+"' AND tut.data1<='"+fiSQL+"' "
                     + "  ORDER BY data1 ASC";

             //System.out.println(SQL1);
             
             try {
                 Statement st = cfg.getCoreCfg().getMysql().createStatement();
                 ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
                 while (rs1 != null && rs1.next()) {
                     
                    BeanReportActuacions bra =  new BeanReportActuacions();
                    
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

                    list.add(bra);
                 }
                 if(rs1!=null) {
                     rs1.close();
                     st.close();
                 }
             } catch (SQLException ex) {
                 Logger.getLogger(EntrevistaPares.class.getName()).log(Level.SEVERE, null, ex);
             }
             
             Object db2 = ReportFactory.createJRBeanCollectionDataSource(list);              
             map.put("subReport",db2);
         }
         
         rc.entrevistaPares(listbean, map, modelNou);
    }

   public void fillTable()
   {
        
        if(modelTable2==null) {
            return;
        }

        isListening=false;
        //esborra la taula
        while(jTable2.getRowCount()>0) {
            modelTable2.removeRow(0);
        }
       
        String SQL1 = "SELECT ent.id, ent.abrev, ent.dia, ent.para, ent.sms, ent.acords, "
               + " CONCAT( SUM(IF(dataContestat IS NULL,0,1)), ' de ',"
               + " SUM(IF(mis.destinatari IS NULL,0,1))) AS score FROM "+dbName+".tuta_entrevistes "
               + " AS ent LEFT JOIN "+dbName+".sig_missatgeria AS mis ON mis.idEntrevista=ent.id  "
               + " WHERE exp2='"+expedient+"' GROUP BY ent.id ORDER BY ent.dia DESC";
               
      
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while (rs1 != null && rs1.next()) {

               int id = rs1.getInt("id");
 
               java.sql.Date data = rs1.getDate("dia");
               int sms= rs1.getInt("sms");
               String rawPara = rs1.getString("para");
               ParseFieldPara pfp = new ParseFieldPara(rawPara);
               String para = pfp.getText(listSelect);
               String score = rs1.getString("score");
               
               CellTableState cts = new CellTableState("",-1,0);
 
               if(!score.equals("0 de 0"))
               {
                    cts.setText(score);
                    cts.setTooltip("Núm. de professors que han contestat");
               }
               else
               {
                   cts.setText("Model antic");
               }
//               }
               
               
               int status = 0;
               if(data.before(new java.util.Date()))
               {
                   status = 1;
               }
               
               CellTableState cts2 = new CellTableState("",id,status);
               cts2.setTooltip("Esborra l'entrevista");
               modelTable2.addRow(new Object[]{cts2, data, para, sms==1, cts, StringUtils.noNull(rs1.getString("acords"))});
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EntrevistaPares.class.getName()).log(Level.SEVERE, null, ex);
        }
        isListening = true;
    }
   
   
    
    // Retorna els comentaris positius o negatius de l'alumne  
    public BeanMissatge getAutoMissatgeria(MyDatabase sgd, int expedient, int idGrupAsig, int idAsig, int idProfe) {
         BeanMissatge sms = new BeanMissatge();
         String autoNotes = cfg.getCoreCfg().getIesClient().getMissatgeriaCollection().getAutoNotes(any, expedient, idGrupAsig, idProfe);
         sms.setNotes(autoNotes);
                 
         InformesSGD infsgd = new InformesSGD(any, cfg.getCoreCfg().getSgdClient()); 
         BeanSGDResumInc inf = infsgd.getResumIncidenciesByAsig(expedient, EvaluacionesCollection.getInicioCurso(cfg.getCoreCfg().getSgdClient()),
                                                            new java.util.Date(), idAsig);
         
         if(inf.getAg()==0 && inf.getAl()==0)
         {
             sms.setActitud("No té amonestacions.");
         }
         else
         {
             String txt = "Té "+inf.getAg()+" Amon. Greus i "+inf.getAl()+" Amon. Lleus. ";
             txt +="Recents: "+infsgd.getLastIncidenciesByAsig(expedient, 4, idAsig, Arrays.asList(new String[]{"AG", "AL"}));
             sms.setActitud(txt);
         }
         
         String cncp = "";
         if(inf.getCp()!=0 || inf.getCn()!=0)
         {
             cncp += infsgd.getLastIncidenciesByAsig(expedient, 4, idAsig, Arrays.asList(new String[]{"CP","CN"}));
         }
         sms.setComentari(cncp);
         
         String feina="";
         if(inf.getFj()>0)
         {
            feina = "Faltes sense justificar = "+inf.getFa()+ ", justificades = "+inf.getFj();
         }
         else if(inf.getFa()>0)
         {
             feina="Té "+inf.getFa()+" faltes sense justificar ";
         }
         sms.setFeina(feina);
         
         return sms;
    }
    
    
    /**
     * Genera la llista de bean-matèries a que estava matriculat l'alumne durant el curs "any"
     */
    public final void generateList()
    {   
        listSelect = new ArrayList<BeanSeleccio>();
        //abans tenia aa.id as idGrupAsig
        //Aquesta query dona les assignatures dels alumnes
        String SQL1 = "SELECT DISTINCT alumn.expediente, alumn.nombre, g.grupo, a.descripcion, "
                + " a.id as idAsig, ga.id as idGrupAsig, "+
                "p.codigo AS profesor, p.nombre AS NombreProfe "+
               " FROM "+dbSgdName+".Asignaturas a "+
                " INNER JOIN "+dbSgdName+".ClasesDetalle cd ON 1=1 "+
                " INNER JOIN "+dbSgdName+".HorasCentro hc ON 1=1 "+
                " INNER JOIN "+dbSgdName+".Horarios h ON 1=1 "+
                " INNER JOIN "+dbSgdName+".GrupAsig ga ON 1=1 "+
                " INNER JOIN "+dbSgdName+".Grupos g ON 1=1 "+
                " INNER JOIN "+dbSgdName+".AsignaturasAlumno aa ON 1=1 "+
                " INNER JOIN "+dbSgdName+".alumnos alumn ON alumn.id=aa.idAlumnos "+
                " LEFT OUTER JOIN "+dbSgdName+".Aulas au ON h.idAulas=au.id "+
               " LEFT OUTER JOIN "+dbSgdName+".Profesores p ON h.idProfesores=p.id "+
               "  WHERE alumn.expediente="+expedient+" AND (a.descripcion NOT LIKE 'Atenció educativa%') AND "+
                " (a.descripcion NOT LIKE 'Tut%') AND (a.descripcion NOT LIKE 'EA%') AND "+
                "  aa.idGrupAsig=ga.id "+
                " AND  (aa.opcion<>'0' AND (cd.opcion='X' OR cd.opcion=aa.opcion)) "+
                " AND  h.idClases=cd.idClases "+
                " AND cd.idGrupAsig=ga.id "+
                " AND  h.idHorasCentro=hc.id "+
                " AND ga.idGrupos=g.id "+
                " AND  ga.idAsignaturas=a.id "+
                " ORDER BY a.descripcion, NombreProfe ";

        nombre ="";
        grupo="";


        try {
            Statement st = cfg.getCoreCfg().getSgd().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1,st);
      
            while (rs1 != null && rs1.next()) {

               nombre= rs1.getString("nombre");
               grupo= rs1.getString("grupo");
               
               int idProfe= rs1.getInt("profesor");      
               //Per un determinat any, quina era la abrev, d'un professor amb idProfe en el sistema SGD
               String abrev = cfg.getCoreCfg().getIesClient().getProfessoratData().getAbrev(any, idProfe);
                       
               String nombreprofe= rs1.getString("NombreProfe");
               String descripcion= rs1.getString("descripcion");
               int idasig = rs1.getInt("idAsig");
               int idgrupasig = rs1.getInt("idGrupAsig");
               
               BeanSeleccio bean = new BeanSeleccio(nombreprofe,abrev,idProfe,descripcion,idasig,idgrupasig);
               listSelect.add(bean);
               //System.out.println("Any "+any+": Seleccio: "+bean.toString());

               //evita demanar-me informacio a mi mateix
               boolean inclou =true;
               if(abrev.equals(cfg.getCoreCfg().getUserInfo().getAbrev()) || 
                       descripcion.toUpperCase().startsWith("EA")) {
                    inclou=false;
                }
                          
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(HistorialEntrevistes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void commitAcords()
    {
            if(getAny()!=cfg.anyAcademicFitxes)
            {
                return;
            }
            jTable2.editCellAt(0, 0);
            //Cal que actualitzi els camps "acords presos" a la base de dades
            for(int i=0; i<jTable2.getRowCount(); i++)
            {
                CellTableState cts  = (CellTableState) jTable2.getValueAt(i,0);
                int id = cts.getCode();
                String SQL1 = "UPDATE "+dbName+".tuta_entrevistes SET acords=? WHERE id="+id;
                int nup = cfg.getCoreCfg().getMysql().preparedUpdate(SQL1, new Object[]{jTable2.getValueAt(i, 5)});
            }
    }

    public int getAny() {
        return any;
    }
}

