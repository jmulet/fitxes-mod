/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import org.iesapp.modules.fitxescore.util.Cfg;

/**
 *
 * @author Josep
 * Cerca els documents associats a un alumne,
 * a les dreceres de cerca estipulades en Cfg.searchPath
 * 
 */
public class SharedDocs {
    
    public static final String separator = "-";
    private int exp;
    private final FileFilter filter;
    private ArrayList<File> list;
    private final Cfg cfg;

    public SharedDocs(int exp, Cfg cfg)
    {
        this.exp = exp;
        this.cfg = cfg;
        filter = new FileFilter() {
            @Override
                public boolean accept(File pathname) {
                    return ( pathname.isDirectory() || pathname.getName().contains(separator+getExp()+separator) );
                }
         };
    }
    
    public ArrayList<File> findURLDocs()
    {
        list = new ArrayList<File>();
        if(Cfg.searchPath.isEmpty()) {
            return list;
        }
        
        for(int i=0; i<Cfg.searchPath.size(); i++ )
        {
            java.io.File file = new File(Cfg.searchPath.get(i).getDirectory());
            File[] listFiles = file.listFiles(filter);
            
            if(listFiles==null) {
                continue;
            }
            boolean recursive = Cfg.searchPath.get(i).isRecursive();
            
            for(int j=0; j<listFiles.length; j++)
            {                
                //recursive
                if(listFiles[j].isDirectory())
                {
                    //Si el directori conté el codig de l'alumne no cal proseguir
                    //una cerca recursiva
                    if(listFiles[j].getName().contains(separator+getExp()+separator))
                    {
                          list.add(listFiles[j]); 
                    }
                    else
                    {
                        if(recursive) {
                            recursiveCheck(listFiles[j], list);
                        }
                    }
                }
                else
                {
                    boolean lectura = listFiles[j].canRead();
                   
                    if(lectura)
                    {
                        list.add(listFiles[j]); 
                    }
                }
            }
        }
        
        return list;
        
    }

    private void recursiveCheck(File file, ArrayList<File> list) {

            File[] listFiles = file.listFiles(filter);
            if(listFiles==null) {
            return;
        }
            
            for(int j=0; j<listFiles.length; j++)
            {                
                //recursive
                if(listFiles[j].isDirectory())
                {
                    if(listFiles[j].getName().contains(separator+getExp()+separator))
                    {
                        list.add(listFiles[j]); 
                    }
                    else
                    {
                        recursiveCheck(listFiles[j], list);
                    }
                }
                else
                {
                    boolean lectura = listFiles[j].canRead();
                   
                    if(lectura)
                    {
                        list.add(listFiles[j]); 
                    }
                }
            }
     
    }

    public File getStudentDirectory() {
        File dir = null;
        
        for(File f: list)
        {
            if(f.isDirectory() && f.getName().contains(separator+getExp()+separator))
            {
                dir = f;
                break;
            }
        }
        
        return dir;
    }

    /**
     * @return the exp
     */
    public int getExp() {
        return exp;
    }

    /**
     * @param exp the exp to set
     */
    public void setExp(int exp) {
        this.exp = exp;
    }

    
}
