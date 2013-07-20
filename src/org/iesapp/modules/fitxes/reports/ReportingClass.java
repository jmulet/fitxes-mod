/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.modules.fitxes.reports;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.clients.iesdigital.fitxes.BeanDadesPersonals;
import org.iesapp.clients.iesdigital.fitxes.BeanEmergencyInfo;
import org.iesapp.clients.iesdigital.fitxes.BeanFitxaCurs;
import org.iesapp.clients.iesdigital.fitxes.BeanLlistaContrassenyes;
import org.iesapp.clients.iesdigital.fitxes.BeanOrla;
import org.iesapp.clients.iesdigital.fitxes.BeanResumFitxes;
import org.iesapp.framework.dialogs.ReportFactory;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.modules.fitxes.DesktopManager;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class ReportingClass{
      private static final String REPORT_PATH1 = "alumnat/detallCurs";
      private static final String REPORT_PATH2 = "alumnat/resumCurs";
      private static final String REPORT_PATH3 = "alumnat/usuarisSGD";
      private static final String REPORT_PATH4 = "alumnat/orla";
      private static final String REPORT_PATH5 = "tutoria/statPares";
      private static final String REPORT_PATH6 = "tutoria/statSolicituds";
        
      private static final String SGD_PATH1 = "alumnat/listaIncidenciasAlumnos";
      private static final String SGD_PATH2 = "alumnat/resumIncidenciasAlumnos";
      private static final String SGD_PATH3 = "tutoria/reportAccions";
      private static final String SGD_PATH4 = "tutoria/reportAccionsPendents";
      private static final String SGD_PATH10 = "tutoria/entrevistaPares_orig";
      private static final String SGD_PATH11 = "tutoria/emergenciaInfo";
      private static final String SGD_PATH12 = "tutoria/entrevistaParesPLUS";
      private static final ReportFactory reportFactory = new ReportFactory();
      private final Cfg cfg;
 
////////////////////////////////////////////////////////////////////////////////
    //Fitxa completa d'un alumne
////////////////////////////////////////////////////////////////////////////////

    public ReportingClass(final Cfg cfg) {
           this.cfg = cfg;
           reportFactory.setReportGeneratedListener(new ActionListener(){
               @Override
            public void actionPerformed(ActionEvent e) {
                DesktopManager.addReport(reportFactory.getGeneratedReport(), reportFactory.getSuitableTitle());
            }
        });
   }

    

    public void reportTask(int nExp, String reportPath, HashMap map, String ensenyament)
    {    
         if(reportPath==null || reportPath.isEmpty())
         {
              //System.out.println("Rule no té mainReport associat");
              return;           
         }
         
         ArrayList<String> fake = new ArrayList<String>();
         fake.add("fake");
         reportFactory.customReport(fake, map, reportPath);
         reportFactory.generateReport();
    }

    
     
    public void sgdReport1(List bean, HashMap map)
    {
         reportFactory.customReport(bean, map, SGD_PATH1);
         reportFactory.generateReport();
    }
    
    public void sgdReport2(List bean, HashMap map)
    {
         reportFactory.customReport(bean, map, SGD_PATH2);
         reportFactory.generateReport();
    }
    
    public void sgdReport3(List bean, HashMap map)
    {
         reportFactory.customReport(bean, map, SGD_PATH3);
         reportFactory.generateReport();
    }
    
    public void sgdReport4(List bean, HashMap map)
    {
         reportFactory.customReport(bean, map, SGD_PATH4);
         reportFactory.generateReport();
    }
    
     public void customReport(List list, HashMap map, String filename) {
         reportFactory.customReport(list, map, filename);
         reportFactory.generateReport();
     }

    
     public void entrevistaPares(List bean, HashMap map, boolean newmodel)
    {          
         String report=SGD_PATH10;
         if(newmodel) {
            report=SGD_PATH12;
        }
         reportFactory.customReport(bean, map, report);
         reportFactory.generateReport();         
    }

    public void emergencyInfo(List<BeanEmergencyInfo> bean, HashMap map)
    {
        reportFactory.customReport(bean, map, SGD_PATH11);
        reportFactory.generateReport();     
    }

    public void exportFitxaCompleta (int nExp)
    {
            // Definimos cual sera nuestra fuente de datos
            HashMap map = new HashMap();
           
            BeanDadesPersonals personal = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
            personal.getFromDB(nExp, cfg.anyAcademicFitxes);
            map.put("photo", personal.getPhoto());
            map.put("llinatge1", personal.getLlinatge1());
            map.put("llinatge2", personal.getLlinatge2());
            map.put("nom1", personal.getNom1());
            map.put("expedient", personal.getExpedient());
            map.put("dni",personal.getDni());
            map.put("dataNaixement",personal.getDataNaixament());
            map.put("paisNaixement",personal.getPaisNaixament());
            map.put("provinciaNaixement",personal.getProvinciaNaixament());
            map.put("municipiNaixement",personal.getLocalitatNaixament());
            map.put("numRepeticions",personal.getNumRep());
            map.put("edat",personal.getEdat());
            map.put("nacionalitat",personal.getNacionalitat());
            map.put("sexe",personal.getSexe());
            map.put("adreca",personal.getAdreca());
            map.put("municipi",personal.getMunicipi());
            map.put("localitat",personal.getLocalitat());
            map.put("cp",personal.getCp());
            map.put("telefons",personal.getTelefonsUrgencia().toString());

            //Ara li passa una llista pel subreport
             map.put("SUBREPORT_DIR",CoreCfg.contextRoot+"\\reports\\");
   
        try {
            Class<?> classDataSource;
            classDataSource = Class.forName("net.sf.jasperreports.engine.data.JRBeanCollectionDataSource");
            Constructor<?> constructor = classDataSource.getConstructor(Collection.class);
            Object db2_ds = constructor.newInstance(personal.getTutorsInfo());
            map.put("db2", db2_ds);
        } catch (Exception ex) {
            Logger.getLogger(ReportingClass.class.getName()).log(Level.SEVERE, null, ex);
        }

         reportFactory.customReport(getList_FitxaCompleta(nExp), map, REPORT_PATH1);
         reportFactory.generateReport();     

    }

  
////////////////////////////////////////////////////////////////////////////////
//resum de les fitxes d'un curs
////////////////////////////////////////////////////////////////////////////////

    public void exportResumFitxes(List nExps)
    {
         reportFactory.customReport(getList_ResumFitxes(nExps), new HashMap(), REPORT_PATH2);
         reportFactory.generateReport();     
    }

////////////////////////////////////////////////////////////////////////////////
//llista de contrassenyes
////////////////////////////////////////////////////////////////////////////////

 public void exportUsuaris(List nExps, java.util.Date desde) 
  {

         reportFactory.customReport(getList_Usuaris(nExps, desde), new HashMap(), REPORT_PATH3);
         reportFactory.generateReport();
        
    }

    public void exportOrles(ArrayList expds, String curs) {
         HashMap map = new HashMap();
         map.put("cursAcademic", curs);
         reportFactory.customReport(getList_Orles(expds), map, REPORT_PATH4);
         reportFactory.generateReport();             
    }

    
    // Estadistiques     
    public void statisticsVisitaPares(List bean, HashMap map) {
         reportFactory.customReport(bean, map, REPORT_PATH5);
         reportFactory.generateReport();  
    }

    public void statisticsSolicituds(List bean, HashMap map) {
         reportFactory.customReport(bean, map, REPORT_PATH6);
         reportFactory.generateReport();  
    }

    
    private List getList_Orles(List nExps) {
        List aux2 = new ArrayList();

        String any = StringUtils.anyAcademic();
       
        String oldgrup ="";
        int myi = 0;
        int mida = nExps.size();
        while(myi<mida)
        {
             BeanOrla mybean = new BeanOrla();
            //1a columna
                boolean include = true;
                int expd = ((Number) nExps.get(myi)).intValue();
                BeanDadesPersonals pBean = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
                pBean.getFromDB(expd, cfg.anyAcademicFitxes);
                BeanFitxaCurs b1 = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
                b1.getFromDB(expd, any);
                String grup = pBean.getEstudis()+" "+pBean.getGrupLletra();
                
                oldgrup = grup;    
                mybean.setGrup(grup);
                mybean.setNom1(pBean.getLlinatge1()+" "+pBean.getLlinatge2()+", "+ pBean.getNom1());
                mybean.setExp1(expd+"");
                mybean.setPhoto1(pBean.getPhoto());
                mybean.setTutor(pBean.getProfTutor());
                myi += 1;

            //2a columna
                if(myi<mida)
                {
                expd = ((Number) nExps.get(myi)).intValue();
                pBean = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
                pBean.getFromDB(expd, cfg.anyAcademicFitxes);
                b1 = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
                b1.getFromDB(expd, any);
                grup = pBean.getEstudis()+" "+pBean.getGrupLletra();
                if(!grup.equals(oldgrup))
                {
                    include=false;
                }
                if(include)
                {
                    mybean.setGrup(grup);
                    mybean.setNom2(pBean.getLlinatge1()+" "+pBean.getLlinatge2()+", "+ pBean.getNom1());
                    mybean.setExp2(expd+"");
                    mybean.setPhoto2(pBean.getPhoto());
                    mybean.setTutor(pBean.getProfTutor());
                    myi += 1;
                }
                }

                //3a columna
                 if(myi<mida)
                {
                expd = ((Number) nExps.get(myi)).intValue();
                pBean = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
                pBean.getFromDB(expd, cfg.anyAcademicFitxes);
                b1 = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
                b1.getFromDB(expd, any);
                grup = pBean.getEstudis()+" "+pBean.getGrupLletra();
                if(!grup.equals(oldgrup))
                {
                    include=false;
                }
                if(include)
                {
                    mybean.setGrup(grup);
                    mybean.setNom3(pBean.getLlinatge1()+" "+pBean.getLlinatge2()+", "+ pBean.getNom1());
                    mybean.setExp3(expd+"");
                    mybean.setPhoto3(pBean.getPhoto());
                    mybean.setTutor(pBean.getProfTutor());
                    myi += 1;
                }
                }
                //4a columna
                if(myi<mida)
                {
                expd = ((Number) nExps.get(myi)).intValue();
                pBean = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
                pBean.getFromDB(expd, cfg.anyAcademicFitxes);
                b1 = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
                b1.getFromDB(expd, any);
                grup = pBean.getEstudis()+" "+pBean.getGrupLletra();
                if(!grup.equals(oldgrup))
                {
                    include=false;
                }
                if(include)
                {
                    mybean.setGrup(grup);
                    mybean.setNom4(pBean.getLlinatge1()+" "+pBean.getLlinatge2()+", "+ pBean.getNom1());
                    mybean.setExp4(expd+"");
                    mybean.setPhoto4(pBean.getPhoto());
                    mybean.setTutor(pBean.getProfTutor());
                    myi += 1;
                }
                }
                //5a columna
                 if(myi<mida)
                {
                expd = ((Number) nExps.get(myi)).intValue();
                pBean = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
                pBean.getFromDB(expd, cfg.anyAcademicFitxes);
                b1 = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
                b1.getFromDB(expd, any);
                grup = pBean.getEstudis()+" "+pBean.getGrupLletra();
                if(!grup.equals(oldgrup))
                {
                    include=false;
                }
                if(include)
                {
                    mybean.setGrup(grup);
                    mybean.setNom5(pBean.getLlinatge1()+" "+pBean.getLlinatge2()+", "+ pBean.getNom1());
                    mybean.setExp5(expd+"");
                    mybean.setPhoto5(pBean.getPhoto());
                    mybean.setTutor(pBean.getProfTutor());
                    myi += 1;
                }
                }
                //6a columna
                 if(myi<mida)
                {
                expd = ((Number) nExps.get(myi)).intValue();
                pBean = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
                pBean.getFromDB(expd, cfg.anyAcademicFitxes);
                b1 = new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
                b1.getFromDB(expd, any);
                grup = pBean.getEstudis()+" "+pBean.getGrupLletra();
                if(!grup.equals(oldgrup))
                {
                    include=false;
                }
                if(include)
                {
                    mybean.setGrup(grup);
                    mybean.setNom6(pBean.getLlinatge1()+" "+pBean.getLlinatge2()+", "+ pBean.getNom1());
                    mybean.setExp6(expd+"");
                    mybean.setPhoto6(pBean.getPhoto());
                    mybean.setTutor(pBean.getProfTutor());
                    myi += 1;
                }
                }
           
            aux2.add(mybean);
        }

        return aux2;
    }

    private List getList_ResumFitxes(List nExps)
    {
        List aux2 = new ArrayList();

        for(int i=0; i<nExps.size(); i++)
        {
            int expd = ((Number) nExps.get(i)).intValue();
            BeanDadesPersonals pBean = new BeanDadesPersonals(cfg.getCoreCfg().getIesClient());
            pBean.getFromDB(expd, cfg.anyAcademicFitxes);

            List<Integer> anys = BeanFitxaCurs.getAnys( expd , cfg.getCoreCfg().getMysql());

            for(int j=0; j<anys.size(); j++)
            {
                String cursacademic = anys.get(j)+"-"+(anys.get(j)+1);
                BeanFitxaCurs bean= new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
                bean.getFromDB(expd, cursacademic);

                BeanResumFitxes mybean = new BeanResumFitxes();

                //create mybean used by Jasper
                String nomcomplet = pBean.getLlinatge1() + " " + pBean.getLlinatge2() + ", " + pBean.getNom1();
                mybean.setNomcomplet(nomcomplet);
                mybean.setAny(bean.getAny_academic());
                String curscomplet = bean.getCurs()+ "-"+ bean.getGrup();
                mybean.setCurs(curscomplet);
                mybean.setNota(""+bean.getNotaMitjanaFinal());
                mybean.setNsuspeses(""+bean.getNumMateriesSuspesesJuny());
                String agtotal = "" + bean.getImportacioSGD().get("AG").getNTotal();;
                mybean.setAg(agtotal);
                String altotal = "" + bean.getImportacioSGD().get("AL").getNTotal();;
                mybean.setAl(altotal);

                //cal abreujar el nom del tutor (eliminam el segon llinatge)
                String tutor2 = bean.getProfessor();
                String nom = StringUtils.AfterLast(tutor2, ",").trim();
                String apellidos = StringUtils.BeforeLast(tutor2, ",").trim();
                String apellido1 = StringUtils.BeforeLast(apellidos, " ").trim();
                if(apellido1.length()==0) {
                    apellido1 = apellidos;
                }
                tutor2 = apellido1+", "+nom;

                mybean.setTutor(tutor2);
                String faltes  = "" + bean.getImportacioSGD().get("FA").getNTotal();
                String faltesJ = "" + bean.getImportacioSGD().get("FJ").getNTotal();

                mybean.setF(faltes);
                mybean.setFj(faltesJ);

                String nota = bean.getNotaMitjanaFinal()+"";
                mybean.setNota(nota);
                String nsus =""+bean.getNumMateriesSuspesesJuny();
                mybean.setNsuspeses(nsus);

                mybean.setCursactual(pBean.getEnsenyament()+", "+pBean.getEstudis()+", "+pBean.getGrupLletra());

                aux2.add(mybean);
            }

        }
        return aux2;
    }

    // desde permet generar llista de contrassenyes que s'hagin donat d'alta 
    // desde la data en questio
    private List getList_Usuaris(List nExps, java.util.Date desde)
    {
        List aux2 = new ArrayList();

        String extra_condition="";
        if(desde!=null)
        {
            extra_condition = "  AND dataAlta>='"+new DataCtrl(desde).getDataSQL()+"' ";
        }
                
        if(nExps!=null)
        {
        for(int i=0; i<nExps.size(); i++)
        {
                 int expd = ((Number) nExps.get(i)).intValue();

                 
                 String SQL1=" SELECT "+
                      " xh.Exp2, "+
                      " Llinatge1, "+
                      " Llinatge2, "+
                      " Nom1, "+
                      " CONCAT(Estudis, ' ', Grup) AS grupo, "+
                      " pwd  "+
                    " FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xal  "+
                     "  INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh  "+
                     "  ON xal.Exp2 = xh.Exp2  "+
                     "  LEFT JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alta AS alt  "+
                     "  ON alt.Exp2 = xh.Exp2  "+
                    "  WHERE xh.AnyAcademic = '"+cfg.anyAcademicFitxes+"'  "+
                    "  AND xh.Exp2='"+expd+"' "+
                    extra_condition;
                   

              try {
                Statement st = cfg.getCoreCfg().getMysql().createStatement();
                ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
              
                while(rs1!=null && rs1.next())
                {
                     BeanLlistaContrassenyes mybean = new BeanLlistaContrassenyes();
                     mybean.setLlinatge1(rs1.getString("Llinatge1"));
                     mybean.setLlinatge2(rs1.getString("Llinatge2"));
                     mybean.setNom1(rs1.getString("Nom1"));
                     mybean.setUsuari(rs1.getString("Exp2"));
                     mybean.setPwd(rs1.getString("pwd"));
                     mybean.setCurs(rs1.getString("grupo"));
                     aux2.add(mybean);
                }
                if(rs1!=null) {
                      rs1.close();
                      st.close();
                  }
              } catch (SQLException ex) {
                Logger.getLogger(ReportingClass.class.getName()).log(Level.SEVERE, null, ex);
              }
        }
        }
        else //Tots els usuaris si la llista és null
        {
              
            String SQL1 = " SELECT "
                    + " xh.Exp2, "
                    + " Llinatge1, "
                    + " Llinatge2, "
                    + " Nom1, "
                    + " CONCAT(Estudis, ' ', Grup) AS grupo, "
                    + " pwd  "
                    + " FROM `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne AS xal  "
                    + "  INNER JOIN `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne_historic AS xh  "
                    + "  ON xal.Exp2 = xh.Exp2  "
                    + "  LEFT JOIN `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alta AS alt  "
                    + "  ON alt.Exp2 = xh.Exp2  "
                    + "  WHERE xh.AnyAcademic = '" + cfg.anyAcademicFitxes + "'  "                    
                    + extra_condition
                    +   " ORDER BY CONCAT(xh.estudis,xh.grup), xal.llinatge1, xal.llinatge2, xal.nom1";
                 
            
            
               try {
                   
                Statement st = cfg.getCoreCfg().getMysql().createStatement();
                ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
              
                while(rs1!=null && rs1.next())
                {
                     BeanLlistaContrassenyes mybean = new BeanLlistaContrassenyes();
                     mybean.setLlinatge1(rs1.getString("Llinatge1"));
                     mybean.setLlinatge2(rs1.getString("Llinatge2"));
                     mybean.setNom1(rs1.getString("Nom1"));
                     mybean.setUsuari(rs1.getString("Exp2"));
                     mybean.setPwd(rs1.getString("pwd"));
                     mybean.setCurs(rs1.getString("grupo"));
                     aux2.add(mybean);
                }
                if(rs1!=null) {
                       rs1.close();
                       st.close();
                   }
              } catch (SQLException ex) {
                Logger.getLogger(ReportingClass.class.getName()).log(Level.SEVERE, null, ex);
              }
        }

       
        return aux2;
    }

    
    
    private List getList_FitxaCompleta(int nExp)
    {
        ArrayList<BeanFitxaCurs> aux2 = new ArrayList<BeanFitxaCurs>();

        ArrayList<Integer> anys = BeanFitxaCurs.getAnys(nExp, cfg.getCoreCfg().getMysql()); //Per a quins anys te fitxa

        for(int i=0; i<anys.size(); i++)
        {
            String cursacademic = anys.get(i)+"-"+(anys.get(i)+1);
            BeanFitxaCurs bean= new BeanFitxaCurs(cfg.getCoreCfg().getIesClient());
            bean.getFromDB(nExp, cursacademic);
            aux2.add(bean);
        }
        return aux2;
    }

}
