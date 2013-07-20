/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxescore.db;

 
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.clients.sgd7.base.SgdBase;
import org.iesapp.framework.data.User;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 * 
 * Comunicacio entre les incidencies de l'sgd i les amonestacions/castig de dimecres
 * del programa de fitxes.
 * En aquesta versio, un cas nomes se li pot associar una incidencia sgd
 * 
 */
public class IncidenciesSGD {
  
    private int idCas;
 
    private int exp2;
    private String doc;
    

    private int idAlumnos;
    private int idProfe;
    private int idAsig;
    private int codighora;
    
    private int idAmonGreu;
    private int idCastigDi;
    private String actuacio;
    private int m_idsgd_new;
    private Date data1;
    private final int idExpulsio;
    private final Cfg cfg;


     
    public IncidenciesSGD(int idCas, Cfg cfg)
    {
        this.cfg = cfg;
       //Determina si aquest cas té incidencies registrades
        this.idCas = idCas;

        String simbolDimecres = (String) CoreCfg.configTableMap.get("simbolCastigDimecres");
         if(simbolDimecres==null || simbolDimecres.isEmpty()) {
            simbolDimecres = "CD";
         }
         String simbolAG = (String) CoreCfg.configTableMap.get("simbolAmonGreu");
         if(simbolAG==null || simbolAG.isEmpty()) {
            simbolAG = "AG";
         }
         String simbolEX = (String) CoreCfg.configTableMap.get("simbolExpulsio");
         if(simbolEX==null || simbolEX.isEmpty()) {
            simbolEX = "EX";
         }
        idAmonGreu = getIdsIncidencies(simbolAG);
        idCastigDi = getIdsIncidencies(simbolDimecres);
        idExpulsio = getIdsIncidencies(simbolEX);
        
    }

    public void close()
    {
        //sgd.close();
    }
    
    private boolean setIdSgd(int idSgd)
    {
        ////System.out.println("setIdSgd....");
        
        int q = getIdSgd();
        String SQL1 = "";
        if(q<0)
        {
            SQL1 = "INSERT INTO tuta_incidenciessgd (idCas, idSgd) VALUES("+this.idCas+", "+idSgd+")";
        }
        else
        {
            SQL1 = "UPDATE tuta_incidenciessgd SET idSgd="+idSgd+" WHERE idCas="+this.idCas;
        }
        
          
        int nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
        ////System.out.println(nup +"::"+SQL1);
       
        return (nup>0);
    }
    
    public boolean createIncSgd()
    {
        ////System.out.println("createIncSgd");
        
        boolean success = false;
        //Comprova si ha quedat algun registre en la base de dades de fitxes
        //Aquest pas fa que es carregui el "exp2" i el "doc" del cas
        int idsgd_old = getIdSgd();
        if(idsgd_old>=0) { 
            deleteIncSgd();
        } 
        
        //Necessitam saber quin es el seu tutor i la idAsig per poder crear
        //una nova incidencia en la base sgd
        cercaTutor(exp2);
        ////System.out.println("dades:: "+exp2+", "+idAlumnos+", "+idProfe+", "+idAsig);
        if(idAlumnos>=0 && idProfe>=0 && idAsig >=0)
        {
            //Analitza el document per saber quin tipus d'incidencia cal aplicar
            int useidInc =  -1;
            java.util.Date useDate = null;
            if(doc.toUpperCase().contains("CASTIG_DIMECRES={X") ||
               doc.toUpperCase().contains("SANCIODIMECRES={X"))
            {
                useidInc = idCastigDi;
                
                HashMap<String,String> map = StringUtils.StringToHash(doc, ";");
                
                String txt = map.get("quinDimecres");
                txt = txt == null? "":txt;
                if(!txt.isEmpty())
                {
                    
                     SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
                     if(txt.contains("-")) {
                        formatter = new SimpleDateFormat("dd-MM-yyyy");   
                    }   
                      
                       ///ara ha de convertir-lo a data
                       //sinó utilitza la data de creacio               
                        try {
                             
                            useDate = (java.util.Date) formatter.parse(txt);                       
                        } catch (ParseException ex) {
                            useDate = data1;
                            Logger.getLogger(IncidenciesSGD.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
                else
                {
                    useDate = data1;
                }
            }
            else if(doc.toUpperCase().contains("1AMON_GREU={X"))
            {
                useidInc = idAmonGreu;
                useDate = data1;      //per una amonestacio greu, utilitza la data de creacio de l'actuacio
            }
            else
            {
                return false;
            }
                
            //Cal crear la incidencia
        org.iesapp.clients.sgd7.incidencias.Incidencias inc = new org.iesapp.clients.sgd7.incidencias.Incidencias();
        inc.setIdAlumnos(idAlumnos);
        inc.setIdProfesores(idProfe+"");
        inc.setIdTipoIncidencias(useidInc);
        inc.setIdHorasCentro(codighora);
        inc.setIdGrupAsig(idAsig);
        inc.setIdTipoObservaciones(0);
        inc.setDia(new java.sql.Date(useDate.getTime()));
        inc.setHora(StringUtils.formatTime(SgdBase.getSgd().getServerTime()));
        inc.setComentarios("<Fitxes> "+actuacio);
        int idsgd_new = inc.save();
//        
//             String SQL1 = "INSERT INTO faltasalumnos (idAlumnos, idProfesores, "
//                     + " idTipoIncidencias, idHorasCentro, idGrupAsig, "
//                     + " idTipoObservaciones, dia, hora, comentarios) "
//                     + " VALUES("+idAlumnos+", "+idProfe+", "+useidInc+", "+codighora+", "+
//                     idAsig+", 0, '"+new DataCtrl(useDate).getDataSQL()+"', CURRENT_TIME(), '<Fitxes> "+actuacio+"')";
//             int idsgd_new = cfg.getCoreCfg().getSgd().executeUpdateID(SQL1);
//             
//             ////System.out.println(idsgd_new +"::"+SQL1);
             
             if(idsgd_new>=0) {
                success = setIdSgd(idsgd_new);
            }
             
             m_idsgd_new = idsgd_new;
             
        }
        else {
            
            //System.out.println("no s'han trobat les ids dels profes, materies, etc");
            //System.out.println("idAlumnos="+idAlumnos+" && idProfe="+idProfe+" && idAsig ="+idAsig);
        }
            
         return success;
    }
    
    
      public String getMessage() {
          String message = "";
          if(doc.toUpperCase().contains("CASTIG_DIMECRES={X") ||
               doc.toUpperCase().contains("SANCIODIMECRES={X"))
            {
                message = "S'ha introduit un càstig de dimecres al sistema SGD.\nid="+m_idsgd_new;
            }
            else if(doc.toUpperCase().contains("1AMON_GREU={X"))
            {
                message = "S'ha introduit una amonestació greu al sistema SGD.\nid="+m_idsgd_new;
            }
       
          return message;
       }
    
    
    
    public boolean deleteIncSgd()
    {
        int idsgd = getIdSgd();
        
        //System.out.println("La id de l'sgd associada es "+idsgd);
        //Primer esborra la incidencia en la base de dades de sgd, si es possible
        int ndel1 = 0;
        if(idsgd>=0)
        {
//             String SQL1 = "DELETE FROM faltasalumnos WHERE id="+idsgd;
//             ndel1 = cfg.getCoreCfg().getSgd().executeUpdate(SQL1);
               ndel1 =  new org.iesapp.clients.sgd7.incidencias.Incidencias(idsgd).delete();
        }
        //Ara esborra el registre de la base de fitxes
         String SQL2= "DELETE FROM tuta_incidenciessgd WHERE idCas="+this.idCas;
         int ndel2 = cfg.getCoreCfg().getMysql().executeUpdate(SQL2);
         return ( (ndel1>0 && idsgd>0) & ndel2>0);
    }
    
    
    // Retorna:
    // -1 = Error; el cas existeix pero no hi ha definit cap idSGD
    // -2 = Error; no existeix cap cas obert
    //   >=0 l'Id de l'incidència sgd existent
    
    private int getIdSgd() {
        
        String SQL1 = "SELECT tra.id, tra.data1, ta.actuacio, tra.exp2, tra.data1, tra.document, CASE WHEN"
                + " ISNULL(tisgd.idSgd) THEN -1 ELSE tisgd.idSgd END AS idSgd FROM"
                + " tuta_reg_actuacions AS tra LEFT JOIN tuta_incidenciessgd AS tisgd "
                + " ON tra.id=tisgd.idCas "
                + " LEFT JOIN tuta_actuacions AS ta ON tra.idActuacio = ta.id "
                + " WHERE tra.id="+this.idCas;
        
        //System.out.println("SQL1:::"+SQL1);
        
         int result = -2;
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                
                result = rs1.getInt("idSgd"); 
                //System.out.println("El resultat es "+result);
                exp2 = rs1.getInt("exp2");
                doc = rs1.getString("document");
                actuacio = rs1.getString("actuacio");
                data1 = rs1.getDate("data1");
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(IncidenciesSGD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        return result;
    }

    private void cercaTutor(int expd) {
        String SQL1 = "SELECT DISTINCT aa.idAlumnos, alumn.expediente, alumn.nombre, aa.idGrupAsig, "+
               " g.grupo, a.asignatura, p.codigo AS profesor,p.nombre AS NombreProfe  "+
                 " , CONCAT(CONCAT(hc.inicio, '-'), hc.fin) AS hora, hc.id as codighora, h.idDias AS dia,  "+
                 " hc.inicio, au.codigo AS aula  "+
                 " FROM Asignaturas a  "+
                 " INNER JOIN ClasesDetalle cd ON 1=1  "+
                 " INNER JOIN HorasCentro hc ON 1=1  "+
                 " INNER JOIN Horarios h ON 1=1  "+
                 " INNER JOIN GrupAsig ga ON 1=1  "+
                 " INNER JOIN Grupos g ON 1=1  "+
                 " INNER JOIN AsignaturasAlumno aa ON 1=1  "+
                 " LEFT OUTER JOIN alumnos alumn ON alumn.id=aa.idAlumnos  "+
                 " LEFT OUTER JOIN Aulas au ON h.idAulas=au.id  "+
                 " LEFT OUTER JOIN Profesores p ON h.idProfesores=p.id  "+
               "  WHERE a.asignatura LIKE 'Tut%'  "+
                 " AND  aa.idGrupAsig=ga.id  "+
                 " AND  (aa.opcion<>'0' AND (cd.opcion='X' OR cd.opcion=aa.opcion))  "+
                 " AND  h.idClases=cd.idClases  "+
                 " AND cd.idGrupAsig=ga.id  "+
                 " AND  h.idHorasCentro=hc.id  "+
                 " AND ga.idGrupos=g.id  "+
                 " AND  ga.idAsignaturas=a.id "+
                 " AND expediente="+expd;
        
         //System.out.println(SQL1);
        
         idAlumnos = -1;
         idProfe = -1;
         idAsig = - 1;
         
          try {
            Statement st = cfg.getCoreCfg().getSgd().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1,st);
            if(rs1!=null && rs1.next())
            {
                idAlumnos = rs1.getInt("idAlumnos");
                idProfe = rs1.getInt("profesor");
                idAsig = rs1.getInt("idGrupAsig");
                codighora = rs1.getInt("codighora");
            }
            if(rs1!=null) {
                  rs1.close();
                  st.close();
              }
        } catch (SQLException ex) {
            Logger.getLogger(IncidenciesSGD.class.getName()).log(Level.SEVERE, null, ex);
        }
          
       //Intenta fer-ho com a convivència, si no pot trobar el professor tutor
       if(idAlumnos==-1)
       {
            SQL1 = "SELECT * from alumnos where expediente='"+expd+"'";
             try {
            Statement st = cfg.getCoreCfg().getSgd().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1,st);
            if(rs1!=null && rs1.next())
            {
                idAlumnos = rs1.getInt("id");
            }
            if(rs1!=null) {
                     rs1.close();
                     st.close();
                 }
            } catch (SQLException ex) {
                Logger.getLogger(IncidenciesSGD.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //L'usuari pot no tenir associada una IDsgd, per tant l'agafa de Cfg.abrevPref
            String usuari = cfg.getCoreCfg().getUserInfo().getAbrev();
            if( (cfg.getCoreCfg().getUserInfo().getGrant()==User.ADMIN || cfg.getCoreCfg().getUserInfo().getGrant()==User.PREF) &&
                 cfg.getCoreCfg().getUserInfo().getIdSGD()<=0)
            {
                usuari = cfg.abrevPref;
            }
            
            SQL1 = "SELECT * from sig_professorat where abrev='"+usuari+"'";
            try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            if(rs1!=null && rs1.next())
            {
                idProfe= rs1.getInt("idSGD");
            }
            if(rs1!=null) {
                    rs1.close();
                    st.close();
            }
            } catch (SQLException ex) {
                Logger.getLogger(IncidenciesSGD.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //TODO: idProfe ha d'existir
            
            codighora =0;
            idAsig = 0;
       }
          
    }

    private int getIdsIncidencies(String simbol) {
        String SQL1 = "Select id from tipoincidencias where simbolo='"+simbol+"' order by descripcion";
         
        int idinc = -1;
        
        try {
            Statement st = cfg.getCoreCfg().getSgd().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1,st);
            if(rs1!=null && rs1.next())
            {
                idinc = rs1.getInt("id");
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(IncidenciesSGD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return idinc;
    }

  
    
}
