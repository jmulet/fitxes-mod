/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxescore.lookups;

import java.util.ArrayList;

/**
 *
 * @author Josep
 */
public class TableSelection {
    protected int currentSelection;
    protected ArrayList<Integer> allSelection;
    protected ArrayList<Integer> belongsSelection;
    protected int anyAcademic;
    
    public TableSelection(int currentSelection, ArrayList<Integer> allSelection, 
            ArrayList<Integer> belongsSelection, int anyAcademic)
    {
        this.currentSelection = currentSelection;
        this.allSelection = allSelection;
        this.belongsSelection = belongsSelection;
        this.anyAcademic = anyAcademic;
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(int currentSelection) {
        this.currentSelection = currentSelection;
    }

    public ArrayList<Integer> getAllSelection() {
        return allSelection;
    }

    public void setAllSelection(ArrayList<Integer> allSelection) {
        this.allSelection = allSelection;
    }

    public ArrayList<Integer> getBelongsSelection() {
        return belongsSelection;
    }

    public void setBelongsSelection(ArrayList<Integer> belongsSelection) {
        this.belongsSelection = belongsSelection;
    }

    public int getAnyAcademic() {
        return anyAcademic;
    }

    public void setAnyAcademic(int anyAcademic) {
        this.anyAcademic = anyAcademic;
    }
    
}
