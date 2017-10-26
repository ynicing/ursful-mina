package com.ursful.framework.tool.ui;

import com.ursful.framework.tool.listener.IFolderListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FolderFrame extends JFrame{
	
	private static final long serialVersionUID = 7707039357181395714L;
	
	private JPanel content;
    private JTextField javaFolder;
    private JTextField webFolder;
    private JTextField packageFolder;
    private JTextField commonFolder;

    private List<IFolderListener> listeners = new ArrayList<IFolderListener>();

    public  void addListener(IFolderListener listener){
        listeners.add(listener);
    }

	public FolderFrame(Map<String, String> map){
		
		setTitle("设置文件生成目录");
		
		//Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        final JFrame p = this;

        content = new JPanel();  
        content.setBorder(new EmptyBorder(0, 0, 0, 0));  
        setContentPane(content);  
        content.setLayout(null); 
        
        JLabel label = new JLabel("Java:", JLabel.RIGHT);
        label.setBounds(5, 0, 70, 30);
        content.add(label);
        javaFolder = new JTextField();
        javaFolder.setBounds(75,0,420,30);
        javaFolder.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("java.folder", javaFolder.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("java.folder", javaFolder.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("java.folder", javaFolder.getText());
                }
            }
        });
        if(map.get("java.folder") != null){
            javaFolder.setText(map.get("java.folder"));
        }
        content.add(javaFolder);
        JButton button = new JButton("...");
        button.setBounds(495, 5, 20, 20);
        button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
                JFileChooser c = new JFileChooser();
                c.setMultiSelectionEnabled(false);
                c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                c.setDialogTitle("选择Java文件生成目录");
                int result = c.showOpenDialog(p);
                if(result == JFileChooser.APPROVE_OPTION) {
                    String path = c.getSelectedFile().getAbsolutePath();
                    javaFolder.setText(path);
                    for(IFolderListener listener : listeners){
                        listener.select("java.folder", path);
                    }
                }
			}});
        content.add(button);




        label = new JLabel("Web:", JLabel.RIGHT);
        label.setBounds(5, 30, 70, 30);
        content.add(label);
        webFolder = new JTextField();
        webFolder.setBounds(75,30,420,30);
        if(map.get("web.folder") != null){
            webFolder.setText(map.get("web.folder"));
        }
        webFolder.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("web.folder", webFolder.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("web.folder", webFolder.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("web.folder", webFolder.getText());
                }
            }
        });
        content.add(webFolder);
        JButton button2 = new JButton("...");
        button2.setBounds(495, 35, 20, 20);
        button2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser c2 = new JFileChooser();
                c2.setMultiSelectionEnabled(false);
                c2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                c2.setDialogTitle("选择Html,JS,多语言生成目录");
                int result = c2.showOpenDialog(p);
                if(result == JFileChooser.APPROVE_OPTION) {
                    String path = c2.getSelectedFile().getAbsolutePath();
                    webFolder.setText(path);
                    for(IFolderListener listener : listeners){
                        listener.select("html.folder", path);
                    }
                }
            }});
        content.add(button2);


        label = new JLabel("package:", JLabel.RIGHT);
        label.setBounds(5, 60, 70, 30);
        content.add(label);
        packageFolder = new JTextField();
        packageFolder.setBounds(75, 60, 420, 30);
        if(map.get("package.folder") != null){
            packageFolder.setText(map.get("package.folder"));
        }
        packageFolder.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("package.folder", packageFolder.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("package.folder", packageFolder.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("package.folder", packageFolder.getText());
                }
            }
        });
        content.add(packageFolder);


        label = new JLabel("common:", JLabel.RIGHT);
        label.setBounds(5, 90, 70, 30);
        content.add(label);
        commonFolder = new JTextField();
        commonFolder.setBounds(75,90,420,30);
        if(map.get("common.folder") != null){
            commonFolder.setText(map.get("common.folder"));
        }
        commonFolder.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("common.folder", commonFolder.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("common.folder", commonFolder.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                for(IFolderListener listener : listeners){
                    listener.select("common.folder", commonFolder.getText());
                }
            }
        });
        content.add(commonFolder);
	 
	}
 
}
