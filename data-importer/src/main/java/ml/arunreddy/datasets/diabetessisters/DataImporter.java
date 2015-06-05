package ml.arunreddy.datasets.diabetessisters;


import com.ohmdb.api.Db;
import com.ohmdb.api.Ohm;
import com.ohmdb.api.Table;
import ml.arunreddy.datasets.tudiabetes.Sentiment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by arun on 6/3/15.
 */
public class DataImporter {

  public static void main(String[] args) throws  Exception{

    File parentDirectory = new File("/home/arun/code/coursework/sml/multiclass-classification/multiclass-classification-data/src/main/resources/DiabetesSisters/");

    Db db = Ohm.db("diabetes-sisters.db");
    Table<Sentiment> sentimentTable = db.table(Sentiment.class);

    //Traverse through category directories.
    for(File file:parentDirectory.listFiles()){

      if(file.isDirectory()){
        //Read all the text files.
        for(File textFile: file.listFiles()){
          BufferedReader br = new BufferedReader(new FileReader(textFile));

          while(br.ready()){
            String line = br.readLine();
            String[] columns = line.split("\t");
            String user = columns[0];
            String text = columns[columns.length-1];
            Document document = Jsoup.parse(text);
            Sentiment sentiment = new Sentiment(document.text());
            sentiment.setUser(user);
            sentimentTable.insert(sentiment);
          }
        }
      }
    }
    db.shutdown();
  }
}
