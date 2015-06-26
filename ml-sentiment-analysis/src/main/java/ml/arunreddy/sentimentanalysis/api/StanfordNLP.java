/*
 * StanfordNLPP.java
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
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import ml.arunreddy.data.pojo.Sentiment;

import java.util.Properties;

/**
 * Created by arun on 6/22/15.
 */
public class StanfordNLP {
  public static int findSentiment(String tweet) {

    Properties props = new Properties();
    props.setProperty("annotators", " tokenize, ssplit, parse, sentiment");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation document = new Annotation(tweet);
    pipeline.annotate(document);
    int sentiment = 0;
    int neg = 0;
    int neut = 0;
    int pos = 0;

    for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
      sentiment = RNNCoreAnnotations.getPredictedClass(tree);
      System.out.println(sentiment);
      String partText = sentence.toString();
      if (sentiment == 1)
        neg = neg + 1;
      else if (sentiment == 2)
        neut = neut + 1;
      else
        pos = pos + 1;
    }

    //System.out.println("neg: "+neg+" neut: "+neut+" pos: "+pos);
    if (neg >= pos) {
      if (neg >= neut)
        return 1;
      else {
        if (neut >= pos)
          return 2;
        else
          return 3;
      }
    } else {
      if (pos > neut)
        return 3;
      else {
        if (neg > neut)
          return 1;
        else
          return 2;
      }
    }
  }


  public static void main(String[] args) {
    Db db = Ohm.db("twitter-sentiment-5000.db");
    Table<Sentiment> sentimentTable = db.table(Sentiment.class);
    System.out.println(sentimentTable.ids().length);

    int counter=0;
    int correct=0;
    int neutral=0;
    for(long id:sentimentTable.ids()){


      if(counter<4000){
        //Do Nothing.
      }else{
        //Test Set.
        Sentiment sentiment = sentimentTable.get(id);
        int sentimentValue = 0;
        try {
          sentimentValue = NLTKApi.getSentiment(sentiment.getText());
        } catch (Exception e) {
          e.printStackTrace();
        }

        if(sentimentValue == -1 && sentiment.getLabel() == 0){
          correct++;
        }else if(sentimentValue == 1 && sentiment.getLabel() == 4){
          correct++;
        }
      }
      counter++;
    }

    System.out.println(correct);
    System.out.println(neutral);
    db.shutdown();

//    System.out.println(findSentiment("Hhh~ It's times like THAT when you want things to stay right  Then someone comes and wrecks it."));
  }
}
