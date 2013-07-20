/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.modules.fitxes.util;

import java.util.ArrayList;

/**
 *
 * @author Josep
 */
public class TasquesPendentsStruct {
    public ArrayList<Integer> idTasks;
    public ArrayList<String>  detallTasks;

    public TasquesPendentsStruct()
    {
        idTasks = new ArrayList<Integer>();
        detallTasks = new ArrayList<String>();
    }
}
