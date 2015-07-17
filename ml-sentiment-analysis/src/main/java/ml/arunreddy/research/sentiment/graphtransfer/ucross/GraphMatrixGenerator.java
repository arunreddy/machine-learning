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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.matrix.sparse.CCSMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import ml.arunreddy.research.sentiment.SentimentClassifierException;

/**
 * @version $Id$
 */
public class GraphMatrixGenerator
{

    private static final Logger logger = LoggerFactory.getLogger(GraphMatrixGenerator.class);

    public UCrossData generateAdjacencyMatrixFromMalletInstances(File malletInstancesFile)
        throws SentimentClassifierException
    {
        if(!malletInstancesFile.exists()){
            throw new SentimentClassifierException("Given instances file not found.!");
        }
        
        InstanceList instanceList = InstanceList.load(malletInstancesFile);
        logger.debug("Total number of instances loaded {}.", instanceList.size());
        logger.debug("Size of the vocabulary {}", instanceList.getAlphabet().size());
        
        // Create lists for source and target, positive and negative instances.
        InstanceList sourcePositiveInstanceList = instanceList.cloneEmpty();
        InstanceList sourceNegativeInstanceList = instanceList.cloneEmpty();
        InstanceList targetPositiveInstanceList = instanceList.cloneEmpty();
        InstanceList targetNegativeInstanceList = instanceList.cloneEmpty();

        // Segregate the instances to different lists.
        for (Instance instance : instanceList) {
            String instanceName = (String) instance.getName();
            if (instanceName.startsWith("AH_POS")) {

                sourcePositiveInstanceList.add(instance);
            } else if (instanceName.startsWith("AH_NEG")) {
                sourceNegativeInstanceList.add(instance);
            } else if (instanceName.startsWith("DB_POS")) {
                targetPositiveInstanceList.add(instance);
            } else if (instanceName.startsWith("DB_NEG")) {
                targetNegativeInstanceList.add(instance);
            }
        }
        logger.info("Segregation of instances into 4 categories completed..");
        logger.info("SRC_POS:{}  SRC_NEG:{}  TGT_POS:{}  TGT_NEG:{}", sourcePositiveInstanceList.size(),
            sourceNegativeInstanceList.size(), targetPositiveInstanceList.size(), targetNegativeInstanceList.size());
        
        targetPositiveInstanceList.shuffle(new Random());
        targetNegativeInstanceList.shuffle(new Random());

        // Consider the first 900 target positive and negative instances.
        targetPositiveInstanceList = targetPositiveInstanceList.subList(0, 900);
        targetNegativeInstanceList = targetNegativeInstanceList.subList(0, 900);
        logger.info("After split: TGT_POS:{}  TGT_NEG:{}", targetPositiveInstanceList.size(),
            targetNegativeInstanceList.size());

        /**
         * SPARSE MATRIX LOGIC.
         */

        // Unique Users.
        List<String> uniqueSourceUsers = getUniqueUsers(sourcePositiveInstanceList, sourceNegativeInstanceList);
        List<String> uniqueTargetUsers = getUniqueUsers(targetPositiveInstanceList, targetNegativeInstanceList);
        logger.info("Unique users in Source: {}  and Target: {}", uniqueSourceUsers.size(), uniqueTargetUsers.size());

        List<String> allUsers = new ArrayList<String>();
        allUsers.addAll(uniqueSourceUsers);
        allUsers.addAll(uniqueTargetUsers);

        int totalUsers = uniqueSourceUsers.size() + uniqueTargetUsers.size();
        int sourceInstanceCount = sourcePositiveInstanceList.size() + sourceNegativeInstanceList.size();
        int targetInstanceCount = targetPositiveInstanceList.size() + targetNegativeInstanceList.size();
        int totalPosts = sourceInstanceCount + targetInstanceCount;
        int vocabularySize = sourcePositiveInstanceList.getAlphabet().size();

        Matrix adjMatrixUsersPosts = new CCSMatrix(totalUsers, totalPosts);
        Matrix adjMatrixPostsVocabularyBinary = new CCSMatrix(totalPosts, vocabularySize);
        Matrix adjMatrixPostsVocabularyCount = new CCSMatrix(totalPosts, vocabularySize);

        Matrix labelMatrix = new CCSMatrix(totalPosts, 1);

        InstanceList combinedInstances = sourcePositiveInstanceList.cloneEmpty();
        combinedInstances.addAll(sourceNegativeInstanceList);
        combinedInstances.addAll(sourcePositiveInstanceList);

        for (int i = 0; i < 900; i++) {
            Instance positiveInstance = targetPositiveInstanceList.get(i);
            Instance negativeInstance = targetNegativeInstanceList.get(i);
            combinedInstances.add(positiveInstance);
            combinedInstances.add(negativeInstance);
        }

        for (int i = 0; i < totalPosts; i++) {
            Instance instance = combinedInstances.get(i);
            String instanceName = (String) instance.getName();
            String user = instanceName.split("__")[1];
            int userIndex = allUsers.indexOf(user);
            if (userIndex == -1) {
                System.out.println(instanceName);
            } else {
                adjMatrixUsersPosts.set(userIndex, i, 1.0);
            }

            if (instanceName.startsWith("AH_POS") || instanceName.startsWith("DB_POS")) {
                labelMatrix.set(i, 0, 1);
            } else if (instanceName.startsWith("AH_NEG") || instanceName.startsWith("DB_NEG")) {
                labelMatrix.set(i, 0, 0);
            }

        }

        Matrix postsAndLabelsMatrix = new CCSMatrix(totalPosts, 1);

        for (int i = 0; i < totalPosts; i++) {
            Instance instance = combinedInstances.get(i);
            FeatureVector instanceFeatureVector = (FeatureVector) instance.getData();
            int[] indices = instanceFeatureVector.getIndices();
            double[] values = instanceFeatureVector.getValues();

            for (int j = 0; j < values.length; j++) {
                double value = values[j];
                int index = indices[j];
                adjMatrixPostsVocabularyBinary.set(i, index, 1.0);
                adjMatrixPostsVocabularyCount.set(i, index, value);
            }

        }

        logger.info("Adjacency Matrix computation completed");
        
        return new UCrossData(adjMatrixUsersPosts, adjMatrixPostsVocabularyBinary,
            postsAndLabelsMatrix,sourceInstanceCount);
    }

    private static List<String> getUniqueUsers(InstanceList posList, InstanceList negList)
    {
        TreeSet<String> uniqueUsersSet = new TreeSet<String>();
        ArrayList<String> uniqueUsersList = new ArrayList<String>();

        // Positive posts.
        for (Instance instance : posList) {
            String instanceName = (String) instance.getName();
            String user = instanceName.split("__")[1];
            uniqueUsersSet.add(user);
        }

        // Negative posts.
        for (Instance instance : negList) {
            String instanceName = (String) instance.getName();
            String user = instanceName.split("__")[1];
            uniqueUsersSet.add(user);
        }

        uniqueUsersList.addAll(uniqueUsersSet);

        return uniqueUsersList;
    }
}
