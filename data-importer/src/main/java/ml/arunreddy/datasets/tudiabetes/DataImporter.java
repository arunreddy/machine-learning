package ml.arunreddy.datasets.tudiabetes;


import com.ohmdb.api.Db;
import com.ohmdb.api.Ohm;
import com.ohmdb.api.Table;
import ml.arunreddy.datasets.tudiabetes.Sentiment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.management.remote.TargetedNotification;
import java.io.BufferedReader;
import  java.io.File;
import java.io.FileReader;

/**
 * Created by arun on 6/3/15.
 */
public class DataImporter {

  public static void main(String[] args) throws  Exception{

    File diabetesFile = new File("/home/arun/Desktop/tu-diabetes/tu-diabetes.txt");
    BufferedReader br = new BufferedReader(new FileReader(diabetesFile));
    Db db = Ohm.db("tu-diabetes.db");

    Table<Sentiment> sentimentTable = db.table(Sentiment.class);

    while(br.ready()){
      String line = br.readLine();
      String[] columns = line.split("\t");
      String user = columns[1];
      String text = columns[columns.length-1];
      Document document = Jsoup.parse(text);
      Sentiment sentiment = new Sentiment(document.text());
      sentiment.setUser(user);
      sentimentTable.insert(sentiment);
    }

    System.out.println(sentimentTable.size());


  }
}
