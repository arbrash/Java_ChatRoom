package server;

import java.io.*;
import java.net.*;
import java.util.*;

import User.User;

public class ChatHandler extends Thread{
    Socket s;
    ArrayList<ChatHandler>handlers;
    ObjectInputStream is = null;
    ObjectOutputStream os = null;
    User u;
    ArrayList<String> online;
    ArrayList<String> temp;
    FileOutputStream fos = null;
    
    public ChatHandler(Socket s, ArrayList<ChatHandler>handlers,ArrayList<String> online){		
        this.s = s;
        this.handlers = handlers;
        this.online = online;
        try{
        	is = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
            os = new ObjectOutputStream(s.getOutputStream());
        }catch(IOException e){
    	    System.out.println("Couldn't create IO streams.");
        }
    }
    
    
    public void run(){
	    try{
            handlers.add(this);
            System.out.println("Number of clients:"+handlers.size());
            while(true){
            	ArrayList<String> temp = new ArrayList<String>(online);
            	u=(User)is.readObject();
        	    System.out.println("flag "+u.getFlag());
        	    if(u.getFlag().equals("new")){
        	    	online.add(u.getName());
        	    	temp.add(u.getName());
        	    	u.setUserList(temp);
        	    	tellEveryone(u);
        	    }
        	    else if(u.getFlag().equals("client"))
        	        tellEveryone(u);
        	    writeObjectToFile(u,u.getName());  
            }
            }catch(IOException e){
    	        System.out.println("Connection Terminated from: "+s.getInetAddress());
            }catch(ClassNotFoundException e){
            }finally {
                handlers.remove(this);
                online.remove(u.getName());
                u.setUserList(online);
                tellEveryone(u);
            }
    }
    public void tellEveryone(User u){
    	try {
    		synchronized(handlers){
    			for (ChatHandler ch : handlers){// broadcast to all clients
    				synchronized(ch.os){
    			    ch.os.writeObject(u);
               	    ch.os.flush();
    				}
                }
    		}
            System.out.println(u.getName() +" says "+ u.getMessage());
        } catch (IOException e) {
        	System.out.println("Someone has left");
	    }
    }
    public static void writeObjectToFile(Object obj,String name)
    {
        File file =new File(name+"'s test.txt");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file,true);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();
        }
    }
}
