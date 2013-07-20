/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes;

/**
 *
 * @author Josep
 */
public class ButtonsPanel extends javax.swing.JPanel {
    public final java.util.List<javax.swing.JButton> buttons = 
            java.util.Arrays.asList(new javax.swing.JButton("view"), new javax.swing.JButton("edit"));
    public ButtonsPanel() {
        super();
        setOpaque(true);
        for(javax.swing.JButton b: buttons) {
            b.setFocusable(false);
            b.setRolloverEnabled(false);
            add(b);
        }
    }
}
