package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer{
  Socket s;
  ArrayList<ChatHandler>handlers;
  ArrayList<String> online;
  public ChatServer(){
	  
    try{
      ServerSocket ss = new ServerSocket(10323);
      handlers = new ArrayList<ChatHandler>();
      online = new ArrayList<String>();
      for(;;){
	s = ss.accept();
	new ChatHandler(s, handlers, online).start();
      }
    }catch(IOException ioe){
      System.out.println(ioe.getMessage());
    }
  }
  public static void main(String[] args){
    ChatServer tes = new ChatServer(); 
  }
  
}
