package ml.arunreddy.research.sentiment.naivebayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class DataUtils {

	
//	public static void main(String[] args){
//		//Read the data. Write CSV Files.
//		
//		try{
//			File file = new File ("/home/arun/media/datasets/rt-polaritydata/rt-polaritydata/rt-polarity.neg");
//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			
//			int counter =5332;
//			StringBuilder builder = new StringBuilder();
//			while(reader.ready()){
//				String line = reader.readLine();
//				builder.append( counter+", -1, "+line+"\n");
//				counter++;
//			}
//			reader.close();
//			
//			File outfile = new File ("/home/arun/media/datasets/rt-polaritydata/rt-polaritydata/rt-polarity-processed.neg");
//			FileWriter writer = new FileWriter(outfile);
//			writer.write(builder.toString());
//			writer.flush();
//			writer.close();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
}
