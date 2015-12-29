package client;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import User.User;
import java.util.*;

public class ChatFrame extends Frame{
    public ChatFrame(){
        setSize(600,500);
        setTitle("Chatting Room");
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                System.exit(0);
            }
        });
        ChatPanel sp = new ChatPanel();
        add(sp, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args){
        ChatFrame sf = new ChatFrame();
    }
}

class ChatPanel extends Panel implements ActionListener, Runnable{
    Thread t;
    Label l;
    TextField tf;
    TextField tfn;
    static JTextArea jta;
    JTextArea userlist;
    Button b;
    Button send;
    Button clear;
    Button history;
    Button save;
    Socket s;
    ObjectInputStream is;
    ObjectOutputStream os;
    User u;
    JComboBox chatTo;
    JList list;
    ArrayList<String> online = new ArrayList<String>();
    boolean connected;
    static String temp;

    public ChatPanel(){
        setLayout(new BorderLayout());
        tf = new TextField();//send message area
        tf.addActionListener(this);
        //tf.setBackground(Color.GRAY);
        send = new Button("Send");
        send.addActionListener(this);
        send.setBackground(Color.ORANGE);
        final JSplitPane splitPanel1 = new JSplitPane();
        splitPanel1.add(tf,JSplitPane.LEFT);
        splitPanel1.add(send,JSplitPane.RIGHT);
        splitPanel1.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPanel1.setDividerLocation(2.0 / 3.0);
            }
        });
        // splitPanel1.setBackground(Color.CYAN);
        Panel buttons = new Panel();
        buttons.setLayout(new GridLayout(1,0));
        chatTo = new JComboBox();
        chatTo.setEditable(false);
        chatTo.setFocusable(false);
        final JSplitPane tool = new JSplitPane();
        Panel buttons1 = new Panel();
        buttons1.setLayout(new GridLayout(1,0));
        clear = new Button("Clear Screen");
        clear.addActionListener(this);
        clear.setBackground(Color.ORANGE);
        save = new Button("Save Chat History");
        save.addActionListener(this);
        save.setBackground(Color.ORANGE);
        history = new Button("Load Chat History");
        history.addActionListener(this);
        history.setBackground(Color.ORANGE);
        buttons1.add(clear);
        buttons1.add(history);
        buttons1.add(save);
        tool.add(buttons1,JSplitPane.LEFT);
        tool.add(chatTo,JSplitPane.RIGHT);
        tool.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                tool.setDividerLocation(2.0 / 3.0);
            }
        });
        Panel gridPanelSend = new Panel();
        gridPanelSend.setLayout(new GridLayout(0,1));
        gridPanelSend.add(tool);
        gridPanelSend.add(splitPanel1);
        add(gridPanelSend, BorderLayout.SOUTH);
        //gridPanelSend.setBackground(Color.BLUE);



        l = new Label();//name and connect
        l.setText("User Name:");
        l.setBackground(Color.ORANGE);
        tfn = new TextField();
        tfn.addActionListener(this);
        b = new Button("Connect");
        b.addActionListener(this);
        b.setBackground(Color.ORANGE);
        Panel gridPanel = new Panel();
        gridPanel.setLayout(new GridLayout(1,0));
        gridPanel.add(l);
        gridPanel.add(tfn);
        gridPanel.add(b);
        add(gridPanel, BorderLayout.NORTH);

        jta = new JTextArea();//display area
        jta.setEditable(false);
        jta.setBackground(Color.white);
        JScrollPane jsp1 = new JScrollPane(jta);
        jsp1.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp1.setBackground(Color.white);
        list = new JList();
        JScrollPane jsp2 = new JScrollPane(list);
        jsp2.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp2.setBackground(Color.lightGray);
        final JSplitPane splitPanel = new JSplitPane();
        splitPanel.add(jsp1,JSplitPane.LEFT);
        splitPanel.add(jsp2,JSplitPane.RIGHT);
        splitPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPanel.setDividerLocation(2.0 / 3.0);
            }
        });
        add(splitPanel,BorderLayout.CENTER);
    }


    public void actionPerformed(ActionEvent ae){
        u = new User();
        if((ae.getSource() == b) && (!connected) && tfn.getText().equals("")){
            JOptionPane.showMessageDialog(this,"Please enter your name!");
        }
        else{
            if((ae.getSource() == b) && (!connected)){
                try{
                    //s = new Socket("afsaccess2.njit.edu", 10323);
                    s = new Socket("127.0.0.1", 10323);
                    os = new ObjectOutputStream(s.getOutputStream());
                    is = new ObjectInputStream(s.getInputStream());
                    tfn.setEditable(false);
                }catch(UnknownHostException uhe){
                    System.out.println(uhe.getMessage());
                }catch(IOException ioe){
                    System.out.println(ioe.getMessage());
                }
                connected = true;
                t = new Thread(this);
                b.setLabel("Disconnect");
                t.start();
                u.setName(tfn.getText());
                u.setMessage(" has entered(System Message)");
                //u.setUserList(online);
                try {
                    os.writeObject(u);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if((ae.getSource() == b) && (connected)){
                connected = false;
                tfn.setEditable(true);
                ArrayList<String> clear = new ArrayList<String>();//clear userlist
                clear.add("");
                list.setListData(clear.toArray());
                jta.setText("");
                try{
                    u.setName(tfn.getText());
                    u.setMessage(" has left(System Message)");
                    u.setFlag("exit");
                    os.writeObject(u);
                    os.flush();
                    os.close();
                    s.close(); //no buffering so, ok
                }catch(IOException ioe){
                    System.out.println(ioe.getMessage());
                }
                b.setLabel("Connect");
            }else if((connected) && ae.getSource() == send){
                u.setMessage(tf.getText());
                u.setName(tfn.getText());
                u.setFlag("client");
                System.out.println(u.getMessage());
                try {
                    os.writeObject(u);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tf.setText("");
            }
            else if((connected) && ae.getSource() == clear){
                jta.setText("");
            }
            else if((connected) && ae.getSource() == history){
                readTxtFile("F:\\JAVA_workspace\\CRClient\\"+tfn.getText()+"'s history.txt");
            }
            else if((connected) && ae.getSource() == save){
                System.out.println(jta.getText());
                writerText(tfn.getText(),jta.getText());//writeObjectToFile(jta.getText(), tfn.getName());
            }
        }
    }
    public void run(){
        try{
            while(true){
                u = (User)is.readObject();
                System.out.println(u.getFlag());
                if(u.getFlag().trim().equals("client")){
                    String line = u.getName()+":"+u.getMessage();
                    jta.append(line+"\n");
                    //writeObjectToFile(u,u.getName());

                }
                else{
                    DefaultListModel<String> model = new DefaultListModel<String>();
                    for(String s : u.getUserList()){
                        model.addElement(s);
                    }
                    list.setModel(model);
                    String line = u.getName()+":"+u.getMessage();
                    jta.append(line+"\n");
                    for(String s : u.getUserList()){
                        System.out.println(s);
                    }
                }
            }
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }catch(ClassNotFoundException e){
            System.out.println("Can't find DataObject.");
        }
    }

    public static void writerText(String name, String content) {

        File dirFile = new File(name+"'s history.txt");

        try {
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(dirFile),"gbk");
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(dirFile, true));
            bw1.write(content);
            bw1.flush();
            bw1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void readTxtFile(String filePath){
        try {
            String encoding="GBK";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    jta.append(lineTxt+"\n");;
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }
}
