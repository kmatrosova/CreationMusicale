import java.io.*;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;


public class Dataset {

    public static void main(String[] args) throws Exception {
    	
    	File file = new File(System.getProperty("user.dir")+"/text_files/");
    	File[] files = file.listFiles();
    	System.out.println(files.length);
    	
    	File dataset = new File("dataset.txt");
    	if(dataset.exists()){
    		
    		dataset.delete();
    		try {
    			dataset.createNewFile();
    		} 
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
        
    	for(File f: files){
    		
    		FileReader fr = new FileReader(f);
    		BufferedReader br = new BufferedReader(fr);
    		FileWriter fw = new FileWriter(dataset, true);
    		String s;
    		
    		while ((s = br.readLine()) != null) { // read a line
				fw.write(s); // write to output file
				fw.flush();
			}
    		fw.write("\n\n");
    		
			br.close();
    		fw.close();

    	}
    	System.out.println("Dataset created with success.");
    }

}
