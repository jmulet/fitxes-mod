/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.lookup;

import java.util.ArrayList;

/**
 *
 * @author Josep
 */
public class ItemTableSelectionAll extends org.openide.util.lookup.AbstractLookup.Item {
    private ArrayList<Integer> list;

    public ItemTableSelectionAll()
    {
        
    }
        
    public void setList(ArrayList<Integer> list)
    {
        this.list = list;
    }
            

    @Override
    public Object getInstance() {
        return list;
    }

    @Override
    public Class getType() {
        return list.getClass();
    }

    @Override
    public String getId() {
        return "tableSelectionAll";
    }

    @Override
    public String getDisplayName() {
        return "TableSelectionAll";
    }
    
}
