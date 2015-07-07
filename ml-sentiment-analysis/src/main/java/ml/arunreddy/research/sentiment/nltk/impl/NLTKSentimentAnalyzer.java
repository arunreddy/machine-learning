/*
 * NLTKSentimentAnalyzer.java
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

package ml.arunreddy.research.sentiment.nltk.impl;

import ml.arunreddy.research.sentiment.SentimentLabel;
import ml.arunreddy.research.sentiment.impl.AbstractSentimentAnalyzer;

/**
 * Created by arun on 6/26/15.
 */
public class NLTKSentimentAnalyzer extends AbstractSentimentAnalyzer {



  @Override
  public SentimentLabel getSentiment(String text) {
//    URL oracle = new URL("http://text-processing.com/api/sentiment/");
//    HttpURLConnection con = (HttpURLConnection) oracle.openConnection();
//    con.setRequestMethod("POST");
//    con.setDoOutput(true);
//    String s = "text="+text;
//    con.getOutputStream().write(s.getBytes());
//
//    BufferedReader in = new BufferedReader(new InputStreamReader(
//        con.getInputStream()));
//    String inputLine;
//    int sentiment =0;
//    while ((inputLine = in.readLine()) != null) {
//      String str = inputLine.split("}")[1];
//      if (str.contains("pos")) {
//        sentiment = 1;
//      } else if (str.contains("neg")) {
//        sentiment = -1;
//      }
//    }
//    in.close();
//
//    return sentiment;

    return null;
  }
}
