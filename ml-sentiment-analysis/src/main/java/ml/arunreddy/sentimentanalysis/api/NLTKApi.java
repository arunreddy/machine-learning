/*
 * NLTKApi.java
 *
 * Copyright (c) 2015  Arun Reddy Nelakurthi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Report bugs or new features to: arunreddy@asu.edu
 */

package ml.arunreddy.sentimentanalysis.api;

import com.ohmdb.api.Db;
import com.ohmdb.api.Ohm;
import com.ohmdb.api.Table;
import ml.arunreddy.data.pojo.Sentiment;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by arun on 6/22/15.
 */
public class NLTKApi {


  public static int getSentiment(String text) throws Exception{

    URL oracle = new URL("http://text-processing.com/api/sentiment/");
    HttpURLConnection con = (HttpURLConnection) oracle.openConnection();
    con.setRequestMethod("POST");
    con.setDoOutput(true);
    String s = "text="+text;
    con.getOutputStream().write(s.getBytes());

    BufferedReader in = new BufferedReader(new InputStreamReader(
        con.getInputStream()));
    String inputLine;
    int sentiment =0;
    while ((inputLine = in.readLine()) != null) {
      String str = inputLine.split("}")[1];
      if (str.contains("pos")) {
        sentiment = 1;
      } else if (str.contains("neg")) {
        sentiment = -1;
      }
    }
    in.close();

    return sentiment;
  }

  public static void main(String[] args) throws Exception{

    Db db = Ohm.db("diabetes-sisters.db");
    Table<Sentiment> sentimentTable = db.table(Sentiment.class);

    System.out.println(sentimentTable.size());

    int pos=0,neg=0;

    StringBuilder stringBuilder = new StringBuilder();

    for (long id: sentimentTable.ids()) {
//
//      if(id<=2310){
//        continue;
//      }
      Sentiment sentiment = sentimentTable.get(id);
      stringBuilder.append(sentiment.getLabel()+"\n");

      if(sentiment.getLabel() == 1){
        pos++;
      }else if(sentiment.getLabel() == -1){
        neg++;
      }
//      System.out.println(sentiment.getText());
//      int sentimentScore = getSentiment(sentiment.getText());
//
//      sentiment.setLabel(sentimentScore);
//      sentimentTable.update(sentiment);
//      System.out.println("ID:" + id + " -- Complete");
//
//      Thread.sleep(20);
    }

    FileWriter writer = new FileWriter("diabetes-sisters.csv");
    writer.write(stringBuilder.toString());
    writer.flush();
    writer.close();


    db.shutdown();
  }
}
