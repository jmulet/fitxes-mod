package org.iesapp.modules.fitxes;

import java.util.HashMap;
import org.iesapp.clients.iesdigital.fitxes.TasquesPendents;
import org.iesapp.framework.pluggable.deamons.TopModuleDeamon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Josep
 */
public class FitxesDeamon extends TopModuleDeamon{
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
                 status = TopModuleDeamon.STATUS_AWAKE;
            }
            else
            {
                 message = "";
                 status = TopModuleDeamon.STATUS_NORMAL;
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
