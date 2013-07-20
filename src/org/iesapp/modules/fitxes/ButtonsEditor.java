/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Josep
 */
class ButtonsEditor extends ButtonsPanel implements javax.swing.table.TableCellEditor {
    public ButtonsEditor(final JTable table) {
        super();
        java.awt.event.MouseListener ml = new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                javax.swing.ButtonModel m = ((javax.swing.JButton) e.getSource()).getModel();
                if(m.isPressed() && table.isRowSelected(table.getEditingRow()) && !e.isShiftDown()) {
                    setBackground(table.getBackground());
                }
            }
        };

        buttons.get(0).addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                JOptionPane.showMessageDialog(table, "Viewing");
            }
        });
        buttons.get(0).addMouseListener(ml);

        buttons.get(1).addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                //Object o = table.getModel().getValueAt(table.getSelectedRow(), 0);
                int row = table.convertRowIndexToModel(table.getEditingRow());
                Object o = table.getModel().getValueAt(row, 0);
                fireEditingStopped();
                JOptionPane.showMessageDialog(table, "Editing: "+o);
            }
        });
        buttons.get(1).addMouseListener(ml);

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                fireEditingStopped();
            }
        });
    }
    @Override public java.awt.Component getTableCellEditorComponent(JTable table, 
            Object value, boolean isSelected, int row, int column) {
        this.setBackground(table.getSelectionBackground());
        return this;
    }
    @Override public Object getCellEditorValue() {
        return "";
    }

    //Copid from AbstractCellEditor
    //protected EventListenerList listenerList = new EventListenerList();
    transient protected ChangeEvent changeEvent = null;

    @Override public boolean isCellEditable(java.util.EventObject e) {
        return true;
    } 
    @Override public boolean shouldSelectCell(java.util.EventObject anEvent) {
        return true;
    }
    @Override public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }
    @Override public void  cancelCellEditing() {
        fireEditingCanceled();
    }
    @Override public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }
    @Override public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }
    public CellEditorListener[] getCellEditorListeners() {
        return (CellEditorListener[])listenerList.getListeners(CellEditorListener.class);
    }
    protected void fireEditingStopped() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for(int i = listeners.length-2; i>=0; i-=2) {
            if(listeners[i]==CellEditorListener.class) {
                // Lazily create the event:
                if(changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
            }
        }
    }
    protected void fireEditingCanceled() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for(int i = listeners.length-2; i>=0; i-=2) {
            if(listeners[i]==CellEditorListener.class) {
                // Lazily create the event:
                if(changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
            }
        }
    }
    
}
