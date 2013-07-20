/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxescore.db;

 
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * En aquesta versio, permet multiples incidencies
 * 
 */
public class IncidenciesSGD1 {
  
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
    private final int idAmonLleu;
    private final int idAmonLleuHist;
    private final Cfg cfg;


     
    public IncidenciesSGD1(int idCas, int exp2, Cfg cfg)
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
         String simbolAL = (String) CoreCfg.configTableMap.get("simbolAmonLleu");
         if(simbolAL==null || simbolAL.isEmpty()) {
            simbolAL = "AL";
         }
         String simbolALH = (String) CoreCfg.configTableMap.get("simbolAmonLleuHist");
         if(simbolALH==null || simbolALH.isEmpty()) {
            simbolALH = "ALH";
         }
         String simbolEX = (String) CoreCfg.configTableMap.get("simbolExpulsio");
         if(simbolEX==null || simbolEX.isEmpty()) {
            simbolEX = "EX";
         }
        idAmonGreu = getIdsIncidencies(simbolAG);
        idCastigDi = getIdsIncidencies(simbolDimecres);
        idExpulsio = getIdsIncidencies(simbolEX);
        idAmonLleu = getIdsIncidencies(simbolAL);
        idAmonLleuHist = getIdsIncidencies(simbolALH);
        
        this.exp2 = exp2;
    }

    public void close()
    {
         
    }
    
    public void clearIdSgd()
    {
        String SQL1 = "DELETE FROM tuta_incidenciessgd WHERE idCas="+this.idCas;          
        int nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
        ////System.out.println(nup +"::"+SQL1);
    }

    public void clearAll()
    {
        String SQL1 = "SELECT idSGD FROM tuta_incidenciessgd WHERE idCas="+this.idCas;          
        try {
             Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                deleteIncSgd(rs1.getInt("idSgd"));
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
        }
        clearIdSgd();
    }
    
    public int deleteIncSgd(int idsgd)
    {
        return new org.iesapp.clients.sgd7.incidencias.Incidencias(idsgd).delete();
    }
    
    private boolean addIdSgd(int idSgd)
    {
        
        String SQL1 = "INSERT INTO tuta_incidenciessgd (idCas, idSgd) VALUES("+this.idCas+", "+idSgd+")";
          
        int nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
        ////System.out.println(nup +"::"+SQL1);
       
        return (nup>0);
    }
    
    //Retornara la id de la incidencia creada
    public int createIncSgd(java.util.Date dia, String codigo, String motiu)
    {
        ////System.out.println("createIncSgd");
        
        int idsgd_new = -1;

        //Necessitam saber quin es el seu tutor i la idAsig per poder crear
        //una nova incidencia en la base sgd
        cercaTutor(exp2);
        int useidInc = getIdsIncidencies(codigo);
        
        //Cal crear la incidencia
        org.iesapp.clients.sgd7.incidencias.Incidencias inc = new org.iesapp.clients.sgd7.incidencias.Incidencias();
        inc.setIdAlumnos(idAlumnos);
        inc.setIdProfesores(idProfe+"");
        inc.setIdTipoIncidencias(useidInc);
        inc.setIdHorasCentro(codighora);
        inc.setIdGrupAsig(idAsig);
        inc.setIdTipoObservaciones(0);
        inc.setDia(new java.sql.Date(dia.getTime()));
        inc.setHora(StringUtils.formatTime(SgdBase.getSgd().getServerTime()));
        inc.setComentarios(motiu);
        idsgd_new = inc.save();
        
        ////System.out.println(idsgd_new +"::"+SQL1);

        if (idsgd_new >= 0) {
            addIdSgd(idsgd_new);
        }

        m_idsgd_new = idsgd_new;
        return idsgd_new;
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
            Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
            }
            
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
            Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return idinc;
    }

  
    
}
