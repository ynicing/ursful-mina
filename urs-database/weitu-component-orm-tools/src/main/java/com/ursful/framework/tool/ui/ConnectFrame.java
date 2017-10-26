package com.ursful.framework.tool.ui;

import com.ursful.framework.tool.Test;
import com.ursful.framework.tool.db.Information;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ynice on 3/27/17.
 */
public class ConnectFrame extends JFrame{

    private JTextField ip;
    private JTextField alias;

    private JTextField userName;
    private JTextField password;
    private JTextField port;
    private JComboBox type;

    public void showAlias(boolean flag){
        System.out.println(alias + " =>set:" + flag);
        if(alias == null){
            return;
        }
        alias.setVisible(flag);
        this.revalidate();
    }

    public void setInfo(Information info){
        if(info != null){
            type.setSelectedItem(info.getType());
            ip.setText(info.getIp());
            port.setText(info.getPort());
            userName.setText(info.getUsername());
            password.setText(info.getPassword());
        }

    }

    public ConnectFrame(){

        Container contentPane = this.getContentPane();

        GridBagLayout layout = new GridBagLayout();
        contentPane.setLayout(layout);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        JLabel label1=new JLabel("数据库:", JLabel.RIGHT);

        constraints.gridx = 0;
        constraints.weightx = 0.33;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        //contentPane.add(label1, constraints);

        type =new JComboBox();
        type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox c = (JComboBox)e.getSource();
                if("oracle".equalsIgnoreCase(c.getSelectedItem().toString())){
                    showAlias(true);
                }else{
                    showAlias(false);
                }
            }
        });
        type.addItem("MySQL");
        type.addItem("Oracle");


        constraints.gridwidth = 1;
        constraints.weightx = 0.66;
        constraints.gridx = 1;
        constraints.gridy = 0;
        //contentPane.add(type, constraints);


        JLabel label4=new JLabel("地址:", JLabel.RIGHT);
        constraints.gridx = 0;
        constraints.weightx = 0.33;
        constraints.gridy = 1;
        contentPane.add(label4, constraints);
        ip = new JTextField();
        ip.setText("127.0.0.1");
        constraints.gridx = 1;
        constraints.weightx = 0.66;
        constraints.gridy = 1;
        contentPane.add(ip, constraints);

        port = new JTextField();
        port.setText("3306");
        constraints.gridx = 2;
        constraints.weightx = 0.66;
        constraints.gridy = 1;
        contentPane.add(port, constraints);

        alias = new JTextField();
        alias.setText("orcl");
        alias.setVisible(false);
        constraints.gridx = 2;
        constraints.weightx = 0.66;
        constraints.gridy = 1;
        contentPane.add(alias, constraints);
        //showAlias(false);

        JLabel label2=new JLabel("帐号:", JLabel.RIGHT);
        constraints.gridx = 0;
        constraints.weightx = 0.33;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        contentPane.add(label2, constraints);
        userName = new JTextField();
        constraints.gridx = 1;
        constraints.weightx = 0.66;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        contentPane.add(userName, constraints);

        JLabel label3=new JLabel("密码:", JLabel.RIGHT);
        constraints.gridx = 0;
        constraints.weightx = 0.33;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        contentPane.add(label3, constraints);
        password = new JTextField();
        constraints.gridx = 1;
        constraints.weightx = 0.66;
        constraints.gridwidth = 2;
        constraints.gridy = 3;
        contentPane.add(password, constraints);


        JButton button = new JButton("保存");
        constraints.gridx = 2;
        constraints.weightx = .5;
        constraints.gridy = 4;
        contentPane.add(button, constraints);

        final JFrame frame = this;

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //type.getActionCommand()
                Information information = new Information(type.getSelectedItem().toString());
                information.setIp(ip.getText().trim());
                information.setPort(port.getText().trim());
                information.setSchema(alias.getText().trim());
                information.setUsername(userName.getText().trim());
                information.setPassword(password.getText().trim());
                if(Test.getTest().setInfo(information)){
                    frame.dispose();
                }
            }
        });
    }

}
