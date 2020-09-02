/*
 * The content of this project itself is licensed under the Creative Commons }
 * Attribution 3.0 Unported license, and the underlying source code used to 
 * format and display that content is licensed under the GNU License
 */
package com.arin.pract01;

import java.util.ArrayList;
import java.util.List;
import javax.smartcardio.*;

/**
 * @author Tere Varano
 * @version 1.1
 * @since 2020/08/31
 */
public class Main {
    
    // Fields
    byte[] keyWrong = {(byte)0xA5,(byte)0xA4,(byte)0xA3,
                    (byte)0xA2,(byte)0xA1,(byte)0xA0};
    
    byte[] keyRight = {(byte)0xFF,(byte)0xFF,(byte)0xFF,
                    (byte)0xFF,(byte)0xFF,(byte)0xFF};

    List <CardTerminal> terminals;
    CardChannel channel;
    CardTerminal terminal;
    
    String rATR;
    String rUID;
    
    // Methods
    public void setTerminals(List <CardTerminal> terminals){
        this.terminals = terminals;
    }
    
    public void setTerminal(CardTerminal terminal){
        this.terminal = terminal;
    }
    
    public void setATR(String atr){
        this.rATR = atr;
    }
			   
    public void setUID(String uid){
        this.rUID = uid;
    }
      
    // Constructor
    public Main(){
	try{
            // show the list of available terminals
            TerminalFactory factory = TerminalFactory.getDefault();
            terminals = factory.terminals().list();
            this.terminal =terminals.get(0);  
		           
            // establish a connection with the card
            Card card = terminal.connect("T=1");
            this.channel = card.getBasicChannel();
	           
            // get ATR
            ATR atr = card.getATR();
            rATR = arrayToHex(atr.getBytes());
            setATR(rATR);
            
            // read UID(4 bytes)+BCC
            byte[] c_readUID = { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x04};
	    CommandAPDU readUID = new CommandAPDU(c_readUID);
            ResponseAPDU r = channel.transmit(readUID);
            rUID = arrayToHex(r.getBytes());
            rUID = rUID.substring(0, 8);
            setUID(rUID);
			
            }catch (Exception e){
            }
    }
    
    // Wrong Authentication
    public void wrongAuthen(int block){
        System.out.println("Wrong Authentication");
	try{
            // show the list of available terminals
	    TerminalFactory factory = TerminalFactory.getDefault();
	    List <CardTerminal> terminals = factory.terminals().list();
	    this.terminal =terminals.get(0);  
	           
            // establish a connection with the card
	    Card card = terminal.connect("T=1");
	    this.channel = card.getBasicChannel();
            
            // load the key -> Get challenge command
            byte[] c_loadKey = { (byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06, 
                    			(byte)0x55, (byte)0x44, (byte)0x33, 
                                        (byte)0x22, (byte)0x11, (byte)0x00};
            CommandAPDU loadKey = new CommandAPDU(c_loadKey);
            ResponseAPDU res = channel.transmit(loadKey);
            String load = arrayToHex(res.getBytes());
            if(load.equals("9000"))
                load = "Load: "+load+" OK";
	    else
	        load = "Load: "+load+" ERROR";
            System.out.println(load);
            
            // authenticate with wrong key (in this case Key A)
            byte b = getByte(block);  
            byte[] c_auth = { (byte) 0xFF, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05,
                (byte) 0x01, (byte) 0x00, (byte) b, (byte) 0x60, (byte) 0x00};
            CommandAPDU auth = new CommandAPDU(c_auth);
            ResponseAPDU rauth = channel.transmit(auth);
            String auten = arrayToHex(rauth.getBytes()); 
            if(auten.equals("9000"))
                auten = "Authentication: "+auten+" OK";
            else
		auten = "Authentication: "+auten+" ERROR";
            System.out.println(auten);
	}catch (Exception e){
	}
    }
    
    // Right Authentication
    public void rightAuthen(int block){
        System.out.println("Right Authentication");
        try{
            // show the list of available terminals
	    TerminalFactory factory = TerminalFactory.getDefault();
	    List <CardTerminal> terminals = factory.terminals().list();
	    this.terminal =terminals.get(0);  
	           
            // establish a connection with the card
	    Card card = terminal.connect("T=1");
	    this.channel = card.getBasicChannel();
	  	                
            // load the key -> Get challenge command
            byte[] c_loadKey = {(byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06,
                keyRight[5], keyRight[4], keyRight[3],
                keyRight[2], keyRight[1], keyRight[0]};
            CommandAPDU loadKey = new CommandAPDU(c_loadKey);
            ResponseAPDU res = channel.transmit(loadKey);
            String load = arrayToHex(res.getBytes());
            if(load.equals("9000"))
                load = "Load: "+load+" OK";
	    else
	        load = "Load: "+load+" ERROR";
            System.out.println(load);
            
            // authenticate with right key (in this case Key B)
            byte b = getByte(block);  
            byte[] c_auth = { (byte) 0xFF, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05,
                (byte) 0x01, (byte) 0x00, (byte) b, (byte) 0x61, (byte) 0x00};
            CommandAPDU auth = new CommandAPDU(c_auth);
            ResponseAPDU rauth = channel.transmit(auth);
            String auten = arrayToHex(rauth.getBytes()); 
            if(auten.equals("9000"))
                auten = "Authentication: "+auten+" OK";
            else
		auten = "Authentication: "+auten+" ERROR";
            System.out.println(auten);
        }catch (Exception e){
	}
    }
    
    // Run & Debug
    public static void main(String[] args) {
        Main test = new Main();
        System.out.println("Terminals: "+test.terminals);
        System.out.println("Terminal seleccionado: "+test.terminal);
        System.out.println("UID: 0x"+test.rUID);
        System.out.println("**********************************************************");
        
        int block = 5;
        int sector = 1;
        
        test.wrongAuthen(block);
        System.out.println("**********************************************************");
        
        test.rightAuthen(block);
        System.out.println("**********************************************************");
               
    }    
    
    
    // Other functions
    private byte getByte(int i){
	if (i > 127){
            i = i - 256;
	}
	byte b = (byte)i; 
	return b;
    }

    private String formatedData(String data) {
	int j = 0;
	String d = "";
	for(int i=0; i<16; i++){
            d = d + data.substring(j, j+2);
            d = d + " ";
            j = j + 2;
	}
	d = d.toUpperCase();
	return d;
    }
    
    private String arrayToHex(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for(int i=0 ; i<data.length; i++){
            String bs = Integer.toHexString(data[i] & 0xFF);
            if (bs.length() == 1){
                sb.append(0);
            }
            sb.append(bs);
        }
	return sb.toString();
    }
}
