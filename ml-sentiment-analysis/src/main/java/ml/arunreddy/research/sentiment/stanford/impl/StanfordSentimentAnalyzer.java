/*
 * StanfordSentimentAnalyzer.java
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

package ml.arunreddy.research.sentiment.stanford.impl;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import ml.arunreddy.research.datasets.adt.Sentiment;
import ml.arunreddy.research.sentiment.SentimentLabel;
import ml.arunreddy.research.sentiment.impl.AbstractSentimentAnalyzer;

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

/**
 * Created by arun on 6/26/15.
 */
public class StanfordSentimentAnalyzer extends AbstractSentimentAnalyzer {



	public double getSentimentScore(String text){
		Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    int mainSentiment = 0;
	    if (text != null && !text.isEmpty()) {
	      int longest = 0;
	      Annotation annotation = pipeline.process(text);
	      for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
	        Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
	        int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
	        String partText = sentence.toString();
	        if (partText.length() > longest) {
	          mainSentiment = sentiment;
	          longest = partText.length();
	        }

	      }
	    }

	    assert mainSentiment >= 0;
	    assert mainSentiment <= 4;

	    double value = (double)mainSentiment/4.0;

	    return value;
	}
	
  @Override
  public SentimentLabel getSentiment(String text) {
	double value = getSentimentScore(text);
   
    if(value < 0.5){
      return SentimentLabel.NEGATIVE;
    }else if(value > 0.5){
      return SentimentLabel.POSITIVE;
    }else{
      return SentimentLabel.NEUTRAL;
    }
   
  }

  public static void main(String[] args) throws Exception{

	StanfordSentimentAnalyzer stanfordSentimentAnalyzer = new StanfordSentimentAnalyzer();
    Db db = Ohm.db("/home/arun/media/datasets/db/semeval-twitter-2013.db");
    Table<Sentiment> sentimentTable = db.table(Sentiment.class);
    
    Set<String> labels = new TreeSet<String>();	
    int counter =1;
    int correct =0;
    int valid = 0;
    
    File file = new File("/home/arun/media/datasets/semeval/csv/test.pos");
    StringBuilder builder = new StringBuilder();
    for(long id:sentimentTable.ids()){
    	
    	Sentiment sentiment = sentimentTable.get(id);
//    	SentimentLabel sentimentLabel = stanfordSentimentAnalyzer.getSentiment(sentiment.getText());
//    	if(sentimentLabel.equals(SentimentLabel.POSITIVE) && sentiment.getLabel().equals("positive")){
//    		correct++;
//    	}
//    	
//    	if(sentimentLabel.equals(SentimentLabel.NEGATIVE) && sentiment.getLabel().equals("negative")){
//    		correct++;
//    	}
//    	
//    	System.out.println(sentimentLabel+" -- "+sentiment.getLabel());
//   
    	if(sentiment.getLabel().equals("positive") && sentiment.getInstanceType() == Sentiment.TEST_INSTANCE){
    		builder.append( "TW_TE_POS_"+counter+", 1, "+sentiment.getText()+"\n");
        	counter++;	
    	}
    	
//    	if(counter==100){
//    		break;
//    	}
//    	
//    	if(!sentiment.getLabel().equals("neutral")){
//    		valid++;
//    	}
//    	
    }

    System.out.println(correct);
    System.out.println(valid);
    System.out.println(counter);

	FileWriter writer = new FileWriter(file);
	writer.write(builder.toString());
	writer.flush();
	writer.close();
    
    db.shutdown();
  }
}
