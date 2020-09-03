package classicpackage;

import java.util.ArrayList;
import java.util.List;
import javax.smartcardio.*;

/**
 * @author Tere Varano
 * @version 1.1
 * @since 2020/08/31
 */
public class App {
    
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
    public void setTerminals(final List <CardTerminal> terminals){
        this.terminals = terminals;
    }
    
    public void setTerminal(final CardTerminal terminal){
        this.terminal = terminal;
    }
    
    public void setATR(final String atr){
        this.rATR = atr;
    }
			   
    public void setUID(final String uid){
        this.rUID = uid;
    }
      
    // Constructor
    public App(){
	    try{
            // show the list of available terminals
            final TerminalFactory factory = TerminalFactory.getDefault();
            terminals = factory.terminals().list();
            this.terminal =terminals.get(0);  
		           
            // establish a connection with the card
            final Card card = terminal.connect("T=1");
            this.channel = card.getBasicChannel();
	           
            // get ATR
            final ATR atr = card.getATR();
            rATR = arrayToHex(atr.getBytes());
            setATR(rATR);
            
            // read UID(4 bytes)+BCC
            final byte[] c_readUID = { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x04};
	         final CommandAPDU readUID = new CommandAPDU(c_readUID);
            final ResponseAPDU r = channel.transmit(readUID);
            rUID = arrayToHex(r.getBytes());
            rUID = rUID.substring(0, 8);
            setUID(rUID);
			
        }catch (final Exception e){
        }
    }
    
    // Wrong Authentication
    public void wrongAuthen(final int block){
        System.out.println("Wrong Authentication");
	    try{
            // show the list of available terminals
	        final TerminalFactory factory = TerminalFactory.getDefault();
	        final List <CardTerminal> terminals = factory.terminals().list();
	        this.terminal =terminals.get(0);  
	           
            // establish a connection with the card
	        final Card card = terminal.connect("T=1");
	        this.channel = card.getBasicChannel();
            
            // load the key -> Get challenge command
            final byte[] c_loadKey = { (byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06, 
                    			        (byte)0x55, (byte)0x44, (byte)0x33, 
                                        (byte)0x22, (byte)0x11, (byte)0x00};
            final CommandAPDU loadKey = new CommandAPDU(c_loadKey);
            final ResponseAPDU res = channel.transmit(loadKey);
            String load = arrayToHex(res.getBytes());
            if(load.equals("9000"))
                load = "Load: "+load+" OK";
	        else
	            load = "Load: "+load+" ERROR";
            System.out.println(load);
            
            // authenticate with wrong key (in this case Key A)
            final byte b = getByte(block);  
            final byte[] c_auth = { (byte) 0xFF, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05,
                                    (byte) 0x01, (byte) 0x00, (byte) b, (byte) 0x60, (byte) 0x00};
            final CommandAPDU auth = new CommandAPDU(c_auth);
            final ResponseAPDU rauth = channel.transmit(auth);
            String auten = arrayToHex(rauth.getBytes()); 
            if(auten.equals("9000"))
                auten = "Authentication: "+auten+" OK";
            else
		        auten = "Authentication: "+auten+" ERROR";
            System.out.println(auten);
	    }catch (final Exception e){
	    }
    }
    
    // Right Authentication
    public void rightAuthen(final int block){
        System.out.println("Right Authentication");
        try{
            // show the list of available terminals
	        final TerminalFactory factory = TerminalFactory.getDefault();
	        final List <CardTerminal> terminals = factory.terminals().list();
	        this.terminal =terminals.get(0);  
	           
            // establish a connection with the card
	        final Card card = terminal.connect("T=1");
	        this.channel = card.getBasicChannel();
	  	                
            // load the key -> Get challenge command
            final byte[] c_loadKey = {(byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06,
                                    keyRight[5], keyRight[4], keyRight[3],
                                    keyRight[2], keyRight[1], keyRight[0]};
            final CommandAPDU loadKey = new CommandAPDU(c_loadKey);
            final ResponseAPDU res = channel.transmit(loadKey);
            String load = arrayToHex(res.getBytes());
            if(load.equals("9000"))
                load = "Load: "+load+" OK";
	        else
	            load = "Load: "+load+" ERROR";
            System.out.println(load);
            
            // authenticate with right key (in this case Key B)
            final byte b = getByte(block);  
            final byte[] c_auth = { (byte) 0xFF, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05,
                                (byte) 0x01, (byte) 0x00, (byte) b, (byte) 0x61, (byte) 0x00};
            final CommandAPDU auth = new CommandAPDU(c_auth);
            final ResponseAPDU rauth = channel.transmit(auth);
            String auten = arrayToHex(rauth.getBytes()); 
            if(auten.equals("9000"))
                auten = "Authentication: "+auten+" OK";
            else
		        auten = "Authentication: "+auten+" ERROR";
            System.out.println(auten);
        }catch (final Exception e){
	    }
    }
    
    // Read Block #b, Sector #s
    public ArrayList<String> read(final int block, final int sector){
        System.out.println("Read Block #"+block);
        final ArrayList<String>response = new ArrayList<String>();
        try{
            // show the list of available terminals
	        final TerminalFactory factory = TerminalFactory.getDefault();
	        final List <CardTerminal> terminals = factory.terminals().list();
	        this.terminal =terminals.get(0);  
	           
            // establish a connection with the card
	        final Card card = terminal.connect("T=1");
	        this.channel = card.getBasicChannel();
	  	                
            // load the key -> Get challenge command
            final byte[] c_loadKey = {(byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06,
                                    keyRight[5], keyRight[4], keyRight[3],
                                    keyRight[2], keyRight[1], keyRight[0]};
            final CommandAPDU loadKey = new CommandAPDU(c_loadKey);
            final ResponseAPDU res = channel.transmit(loadKey);
            String load = arrayToHex(res.getBytes());
            if(load.equals("9000"))
                load = "Load: "+load+" OK";
	        else
	            load = "Load: "+load+" ERROR";
            System.out.println(load);
            
            // authenticate with right key (in this case Key A)
            final byte b = getByte(block);  
            final byte[] c_auth = { (byte) 0xFF, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05,
                                (byte) 0x01, (byte) 0x00, (byte) b, (byte) 0x60, (byte) 0x00};
            final CommandAPDU auth = new CommandAPDU(c_auth);
            final ResponseAPDU rauth = channel.transmit(auth);
            String auten = arrayToHex(rauth.getBytes()); 
            if(auten.equals("9000"))
                auten = "Authentication: "+auten+" OK";
            else
		        auten = "Authentication: "+auten+" ERROR";
            System.out.println(auten);
            
            // reads the block 
            final byte[] c_read = { (byte) 0xFF, (byte) 0xB0, (byte) 0x00, (byte) b , (byte) 0x10};
	        final CommandAPDU read = new CommandAPDU(c_read);
	        final ResponseAPDU rread = channel.transmit(read);
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
        catch(final Exception e){
        }
        return response;
    }
    
    // Write Block #b, Sector #s
    public void write(final byte[] newData, final int block, final int sector){
        System.out.println("Write Block #"+block);
        //final ArrayList<String> output = new ArrayList<>();
        try{
            // show the list of available terminals
	        final TerminalFactory factory = TerminalFactory.getDefault();
	        final List <CardTerminal> terminals = factory.terminals().list();
	        this.terminal =terminals.get(0);  
	           
            // establish a connection with the card
	        final Card card = terminal.connect("T=1");
	        this.channel = card.getBasicChannel();
            
            // load the key -> Get challenge command
            final byte[] c_loadKey = {(byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06,
                                        keyRight[5], keyRight[4], keyRight[3],
                                        keyRight[2], keyRight[1], keyRight[0]};
            final CommandAPDU loadKey = new CommandAPDU(c_loadKey);
            final ResponseAPDU res = channel.transmit(loadKey);
            String load = arrayToHex(res.getBytes());
            if(load.equals("9000"))
                load = "Load: "+load+" OK";
	        else
	            load = "Load: "+load+" ERROR";
            System.out.println(load);
            
            // authenticate with right key (in this case Key B)
            final byte b = getByte(block);  
            final byte[] c_auth = { (byte) 0xFF, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05,
                                        (byte) 0x01, (byte) 0x00, (byte) b, (byte) 0x60, (byte) 0x00};
            final CommandAPDU auth = new CommandAPDU(c_auth);
            final ResponseAPDU rauth = channel.transmit(auth);
            String auten = arrayToHex(rauth.getBytes()); 
            if(auten.equals("9000"))
                auten = "Authentication: "+auten+" OK";
            else
		        auten = "Authentication: "+auten+" ERROR";
            System.out.println(auten);
            
            // writes the block 
            final byte[] c_write = { (byte) 0xFF, (byte) 0xD6, (byte) 0x00, (byte) b , (byte)0x10,
                                newData[15], newData[14], newData[13],newData[12],
                                newData[11], newData[10], newData[9],newData[8],
                                newData[7], newData[6], newData[5],newData[4],
                                newData[3], newData[2], newData[1],newData[0]};
	        final CommandAPDU write = new CommandAPDU(c_write);
	        final ResponseAPDU rwrite = channel.transmit(write);
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
        catch(final Exception e){
        }
    }
    
    // Increment Value to Block 
    public void incValue(final byte[] val, final int block, final int sector){
        System.out.println("Increment Value to Block #"+block);
        //final ArrayList<String>response = new ArrayList<String>();
        try{
            // show the list of available terminals
	        final TerminalFactory factory = TerminalFactory.getDefault();
	        final List <CardTerminal> terminals = factory.terminals().list();
	        this.terminal =terminals.get(0);  
	           
            // establish a connection with the card
	        final Card card = terminal.connect("T=1");
	        this.channel = card.getBasicChannel();
            
            // load the key for sector #01 -> Get challenge command
            final byte[] c_loadKey = {(byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06,
                                            keyRight[5], keyRight[4], keyRight[3],
                                            keyRight[2], keyRight[1], keyRight[0]};
            final CommandAPDU loadKey = new CommandAPDU(c_loadKey);
            final ResponseAPDU res = channel.transmit(loadKey);
            String load = arrayToHex(res.getBytes());
            if(load.equals("9000"))
                load = "Load: "+load+" OK";
	        else
	            load = "Load: "+load+" ERROR";
            System.out.println(load);
            
            // authenticate with right key
            final byte b = getByte(block);  
            final byte[] c_auth = { (byte) 0xFF, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05,
                                        (byte) 0x01, (byte) 0x00, (byte) b, (byte) 0x60, (byte) 0x00};
            final CommandAPDU auth = new CommandAPDU(c_auth);
            final ResponseAPDU rauth = channel.transmit(auth);
            String auten = arrayToHex(rauth.getBytes()); 
            if(auten.equals("9000"))
                auten = "Authentication: "+auten+" OK";
            else
		        auten = "Authentication: "+auten+" ERROR";
            System.out.println(auten);
            
            // Increment Value to Block #b
            final byte[] c_valueAdd = { (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x05, (byte) 0x06,  
                                  (byte) 0xC1, (byte) 0x05, val[3], val[2], val[1],
                                  val[0]};

            final CommandAPDU addValue = new CommandAPDU(c_valueAdd);
	        final ResponseAPDU rvalueAdd = channel.transmit(addValue);
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
        catch(final Exception e){
        }
    }
    
    // Decrement Value to Block 
    public void decValue(final byte[] val, final int block, final int sector){
        System.out.println("Decrement Value to Block #"+block);
        //final ArrayList<String>response = new ArrayList<String>();
        try{
            // show the list of available terminals
	        final TerminalFactory factory = TerminalFactory.getDefault();
	        final List <CardTerminal> terminals = factory.terminals().list();
	        this.terminal =terminals.get(0);  
	           
            // establish a connection with the card
	        final Card card = terminal.connect("T=1");
	        this.channel = card.getBasicChannel();
            
            // load the key for sector #01 -> Get challenge command
            final byte[] c_loadKey = {(byte) 0xFF, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06,
                                            keyRight[5], keyRight[4], keyRight[3],
                                            keyRight[2], keyRight[1], keyRight[0]};
            final CommandAPDU loadKey = new CommandAPDU(c_loadKey);
            final ResponseAPDU res = channel.transmit(loadKey);
            String load = arrayToHex(res.getBytes());
            if(load.equals("9000"))
                load = "Load: "+load+" OK";
	        else
	            load = "Load: "+load+" ERROR";
            System.out.println(load);
            
            // authenticate with right key
            final byte b = getByte(block);  
            final byte[] c_auth = { (byte) 0xFF, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05,
                                        (byte) 0x01, (byte) 0x00, (byte) b, (byte) 0x60, (byte) 0x00};
            final CommandAPDU auth = new CommandAPDU(c_auth);
            final ResponseAPDU rauth = channel.transmit(auth);
            String auten = arrayToHex(rauth.getBytes()); 
            if(auten.equals("9000"))
                auten = "Authentication: "+auten+" OK";
            else
		        auten = "Authentication: "+auten+" ERROR";
            System.out.println(auten);
            
            // Decrement Value to Block #b
            final byte[] c_valueAdd = { (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x05, (byte) 0x06,  
                                  (byte) 0xC0, (byte) 0x05, val[3], val[2], val[1], val[0]};

            final CommandAPDU addValue = new CommandAPDU(c_valueAdd);
	        final ResponseAPDU rvalueAdd = channel.transmit(addValue);
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
        catch(final Exception e){
        }
    }
    
    // Run & Debug
    public static void main(final String[] args) {
        final App test = new App();
        System.out.println("Terminals: "+test.terminals);
        System.out.println("Terminal seleccionado: "+test.terminal);
        System.out.println("UID: 0x"+test.rUID);
        System.out.println("**********************************************************");
        
        int block = 5;
        final int sector = 1;
        
        test.wrongAuthen(block);
        System.out.println("**********************************************************");
        
        test.rightAuthen(block);
        System.out.println("**********************************************************");
        
        ArrayList<String>output = test.read(block,sector);
	    for(int i=0; i<output.size();i++){
	        System.out.println(output.get(i));
	    }
        System.out.println("**********************************************************");
	
        /*byte[] newData = {(byte)0x0F,(byte)0x0E,(byte)0x0D,(byte)0x0C,
                          (byte)0x0B,(byte)0x0A,(byte)0x09,(byte)0x08,
                          (byte)0x07,(byte)0x06,(byte)0x05,(byte)0x04,
                          (byte)0x03,(byte)0x02,(byte)0x01,(byte)0x00};*/
        
        // Prepare block as value block
        final byte[] newData = {(byte)0xFA,(byte)0x05,(byte)0xFA,(byte)0x05,
                          (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                          (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
                          (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};
        
        // Modify sector trailer                  
        /*byte[] newData = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
                          (byte)0xFF,(byte)0xFF,(byte)0x8E,(byte)0x77,
                          (byte)0x18,(byte)0x69,(byte)0xFF,(byte)0xFF,
                          (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};*/
        
        test.write(newData,block,sector); 
        
        output = test.read(block,sector);
	    for(int i=0; i<output.size();i++){
	        System.out.println(output.get(i));
	    }
        System.out.println("**********************************************************");
        
        block = 5;
        final byte[] Inc = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x64};
        test.incValue(Inc,block,sector);
                
        output = test.read(block,sector);
	    for(int i=0; i<output.size();i++){
	        System.out.println(output.get(i));
	    }
        System.out.println("**********************************************************");
        
        final byte[] Dec = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01};
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
	    final byte b = (byte)i; 
	    return b;
    }

    private String formatedData(final String data) {
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
    
    private String arrayToHex(final byte[] data) {
        final StringBuffer sb = new StringBuffer();
        for(int i=0 ; i<data.length; i++){
            final String bs = Integer.toHexString(data[i] & 0xFF);
            if (bs.length() == 1){
                sb.append(0);
            }
            sb.append(bs);
        }
	    return sb.toString();
    }
}

