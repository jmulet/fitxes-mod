/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.clients.iesdigital.actuacions.FactoryRules;
import org.iesapp.clients.iesdigital.fitxes.TasquesPendents;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.modules.fitxescore.util.Cfg;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class Finder {
    private String nom1;
    private String llinatge2;
    private String llinatge1;
    private String expd;
    private boolean anee;
    private boolean repetidor;
    private String grup;
    private String estudis;
    private String ensenyament;
    protected int whichSelect = 0;
    private String and = "";
    protected String orderQuery = "";
    private String convivenciaIniciades;
    private boolean amagaFinalitzades;
    private String assistenciaIniciades;
    private ArrayList<String> listExtraConditions;
    private final String conditionsCarta;
    private final String conditionsSMS;
    private boolean smsPendent;
    private boolean smsEnviat;
    private boolean cartaPendent;
    private boolean cartaEnviat;
    private final Cfg cfg;

    public Finder(Cfg cfg)
    {
        this.cfg = cfg;
        listExtraConditions = new ArrayList<String>();
        conditionsCarta = cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getConditionEnviament(FactoryRules.CARTA);
        conditionsSMS = cfg.getCoreCfg().getIesClient().getFitxesClient().getFactoryRules().getConditionEnviament(FactoryRules.SMS);
    }
    
    public void clearAll()
    {
        listExtraConditions.clear();
        nom1 = null;
        llinatge2 = null;
        llinatge1 = null;
        expd = null;
        anee = false;
        repetidor = false;
        grup = null;
        estudis = null;
        ensenyament = null;
        whichSelect = 0;
        and = "";
        orderQuery = "";
        convivenciaIniciades = null;
        amagaFinalitzades = false;
        assistenciaIniciades = null;
        smsPendent = false;
        smsEnviat = false;
        cartaPendent = false;
        cartaEnviat = false;
    }
    
    public String createQuery()
    {
        StringBuilder query = createBasicType(whichSelect);
        StringBuilder conditions = getWhereNivell();
            conditions.append(getWhereNom());
            conditions.append(getWhereEspecial());
            if(whichSelect==1)
            {
                conditions.append(getWhereIniciades());
            }
        
       //Aplica condicions extra
        for(String s: listExtraConditions)
        {
           conditions.append(and).append(" ").append(s).append(" ");
           and = "AND";
        }
            
            
        if(conditions.length()>0)
        {
            query.append(" WHERE ").append(conditions);
        }
        query.append(" GROUP BY xh.Exp2 ");
        query.append(orderQuery);
        //System.out.println(query.toString());
        return query.toString();
    }
    
    public FinderSet getResultSet()
    {
        FinderSet set = new FinderSet();
        Statement st=null;
        ResultSet rs=null;
        try {
            st = cfg.getCoreCfg().getMysql().createStatement();
            set.setStament(st);
            rs = cfg.getCoreCfg().getMysql().getResultSet(this.createQuery(),st);
            set.setResultSet(rs);
        } catch (SQLException ex) {
            Logger.getLogger(Finder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return set;
    }
    
    
    void setEnsenyament(String string) {
        this.ensenyament = string;
    }

    void setEstudis(String string) {
        this.estudis = string;
    }

    void setGrup(String string) {
        this.grup = string;
    }

    void setRepetidor(boolean selected) {
        this.repetidor = selected;
    }

    void setAnee(boolean selected) {
        this.anee = selected;
    }

    void setExpd(String string) {
        this.expd = string;
    }

    void setLlinatge1(String string) {
        this.llinatge1 = string;
    }

    void setLlinatge2(String string) {
        this.llinatge2 = string;
    }

    void setNom1(String string) {
        this.nom1 = string;
    }

    private StringBuilder createBasicType(int whichSelect) {
        StringBuilder builder = new StringBuilder();
        if(whichSelect==0)
        {
            //10-10-2012 xes.Permisos -> xh.Permisos
             builder.append("SELECT DISTINCT xes.Permisos, xes.Llinatge1, xes.Llinatge2, xes.Nom1, xes.Exp2, xes.sexe, xes.anee, xd.Foto, ");
             builder.append(" xh.Ensenyament, xh.Estudis, xh.Grup,  IF(MAX(tenv.dia)>=CURRENT_DATE(), MAX(tenv.dia), NULL) AS entrevistes  FROM `");
             builder.append(CoreCfg.core_mysqlDBPrefix).append("`.xes_alumne_historic AS xh INNER JOIN `").append(CoreCfg.core_mysqlDBPrefix).append("`.xes_alumne AS xes ON (xes.Exp2=xh.Exp2 AND xh.AnyAcademic='").append(cfg.anyAcademicFitxes).append("') ");
             builder.append(" LEFT JOIN `").append(CoreCfg.core_mysqlDBPrefix).append("`.xes_alumne_detall AS xd on xd.Exp_FK_ID = xes.Exp2 ");
             builder.append(" LEFT JOIN tuta_entrevistes AS tenv ON tenv.exp2=xh.Exp2 ");

        }
        else
        {
            builder.append("SELECT DISTINCT xes.Permisos, xes.Llinatge1, xes.Llinatge2, xes.Nom1, xes.Exp2, xes.sexe, xes.anee, xd.Foto, ");
            builder.append(" xh.Ensenyament, xh.Estudis, xh.Grup,  IF(MAX(tenv.dia)>=CURRENT_DATE(), MAX(tenv.dia), NULL) AS entrevistes FROM `");
            builder.append(CoreCfg.core_mysqlDBPrefix).append("`.xes_alumne_historic as xh INNER JOIN `").append(CoreCfg.core_mysqlDBPrefix).append("`.xes_alumne AS xes ON (xh.Exp2=xes.Exp2 AND xh.AnyAcademic='").append(cfg.anyAcademicFitxes).append("') ");
            builder.append(" LEFT JOIN `").append(CoreCfg.core_mysqlDBPrefix).append("`.xes_alumne_detall AS xd on xd.Exp_FK_ID = xes.Exp2 ");
            builder.append(" INNER JOIN tuta_reg_actuacions AS tuta ON xes.Exp2 = tuta.exp2 ");
            builder.append(" LEFT JOIN tuta_entrevistes AS tenv ON tenv.exp2=xh.Exp2 ");

        }
        return builder;   
}
    
    private String newSimpleCondition(String field, String value, boolean relaxed)
    {
        String query = "";
        if(value!=null)
        {
            if(relaxed)
            {
                query = and+" "+field+" LIKE '%"+value+"%' ";
            }
            else
            {
                query = and+" "+field+"='"+value+"' ";
            }
            and = "AND";
        }
            
        return query;
    }
    
    private StringBuilder getWhereNivell()
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append(newSimpleCondition("Ensenyament",ensenyament,false));
        builder.append(newSimpleCondition("Estudis",estudis,false));
        builder.append(newSimpleCondition("Grup",grup,false));
        
//        //Add braces
//        if(builder.length()>0)
//        {
//            builder.insert(0, " (");
//            builder.append(") ");
//        }
        return builder;
    }
    
    private StringBuilder getWhereNom()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(newSimpleCondition("xes.Exp2",expd,false));
        builder.append(newSimpleCondition("Llinatge1",llinatge1,true));
        builder.append(newSimpleCondition("Llinatge2",llinatge2,true));
        builder.append(newSimpleCondition("Nom1",nom1,true));
        
//        //Add braces
//        if(builder.length()>0)
//        {
//            builder.insert(0, " (");
//            builder.append(") ");
//        }
        return builder;
    }


    private StringBuilder getWhereIniciades()
    {
        StringBuilder builder = new StringBuilder();
        if(whichSelect!=1) {
            return builder;
        }
        
        String modifier = ") ";
        if(amagaFinalitzades)
        {
            modifier = " AND data2 IS NULL) ";
        }
        
        if(assistenciaIniciades!=null && convivenciaIniciades==null)
        {
            builder.append(and).append(" (idActuacio='").append(assistenciaIniciades).append("' ").append(modifier);
            and = "AND";
        }
        else if(assistenciaIniciades==null && convivenciaIniciades!=null)
        {
            builder.append(and).append(" (idActuacio='").append(convivenciaIniciades).append("' ").append(modifier);
            and = "AND";
        }
        else if(assistenciaIniciades!=null && convivenciaIniciades!=null)
        {
            builder.append(and).append(" (idActuacio='").append(convivenciaIniciades).append("' OR idActuacio='").append(assistenciaIniciades).append("' ").append(modifier);
            and = "AND";
        }
        
        if(!conditionsSMS.isEmpty())
        {
            if(smsPendent)
            {
                builder.append(and).append("((").append(conditionsSMS).append(") AND data2 IS NULL)");
            }
            else if(smsEnviat)
            {
                 builder.append(and).append("((").append(conditionsSMS).append(") AND data2 IS NOT NULL)");
            }
        }
        
        if(!conditionsCarta.isEmpty())
        {
            if(cartaPendent)
            {
                 builder.append(and).append("((").append(conditionsCarta).append(") AND data2 IS NULL)");
            }
            else if(cartaEnviat)
            {
                 builder.append(and).append("((").append(conditionsCarta).append(") AND data2 IS NOT NULL)");
            }
        }
//        //Add braces
//        if(builder.length()>0)
//        {
//            builder.insert(0, " (");
//            builder.append(") ");
//        }
        return builder;
    }

    private StringBuilder getWhereEspecial()
    {
        StringBuilder builder = new StringBuilder();
        
        if(repetidor) //Filra nomes els alumnes repetidors en el curs actual
        {
            builder.append(and).append(" ( ( estudis IN (SELECT estudis FROM `").append(CoreCfg.core_mysqlDBPrefix)
                    .append("`.xes_alumne_historic AS xh2 WHERE xh.Exp2=xh2.Exp2 AND xh2.AnyAcademic<")
                    .append(cfg.anyAcademicFitxes).append(" AND xh2.AnyAcademic>0) ) OR xes.Repetidor>0 ) ");
            and = "AND";
        }
        
        if(anee) //Filtra nomes els alumnes Anees
        {
            builder.append(and).append(" ( xes.anee<>'' AND xes.anee IS NOT NULL ) ") ;
            and = "AND";
        }
       
        return builder;
    }
    /**
     * @return the whichSelect
     */
    public int getWhichSelect() {
        return whichSelect;
    }

    /**
     * @param whichSelect the whichSelect to set
     */
    public void setWhichSelect(int whichSelect) {
        this.whichSelect = whichSelect;
    }

    /**
     * @return the orderQuery
     */
    public String getOrderQuery() {
        return orderQuery;
    }

    /**
     * @param orderQuery the orderQuery to set
     */
    public void setOrderQuery(String orderQuery) {
        this.orderQuery = orderQuery;
    }

    void setAssistenciaIniciades(Object object) {
         String tmp = (String) object;
        if(!tmp.isEmpty())
        {
            tmp = StringUtils.AfterLast(tmp, "[");
            tmp = StringUtils.BeforeFirst(tmp, "]").trim();
        }
        else
        {
            tmp = null;
        }
        assistenciaIniciades = tmp;
    }

    void setConvivenciaIniciades(Object object) {
        String tmp = (String) object;
        if(!tmp.isEmpty())
        {
            tmp = StringUtils.AfterLast(tmp, "[");
            tmp = StringUtils.BeforeFirst(tmp, "]").trim();
        }
        else
        {
            tmp = null;
        }
        convivenciaIniciades = tmp;
    }

    void setAmagaFinalitzades(boolean selected) {
       amagaFinalitzades = selected;
    }

    void appendExtraConditions(String extracondition) {
        listExtraConditions.add(extracondition);
    }

    void setEnviamentSMS(boolean selected, boolean selected0) {
        smsPendent = selected;
        smsEnviat = selected0;
    }

    void setEnviamentCarta(boolean selected, boolean selected0) {
        cartaPendent = selected;
        cartaEnviat = selected0;
     }
            
    
    public FinderSet getResultSetPendents(final String actuacio)
    {
            FinderSet set = new FinderSet();
            String tmp = StringUtils.AfterLast(actuacio,"[");
            tmp = StringUtils.BeforeFirst(tmp, "]");
            int idRule = Integer.parseInt(tmp);
            
            if(idRule<=0) {
                return null;
            }
            ArrayList<Integer> list = Finder.getListPendents(idRule, cfg);
            String expds;
            if(list.isEmpty())
            {
               expds  = "0";
            }
            else
            {
               expds  = list.toString().replaceAll("\\[", "").replace("]","");
            }
            StringBuilder query = createBasicType(0);
            query.append( " WHERE xh.exp2 IN (").append(expds).append(") ");
            query.append(" GROUP BY xh.Exp2 ");
            query.append(orderQuery);
            //System.out.println(query.toString());
       try{
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs = cfg.getCoreCfg().getMysql().getResultSet( query.toString() , st);
            set.setStament(st);
            set.setResultSet(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(Finder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return set;
    }
    
    public static ArrayList<Integer> getListPendents(int idRule, Cfg cfg)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        //To ensure thread safety, we must create a new tasques pendents process
        //and run it in foreground
        TasquesPendents tp = new TasquesPendents(cfg.getCoreCfg().getIesClient());
        tp.checkTasquesPendentsForeground();
        if(tp==null) {
            return list;
        }
        
        for(int exp: tp.jobs.keySet())
        {
             if(tp.jobs.get(exp).idTasks.contains(idRule))
             {
                 list.add(exp);
             }
        }
        return list;
    }
}
