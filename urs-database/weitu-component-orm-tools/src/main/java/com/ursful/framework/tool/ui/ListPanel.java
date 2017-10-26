package com.ursful.framework.tool.ui;

import com.ursful.framework.tool.listener.IListener;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


public abstract class ListPanel extends JPanel
{
    protected final DefaultListModel listModel = new DefaultListModel();
    protected final JList            list      = new JList(listModel);

    protected int start = 0;
    protected List<IListener> listeners = new ArrayList<IListener>();

    public void addListener(IListener listener){
        listeners.add(listener);
    }

    protected ListPanel()
    {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        final GridBagConstraints listConstraints = new GridBagConstraints();
        listConstraints.gridheight = GridBagConstraints.REMAINDER;
        listConstraints.fill       = GridBagConstraints.BOTH;
        listConstraints.weightx    = 1.0;
        listConstraints.weighty    = 1.0;
        listConstraints.anchor     = GridBagConstraints.NORTHWEST;
        listConstraints.insets     = new Insets(0, 2, 0, 2);

        // Make sure some buttons are disabled or enabled depending on whether
        // the selection is empty or not.
        list.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {

                JList tmp = (JList) e.getSource();
                if(tmp.getValueIsAdjusting()) {
                    System.out.println(tmp.getSelectedValuesList());
                    for(IListener listener : listeners){
                        listener.select(tmp.getSelectedValuesList());
                    }
                }
               // enableSelectionButtons();
            }
        });

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        add(new JScrollPane(list), listConstraints);

        // something like the following calls are up to the extending class:
        //addAddButton();
        //addEditButton();
        //addRemoveButton();
        //addUpButton();
        //addDownButton();
        //
        //pubi();
    }


    protected void addRemoveButton()
    {
        JButton removeButton = new JButton(msg("remove"));
        removeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Remove the selected elements.
                removeElementsAt(list.getSelectedIndices());
            }
        });

        addButton(tip(removeButton, "removeTip"));
    }


    protected void addUpButton()
    {
        JButton upButton = new JButton(msg("moveUp"));
        upButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int[] selectedIndices = list.getSelectedIndices();
                if (selectedIndices.length > 0 &&
                    selectedIndices[0] > 0)
                {
                    // Move the selected elements up.
                    moveElementsAt(selectedIndices, -1);
                }
            }
        });

        addButton(tip(upButton, "moveUpTip"));
    }


    protected void addDownButton()
    {
        JButton downButton = new JButton(msg("moveDown"));
        downButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int[] selectedIndices = list.getSelectedIndices();
                if (selectedIndices.length > 0 &&
                    selectedIndices[selectedIndices.length-1] < listModel.getSize()-1)
                {
                    // Move the selected elements down.
                    moveElementsAt(selectedIndices, 1);
                }
            }
        });

        addButton(tip(downButton, "moveDownTip"));
    }



    protected void addButton(JComponent button)
    {
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridwidth = GridBagConstraints.REMAINDER;
        buttonConstraints.fill      = GridBagConstraints.HORIZONTAL;
        buttonConstraints.anchor    = GridBagConstraints.NORTHWEST;
        buttonConstraints.insets    = new Insets(0, 2, 0, 2);

        add(button, buttonConstraints);
    }



    protected void addElement(Object element)
    {
        listModel.addElement(element);

        // Make sure it is selected.
        list.setSelectedIndex(listModel.size() - 1);
    }

    public void addElements(List elements) {
        // Add the elements one by one.
        listModel.removeAllElements();
        for (int index = 0; index < elements.size(); index++) {
            listModel.addElement(elements.get(index));
        }
    }

    public void addElements(Object[] elements){
        // Add the elements one by one.
        for (int index = 0; index < elements.length; index++)
        {
            listModel.addElement(elements[index]);
        }

        /*
        // Make sure they are selected.
        int[] selectedIndices = new int[elements.length];
        for (int index = 0; index < selectedIndices.length; index++)
        {
            selectedIndices[index] =
                listModel.size() - selectedIndices.length + index;
        }
        list.setSelectedIndices(selectedIndices);*/
    }


    protected void moveElementsAt(int[] indices, int offset)
    {
        // Remember the selected elements.
        Object[] selectedElements = list.getSelectedValues();

        // Remove the selected elements.
        removeElementsAt(indices);

        // Update the element indices.
        for (int index = 0; index < indices.length; index++)
        {
            indices[index] += offset;
        }

        // Reinsert the selected elements.
        insertElementsAt(selectedElements, indices);
    }


    protected void insertElementsAt(Object[] elements, int[] indices)
    {
        for (int index = 0; index < elements.length; index++)
        {
            listModel.insertElementAt(elements[index], indices[index]);
        }

        // Make sure they are selected.
        list.setSelectedIndices(indices);
    }


    protected void setElementAt(Object element, int index)
    {
        listModel.setElementAt(element, index);

        // Make sure it is selected.
        list.setSelectedIndex(index);
    }


    protected void setElementsAt(Object[] elements, int[] indices)
    {
        for (int index = 0; index < elements.length; index++)
        {
            listModel.setElementAt(elements[index], indices[index]);
        }

        // Make sure they are selected.
        list.setSelectedIndices(indices);
    }


    protected void removeElementsAt(int[] indices)
    {
        for (int index = indices.length - 1; index >= 0; index--)
        {
            listModel.removeElementAt(indices[index]);
        }

        // Make sure nothing is selected.
        list.clearSelection();

        // Make sure the selection buttons are properly enabled,
        // since the above method doesn't seem to notify the listener.
        enableSelectionButtons();
    }


    protected void removeAllElements()
    {
        listModel.removeAllElements();

        // Make sure the selection buttons are properly enabled,
        // since the above method doesn't seem to notify the listener.
        enableSelectionButtons();
    }

    protected void enableSelectionButtons()
    {
         boolean selected = !list.isSelectionEmpty();

        // Loop over all components, except the list itself and the Add button.
        for (int index = 0; index < getComponentCount(); index++)
        {
            getComponent(index).setEnabled(selected);
        }
    }


    public void clear(){
        listModel.removeAllElements();
    }

    /**
     * Enables or disables the buttons that depend on a selection.
     */
    public void enableSelectionButtons(int index, boolean selected)
    {
       // boolean selected = !list.isSelectionEmpty();
//getComponentCount()
        // Loop over all components, except the list itself and the Add button.
       // for (int index = 0; index < start; index++)
       // {
            getComponent(index).setEnabled(selected);
        //}
    }


    /**
     * Attaches the tool tip from the GUI resources that corresponds to the
     * given key, to the given component.
     */
    private static JComponent tip(JComponent component, String messageKey)
    {
        component.setToolTipText(msg(messageKey));

        return component;
    }


    /**
     * Returns the message from the GUI resources that corresponds to the given
     * key.
     */
    private static String msg(String messageKey)
    {
         return messageKey;
    }
}
