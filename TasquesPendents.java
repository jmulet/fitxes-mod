/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.modules.fitxes.util;
 
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptException;
import org.iesapp.database.MyDatabase;
import org.iesapp.framework.pluggable.modulesAPI.ErrorDisplay;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.JSEngine;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class TasquesPendents 
{
    public HashMap<Integer, TasquesPendentsStruct> jobs;
    public ArrayList<Integer> oberts;
    private String any;
    private MyDatabase con;
    private boolean connected;
    private PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
    private boolean running;
    private  ArrayList<BeanCheckeables> listCheckeables;
    private  JSEngine jsEngine;
    private final CoreCfg coreCfg;
    private String abrevTutor;

    public TasquesPendents(CoreCfg coreCfg) {
        this.coreCfg = coreCfg;
        any = coreCfg.anyAcademic + "";
        initialize();
    }
    
    public TasquesPendents(Cfg cfg)
    {
        this.coreCfg = cfg.getCoreCfg();
        oberts = new ArrayList<Integer>();
        any = ""+cfg.anyAcademicFitxes;
        initialize(); 
    }
    
    private void initialize()
    {
        jobs = new HashMap<Integer, TasquesPendentsStruct>();
        con = new MyDatabase(CoreCfg.core_mysqlHost, CoreCfg.core_mysqlDBPrefix+any, 
                CoreCfg.core_mysqlUser, CoreCfg.core_mysqlPasswd, CoreCfg.coreDB_sgdParam);
        connected = con.connect();
        
        //Crea un jsEngine
        jsEngine = JSEngine.getJSEngine(getClass(), CoreCfg.contextRoot);
        
        //Carrega totes les RULES que són susceptibles d'esser comprovades com a tasques pendents
        //ambits = new HashMap<Integer,String>();
        listCheckeables = new ArrayList<BeanCheckeables>();
        
        String SQL1 = "SELECT ta.id, ta.actuacio, ta.simbol, ta.threshold, ta.vrepeticio, ta.vmax, "
                + "ta.repetir, ta.equivalencies, ta.form, taf.ambits, taf.estudis, taf.minAge, taf.maxAge "
                + "FROM tuta_actuacions AS ta INNER JOIN tuta_actuacions_fields "
                + "AS taf ON ta.id=taf.idRule WHERE ta.simbol<>'' AND ta.threshold<>0 AND ta.alertActuacionsPendents='S'";
      
        try {
             Statement st = con.createStatement();
            ResultSet rs1 = con.getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                 BeanCheckeables bean = new BeanCheckeables();
                 bean.actuacio = rs1.getString("actuacio");
                 bean.ambits = rs1.getString("ambits");
                 bean.estudis = rs1.getString("estudis");
                 bean.id = rs1.getInt("id");
                 bean.maxAge = rs1.getInt("maxAge");
                 bean.minAge = rs1.getInt("minAge");
                 bean.repetir = rs1.getString("repetir").equalsIgnoreCase("S");
                 bean.simbol = rs1.getString("simbol");
                 bean.threshold = rs1.getInt("threshold");
                 bean.vmax = rs1.getInt("vmax");
                 bean.vrepeticio = rs1.getInt("vrepeticio");
                 bean.equivalences = rs1.getString("equivalencies");
                 String form2 = StringUtils.noNull(rs1.getString("form"));
                 bean.setExtensions(StringUtils.StringToHash(form2, ";"));
                 listCheckeables.add(bean);
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(TasquesPendents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void checkTasquesPendents()
    {
        abrevTutor = null;
        if(!running && connected)
        {
           //resteja els arrays
            oberts = new ArrayList<Integer>();
            jobs = new HashMap<Integer, TasquesPendentsStruct>();
            
            LongTask task = new LongTask(-1);
            new Thread(task).start();
            running = true;
        }
    }
    
    public void checkTasquesPendentsForeground()
    {
        this.abrevTutor = null;
        if(!running && connected)
        {
           //resteja els arrays
            oberts = new ArrayList<Integer>();
            jobs = new HashMap<Integer, TasquesPendentsStruct>();
            
            LongTask task = new LongTask(-1);
            task.doCheck();
            running = true;
        }
    }
    
    
    public void checkTasquesPendentsForeground(String abrevTutor) {
        this.abrevTutor = abrevTutor;
        if(!running && connected)
        {
           //resteja els arrays
            oberts = new ArrayList<Integer>();
            jobs = new HashMap<Integer, TasquesPendentsStruct>();
            
            LongTask task = new LongTask(-1);
            task.doCheck();
            running = true;
        }
    }
    
    //Comprova les tasques pendents d'un sol alumne
    public void checkTasquesPendents(int expd)
    {
        abrevTutor = null;
        //Remove alumne de la llista
        if(expd>0)
        {
            //Lleva nomes aquest alumne
            jobs.remove(expd);
            this.removeOberta(expd);
        }
        else
        {
            //hard reset
            jobs.clear();
            oberts.clear();
        }
        
        if(!running && connected)
        {
            LongTask task = new LongTask(expd);
            new Thread(task).start();
            running = true;
        }
    }

    public void removeTasca(int exp, int idtasca)
    {
        if(jobs.containsKey(exp))
        {
            int index = jobs.get(exp).idTasks.indexOf(idtasca);
            if(index>=0)
            {
                Integer removed = jobs.get(exp).idTasks.remove(index);
                String remove = jobs.get(exp).detallTasks.remove(index);
                //////System.outprintln("Removed:"+removed+";"+remove);
            }

            if(jobs.get(exp).idTasks.isEmpty())
            {
                jobs.remove(exp);
                //////System.outprintln("removed because is empty");

            }
        }

    }

    
    public void removeOberta(int exp)
    {
        if(oberts.contains(exp))
        {
            int index= oberts.indexOf(exp);
            if(index>=0) {
                oberts.remove(index);
            }
        }
    }

    public void addOberta(int exp) {
        if(!oberts.contains(exp))
        {
            oberts.add(exp);
        }
    }
    
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      propChangeSupport.addPropertyChangeListener(listener);
   }


    //comprova tasques pendents
    //Si expd=-1, comprova de tots els alumnes
    //si expd>=0, comprova de l'alumne en questio
    class LongTask implements Runnable
    {
        private int expedient;
        private String conditionExpd="";
        private String conditionExpdxh="";
                
        public LongTask(int expd)
        {
            expedient = expd;
            if(expedient>=0)
            {
                conditionExpd = " AND Exp2='"+expedient+"'  ";
                conditionExpdxh = " AND xh.Exp2='"+expedient+"'  ";
            }
        }
        
        
        @Override
        public void run()
        {
              running = false;
              doCheck();
              propChangeSupport.firePropertyChange("running", true, false);
           
        }
        
        public void doCheck()
        {
            Bindings bindings = jsEngine.getBindings();
            bindings.put("mysql", con);
            bindings.put("jobs", jobs);
            bindings.put("expedient", expedient);
            bindings.put("coreCfg", coreCfg);
            bindings.put("any", any);
            try {
                jsEngine.evalBindings(bindings);
            } catch (ScriptException ex) {
                Logger.getLogger(TasquesPendents.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //System.out.println("before"+jobs);
            for(BeanCheckeables bean: listCheckeables)
            {
            
                //Try to implement this in script if exists
                boolean success = false;
                if (jsEngine.getCompiledScript() != null) {
                    try {
                        success = (Boolean) jsEngine.invokeFunction("checkForSimbol",bean);
                        //System.out.println("Checking for simbol js: "+bean.getSimbol()+" returns "+success);
                    } catch (Exception ex) {
                        ErrorDisplay.showMsg(ex.toString());
                    }
                }
                
                if(success)
                {
                    continue;
                }
                //System.out.println("JS FAILED: using built-in");

                //Use build-in if js fails
                checkForSimbol(bean);
            }
            
            //System.out.println("after"+jobs);
        
              //Determina ara els alumnes amb actuacions sense tancar (obertes)
              //Aqui cal afegir la condicio sobre l'any actual, per si existeixen actuacions d'altres anys
              //String SQL1 = "SELECT DISTINCT exp2 FROM tuta_reg_actuacions WHERE data2 IS NULL ORDER BY exp2"; 
              
              //Determina les actuacions que estan obertes
              
              String SQL1 = "SELECT DISTINCT exp2 FROM tuta_reg_actuacions WHERE data2 IS NULL "
                      +conditionExpd+" ORDER BY exp2";
              
             
              try {
                    Statement st = con.createStatement();
                    ResultSet rs1 = con.getResultSet(SQL1,st);
                    while (rs1 != null && rs1.next()) {
                        int exp2 = rs1.getInt("exp2");
                        oberts.add(exp2);
                        
                    }
                     if(rs1!=null) {
                      rs1.close();
                      st.close();
                  }
                } catch (SQLException ex) {
                    Logger.getLogger(TasquesPendents.class.getName()).log(Level.SEVERE, null, ex);
                }
        
        }

        private void checkForSimbol(BeanCheckeables bean)
        {

           String condicioEnsenyament = getCondicioEnsenyament(bean);
           String condicioEstudis = getCondicioEstudis(bean); 
           String idrules = "";
           if(bean.equivalences==null || bean.equivalences.isEmpty())
           {
               idrules ="("+bean.id+")";
           }
           else
           {
               idrules = "("+bean.id+","+bean.equivalences+")";
           }
           
           String condicioTutor = "";
           if(abrevTutor!=null)
           {
               condicioTutor = " AND xes.Permisos LIKE '%"+abrevTutor+"%' ";
           }
           
           String SQL1 = " SELECT DISTINCT xh.Exp2, fc.falt, SUM(IF(treg.idActuacio IN "+idrules+",1,0)) AS total FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic as xh  "
                        + " ON xes.Exp2=xh.Exp2 INNER JOIN (SELECT Exp_FK_ID, IdCurs_FK_ID, ("+bean.simbol+"_1A+"+bean.simbol+"_2A+"+bean.simbol+"_3A) as falt FROM `"+CoreCfg.core_mysqlDBPrefix+"`.fitxa_alumne_curs) AS fc "
                        + "  ON fc.Exp_FK_ID = xh.Exp2  AND fc.IdCurs_FK_ID=xh.AnyAcademic LEFT JOIN tuta_reg_actuacions AS treg ON treg.exp2=xh.Exp2 "
                        + " WHERE fc.falt >= "+bean.threshold 
                        + " AND xh.AnyAcademic='"+any+"' "+conditionExpdxh
                        + " AND DATEDIFF( CURRENT_DATE(), ADDDATE(xes.DataNaixement, INTERVAL "+bean.maxAge+" YEAR)) <= 0 "
                        + " AND DATEDIFF( CURRENT_DATE(), ADDDATE(xes.DataNaixement, INTERVAL "+bean.minAge+" YEAR)) >= 0 "           
                        + condicioEnsenyament
                        + condicioEstudis
                        + condicioTutor
                        + " GROUP BY Exp2";

            try {
                //System.out.println("sql1 TPEND:"+SQL1);
                Statement st = con.createStatement();
                ResultSet rs1 = con.getResultSet(SQL1,st);  

                    while (rs1 != null && rs1.next()) {
                        int exp2 = rs1.getInt("Exp2");
                        int falt = rs1.getInt("falt");
                        int total = rs1.getInt("total");
                        
                        TasquesPendentsStruct tp = null;
                        if(jobs.containsKey(exp2))
                        {
                            tp=jobs.get(exp2);                            
                        }
                        else
                        {
                            tp = new TasquesPendentsStruct();
                        }
                      
                     
                        if(bean.repetir)
                        {
                            //int npend = (int) Math.floor((falt-bean.threshold*total)/(1.*bean.threshold));
                            if(bean.vmax!=0 && falt>bean.vmax)
                            {
                                falt = bean.vmax;
                            }
                            int npend = (int) Math.floor( (falt-bean.threshold)/(1.0*bean.getVrepeticio()) - (total-1) );
                           
                            for(int i=0; i<npend; i++)
                            {
                                tp.idTasks.add(bean.id);
                                tp.detallTasks.add(bean.actuacio+": cas nº"+ (total+ i + 1));  
                                jobs.put(exp2, tp);
                            }
                       
                        }                        
                        else if(!bean.repetir && total==0)
                        {
                             tp.idTasks.add(bean.id);
                             tp.detallTasks.add(bean.actuacio);
                             jobs.put(exp2, tp);
                        }
                        

                    }
                     if(rs1!=null) {
                         rs1.close();
                         st.close();
                     }
                } catch (SQLException ex) {
                    Logger.getLogger(TasquesPendents.class.getName()).log(Level.SEVERE, null, ex);
                }

        }
           
        private String getCondicioEnsenyament(BeanCheckeables bean) {
            String txt = "";
            if(!bean.ambits.contains("*"))
            {
                txt =" AND xh.ensenyament IN ('"+bean.ambits.replaceAll(",", "','") +"') ";
            }
            return txt;
        }

        private String getCondicioEstudis(BeanCheckeables bean) {
            String txt = "";
            if(!bean.estudis.contains("*"))
            {
                txt =" AND xh.estudis IN ('"+bean.estudis.replaceAll(",", "','") +"') ";
            }
            return txt;
        }
    
    }


    public boolean isRunning() {
        return running;
    }

    public void setRunnig(boolean isRunning) {
        this.running = isRunning;
    }


    //
//
//       
//       private void checkRetards(BeanCheckeables bean)
//       {
//        
//           
//           String idrules = "";
//           if(bean.equivalences==null || bean.equivalences.isEmpty())
//           {
//               idrules ="("+bean.id+")";
//           }
//           else
//           {
//               idrules = "("+bean.id+","+bean.equivalences+")";
//           }
//
//            String SQL1 = "  SELECT "
//                    + "  xh.Exp2, fc.retards,  "
//                    + "  SUM(IF(treg.idActuacio IN "+idrules+",1,0)) AS total "                    
//                    + "  FROM  `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh  "
//                    + "    INNER JOIN ( SELECT Exp_FK_ID, IdCurs_FK_ID, (Ret_1rTRI + Ret_2nTRI + Ret_3rTRI) AS retards FROM `"+CoreCfg.core_mysqlDBPrefix+"`.fitxa_alumne_curs) AS fc "
//                    + "  ON fc.Exp_FK_ID = xh.Exp2  AND fc.IdCurs_FK_ID=xh.AnyAcademic "
//                    + "  LEFT JOIN "
//                    + "  tuta_reg_actuacions AS treg "
//                    + "  ON treg.exp2=xh.Exp2 "
//                    + "  WHERE fc.retards>="+bean.threshold
//                    + getCondicioEstudis(bean)
//                    + getCondicioEnsenyament(bean)
//                    + " AND xh.AnyAcademic='" + any + "' "
//                    + conditionExpdxh
//                    + " GROUP BY xh.Exp2";
//         
//            //System.outprintln("TASK PEND RETARDS SQL:"+SQL1);
//              try {
//                     Statement st = con.createStatement();
//                    ResultSet rs1 = con.getResultSet(SQL1,st);
//
//                    while (rs1 != null && rs1.next()) {
//                        int exp2 = rs1.getInt("Exp2");
//                        int retards = rs1.getInt("retards");
//                        int total = rs1.getInt("total");
//                        
//                        //Determina si hi ha alguna acció de retards pendent
//                        
//                       TasquesPendentsStruct tp=null;
//                        if(jobs.containsKey(exp2))
//                        {
//                            tp=jobs.get(exp2);                            
//                        }
//                        else
//                        {
//                            tp = new TasquesPendentsStruct();
//                        }
//                      
//                     
//                        if(bean.repetir)
//                        {
//                            //int npend = (int) Math.floor((retards-bean.threshold*total)/(1.*bean.threshold));
//                            if(bean.vmax!=0 && retards>bean.vmax)
//                            {
//                                retards = bean.vmax;
//                            }
//                            int npend = (int) Math.floor( (retards-bean.threshold)/(1.0*bean.getVrepeticio()) - (total-1) );
//                            
//                            for(int i=0; i<npend; i++)
//                            {
//                                tp.idTasks.add(bean.id);
//                                tp.detallTasks.add(bean.actuacio+": cas nº"+ (total+ i + 1));  
//                                jobs.put(exp2, tp);
//                            }
//                       
//                        }                        
//                        else if(!bean.repetir && total==0 && retards>=bean.threshold)
//                        {
//                             tp.idTasks.add(bean.id);
//                             tp.detallTasks.add(bean.actuacio);
//                             jobs.put(exp2, tp);
//                        }
//                        
//                    }
//                     if(rs1!=null) {
//                      rs1.close();
//                      st.close();
//                  }
//                } catch (SQLException ex) {
//                    Logger.getLogger(TasquesPendents.class.getName()).log(Level.SEVERE, null, ex);
//                }
//         }
//             
//        
//                
//        private void checkAmon(BeanCheckeables bean)
//        {
//           String condicioEnsenyament = getCondicioEnsenyament(bean);
//           String condicioEstudis = getCondicioEstudis(bean); 
//           String idrules = "";
//           if(bean.equivalences==null || bean.equivalences.isEmpty())
//           {
//               idrules ="("+bean.id+")";
//           }
//           else
//           {
//               idrules = "("+bean.id+","+bean.equivalences+")";
//           }
//
//           String SQL1 = " SELECT DISTINCT xh.Exp2, fc.amon, SUM(IF(treg.idActuacio IN "+idrules+",1,0)) AS total FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic as xh  "
//                        + " ON xes.Exp2=xh.Exp2  INNER JOIN (SELECT Exp_FK_ID, IdCurs_FK_ID, (NumAL_1rTRI + NumAL_2nTRI + NumAL_3rTRI)/5+  NumAG_1rTRI + NumAG_2nTRI + NumAG_3rTRI  AS amon FROM `"+CoreCfg.core_mysqlDBPrefix+"`.fitxa_alumne_curs) AS fc "
//                        + "  ON fc.Exp_FK_ID = xh.Exp2  AND fc.IdCurs_FK_ID=xh.AnyAcademic LEFT JOIN tuta_reg_actuacions AS treg ON treg.exp2=xh.Exp2 "
//                        + " WHERE fc.amon >= "+bean.threshold 
//                        + " AND xh.AnyAcademic='"+any+"' "+conditionExpdxh
//                        + " AND DATEDIFF( CURRENT_DATE(), ADDDATE(xes.DataNaixement, INTERVAL "+bean.maxAge+" YEAR)) <= 0 "
//                        + " AND DATEDIFF( CURRENT_DATE(), ADDDATE(xes.DataNaixement, INTERVAL "+bean.minAge+" YEAR)) >= 0 "           
//                        + condicioEnsenyament
//                        + condicioEstudis
//                        + " GROUP BY EXP2";
//
//            try {
//                //System.outprintln("sql1 TPEND:"+SQL1);
//                Statement st = con.createStatement();
//                ResultSet rs1 = con.getResultSet(SQL1,st);
//
//                    while (rs1 != null && rs1.next()) {
//                        int exp2 = rs1.getInt("Exp2");
//                        int falt = Math.round( rs1.getFloat("amon") );
//                        int total = rs1.getInt("total");
//                        
//                        TasquesPendentsStruct tp=null;
//                        if(jobs.containsKey(exp2))
//                        {
//                            tp=jobs.get(exp2);                            
//                        }
//                        else
//                        {
//                            tp = new TasquesPendentsStruct();
//                        }
//                      
//                     
//                        if(bean.repetir)
//                        {
//                            //int npend = (int) Math.floor((falt-bean.threshold*total)/(1.*bean.threshold));
//                             if(bean.vmax!=0 && falt>bean.vmax)
//                            {
//                                falt = bean.vmax;
//                            }
//                            int npend = (int) Math.floor( (falt-bean.threshold)/(1.0*bean.getVrepeticio()) - (total-1) );
//                           
//                            for(int i=0; i<npend; i++)
//                            {
//                                tp.idTasks.add(bean.id);
//                                tp.detallTasks.add(bean.actuacio+": cas nº"+ (total+ i + 1));  
//                                jobs.put(exp2, tp);
//                            }
//                       
//                        }                        
//                        else if(!bean.repetir && total==0)
//                        {
//                             tp.idTasks.add(bean.id);
//                             tp.detallTasks.add(bean.actuacio);
//                             jobs.put(exp2, tp);
//                        }
//                        
//
//                    }
//                     if(rs1!=null) {
//                         rs1.close();
//                         st.close();
//                     }
//                } catch (SQLException ex) {
//                    Logger.getLogger(TasquesPendents.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//         
//              
//        }
//
//
//
//        private void checkFaltes(BeanCheckeables bean)
//        {
//
//           String condicioEnsenyament = getCondicioEnsenyament(bean);
//           String condicioEstudis = getCondicioEstudis(bean); 
//              String idrules = "";
//           if(bean.equivalences==null || bean.equivalences.isEmpty())
//           {
//               idrules ="("+bean.id+")";
//           }
//           else
//           {
//               idrules = "("+bean.id+","+bean.equivalences+")";
//           }
//
//
//           String SQL1 = " SELECT DISTINCT xh.Exp2, fc.falt, SUM(IF(treg.idActuacio IN "+idrules+",1,0)) AS total FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic as xh  "
//                        + " ON xes.Exp2=xh.Exp2 INNER JOIN (SELECT Exp_FK_ID, IdCurs_FK_ID, (F_1rTRI+F_2nTRI+F_3rTRI) as falt FROM `"+CoreCfg.core_mysqlDBPrefix+"`.fitxa_alumne_curs) AS fc "
//                        + "  ON fc.Exp_FK_ID = xh.Exp2  AND fc.IdCurs_FK_ID=xh.AnyAcademic LEFT JOIN tuta_reg_actuacions AS treg ON treg.exp2=xh.Exp2 "
//                        + " WHERE fc.falt >= "+bean.threshold 
//                        + " AND xh.AnyAcademic='"+any+"' "+conditionExpdxh
//                        + " AND DATEDIFF( CURRENT_DATE(), ADDDATE(xes.DataNaixement, INTERVAL "+bean.maxAge+" YEAR)) <= 0 "
//                        + " AND DATEDIFF( CURRENT_DATE(), ADDDATE(xes.DataNaixement, INTERVAL "+bean.minAge+" YEAR)) >= 0 "           
//                        + condicioEnsenyament
//                        + condicioEstudis
//                        + " GROUP BY Exp2";
//
//            try {
//                //System.outprintln("sql1 TPEND:"+SQL1);
//                Statement st = con.createStatement();
//                ResultSet rs1 = con.getResultSet(SQL1,st);  
//
//                    while (rs1 != null && rs1.next()) {
//                        int exp2 = rs1.getInt("Exp2");
//                        int falt = rs1.getInt("falt");
//                        int total = rs1.getInt("total");
//                        
//                        TasquesPendentsStruct tp=null;
//                        if(jobs.containsKey(exp2))
//                        {
//                            tp=jobs.get(exp2);                            
//                        }
//                        else
//                        {
//                            tp = new TasquesPendentsStruct();
//                        }
//                      
//                     
//                        if(bean.repetir)
//                        {
//                            //int npend = (int) Math.floor((falt-bean.threshold*total)/(1.*bean.threshold));
//                            if(bean.vmax!=0 && falt>bean.vmax)
//                            {
//                                falt = bean.vmax;
//                            }
//                            int npend = (int) Math.floor( (falt-bean.threshold)/(1.0*bean.getVrepeticio()) - (total-1) );
//                           
//                            for(int i=0; i<npend; i++)
//                            {
//                                tp.idTasks.add(bean.id);
//                                tp.detallTasks.add(bean.actuacio+": cas nº"+ (total+ i + 1));  
//                                jobs.put(exp2, tp);
//                            }
//                       
//                        }                        
//                        else if(!bean.repetir && total==0)
//                        {
//                             tp.idTasks.add(bean.id);
//                             tp.detallTasks.add(bean.actuacio);
//                             jobs.put(exp2, tp);
//                        }
//                        
//
//                    }
//                     if(rs1!=null) {
//                         rs1.close();
//                         st.close();
//                     }
//                } catch (SQLException ex) {
//                    Logger.getLogger(TasquesPendents.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//        }


}

