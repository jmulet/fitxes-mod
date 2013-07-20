/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.dialogs;

import java.util.ArrayList;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class ParseFieldPara {
  
    private String txt;

    public ParseFieldPara(String txt) {
        this.txt = txt;
    }

    public String getText(ArrayList<BeanSeleccio> listSelect) {
        String out = txt;

        if (txt.startsWith("[")) {
            out = StringUtils.AfterFirst(txt, "[");
            out = StringUtils.BeforeLast(out, "]");
            ArrayList<String> parsed = StringUtils.parseStringToArray(out, ",", StringUtils.CASE_UPPER);
            out = "";
            for (int i = 0; i < parsed.size(); i++) {
                //S'ocupa de cercar l'abrev dins la llistaSelect
                for(int j=0; j<listSelect.size();j++)
                {
                    if(listSelect.get(j).abrev.equalsIgnoreCase(parsed.get(i)))
                    {
                        out += listSelect.get(j).nomProfe + ";\n";
                        break;
                        //nomes posa un pic el nom del professor
                    }
                }
            }
        }
        return out;

    }
        
}
 
