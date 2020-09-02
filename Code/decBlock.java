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
    
    // Read Block #b, Sector #s
    public ArrayList<String> read(int block, int sector){
        System.out.println("Read Block #"+block);
        ArrayList<String>response = new ArrayList<String>();
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
            
            // authenticate with right key (in this case Key A)
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
            
            // reads the block 
            byte[] c_read = { (byte) 0xFF, (byte) 0xB0, (byte) 0x00, (byte) b , (byte) 0x10};
	    CommandAPDU read = new CommandAPDU(c_read);
	    ResponseAPDU rread = channel.transmit(read);
	    String data = arrayToHex(rread.getBytes());
	    if(data.equals("6982")){
	      	response.add("Addr #04: AUTEN ERROR");
		data = "ERROR";
            }
            else{
		data = data.substring(0, 32);
	        response.add("Addr #04: "+formatedData(data));
            }
	    if(load.equals("ERROR")||auten.equals("ERROR")||data.equals("ERROR"))
                return response;
        }
        catch(Exception e){
        }
        return response;
    }
    
    // Write Block #b, Sector #s
    public void write(byte[] newData, int block, int sector){
        System.out.println("Write Block #"+block);
        ArrayList<String>response = new ArrayList<String>();
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
                (byte) 0x01, (byte) 0x00, (byte) b, (byte) 0x60, (byte) 0x00};
            CommandAPDU auth = new CommandAPDU(c_auth);
            ResponseAPDU rauth = channel.transmit(auth);
            String auten = arrayToHex(rauth.getBytes()); 
            if(auten.equals("9000"))
                auten = "Authentication: "+auten+" OK";
            else
		auten = "Authentication: "+auten+" ERROR";
            System.out.println(auten);
            
            // writes the block 
            byte[] c_write = { (byte) 0xFF, (byte) 0xD6, (byte) 0x00, (byte) b , (byte)0x10,
                                newData[15], newData[14], newData[13],newData[12],
                                newData[11], newData[10], newData[9],newData[8],
                                newData[7], newData[6], newData[5],newData[4],
                                newData[3], newData[2], newData[1],newData[0]};
	    CommandAPDU write = new CommandAPDU(c_write);
	    ResponseAPDU rwrite = channel.transmit(write);
	    String data = arrayToHex(rwrite.getBytes());
	    if(data.equals("6982")){
	      	System.out.println("Addr #04: AUTEN ERROR");
		data = "ERROR";
            }
            else{
		data = data.substring(0, 32);
	        System.out.println("Addr #04: "+formatedData(data));
            }
            System.out.println(data);
        }
        catch(Exception e){
        }
    }
       
    // Decrement Value to Block 
    public void decValue(byte[] val, int block, int sector){
        System.out.println("Decrement Value to Block #"+block);
        ArrayList<String>response = new ArrayList<String>();
        try{
            // show the list of available terminals
	    TerminalFactory factory = TerminalFactory.getDefault();
	    List <CardTerminal> terminals = factory.terminals().list();
	    this.terminal =terminals.get(0);  
	           
            // establish a connection with the card
	    Card card = terminal.connect("T=1");
	    this.channel = card.getBasicChannel();
            
            // load the key for sector #01 -> Get challenge command
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
            
            // authenticate with right key
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
            
            // Decrement Value to Block #b
            byte[] c_valueAdd = { (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x05, (byte) 0x06,  
                                  (byte) 0xC0, (byte) 0x05, val[3], val[2], val[1], val[0]};

            CommandAPDU addValue = new CommandAPDU(c_valueAdd);
	    ResponseAPDU rvalueAdd = channel.transmit(addValue);
	    String data = arrayToHex(rvalueAdd.getBytes());
            System.out.println(data);
	    if(data.equals("6982")){
	      	System.out.println("Addr #05: AUTEN ERROR");
            }
            else{
		data = data.substring(0, 32);
	        System.out.println("Addr #05: "+formatedData(data));
            }
            System.out.println(data);
        }
        catch(Exception e){
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
	       
        /* Prepare block as value block
        byte[] newData = {(byte)0xFA,(byte)0x05,(byte)0xFA,(byte)0x05,
                          (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                          (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
                          (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};
                
        test.write(newData,block,sector); */
        
        output = test.read(block,sector);
	for(int i=0; i<output.size();i++){
	    System.out.println(output.get(i));
	}
        System.out.println("**********************************************************");
        
        byte[] Dec = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01};
        test.decValue(Dec,block,sector);
                
        output = test.read(block,sector);
	for(int i=0; i<output.size();i++){
	    System.out.println(output.get(i));
	}
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
