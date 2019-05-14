import java.io.*;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;


public class MidiToText_monoChannel {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    //public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static void main(String[] args) throws Exception {
    	
    	File file = new File(System.getProperty("user.dir")+"/midi_files/");
    	File[] files = file.listFiles();
        File file_results = new File(System.getProperty("user.dir")+"/text_files/");
        int i = 1;
        if (!file_results.exists())
        	file_results.mkdir();
        else {
        	for(File file2: file_results.listFiles())
        		if (!file2.isDirectory()) file2.delete();
        }
    	for(File f: files){
          try (Writer writer = new BufferedWriter(new OutputStreamWriter(
        		  new FileOutputStream(System.getProperty("user.dir") + "/text_files/" + i + ".txt"), "utf-8"))) {
        	  		writer.write("File name: " + f.getName() + "\n\n" + read_midi_file(f));
          }
          System.out.println("File \"" + i + ".txt\" from " + f.getName() + " was created");
          i++;
    	}
    }

    public static String read_midi_file(File file) throws Exception {
    	
    	int v = 0;
    	long tt = 0;
    	String res = "";
    	Sequence sequence = MidiSystem.getSequence(file);
        ArrayList<Ligne> lignes = new ArrayList<Ligne>();
        Ligne ligne;

    	for (Track track :  sequence.getTracks()) {

    	    for (int i=0; i < track.size(); i++) {
        		MidiEvent event = track.get(i);
        		MidiMessage message = event.getMessage();

        		if (message instanceof ShortMessage) {
        		    ShortMessage sm = (ShortMessage) message;

        		    if (sm.getCommand() == NOTE_ON) {
        		    	long t = event.getTick();
            			int key = sm.getData1();
            			int velocity = sm.getData2();
	
            			ligne = new Ligne(t, key, velocity);
            			lignes.add(ligne);
        		    }
            }
          }
      }

      ArrayList<Ligne> l = sortByTick(lignes);

      for(int j=0; j<lignes.size(); j++) {
        
        String noteStatus;
        if (l.get(j).getVelocity() > 0) noteStatus = "ON"; else noteStatus = "OFF";

        long shift = l.get(j).getTick()-tt;
        if(shift != 0) {
          res += "SHIFT<" + shift + ">";
          tt = l.get(j).getTick();
        }

        if (noteStatus.equals("ON")) {
          if(v != l.get(j).getVelocity()) {
            v = l.get(j).getVelocity();
            res += "SET_V<" + v + ">";
          }
          res += "ON<" + l.get(j).getKey() + ">";
        }
        else res += "OFF<" + l.get(j).getKey() + ">";
      }
      return res;
    }


    public static ArrayList<Ligne> sortByTick (ArrayList<Ligne> l) {
    	
      int longueur=l.size();
      Ligne tmp;
      boolean inversion;

      do {
        inversion=false;

        for(int i=0;i<longueur-1;i++)
        {
            if(l.get(i).getTick() > l.get(i+1).getTick()) 
            {
                tmp = l.get(i);
          		l.set(i, l.get(i+1));
          		l.set(i+1, tmp);
                inversion=true;
            }
        }
      } while(inversion);

      return l;
    }

}
