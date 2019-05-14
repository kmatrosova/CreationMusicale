import java.io.*;
import javax.sound.midi.*;

public class TextToMidi_monoChannel
{
	public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    
    public static void main(String[] args) throws Exception
    {
    	try
        {
        	String fileName = "resultsFromTraining.txt";
            FileInputStream fileInputStream = new FileInputStream(fileName); 
            
            try (InputStreamReader inputStreamReader = 
              new InputStreamReader(fileInputStream, "UTF-8")) {
            	
            	/****  Create a new MIDI sequence with 24 ticks per beat  ****/
        		Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ,500);
        		
        		/****  Obtain a MIDI track from the sequence  ****/
        		Track t = s.createTrack();

        		/****  General MIDI sysex -- turn on General MIDI sound set  ****/
        		byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
        		SysexMessage sm = new SysexMessage();
        		sm.setMessage(b, 6);
        		MidiEvent me = new MidiEvent(sm,(long)0);
        		t.add(me);
        		
        		/****  set tempo (meta event)  ****/
        		MetaMessage mt = new MetaMessage();
                byte[] bt = {0x02, (byte)0x00, 0x00};
        		mt.setMessage(0x51 ,bt, 3);
        		me = new MidiEvent(mt,(long)0);
        		t.add(me);

        		/****  set track name (meta event)  ****/
        		mt = new MetaMessage();
        		String TrackName = new String("midifile track");
        		mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
        		me = new MidiEvent(mt,(long)0);
        		t.add(me);

        		/****  set omni on  ****/
        		ShortMessage mm = new ShortMessage();
        		mm.setMessage(0xB0, 0x7D,0x00);
        		me = new MidiEvent(mm,(long)0);
        		t.add(me);

        		/****  set poly on  ****/
        		mm = new ShortMessage();
        		mm.setMessage(0xB0, 0x7F,0x00);
        		me = new MidiEvent(mm,(long)0);
        		t.add(me);

        		/****  set instrument to Piano  ****/
        		mm = new ShortMessage();
        		mm.setMessage(0xC0, 0x00, 0x00);
        		me = new MidiEvent(mm,(long)0);
        		t.add(me);
            	
              int velocity=0, tick=0;

              int c=0;
              char word[]= new char[20];
              char word2[] = new char[20];
              int i=0, j=0;
              int verif=0;
              
              while(c != -1 && ((char) c != '\n')) {  
 
                do {
        			c = inputStreamReader.read();
        			word[i] = (char) c;
        			i++;
        		} while(((char) c)!=('>') && i<19);
                String ligne = new String(word);
                ligne = ligne.trim();
                
                
                for(i=0; i<20; i++) {
                	if (verif == 1) {
                		if(word[i] == '>') verif=0;
                		else {
                			word2[j]=word[i];
                			j++;
                		}
                	}else if(word[i] == '<') verif=1;	
                	
                }
                if (j==0) ligne="";
                String number = new String(word2);
                
                if(!ligne.equals("")) {
                	//System.out.println(ligne.substring(0,2));
                    number = number.trim();
                    int result = Integer.parseInt(number);
                    
                    if(ligne.substring(0,2).equals("SE")) {
                    	velocity = result;
                    	
                    } 
                    else if(ligne.substring(0,2).equals("SH")) {
                    	tick = tick+result;
                    } 
                    else if(ligne.substring(0,2).equals("ON")) {
                    	//  note on - middle C 
                		mm = new ShortMessage();
                		mm.setMessage(NOTE_ON,1,result,velocity);
                		me = new MidiEvent(mm,tick);
                		t.add(me);
                    } 
                    else if(ligne.substring(0,2).equals("OF")) {
                    	//  note off - middle C - 120 ticks later
                		mm = new ShortMessage();
                		mm.setMessage(ShortMessage.NOTE_OFF, 0, result, 0);
                		me = new MidiEvent(mm,tick);
                		t.add(me);
                    }
                }
        		
        		i=0;
        		j=0;
        		word = new char[20];
        		word2 = new char[20];
     
              }
              
	            //****  set end of track (meta event) 19 ticks later  ****
	      		mt = new MetaMessage();
	              byte[] bet = {}; // empty array
	      		mt.setMessage(0x2F,bet,0);
	      		me = new MidiEvent(mt, (long)140);
	      		t.add(me);
	
	      		//****  write the MIDI sequence to a MIDI file  ****
	      		File f = new File("midifile.mid");
	      		MidiSystem.write(s,1,f);
	      		System.out.println("midi file created with success");
            }
            
        	

        } catch(FileNotFoundException ex) {
            System.out.println("Unable to open file");
        }
        catch(IOException ex) {
            System.out.println("Error reading file");
        }

    }
}
