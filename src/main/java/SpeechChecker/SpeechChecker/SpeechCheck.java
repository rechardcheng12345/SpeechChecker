package SpeechChecker.SpeechChecker;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.json.JSONObject;

public class SpeechCheck {

	public static void main(String [] args) {
		
		SpeechCheck test = new SpeechCheck();
		
		System.out.println();
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		 int returnValue = jfc.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			System.out.println(selectedFile.getAbsolutePath());
			try {
				String textFile = FileUtils.readFileToString(selectedFile,"UTF-8");
				
				List<String> strLine = new ArrayList<String>();
				for (String str : textFile.split("[\\r\\n]+")) {
					strLine.add(str);
				}
				FileWriter fw = new FileWriter(selectedFile.getAbsolutePath(),true); 
				for (String str : strLine)
				{
					String afterTranslate = test.dolphinSpeechCheck(str);
					fw.append("\r\n\n");
					fw.append(afterTranslate);
				}
			    fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String dolphinSpeechCheck(String string) {
		String json = retrieveMeaning("dolphin");
		JSONObject object = new JSONObject(json);
		
		String newString = "";
		for (String word : string.split(" ")) {
			double biggestPossible = 0;
			String biggestPossibleString = null;
			Iterator<?> keys = object.keys();
			while( keys.hasNext() ) {
			    String key = (String)keys.next();
			    double distance = StringUtils.getJaroWinklerDistance(key,word);
			    
			    //set the fuzziness threshold 0.7
			    if(distance>0.7 && distance > biggestPossible) {
			    	biggestPossible = distance;
			    	biggestPossibleString = object.getString(key);
			    }
			    
			}
			newString = newString.concat(biggestPossibleString !=null ? biggestPossibleString :word);
			newString = newString.concat(" ");
		}
		System.out.println(newString);
		
		return newString;
	}
	
	private String retrieveMeaning(String animal) {
		ClassLoader classLoader = getClass().getClassLoader();
		StringBuilder result = new StringBuilder("");
		if(animal.equals("dolphin")) {
			File file = new File(classLoader.getResource("dolphin/meaning.json").getFile());
			
			try{
				Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					result.append(line).append("\n");
				}
				scanner.close();
	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}
}
