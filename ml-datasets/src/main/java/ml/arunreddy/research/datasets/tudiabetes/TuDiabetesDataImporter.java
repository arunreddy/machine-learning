/*
 * Copyright (C) 2015 Arun Reddy Nelakurthi
* 
* This file is part of ml-datasets.
*
* ml-datasets is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* ml-datasets is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ml.arunreddy.research.datasets.tudiabetes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ml.arunreddy.research.datasets.DataSets;
import ml.arunreddy.research.datasets.adt.Sentiment;
import ml.arunreddy.research.datasets.impl.AbstractDataImporter;
import ml.arunreddy.research.datasets.utils.DataSetFileReader;

/**
 * 
 * @version $Id$
 */
public class TuDiabetesDataImporter  extends AbstractDataImporter
{
    private static final Logger logger = LoggerFactory.getLogger(TuDiabetesDataImporter.class);

    public static final String TUDIABETES = "tu-diabetes.db";

    
    /**
     * 
     */
    public TuDiabetesDataImporter(String dbName)
    {
        super(dbName,true);
    }

    /**
     * Read posts from tu-diabetes dataset.
     * 
     * @param filePath
     * @param instanceType
     */
    public List<Sentiment> readSentimentData(File file, int instanceType)
    {

        List<Sentiment> sentimentList = new ArrayList<Sentiment>();
        BufferedReader reader = getBufferedReader(file, file.getName().endsWith("gz") ? true : false);
        if (reader != null) {
            try {
                int counter = 0;
                while (reader.ready()) {
                    String line = reader.readLine();
                    String[] columns = line.split("\t");
                    String user = columns[1];
                    String text = columns[columns.length-1];
                    Document document = Jsoup.parse(text);
                    String sentimentId = "TUDIABETES_"+(counter++);
                    Sentiment sentiment = new Sentiment(sentimentId,document.text(),user);
                    sentiment.setUser(user);
                    sentimentList.add(sentiment);

                }
            } catch (IOException e) {
                logger.error("Error reading the given file {}.", file.getName());
                e.printStackTrace();
            }
        }
        return sentimentList;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TuDiabetesDataImporter tuDiabetesDataImporter = new TuDiabetesDataImporter(TUDIABETES);
        File dataSetFile = DataSetFileReader.getFile(DataSets.TU_DIABETES);
        
        List<Sentiment> sentimentList = tuDiabetesDataImporter.readSentimentData(dataSetFile, 0);
        tuDiabetesDataImporter.writeSentimentListToDb(sentimentList);
        
        tuDiabetesDataImporter.cleanUp();
    }

}
