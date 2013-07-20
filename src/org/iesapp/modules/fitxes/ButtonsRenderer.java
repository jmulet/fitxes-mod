/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes;

import javax.swing.JTable;

/**
 *
 * @author Josep
 */
class ButtonsRenderer extends ButtonsPanel implements javax.swing.table.TableCellRenderer {
    public ButtonsRenderer() {
        super();
        setName("Table.cellRenderer");
    }
    @Override public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setBackground(isSelected?table.getSelectionBackground():table.getBackground());
        return this;
    }
}
