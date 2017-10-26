package com.ursful.framework.tool.ui;

import com.ursful.framework.tool.listener.IListener;
import com.ursful.framework.tool.listener.ITableListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This <code>ListPanel</code> allows the user to add, edit, filter, move, and
 * remove ClassPathEntry objects in a ClassPath object.
 *
 * @author Eric Lafortune
 */
public class TablePanel extends ListPanel
{
    private final JFrame       owner;
    private final boolean      inputAndOutput;

    private String db;

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    private List<ITableListener> tableListenerList = new ArrayList<ITableListener>();


    public void addTableListener(ITableListener tableListener){
        tableListenerList.add(tableListener);
    }


    public TablePanel(JFrame owner, boolean inputAndOutput)
    {
        super();

        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


        this.owner          = owner;
        this.inputAndOutput = inputAndOutput;


        list.setCellRenderer(new MyListCellRenderer());

        list.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {

                JList tmp = (JList) e.getSource();
                getComponent(3).setEnabled(!tmp.isSelectionEmpty());
            }
        });

        //全选，反选，生成
        addAllSelectButton();
        addDeSelectButton();
        addGenerateButton();

        start = 3;

        enableSelectionButtons();
    }


    protected void addAllSelectButton()
    {
        JButton addButton = new JButton("全选");
        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int size = listModel.size();

                int[] selectedIndices = new int[size];
                for (int index = 0; index < size; index++)
                {
                    selectedIndices[index] = index;
                }
                list.setSelectedIndices(selectedIndices);
            }
        });

        addButton(tip(addButton, "addTip"));
    }


    protected void addDeSelectButton()
    {
        JButton editButton = new JButton("反选");
        editButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int size = listModel.size();
                int [] selected = list.getSelectedIndices();//[1,2]
                int [] selectedIndices = new int[size - selected.length];
                int i = 0;
                for (int index = 0; index < size; index++)
                {
                    boolean isContain = false;
                    for(int j : selected){
                        if(j == index){
                            isContain = true;
                            break;
                        }
                    }
                    if(!isContain) {
                        selectedIndices[i] = index;
                        i++;
                    }
                }
                list.setSelectedIndices(selectedIndices);
            }
        });

        addButton(tip(editButton, "editTip"));
    }


    protected void addGenerateButton()
    {
        JButton filterButton = new JButton("生成");
        filterButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                for(ITableListener listener : tableListenerList){
                    listener.select(getDb(), list.getSelectedValuesList());
                }
            }
        });

        addButton(tip(filterButton, "filterTip"));
    }


    /**
     * Sets the ClassPath to be represented in this panel.
     *
    public void setClassPath(ClassPath classPath)
    {
        listModel.clear();

        if (classPath != null)
        {
            for (int index = 0; index < classPath.size(); index++)
            {
                listModel.addElement(classPath.get(index));
            }
        }

        // Make sure the selection buttons are properly enabled,
        // since the clear method doesn't seem to notify the listener.
        enableSelectionButtons();
    }*/


    /**
     * Returns the ClassPath currently represented in this panel.
     */
    /*
    public ClassPath getClassPath()
    {
        int size = listModel.size();
        if (size == 0)
        {
            return null;
        }

        ClassPath classPath = new ClassPath();
        for (int index = 0; index < size; index++)
        {
            classPath.add((ClassPathEntry)listModel.get(index));
        }

        return classPath;
    }*/








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


    /**
     * This ListCellRenderer renders ClassPathEntry objects.
     */
    private class MyListCellRenderer implements ListCellRenderer
    {
        private static final String ARROW_IMAGE_FILE = "arrow.gif";

        private final JPanel cellPanel    = new JPanel(new GridBagLayout());
        private final JLabel iconLabel    = new JLabel("", JLabel.RIGHT);
        private final JLabel jarNameLabel = new JLabel("", JLabel.RIGHT);
        private final JLabel filterLabel  = new JLabel("", JLabel.RIGHT);

        //private final Icon arrowIcon;


        public MyListCellRenderer()
        {
            GridBagConstraints jarNameLabelConstraints = new GridBagConstraints();
            jarNameLabelConstraints.anchor             = GridBagConstraints.WEST;
            jarNameLabelConstraints.insets             = new Insets(1, 2, 1, 2);

            GridBagConstraints filterLabelConstraints  = new GridBagConstraints();
            filterLabelConstraints.gridwidth           = GridBagConstraints.REMAINDER;
            filterLabelConstraints.fill                = GridBagConstraints.HORIZONTAL;
            filterLabelConstraints.weightx             = 1.0;
            filterLabelConstraints.anchor              = GridBagConstraints.EAST;
            filterLabelConstraints.insets              = jarNameLabelConstraints.insets;

            //arrowIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(ARROW_IMAGE_FILE)));

            cellPanel.add(iconLabel,    jarNameLabelConstraints);
            cellPanel.add(jarNameLabel, jarNameLabelConstraints);
            cellPanel.add(filterLabel,  filterLabelConstraints);
        }


        // Implementations for ListCellRenderer.

        public Component getListCellRendererComponent(JList   list,
                                                      Object  value,
                                                      int     index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus)
        {
            //iconLabel.setIcon(arrowIcon);

            // Set the entry name text.
            jarNameLabel.setText(value.toString());



            // Set the colors.
            if (isSelected)
            {
                cellPanel.setBackground(list.getSelectionBackground());
                jarNameLabel.setForeground(list.getSelectionForeground());
                filterLabel.setForeground(list.getSelectionForeground());
            }
            else
            {
                cellPanel.setBackground(list.getBackground());
                jarNameLabel.setForeground(list.getForeground());
                filterLabel.setForeground(list.getForeground());
            }

            // Make the font color red if this is an input file that can't be read.
            if (!inputAndOutput)
            {
               // jarNameLabel.setForeground(Color.red);
            }

            cellPanel.setOpaque(true);

            return cellPanel;
        }



    }
}
