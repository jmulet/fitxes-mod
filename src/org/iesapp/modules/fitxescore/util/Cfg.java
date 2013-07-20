/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.modules.fitxescore.util;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.iesapp.clients.iesdigital.fitxes.BeanFitxaCurs;
import org.iesapp.framework.data.User;
import org.iesapp.framework.pluggable.grantsystem.GrantBean;
import org.iesapp.framework.pluggable.grantsystem.GrantModule;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class Cfg {

    public static final String FITXESINI = "config/fitxes.ini";
    public String dadesAlumnes="`"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne";
    //public Permisos usuariPermisos;
    public ArrayList<String> actions;
    public int pid=-1;
    public int timeout=0;
    public String cursActual;
    public String abrevPref;
    public boolean activaMissatgeria=false;
    public static ArrayList<PathSearch> searchPath;
    
    //Models per poder omplir els combo    
    public static ArrayList<String> modelComboProgrames;    
    //nomes pel curs seleccionat
    public static ArrayList<String> modelComboEnsenyament;
    public static ArrayList<String> modelComboEstudis;
    public static ArrayList<String> modelComboGrup;
    //idem que anterior, pero no filtra per anys
    public static ArrayList<String> modelComboEnsenyament_tots;
    public static ArrayList<String> modelComboEstudis_tots;
    public static ArrayList<String> modelComboGrup_tots;
    
    public int anyAcademicFitxes;
    public HashMap<String, String> abrev2prof;
    public static GrantModule defaultsGrant;
    protected final CoreCfg coreCfg;
   // private DbUtils dbUtils;
    
    public Cfg(CoreCfg coreCfg)
    {
        this.coreCfg = coreCfg;
        
        initializeArrays();
        cursActual = StringUtils.anyAcademic_primer();
        
        readIniFile();
        createComboModels(false);
        createComboModels(true);
        User userInfo = coreCfg.getUserInfo();
        if(userInfo!=null)
        {
             readPermisos(userInfo.getAbrev());
        }
        
        anyAcademicFitxes = coreCfg.anyAcademic;
        abrev2prof = coreCfg.getIesClient().getProfessoratData().getMapAbrev();

    }

    
    private void initializeArrays()
    {
        //usuariPermisos = new Permisos();
        actions = new ArrayList<String>();
        searchPath = new ArrayList<PathSearch>();
        
        //Combos que utilitzen les fitxes (es creen aqui un unic pic)
        modelComboProgrames = new ArrayList<String>();
        modelComboEnsenyament = new ArrayList<String>();
        modelComboEstudis = new ArrayList<String>();
        modelComboGrup = new ArrayList<String>();
        
        modelComboEnsenyament_tots = new ArrayList<String>();
        modelComboEstudis_tots = new ArrayList<String>();
        modelComboGrup_tots = new ArrayList<String>();

    }

    private void readIniFile() {

        File propfile = new File(CoreCfg.contextRoot+File.separator+Cfg.FITXESINI);
        if(!propfile.exists()) {
            saveIni();
        }


        Properties props = new Properties();
        //try retrieve data from file
        try {
              FileInputStream filestream = new FileInputStream(CoreCfg.contextRoot+File.separator+Cfg.FITXESINI);
              props.load(filestream);

              abrevPref = props.getProperty("abrevPref").trim().toUpperCase();
              String message = props.getProperty("timeout");
              if(message!=null) {
                timeout = (int) Double.parseDouble(message);
            }
              String msg = props.getProperty("activaMissatgeria");
              activaMissatgeria= ( (int) Double.parseDouble(msg) ) > 0;
              msg = props.getProperty("searchPath");
              ArrayList<String> aux = StringUtils.parseStringToArray(msg, ";", StringUtils.CASE_INSENSITIVE);
         
              for(int i=0; i<aux.size()/2.; i+=1)
              {
                  searchPath.add(new PathSearch(aux.get(2*i),aux.get(2*i+1).equals("1")));                  
              }
 
              filestream.close();
            }
            catch(Exception ex)
            {
                 Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    private void saveIni() {


           Properties props = new Properties();

            try {
              props.setProperty("timeout", ""+timeout);
              props.setProperty("abrevPref", ""+abrevPref);
              props.setProperty("activaMissatgeria", ""+(activaMissatgeria?1:0));
              FileOutputStream filestream = new FileOutputStream(CoreCfg.contextRoot+File.separator+Cfg.FITXESINI);
              props.store(filestream, null);
              filestream.close();

            } catch (IOException ex) {
                    Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
            }

    }

    public final void readPermisos(String abrev) {


          ResultSet rs = null;
          String SQL;
          

          if( coreCfg.getMysql().isClosed() ) {
            return;
        }  //do nothing if there is no connection

          String grup="NOTUTOR";
          if(abrev.equals("ADMIN"))
          {
              grup = abrev;  //EL role de ADMIN es ADMIN
          }
          else
          {
                SQL = "SELECT * FROM usu_usuari where Nom='"+abrev+"'";
                
                try {
                Statement st = coreCfg.getMysql().createStatement();
                rs = coreCfg.getMysql().getResultSet(SQL,st);
                while (rs.next()) {
                    grup = rs.getString("GrupFitxes");
                }
                rs.close();
                st.close();
         } catch (SQLException ex) {
               Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
         }

        }
        //Crea el sistema de permisos intern
        defaultsGrant = new GrantModule(null, coreCfg);
        defaultsGrant.register("dadesPersonals_edit", "Permet editar dades personals", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("dadesPersonals_view", "Permet veure dades personals", GrantBean.ALL, GrantBean.FULL_CONFIG);
        defaultsGrant.register("fitxaActual_edit", "Permet editar fitxa actual", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("fitxaActual_view", "Permet veure fitxa actual", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("fitxaAnterior_edit", "Permet editar fitxes passades", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("fitxaAnterior_view", "Permet veure fitxes passades", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("fitxesCtrl_crear", "Permet crear noves fitxes", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("fitxesCtrl_esborrar", "Permet esborrar fitxes", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("informePasswords_gen", "Permet generar informe passwords", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("informeResumFitxa_gen", "Permet generar resum fitxes", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("informeSGD_gen", "Permet generar informes SGD", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("informeAccions", "Permet generar informes d'actuacions", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("informeSancions", "Permet generar informes de sancions", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("imported_edit", "Permet editar dades importades en fitxa", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("accions_view", "Permet veure les actuacions", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("accions_fullEdit", "Permet l'edicio d'actuacions", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("accions_tancar", "Permet tancar les actuacions", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("accions_esborrar", "Permet esborrar les actuacions", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("cerca_mostraAvancada", "Mostra cerca avançada", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("cerca_permetAccions", "Permet la cerca per actuacions", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("medicaments_edit", "Permet editar els medicaments", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("medicaments_view", "Permet veure els medicaments", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("medicaments_give", "Permet subministrar medicaments", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("informeMedicaments", "Permet generar informe medicaments", GrantBean.NONE, GrantBean.BASIC_CONFIG);
        defaultsGrant.register("nese_edit", "Permet editar dades NESE", GrantBean.NONE, GrantBean.FULL_CONFIG);
        defaultsGrant.register("nese_view", "Permet veure dades NESE", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("entrevistaPares_edit", "Permet editar entrevista amb pares", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("entrevistaPares_view", "Permet veure entrevista amb pares", GrantBean.BELONGS, GrantBean.FULL_CONFIG);
        defaultsGrant.register("justificarFaltes", "Permet justificar faltes", GrantBean.NONE, GrantBean.FULL_CONFIG);
         
       //deprecated
//        SQL = "SELECT * FROM fitxa_permisos where grup='"+grup+"'";
//        
//         try {
//             Statement st = coreCfg.getMysql().createStatement();
//             rs = coreCfg.getMysql().getResultSet(SQL, st);
//                while (rs.next()) {
//                    usuariPermisos.dadesPersonals_edit = (short) rs.getInt("dadesPersonals_edit");
//                    usuariPermisos.dadesPersonals_view = (short) rs.getInt("dadesPersonals_view");
//                    usuariPermisos.dadesPrimaria_view = (short) rs.getInt("dadesPrimaria_view");
//                    usuariPermisos.fitxaActual_edit = (short) rs.getInt("fitxaActual_edit");
//                    usuariPermisos.fitxaActual_view = (short) rs.getInt("fitxaActual_view");
//                    usuariPermisos.fitxaAnterior_edit = (short) rs.getInt("fitxaAnterior_edit");
//                    usuariPermisos.fitxaAnterior_view = (short) rs.getInt("fitxaAnterior_view");
//                    usuariPermisos.fitxesCtrl_crear = (short) rs.getInt("fitxesCtrl_crear");
//                    usuariPermisos.fitxesCtrl_esborrar = (short) rs.getInt("fitxesCtrl_esborrar");
//                    usuariPermisos.informeFitxaTutoria_gen = (short) rs.getInt("informeFitxaTutoria_gen");
//                    usuariPermisos.informePasswords_gen = (short) rs.getInt("informePasswords_gen");
//                    usuariPermisos.informeResumFitxa_gen = (short) rs.getInt("informeResumFitxa_gen");
//                    usuariPermisos.informeSGD_gen = (short) rs.getInt("informeSGD_gen");
//                    usuariPermisos.informeAccions = (short) rs.getInt("informeAccions");
//                    usuariPermisos.informeSancions = (short) rs.getInt("informeSancions");
//                    usuariPermisos.imported_edit = (short) rs.getInt("imported_edit");
//                    usuariPermisos.accions_view = (short) rs.getInt("accions_view");
//                    usuariPermisos.accions_fullEdit = (short) rs.getInt("accions_fullEdit");
//                    usuariPermisos.accions_tancar = (short) rs.getInt("accions_tancar");
//                    usuariPermisos.accions_esborrar = (short) rs.getInt("accions_esborrar");
//                    usuariPermisos.cerca_mostraAvancada = (short) rs.getInt("cerca_mostraAvancada");
//                    usuariPermisos.cerca_permetAccions = (short) rs.getInt("cerca_permetAccions");
//                    usuariPermisos.medicaments_edit = (short) rs.getInt("medicaments_edit");
//                    usuariPermisos.medicaments_view = (short) rs.getInt("medicaments_view");
//                    usuariPermisos.medicaments_give = (short) rs.getInt("medicaments_give");
//                    usuariPermisos.informeMedicaments = (short) rs.getInt("informeMedicaments");
//                    usuariPermisos.nese_edit = (short) rs.getInt("nese_edit");
//                    usuariPermisos.nese_view = (short) rs.getInt("nese_view");
//                    usuariPermisos.entrevistaPares_edit = (short) rs.getInt("entrevistaPares_edit");
//                    usuariPermisos.entrevistaPares_view = (short) rs.getInt("entrevistaPares_view");
//                    usuariPermisos.justificarFaltes = (short) rs.getInt("justificarFaltes");
//                    
//                }
//                if(rs!=null)
//                {
//                    rs.close();
//                    st.close();
//                }
//         } catch (SQLException ex) {
//               MyLogger.log("Error llegint la taula FITXA_PERMISOS:"+ex, MyLogger.logERROR);
//         }

        // //System.out.println("Permisos::"+grup+"; "+ usuariPermisos.fitxaActual_view +   usuariPermisos.fitxaAnterior_view);

    }

 
    //Per tal que les fitxes del cursos tinguin la possibilitat
    //de seleccionar els possibles curs, grup i nivell
    //els llegim de la base de dades
    
    public final void createComboModels(boolean totsAnys) {
      
        modelComboProgrames.clear();
        //Crea els programes
        
        String SQL1 = "Select * from fitxa_programes where disponible=1";
        
       // System.out.println("aaa"+coreCfg.getMysql()+" "+rs1);
        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1,st);
            while (rs1 != null && rs1.next()) {
                String str = StringUtils.noNull(rs1.getString("curt"))+": "+ 
                        StringUtils.noNull(rs1.getString("llarg"));
                modelComboProgrames.add(str);
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Crea Models grups
        
        String filtraAnys = " ";
        if(totsAnys)
        {
            modelComboEstudis_tots.clear();
            modelComboEnsenyament_tots.clear();
            modelComboGrup_tots.clear();
            filtraAnys = " ";
        }
        else
        {
            modelComboEstudis.clear();
            modelComboEnsenyament.clear();
            modelComboGrup.clear();
            filtraAnys =" WHERE AnyAcademic='"+coreCfg.anyAcademic+"' ";
        }
        
        SQL1 = "Select distinct Ensenyament from `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic "+filtraAnys+" order by Ensenyament ";
         
        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1,st);
            while (rs1 != null && rs1.next()) {
                String nivel = rs1.getString(1);
                if(totsAnys)
                {
                    modelComboEnsenyament_tots.add(nivel);
                }
                else
                {
                    modelComboEnsenyament.add(nivel);
                }
            }
             if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
        }


        SQL1 = "Select distinct Estudis from `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic "+filtraAnys+"  order by Estudis ";
         try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1,st);
            while (rs1 != null && rs1.next()) {
                String nivel = rs1.getString("Estudis").toUpperCase();
                if(nivel.contains("ESO")) {
                    nivel = nivel.substring(0,2) + " ESO";
                }
                else if(nivel.toUpperCase().contains("BAT")) {
                    nivel = nivel.substring(0,2) + " BATX";
                }
                
                 if(totsAnys)
                {
                    modelComboEstudis_tots.add(nivel);
                }
                else
                {
                    modelComboEstudis.add(nivel);
                }
            }
             if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
        }

        SQL1 = "Select distinct Grup from `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic "+filtraAnys+"  order by Grup ";
        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs2 = coreCfg.getMysql().getResultSet(SQL1,st);

            while (rs2!= null && rs2.next()) {
                String nivel = rs2.getString(1);
                if(totsAnys)
                {
                    modelComboGrup_tots.add(nivel);
                }
                else
                {
                    modelComboGrup.add(nivel);
                }
            }
             if(rs2!=null) {
                rs2.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public CoreCfg getCoreCfg() {
        return coreCfg;
    }
    
     
    //Utilitat -  donar de baixa un alumne
    public void donaBaixa(int expedient) {

        Object[] options = {"No", "Sí"};
        String missatge = "Voleu donar de baixa aquest alumne/a del sistema IESDIGITAL?";

        int n = JOptionPane.showOptionDialog(javar.JRDialog.getActiveFrame(),
                missatge, "Confirmació",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n != 1) {
            return;
        }

        //Esborra l'alumne/a
        String SQL1 = "DELETE from `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne_historic WHERE Exp2='" + expedient + "' AND anyAcademic='" + anyAcademicFitxes + "'";
        int nup = getCoreCfg().getMysql().executeUpdate(SQL1);
        SQL1 = "DELETE from `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne WHERE Exp2='" + expedient + "'";
        nup = getCoreCfg().getMysql().executeUpdate(SQL1);
        SQL1 = "DELETE from `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne_detall WHERE Exp_FK_ID='" + expedient + "'";
        nup = getCoreCfg().getMysql().executeUpdate(SQL1);
        SQL1 = "DELETE from `" + CoreCfg.core_mysqlDBPrefix + "`.xes_dades_pares WHERE Exp2='" + expedient + "'";
        nup = getCoreCfg().getMysql().executeUpdate(SQL1);

        //Si te fitxa creada, se li indica que es baixa

        String thisany = anyAcademicFitxes + "-" + (anyAcademicFitxes + 1);
        if (BeanFitxaCurs.getAnys(expedient, getCoreCfg().getMysql()).contains(anyAcademicFitxes) )
        {
            BeanFitxaCurs fc = new BeanFitxaCurs(coreCfg.getIesClient());
            fc.getFromDB(expedient, thisany);
            String observ = fc.getObservacions();
            observ += "\nL'alumne/a s'ha donat de baixa en el programa de fitxes dia " + new DataCtrl().getDiaMesComplet();
            fc.setObservacions(observ);
            fc.commitToDB(expedient);
        }
        
        missatge = "Voleu donar de baixa aquest alumne/a del sistema SGD?\nDesmatricula l'alumne de totes les matèries.";

        n = JOptionPane.showOptionDialog(javar.JRDialog.getActiveFrame(),
                missatge, "Confirmació",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n != 1) {
            return;
        }
       SQL1 = "UPDATE asignaturasalumno AS aa INNER JOIN alumnos AS a ON aa.idAlumnos=a.id SET aa.opcion='0' WHERE a.expediente="+expedient;
       nup = getCoreCfg().getSgd().executeUpdate(SQL1);

        
    }
    
    
    
}
