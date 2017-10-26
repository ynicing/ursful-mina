package com.ursful.framework.tool;

import com.ursful.framework.tool.db.DBUtil;
import com.ursful.framework.tool.db.Information;
import com.ursful.framework.tool.listener.IFolderListener;
import com.ursful.framework.tool.listener.IListener;
import com.ursful.framework.tool.listener.ITableListener;
import com.ursful.framework.tool.load.InfiniteProgressPanel;
import com.ursful.framework.tool.load.Loading;
import com.ursful.framework.tool.ui.*;
import com.ursful.framework.tool.util.TextUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;


public class Test extends JFrame{
 
	private static final long serialVersionUID = 1554119310077614846L;

	private static Test test = new Test();

    public static Test getTest(){
        return test;
    }
	
	Point loc = null;
    Point tmp = null;
    boolean isDragged = false;
    
    private DatabasePanel dbPanel;
    private TablePanel tablePanel;

    private Information info;

    private Properties props = new Properties();

    public boolean setInfo(Information info){
        boolean  b = verifyDatasource(info);
        if(b){
            info.setDb(null);

            this.info = info;


            props.setProperty("database.type", info.getType());
            props.setProperty("database.ip", info.getIp());
            props.setProperty("database.port", info.getPort());
            props.setProperty("database.schema", info.getSchema());
            props.setProperty("database.username", info.getUsername());
            props.setProperty("database.password", info.getPassword());
            try {
                props.store(new FileOutputStream(getFilePath()), null);
            }catch (Exception e){

            }

            System.out.println("setInfo:" + info);

            List<String> tables =  DBUtil.getDBS(info);
            dbPanel.addElements(tables);

            tablePanel.clear();
            tablePanel.enableSelectionButtons(1, false);
            tablePanel.enableSelectionButtons(2, false);
            tablePanel.enableSelectionButtons(3, false);

            //dbPanel.load(info);
        }
        return b;

    }


    
    private void setDragable() {
    	 
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent e) {
               isDragged = false;
               test.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
               tmp = new Point(e.getX(), e.getY());
               isDragged = true;
               test.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
        });
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
               if(isDragged) {
                   loc = new Point(test.getLocation().x + e.getX() - tmp.x,
                		   test.getLocation().y + e.getY() - tmp.y);
                   test.setLocation(loc);
               }
            }
        });
    }


    public static void main(String[] args) {
		test.setVisible(true);
	}


    public Map<String, String> getMap(){
        Map<String, String> map = new HashMap<String, String>();
        if(props.getProperty("java.folder") != null){
            map.put("java.folder", props.getProperty("java.folder"));
        }
        if(props.getProperty("web.folder") != null){
            map.put("web.folder", props.getProperty("web.folder"));
        }
        if(props.getProperty("package.folder") != null){
            map.put("package.folder", props.getProperty("package.folder"));
        }else{
            map.put("package.folder","com.weitu.framework.rename.solo");
        }
        if(props.getProperty("common.folder") != null){
            map.put("common.folder", props.getProperty("common.folder"));
        }else{
            map.put("common.folder","com.weitu.framework.rename.solo");
        }
        return map;
    }

	private Test() {
		
		this.setUndecorated(true);
		setDragable();
		
	    
	    JMenu jm2=new JMenu("设置") ;     //����JMenu�˵�����
	    

	    final JMenuItem t1=new JMenuItem("连接") ;  //�˵���
	    jm2.add(t1) ;   //���˵���Ŀ��ӵ��˵�
        t1.addActionListener(new ActionListener() {

            
            public void actionPerformed(ActionEvent e) {
                ConnectFrame cf = new ConnectFrame();
                cf.setInfo(info);
                cf.setBounds(test.getX(), test.getY(), 250, 150);
                cf.setVisible(true);
                cf.toFront();

            }
        });
	    
	    JMenuItem t2=new JMenuItem("目录") ;
	    jm2.add(t2) ;

	    
	    t2.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				FolderFrame cf = new FolderFrame(getMap());
                cf.addListener(new IFolderListener() {
                    
                    public void select(String type, String folder) {
                        props.put(type, folder);
                        try{
                            props.store(new FileOutputStream(getFilePath()), null);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
				cf.setBounds(test.getX(), test.getY(), 600, 200);
				cf.setVisible(true);
				cf.toFront();
				
			}
		});
	    jm2.addSeparator();
	    JMenuItem t3=new JMenuItem("退出") ;  //�˵���
	    jm2.add(t3) ;   //���˵���Ŀ��ӵ��˵�
	    t3.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				//test.setVisible(false);
			}
		});
	    final JMenuBar  br=new  JMenuBar() ;  //�����˵�������
	    br.add(jm2) ;      //���˵����ӵ��˵�������
	    this.setJMenuBar(br) ;  //Ϊ ��������  �˵�������
		
	    //String home = System.getProperty("home.dir");
		 
		this.setTitle("快捷工具");
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);// �Ѵ�����������Ļ�м�
		
		//systemTray();

        this.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				test.setVisible(false);
			}
		}); 
		
		this.setLayout(new BorderLayout());
		 
		t1.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				
			}
		});
		
        this.getContentPane().setVisible(true);
		this.setVisible(true);
		this.setBackground(Color.lightGray);


		/*dbPanel = new DBPanel(new IDBListener() {
            
            public void select(String db) {
                tablePanel.reload(info, db);
            }
        });*/
        dbPanel = new DatabasePanel(this, false);
        dbPanel.addListener(new IListener() {
            
            public void select(List<String> db) {

                List<String> tables = DBUtil.getTables(info, db.get(0));
                if(tables.isEmpty()){
                    tablePanel.enableSelectionButtons(1, false);
                    tablePanel.enableSelectionButtons(2, false);
                    tablePanel.enableSelectionButtons(3, false);
                }else{
                    tablePanel.enableSelectionButtons(1, true);
                    tablePanel.enableSelectionButtons(2, true);
                    tablePanel.enableSelectionButtons(3, false);
                }
                tablePanel.setDb(db.get(0));
                tablePanel.addElements(tables);
            }
        });
		//dbPanel.setBackground(Color.red);
		dbPanel.setVisible(true);
		dbPanel.setPreferredSize(new Dimension(200, 0));
		 
		tablePanel = new TablePanel(this, false);
		//tablePanel.setBackground(Color.yellow);
        //panel.setBounds(0, 0, 200, 300);

        tablePanel.addTableListener(new ITableListener() {
            
            public void select(String db, List<String> tables) {
                System.out.println("create : " + tables);
                System.out.println(props);

                String folderMenu = "admin";

                InfiniteProgressPanel gp = new InfiniteProgressPanel();
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                gp.setBounds(0, 0, Test.getTest().getWidth(),Test.getTest().getHeight());

                Test.getTest().setGlassPane(gp);
                gp.start();//开始动画加载效果
                System.out.println("start...");

                // info.setUrl(info.getUrl() + "/"  +tablePanel.getDb());

                info.setDb(db);



                new Thread(new Loading(info, db, tables, folderMenu, props, gp)).start();
                //frame.setVisible(true);

               // Test.getTest().stopLoading();
            }
        });
       
        /*
        JButton button = new JButton("Generate");
        button.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                List<String> tmp = tablePanel.getNodes();
                System.out.println(tmp);
            }
        });*/
       
        Container contentPane = this.getContentPane(); 
        contentPane.add(dbPanel, BorderLayout.WEST);
        contentPane.add(tablePanel, BorderLayout.CENTER);
        //contentPane.add(button, BorderLayout.SOUTH);

        try{
            FileInputStream fis = new FileInputStream(getFilePath());
            if(fis != null){
                props.load(fis);
                String pkg = props.getProperty("package.folder");
                if(pkg == null){
                    props.put("package.folder","com.weitu.framework.rename.solo");
                }
                pkg = props.getProperty("common.folder");
                if(pkg == null){
                    props.put("common.folder","com.weitu.framework.rename.solo");
                }

                Information information = new Information(props.getProperty("database.type"));
                information.setIp(props.getProperty("database.ip"));
                information.setPort(props.getProperty("database.port"));
                information.setUsername(props.getProperty("database.username"));
                information.setPassword(props.getProperty("database.password"));
                setInfo(information);
            }
            fis.close();
        }catch (Exception e){

        }

		
	}

    private static InfiniteProgressPanel glasspane;


    public void stopLoading(){
        System.out.println("stop.");
        glasspane.stop();
    }



    public String getFilePath(){
        return System.getProperty("java.io.tmpdir") + File.separator + "test_config.properties";
    }

	
	/**
	 * ����ϵͳ����
	 */
	private void systemTray() {
		if (SystemTray.isSupported()) {

			PopupMenu popupMenu = new PopupMenu();

			MenuItem itemExit = new MenuItem("退出");
			itemExit.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			popupMenu.add(itemExit);

		    InputStream is =  Test.class.getClassLoader().getResourceAsStream("resources/green.png");
		    BufferedImage img = null;
			try {
				img = ImageIO.read(is);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			ImageIcon icon = new ImageIcon(img);
			TrayIcon trayIcon = new TrayIcon(icon.getImage(), "JavaTool", popupMenu);
			trayIcon.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					test.setVisible(true);
				}
			});

			try {
				SystemTray.getSystemTray().add(trayIcon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}


    public boolean verifyDatasource(Information info){

        Connection conn = null;
        ResultSet rs = null;
        Statement stmt = null;
        try {
            Class.forName(info.getDriver());
            conn = DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
            stmt = conn.createStatement();
            rs = stmt.executeQuery(info.getTestSQL());
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e2) {
            }
        }

        return false;
    }
}
