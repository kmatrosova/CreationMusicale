import java.io.*;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiToText_polyChannel {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    

    public static void main(String[] args) throws Exception {
    	File file = new File(System.getProperty("user.dir")+"/midi_files/");
    	File[] files = file.listFiles();
      File file_results = new File(System.getProperty("user.dir")+"/results/"); //on vérifie si le fichier results existe, si non, on le creer
      int i = 1;
      if (!file_results.exists())
        file_results.mkdir();
      else {
        for(File file2: file_results.listFiles())
          if (!file2.isDirectory())
            file2.delete();
      }
    	for(File f: files){
          try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(System.getProperty("user.dir") + "/results/" + i + ".txt"), "utf-8"))) {
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
    	int trackNumber = 0;
    	
    	for (Track track :  sequence.getTracks()) {
    	    trackNumber++;
    	    
    	    for (int i=0; i < track.size(); i++) {
        		MidiEvent event = track.get(i);
        		MidiMessage message = event.getMessage();
        		
        		if (message instanceof ShortMessage) {
        		    ShortMessage sm = (ShortMessage) message;
        		    
        		    if (sm.getCommand() == NOTE_ON) {
            			int key = sm.getData1();
            			int octave = (key / 12)-1;
            			int note = key % 12;
            			String noteName = NOTE_NAMES[note];
            			String noteStatus;
            			int velocity = sm.getData2();
            			long t = event.getTick();
            			
            			long shift = t-tt;
            			
						if (velocity > 0) noteStatus = "ON"; else noteStatus = "OFF";
						  
						if(shift != 0) {
							res += "SHIFT<" + shift + ">";
							tt = t;
						} 
						  
						if (noteStatus.equals("ON")) {
						if(v != velocity) {
							v = velocity;
							res += "SET_V<" + v + ">";
						}
						res += "ON<" + key + ">";
						}
						else {
							res += "OFF<" + key + ">";
						}
            			/*res += "@" + event.getTick() + " ";
            			res += "Channel: " + sm.getChannel() + " ";
            			res += noteStatus + ", " + noteName + octave + " key=" + key + " velocity: " + velocity;
            			res += '\n';*/
        		    }
            }
          }
      }
    return res;
    }
}
