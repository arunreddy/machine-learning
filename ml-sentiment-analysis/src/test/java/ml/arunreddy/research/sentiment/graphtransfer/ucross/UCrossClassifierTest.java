/*
 * Copyright (C) 2015 Arun Reddy Nelakurthi
* 
* This file is part of ml-sentiment-analysis.
*
* ml-sentiment-analysis is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* ml-sentiment-analysis is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ml.arunreddy.research.sentiment.graphtransfer.ucross;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ml.arunreddy.research.sentiment.AbstractSentimentClassifierTest;
import ml.arunreddy.research.sentiment.SentimentClassifierException;

/**
 * 
 * @version $Id$
 */
public class UCrossClassifierTest extends AbstractSentimentClassifierTest
{

    private static final Logger logger = LoggerFactory.getLogger(UCrossClassifierTest.class);
    
    /**
     * Test method for Amazon Health dataset as source and DB Sisters as target.
     */
    @Test
    public void testUCrossClassifier_AmazonHealthAndDbSisters()
    {
        logger.info("Executing UCross Classifier for Amazon Health and DB Sisters.");
        File malletInstancesFile = new File("/home/arun/media/datasets/mallet/amazon-health-db-sisters.instances");
        GraphMatrixGenerator graphMatrixGenerator = new GraphMatrixGenerator();
        try{
            UCrossData uCrossData = graphMatrixGenerator.generateAdjacencyMatrixFromMalletInstances(malletInstancesFile);
            UCrossClassifier classifier = new UCrossClassifier(uCrossData);
            double result = classifier.classify(100, 2, 0.9);
        }catch(SentimentClassifierException ex){
            logger.debug(ex.getMessage());
        }
    }

}
