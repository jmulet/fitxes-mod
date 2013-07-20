/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.dialogs;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;    
import org.iesapp.clients.iesdigital.actuacions.BeanFieldReport;
import org.iesapp.clients.iesdigital.fitxes.BeanDadesPersonals;
import org.iesapp.clients.sgd7.reports.InformesSGD;
import org.iesapp.framework.dialogs.ReportFactory;
import org.iesapp.framework.util.SplashWindow3;
import org.iesapp.modules.fitxes.FitxesGUI;
import org.iesapp.modules.fitxes.reports.ReportingClass;
import org.iesapp.modules.fitxescore.util.Cfg;
    
/**
 *
 * @author Josep
 */
class ReportListener implements ActionListener {
    private final int expedient;
    private final PreDocManager dlg;
    private final BeanDadesPersonals dp;
    private final AccionsAlumne4 accionsAlumne4;
    private final Cfg cfg;

    public ReportListener(final int expedient, final PreDocManager dlg, 
            final BeanDadesPersonals dp, final AccionsAlumne4 accionsAlumne4, final Cfg cfg) {
        this.cfg = cfg;
        this.expedient = expedient;
        this.dlg = dlg;
        this.dp = dp;
        this.accionsAlumne4 = accionsAlumne4;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
               String doCheck = dlg.getFormulari().doCheck();
               if(doCheck!=null && !doCheck.isEmpty()) {
                    return;
               }
               
        //En mostrar el document d'una actuacio nova, la desa a la base
        if (dlg.actuacio.nova) {
            dlg.actuacio.nova = false;
            dlg.actuacio.map = dlg.getFormulari().getMap();
            dlg.actuacio.save();

            //Comprova si ha de modificar la fitxa personal de l'alumne
            accionsAlumne4.updateFitxaAlumne();

            //Fa un update de les tasques pendents
            FitxesGUI.pend.checkTasquesPendents(expedient);
            accionsAlumne4.timer.start();
            if (dlg.actuacio.data2 == null) {
                FitxesGUI.pend.addOberta(expedient);
            }
            accionsAlumne4.fillTable();
        }
               
               // dlg.getFormulari().updateDocDatabase();
               HashMap<String,Object> cloned = (HashMap<String,Object>) dlg.getMap().clone();
               BeanFieldReport selectedReport = dlg.getFormulari().getSelectedReport();
               
               //Mostra el popup amb instruccions si l'actuacio es nova
               // 
               if(!dlg.getFormulari().isMainReportGenerated() && selectedReport.getPopupInstructions()!=null && 
                       !selectedReport.getPopupInstructions().isEmpty())
               {
                   SplashWindow3 splash = new SplashWindow3(selectedReport.getPopupInstructions(),
                           (javax.swing.JFrame) null, 8000, new Point(20,200)); //dlg.actuacio.position
                   splash.setOpacity(0.75f);
                   splash.setAlwaysOnTop(true);
                   splash.setVisible(true);
               }
               if(selectedReport.isImportant())
               {
                   dlg.getFormulari().setMainReportGenerated(true);
               }
               
               
               if(selectedReport.getIncludeSubReport().equals("F")) //incidencies que generen informe de faltes
               {
                    InformesSGD informes = new InformesSGD(cfg.getCoreCfg().getSgdClient());
                    List lexp = new ArrayList<Integer>();
                    lexp.add(expedient);
                    List linc = new ArrayList<String>();
                    linc.add("FA");
                    linc.add("F");
                    linc.add("FJ");

                    int limit = 0;                     //=0 no limita el nombre de fnj
                    if(dlg.isLimitador())
                    {
                        limit = selectedReport.getLimitInc();
                    }

                    List m_aux = informes.getListIncidencies(lexp, linc, limit);

                    Object db2_ds = ReportFactory.createJRBeanCollectionDataSource(m_aux);                         
                    cloned.put("db2",db2_ds);

                }
                else if(selectedReport.getIncludeSubReport().equals("A")) //incidencies que generen informe d'amonestacions
                {
                    InformesSGD informes = new InformesSGD(cfg.getCoreCfg().getSgdClient());
                    List lexp = new ArrayList<Integer>();
                    lexp.add(expedient);
                    List linc = new ArrayList<String>();
                    linc.add("AG");
                    linc.add("AL");
                    linc.add("ALH");

                    int limit = 0;                     //=0 no limita el nombre d'amonestacions greus
                    if(dlg.isLimitador())
                    {
                       limit = selectedReport.getLimitInc();
                    }

                    List m_aux = informes.getListIncidencies(lexp, linc, limit);

                    Object db2 = ReportFactory.createJRBeanCollectionDataSource(m_aux);       
                    cloned.put("db2",db2);

                }
                 ReportingClass rc = new ReportingClass(cfg);
                 rc.reportTask(expedient, selectedReport.getReportPath(), cloned, dp.getEnsenyament());
            
            
               // parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }              
            
    }
    