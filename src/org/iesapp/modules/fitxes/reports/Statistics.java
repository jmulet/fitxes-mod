/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.reports;

 
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.iesapp.framework.dialogs.Wait;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.modules.fitxescore.util.Cfg;

/**
 *
 * @author Josep
 */
public class Statistics {
    private final String path;
    private WritableWorkbook workbook;
    private WritableSheet sheet;
 
    private Wait dlg;
    private final Cfg cfg;
    
    public Statistics(final Cfg cfg)
    {
       this.cfg = cfg;
       path = System.getProperty("user.home")+"/estadistiques"+cfg.anyAcademicFitxes+".xls"; 
    }

    
    public void doTask()
    {
        dlg = new Wait();
        dlg.setLocationRelativeTo(null);
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
        LongTask longTask = new LongTask();
        longTask.start();
    }
      /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    
    
    class LongTask extends Thread
    {
        @Override
        public void run() {
            try {

                WorkbookSettings ws = new WorkbookSettings();
                ws.setLocale(new Locale("ca", "ES"));
                ws.setEncoding("ISO-8859-1");

                Workbook template = Workbook.getWorkbook(new File(CoreCfg.contextRoot+File.separator+"reports/estadistiques/template-stat1.xls"), ws);


                workbook = Workbook.createWorkbook(new File(path), template);

                sheet = workbook.getSheet("AL-AG");
                doALAG();
                
                sheet = workbook.getSheet("AMONESTA");
                doAmonestacions();
                

                sheet = workbook.getSheet("EXPEDIENTS");
                doExpedients();
                
             
                sheet = workbook.getSheet("USUARIS");
                doUsuaris();
                
                sheet = workbook.getSheet("ENTREVISTES");
                doEntrevistes();
                
                workbook.write();
                workbook.close();

            } catch (WriteException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BiffException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }

            
            dlg.dispose();
            
               try {
                 Desktop.getDesktop().open(new java.io.File(path));
                } catch (IOException ex) {
                //
                }
        }
        
  
    
    // Genera una estadistica en el nombre d'amonestacions lleus i greus
    //
    private void doALAG() throws WriteException {
        
        final int INIROW = 4;
        
        String SQL1 = "SELECT "
                + " MONTH(dia) AS mes, "
                + " COUNT(fal.id) AS suma "
                + " FROM faltasalumnos AS fal "
                + " LEFT JOIN tipoincidencias AS tip "
                + "   ON tip.id = fal.idTipoIncidencias "
                + " LEFT JOIN tipoobservaciones AS tob "
                + "  ON tob.id = fal.idTipoObservaciones "
                + " WHERE tip.simbolo IN('AG') AND tob.descripcion NOT LIKE '%acumulacio%' "
                + " GROUP BY MONTH(dia) "
                + " ORDER BY dia";

        HashMap<Integer, Integer> ag = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> al = new HashMap<Integer, Integer>();

        ResultSet rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1);
        try {
            while (rs1 != null && rs1.next()) {
                ag.put(rs1.getInt("mes"), rs1.getInt("suma"));
            }
            if (rs1 != null) {
                rs1.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }

        SQL1 = "SELECT "
                + " MONTH(dia) AS mes, "
                + " COUNT(fal.id) AS suma "
                + " FROM faltasalumnos AS fal "
                + " LEFT JOIN tipoincidencias AS tip "
                + "   ON tip.id = fal.idTipoIncidencias "
                + " LEFT JOIN tipoobservaciones AS tob "
                + "  ON tob.id = fal.idTipoObservaciones "
                + " WHERE tip.simbolo IN('AL','ALH') "
                + " GROUP BY MONTH(dia) "
                + " ORDER BY dia";

        rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1);
        try {
            while (rs1 != null && rs1.next()) {
                al.put(rs1.getInt("mes"), rs1.getInt("suma"));
            }
            if (rs1 != null) {
                rs1.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Modified
        for (int k = INIROW; k < INIROW+10; k++)
        {
            Cell cell = sheet.getCell(0, k);
            NumberCell ncell = null;
            if(cell.getType()==CellType.NUMBER) {
                    ncell = (NumberCell) cell;
                }
          
            if (ncell != null) {
                int id = (int) ncell.getValue();

                if (al.containsKey(id)) {
                    CellFormat cellFormat = sheet.getWritableCell(2, k).getCellFormat();
                    WritableCell number = new jxl.write.Number(2, k, al.get(id));                   
                    number.setCellFormat(cellFormat);
                    sheet.addCell(number); //columna-1, fila-1
                    
                }
                if (ag.containsKey(id)) {
                    CellFormat cellFormat = sheet.getWritableCell(3, k).getCellFormat();
                    WritableCell number = new jxl.write.Number(3, k, ag.get(id));                   
                    number.setCellFormat(cellFormat);
                    sheet.addCell(number); //columna-1, fila-1
                }
            }
        }
        
        amonestacionsPerNivell(4,1,"ESO");
        amonestacionsPerNivell(6,2,"ESO");
        amonestacionsPerNivell(8,3,"ESO");
        amonestacionsPerNivell(10,4,"ESO");
        amonestacionsPerNivell(12,1,"BAT");
        amonestacionsPerNivell(14,2,"BAT");
        
    }

   //Query per a la utilització del programa de fitxes
    private void doUsuaris() throws WriteException {
          final int INIROW = 4;
           
          doUsuaris(1, "DISTINCT", "Fitxes");
          doUsuaris(2, "DISTINCT", "Reserves");
          doUsuaris(3, "", "Fitxes");
          doUsuaris(4, "", "Reserves");
         
          HashMap<String, Integer> compDept = new HashMap<String, Integer>();
          HashMap<String, Float> fitxesDept = new HashMap<String, Float>();
          HashMap<String, Float> reservesDept = new HashMap<String, Float>();
          
        //Nombre de departaments i num. d'integrants
        String SQL1 = "SELECT COUNT(id) AS suma, depart FROM sig_professorat GROUP BY depart ORDER BY depart";
        ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                    compDept.put(rs1.getString("depart"), rs1.getInt("suma"));
                    fitxesDept.put(rs1.getString("depart"), 0f);
                    reservesDept.put(rs1.getString("depart"), 0f);
                }
                if(rs1!=null) {
                    rs1.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        
        //Utilització per departament
        SQL1 = "SELECT "
                + " COUNT(slog.id) AS suma, "
                + " prof.depart "
                + " FROM "
                + " sig_log AS slog  "
                + " INNER JOIN "
                + "  sig_professorat AS prof  "
                + " ON prof.abrev = slog.usua  "
                + " WHERE usua <> 'ADMIN'  "
                + " AND usua <> 'PREF'  "
                + " AND usua <> 'PROGRAMAT' "
                + "  AND usua <> 'GUARD'  "
                + " AND tasca = 'Fitxes'  "
                + " GROUP BY prof.depart  "
                + " ORDER BY inici  ";
           rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                    String depart = rs1.getString("depart");
                    int nmembres = compDept.get(depart);
                    fitxesDept.put(rs1.getString("depart"), rs1.getInt("suma")/(1f*nmembres));
                }
                if(rs1!=null) {
                    rs1.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
              
            SQL1 = "SELECT "
                + " COUNT(slog.id) AS suma, "
                + " prof.depart "
                + " FROM "
                + " sig_log AS slog  "
                + " INNER JOIN "
                + "  sig_professorat AS prof  "
                + " ON prof.abrev = slog.usua  "
                + " WHERE usua <> 'ADMIN'  "
                + " AND usua <> 'PREF'  "
                + " AND usua <> 'PROGRAMAT' "
                + "  AND usua <> 'GUARD'  "
                + " AND tasca = 'Reserves'  "
                + " GROUP BY prof.depart  "
                + " ORDER BY inici  ";
           rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                    String depart = rs1.getString("depart");
                    int nmembres = compDept.get(depart);
                    reservesDept.put(depart, rs1.getInt("suma")/(1f*nmembres));
                }
                if(rs1!=null) {
                    rs1.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
        
             
            int row=4;
            
            TreeSet<String> keys = new TreeSet<String>(fitxesDept.keySet());

            for(String ky: keys)
            {
                    sheet.addCell(new jxl.write.Label(6, row, ky)); //columna
                    sheet.addCell(new jxl.write.Number(7, row, fitxesDept.get(ky))); //columna
                    sheet.addCell(new jxl.write.Number(8, row, reservesDept.get(ky))); //columna
                    row += 1;
            }
            
    }


    private void amonestacionsPerNivell(int column, int curs, String estudis) throws WriteException
    {
        final int INIROW = 4;
       
        String SQL1 = " SELECT "
                + " MONTH(dia) AS mes, "
                + " COUNT(fal.id) AS suma "
                + " FROM "
                + " faltasalumnos AS fal  "
                + "   LEFT JOIN "
                + "   tipoincidencias AS tip  "
                + "   ON tip.id = fal.idTipoIncidencias  "
                + "   LEFT JOIN "
                + "   tipoobservaciones AS tob  "
                + "   ON tob.id = fal.idTipoObservaciones  "
                + "   LEFT JOIN gruposalumno AS gal "
                + "  ON gal.idAlumnos = fal.idAlumnos "
                + "   LEFT JOIN grupos  "
                + "  ON grupos.grupoGestion = gal.grupoGestion "
                + " WHERE tip.simbolo IN ('AG')  "
                + "   AND tob.descripcion NOT LIKE '%acumulacio%'  "
                + " AND grupos.descripcion LIKE '" + curs + "%" + estudis + "%' "
                + " GROUP BY MONTH(dia) "
                + " ORDER BY dia  ";
        
        HashMap<Integer, Integer> ag = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> al = new HashMap<Integer, Integer>();

        ResultSet rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1);
        try {
            while (rs1 != null && rs1.next()) {
                ag.put(rs1.getInt("mes"), rs1.getInt("suma"));
            }
            if (rs1 != null) {
                rs1.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }

       SQL1 = "SELECT "
                + " MONTH(dia) AS mes, "
                + " COUNT(fal.id) AS suma "
                + " FROM faltasalumnos AS fal "
                + " LEFT JOIN tipoincidencias AS tip "
                + "   ON tip.id = fal.idTipoIncidencias "
                + " LEFT JOIN tipoobservaciones AS tob "
                + "  ON tob.id = fal.idTipoObservaciones "
                + "   LEFT JOIN gruposalumno AS gal "
                + "  ON gal.idAlumnos = fal.idAlumnos "
                + "   LEFT JOIN grupos  "
                + "  ON grupos.grupoGestion = gal.grupoGestion "
                + " WHERE tip.simbolo IN('AL','ALH') "
                + " AND grupos.descripcion LIKE '" + curs + "%" + estudis + "%' "
                + " GROUP BY MONTH(dia) "
                + " ORDER BY dia";

        rs1 = cfg.getCoreCfg().getSgd().getResultSet(SQL1);
        try {
            while (rs1 != null && rs1.next()) {
                al.put(rs1.getInt("mes"), rs1.getInt("suma"));
            }
            if (rs1 != null) {
                rs1.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Modified
        for (int k = INIROW; k < INIROW+10; k++)
        {
            Cell cell = sheet.getCell(0, k);
            NumberCell ncell = null;
            if(cell.getType()==CellType.NUMBER) {
                    ncell = (NumberCell) cell;
                }
            
          
            if (ncell != null) {
                int id = (int) ncell.getValue();

                if (al.containsKey(id)) {
                    CellFormat cellFormat = sheet.getWritableCell(column, k).getCellFormat();
                    WritableCell number = new jxl.write.Number(column, k, al.get(id));                   
                    number.setCellFormat(cellFormat);
                    sheet.addCell(number); //columna-1, fila-1                    
                }
                if (ag.containsKey(id)) {
                    CellFormat cellFormat = sheet.getWritableCell(column+1, k).getCellFormat();
                    WritableCell number = new jxl.write.Number(column+1, k, ag.get(id));                   
                    number.setCellFormat(cellFormat);
                    sheet.addCell(number); //columna-1, fila-1
                }
            }
        }
 
    }

        private void doUsuaris(int column, String distinct, String program) throws WriteException {
          
          final int INIROW = 3;
            
          HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
                    
          String SQL1 = "SELECT WEEK(inici) AS setmana, COUNT( "+distinct+" (usua)) AS suma "
                  + " FROM sig_log WHERE usua<>'ADMIN' AND usua<>'PREF' AND  "
                  + " usua<>'PROGRAMAT' AND usua<>'GUARD' AND tasca='"+program+"' "
                  + " GROUP BY WEEK(inici) ORDER BY inici";
          
          ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                    map.put(rs1.getInt("setmana"), rs1.getInt("suma"));
                }
                if(rs1!=null) {
                    rs1.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
          
          for(int k=INIROW; k<72; k++)
          {
            Cell cell = sheet.getCell(0, k);
            NumberCell ncell = null;
            if(cell.getType()==CellType.NUMBER || cell.getType()==CellType.NUMBER_FORMULA) {
                    ncell = (NumberCell) cell;
                }
            
            if (ncell != null) 
            {
               int id = (int) ncell.getValue();
 
               if(map.containsKey(id)) {
                        sheet.addCell(  new jxl.write.Number(column, k, map.get(id)) );
                    } //columna-1, fila-1
               
            }
          }
        }
   
    
        private HashMap<Integer, Integer> getNumActuacionsPerTrimestre(String idsActuacions)
        {
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
            map.put(1, 0);
            map.put(2, 0);
            map.put(3, 0);
       
            String SQL1 = " SELECT IF(data1<(SELECT fechaFin FROM evaluacionesdetalle WHERE valorExportable LIKE '1%'),1, "
                    + "IF(data1<(SELECT fechaFin FROM evaluacionesdetalle WHERE valorExportable LIKE '2%'),2,3)) AS trimestre, "
                    + "COUNT(idActuacio) AS suma "
                    + "FROM tuta_reg_actuacions WHERE idActuacio IN ("+idsActuacions+") GROUP BY trimestre ORDER BY trimestre ";
        
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                       map.put(rs1.getInt("trimestre"), rs1.getInt("suma"));
                }
                if(rs1!=null) {
                    rs1.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
            return map;
        }
        
        private HashMap<Integer, Integer> getNumActuacionsPerMes(String idsActuacions)
        {
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
            
            String SQL1 = "SELECT MONTH(data1) AS mes, "
                    + "COUNT(idActuacio) AS suma "
                    + "FROM tuta_reg_actuacions WHERE idActuacio IN ("+idsActuacions+") GROUP BY MONTH(data1)";
        
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                       map.put(rs1.getInt("mes"), rs1.getInt("suma"));
                }
                if(rs1!=null) {
                    rs1.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
            return map;
        }
        
         private HashMap<Integer, Integer> getNumActuacionsPerMesGrup(String idsActuacions, int curs, String estudis)
         {
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
            
            String SQL1 = "SELECT MONTH(data1) AS mes, "
                    + " COUNT(idActuacio) AS suma "
                    + " FROM tuta_reg_actuacions AS treg INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh ON "
                    + " xh.Exp2=treg.exp2 AND xh.AnyAcademic="+cfg.anyAcademicFitxes 
                    + " WHERE idActuacio IN ("+idsActuacions+")   "
                    + " AND xh.Estudis LIKE '%"+curs+"%"+estudis+"%' GROUP BY MONTH(data1)";
           ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                       map.put(rs1.getInt("mes"), rs1.getInt("suma"));
                }
                if(rs1!=null) {
                    rs1.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
            return map;
        }

        private void doExpedients() throws WriteException {
            final int INIROW = 4;
            
            HashMap<Integer, Integer> perAcumulacio = getNumActuacionsPerTrimestre("3,4,5,6,7");
            HashMap<Integer, Integer> directe = getNumActuacionsPerTrimestre("8");
            HashMap<Integer, Integer> faltagreu = getNumActuacionsPerTrimestre("9");
            
            TreeSet<Integer> keys = new TreeSet<Integer>(perAcumulacio.keySet());
            
            int k=INIROW;
            for(int trimestre: keys)
            {
                sheet.addCell(  new jxl.write.Number(1, k, perAcumulacio.get(trimestre)) ); //columna-1, fila-1
                sheet.addCell(  new jxl.write.Number(2, k, directe.get(trimestre)) ); //columna-1, fila-1
                sheet.addCell(  new jxl.write.Number(3, k, faltagreu.get(trimestre)) ); //columna-1, fila-1
                k += 1;
            }
            
        }

        private void doAmonestacions() throws WriteException {
           //Globals
           HashMap<Integer, Integer> map = getNumActuacionsPerMes("1");
           writeColumnWithKeys(0,4,map,2);
           map = getNumActuacionsPerMes("2");
           writeColumnWithKeys(0,4,map,3);
           map = getNumActuacionsPerMes("3");
           writeColumnWithKeys(0,4,map,4);
           
           //1r ESO
           for(int i=1; i<4; i++)
           {
                map = getNumActuacionsPerMesGrup(""+i, 1,"ESO");
                writeColumnWithKeys(0,4,map,4+i);
           }
            //2n ESO
           for(int i=1; i<4; i++)
           {
                map = getNumActuacionsPerMesGrup(""+i, 2,"ESO");
                writeColumnWithKeys(0,4,map,7+i);
           }
            //3r ESO
           for(int i=1; i<4; i++)
           {
                map = getNumActuacionsPerMesGrup(""+i, 3,"ESO");
                writeColumnWithKeys(0,4,map,10+i);
           }
            //4t ESO
           for(int i=1; i<4; i++)
           {
                map = getNumActuacionsPerMesGrup(""+i, 4,"ESO");
                writeColumnWithKeys(0,4,map,13+i);
           }
           //1r BATX
           for(int i=1; i<4; i++)
           {
                map = getNumActuacionsPerMesGrup(""+i, 1,"BAT");
                writeColumnWithKeys(0,4,map,16+i);
           }
           //2n BATX
           for(int i=1; i<4; i++)
           {
                map = getNumActuacionsPerMesGrup(""+i, 2,"BAT");
                writeColumnWithKeys(0,4,map,19+i);
           }
              
        }

        private void writeColumnWithKeys(int colKey, int rowKey, HashMap<Integer, Integer> map, int colData) throws WriteException {
            TreeSet<Integer> keys = new TreeSet<Integer>(map.keySet());
            
            
            for(int k=rowKey; k<15; k++)
            {
                Cell cell = sheet.getCell(0, k);
                NumberCell ncell = null;
                if (cell.getType() == CellType.NUMBER || cell.getType() == CellType.NUMBER_FORMULA) {
                    ncell = (NumberCell) cell;
                }

                if (ncell != null) {
                    int id = (int) ncell.getValue();

                    if (map.containsKey(id)) {
                        CellFormat cellFormat = sheet.getWritableCell(colData, k).getCellFormat();
                        WritableCell number = new jxl.write.Number(colData, k, map.get(id));                   
                        number.setCellFormat(cellFormat);
                        sheet.addCell(number); //columna-1, fila-1
                    }
                }

            }
        }
    }

    
    private void doEntrevistes() throws WriteException
    {
        HashMap<Integer, Integer> mapESO = new HashMap<Integer,Integer>();
        HashMap<Integer, Integer> mapBAT = new HashMap<Integer,Integer>();
        HashMap<Integer, Integer> enTermini = new HashMap<Integer,Integer>();
        HashMap<Integer, Integer> foraTermini = new HashMap<Integer,Integer>();
        HashMap<Integer, Integer> noContesta = new HashMap<Integer,Integer>();
        
//col 1 i 2: enviats eso i batx
        String SQL1= "SELECT WEEK(dia) AS setmana, COUNT(te.id) AS total FROM tuta_entrevistes AS te "+
        " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh ON xh.Exp2=te.exp2 AND xh.AnyAcademic='"+cfg.anyAcademicFitxes+"' "
                + " WHERE xh.Ensenyament LIKE '%ESO%' GROUP BY "+
        " xh.Ensenyament, WEEK(dia) ORDER BY dia";
        ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                       mapESO.put(rs1.getInt("setmana"), rs1.getInt("total"));
                }
                if(rs1!=null) {
                rs1.close();
            }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
    
        SQL1= "SELECT WEEK(dia) AS setmana, COUNT(te.id) AS total, xh.Ensenyament FROM tuta_entrevistes AS te "+
        " INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh ON xh.Exp2=te.exp2 AND xh.AnyAcademic='"+cfg.anyAcademicFitxes+"' "
                + " WHERE xh.Ensenyament LIKE '%BAT%' GROUP BY "+
        " xh.Ensenyament, WEEK(dia) ORDER BY dia";
        rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                       mapBAT.put(rs1.getInt("setmana"), rs1.getInt("total"));
                }
                if(rs1!=null) {
                rs1.close();
            }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
            
//Col 3: contesta en termini
  SQL1= "SELECT WEEK(dia) AS setmana, COUNT(sm.id) AS total FROM tuta_entrevistes AS te INNER JOIN sig_missatgeria AS sm ON "
          + " sm.idEntrevista=te.id INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh ON xh.Exp2=te.exp2 AND xh.AnyAcademic='"+cfg.anyAcademicFitxes
          +"' WHERE sm.dataContestat IS NOT NULL AND sm.dataContestat<=te.dia GROUP BY WEEK(dia) ORDER BY dia";
 
        rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                       enTermini.put(rs1.getInt("setmana"), rs1.getInt("total"));
                }
                if(rs1!=null) {
                rs1.close();
            }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
            
//Col 4: fora termini
SQL1= "SELECT WEEK(dia) AS setmana, COUNT(sm.id) AS total FROM tuta_entrevistes AS te INNER JOIN sig_missatgeria AS sm ON "
          + " sm.idEntrevista=te.id INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh ON xh.Exp2=te.exp2 AND xh.AnyAcademic='"+cfg.anyAcademicFitxes
          +"' WHERE sm.dataContestat IS NOT NULL AND sm.dataContestat>te.dia GROUP BY WEEK(dia) ORDER BY dia";
 
        rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                       foraTermini.put(rs1.getInt("setmana"), rs1.getInt("total"));
                }
                if(rs1!=null) {
                rs1.close();
            }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
            
//col 5: no contestats
       SQL1= "SELECT WEEK(dia) AS setmana, COUNT(sm.id) AS total FROM tuta_entrevistes AS te INNER JOIN sig_missatgeria AS sm ON "
          + " sm.idEntrevista=te.id INNER JOIN `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh ON xh.Exp2=te.exp2 AND xh.AnyAcademic='"+cfg.anyAcademicFitxes
          +"' WHERE sm.dataContestat IS NULL GROUP BY WEEK(dia) ORDER BY dia";
 
        rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1);
            try {
                while(rs1!=null && rs1.next())
                {
                       noContesta.put(rs1.getInt("setmana"), rs1.getInt("total"));
                }
                if(rs1!=null) {
                rs1.close();
            }
            } catch (SQLException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }

          final int INIROW = 2;
          
          for(int k=INIROW; k<72; k++)
          {
            Cell cell = sheet.getCell(0, k);
            NumberCell ncell = null;
            if(cell.getType()==CellType.NUMBER || cell.getType()==CellType.NUMBER_FORMULA) {
                ncell = (NumberCell) cell;
            }
            
            if (ncell != null) 
            {
               int id = (int) ncell.getValue();
 
               if(mapESO.containsKey(id)) {
                    sheet.addCell(  new jxl.write.Number(1, k, mapESO.get(id)) );
                } //columna-1, fila-1
               
                 if(mapBAT.containsKey(id)) {
                    sheet.addCell(  new jxl.write.Number(2, k, mapBAT.get(id)) );
                } //columna-1, fila-1
                   
                 if(enTermini.containsKey(id)) {
                    sheet.addCell(  new jxl.write.Number(3, k, enTermini.get(id)) );
                } //columna-1, fila-1
                 
                 if(foraTermini.containsKey(id)) {
                    sheet.addCell(  new jxl.write.Number(4, k, foraTermini.get(id)) );
                } //columna-1, fila-1
                 
                 if(noContesta.containsKey(id)) {
                    sheet.addCell(  new jxl.write.Number(5, k, noContesta.get(id)) );
                } //columna-1, fila-1
               
            }
          }
    
    
    }
    
    
     
}

