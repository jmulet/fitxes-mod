package org.iesapp.modules.fitxes;

import java.util.HashMap;
import org.iesapp.clients.iesdigital.fitxes.TasquesPendents;
import org.iesapp.framework.pluggable.daemons.TopModuleDaemon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Josep
 */
public class FitxesDeamon extends TopModuleDaemon{
    private TasquesPendents tp;
    private String message;
    private int solPendents = 0;

    @Override
    protected void checkStatusProcedure() {
       if(coreCfg.getUserInfo()!=null)
       {
            tp = new TasquesPendents(coreCfg.getIesClient());
            tp.checkTasquesPendentsForeground(coreCfg.getUserInfo().getAbrev());
            int solPendentsNew = tp.jobs.size();
            if(solPendentsNew>0)
            {
                 message = solPendentsNew+" alumnes requereixen actuacions";
                 status = TopModuleDaemon.STATUS_AWAKE;
            }
            else
            {
                 message = "";
                 status = TopModuleDaemon.STATUS_NORMAL;
            }

            if( solPendents!=solPendentsNew )
            {
                 //System.out.println("Fire...for "+coreCfg.getUserInfo().getAbrev()+": "+"actuacions pendents->"+ solPendents+""+ solPendentsNew);
                 this.propertyChangeSupport.firePropertyChange("solpendentes", solPendents, solPendentsNew);
            }
            solPendents = solPendentsNew;
       }
        
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HashMap getCurrentValues() {
        return new HashMap();
    }

    @Override
    public void reset() {
        //
    }
 
 
    
}
