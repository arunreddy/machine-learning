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

/**
 * @version $Id$
 */
public class GraphMatrixGenerator
{

    private static final Logger logger = LoggerFactory.getLogger(GraphMatrixGenerator.class);

    /**
     * 
     */
    public GraphMatrixGenerator()
    {

    }

    public static void main(String[] args)
    {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

        File instancesFile = new File("/home/arun/media/datasets/mallet/amazon-health-db-sisters.instances");
        InstanceList instanceList = InstanceList.load(instancesFile);

        logger.info("Total number of instances loaded {}.", instanceList.size());
        logger.info("Size of the vocabulary {}", instanceList.getAlphabet().size());

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
        
        /**
         * %% Initialize the matrices.
         * 
         * <pre>
        users = [users_S users_T];
        A_12 = users(:,1:num_S+num_target);
        A_13 = users(:,num_S+num_target+1:num_Total);
        
        data = [data_S' data_T']';
        A_24 = data(1:num_S+num_target,:);Damn... we know whats going on !! What say Vissu Potluri.
        A_34 = data(num_S+num_target+1:num_Total,:);
        
        labels = [labels_S' labels_T']';
        Y2 = labels(1:num_S+num_target,:);
        Y3_TRUTH = labels(num_S+num_target+1:num_Total,:);
        
        % Calculate the degree matrix.
        D_11 = full(sum(A_12,2)+sum(A_13,2));
        D_22 = full(sum(A_12',2)+sum(A_24,2));
        D_33 = full(sum(A_13',2)+sum(A_34,2));
        D_44 = full(sum(A_24',2)+sum(A_34',2));
        
        % Calculate the sqrt of degree matrix.
        D_11 = sqrt(1./D_11);
        D_22 = sqrt(1./D_22);
        D_33 = sqrt(1./D_33);
        D_44 = sqrt(1./D_44);
        
        % To sparse matrix.
        D_11 = sparse(diag(D_11));
        D_22 = sparse(diag(D_22));
        D_33 = sparse(diag(D_33));
        
        D_44_SPARSE = speye(size(D_44,1));
        for m = 1:size(D_44,1)
        D_44_SPARSE(m,m) = D_44(m,1); 
        end
        D_44 = D_44_SPARSE;
        
        %% Normalized Laplacian matrices.
        S_12 = D_11 * A_12 * D_22;
        S_13 = D_11 * A_13 * D_33;
        S_24 = D_22 * A_24 * D_44;
        S_34 = D_33 * A_34 * D_44;
        
        F3 = zeros(size(S_34,1),1);
        
        Y1 = zeros(size(S_12,1),1);
        Y3 = zeros(size(S_34,1),1);
        Y4 = zeros(size(S_34,2),1);
        
        %% Assumptions/ Set up
        F3_PREV = F3;
        %% TRITER Algorithm.
        for i = 1:iterations
        
        % F1.
        F1 = (var_alpha)*(S_12*Y2 + S_13*F3_PREV) + ((1-var_alpha)*Y1);
        
        % F4
        F4 = (var_alpha)*(S_24'*Y2 + S_34'*F3_PREV) + ((1-var_alpha)*Y4);
        
        % F3
        F3_CURR = (var_alpha)*(S_13'*F1 + S_34*F4) + ((1-var_alpha)*Y3);
        
        F3_PREV = F3_CURR;
        end
        
        
        %% Test Algorithm.
        test_err=0;
        no_of_test = size(Y3_TRUTH,1);
        for i = 1:no_of_test
        if F3_CURR(i,1)*Y3_TRUTH(i,1) <= 0
        test_err = test_err+ 1/no_of_test; 
        end
        end
         * </pre>
         */

        // Calculate the degree matrix.
        Matrix degreeMatrix_one = new CCSMatrix(totalUsers, totalUsers);
        Matrix degreeMatrix_two = new CCSMatrix(totalPosts, totalPosts);
        Matrix degreeMatrix_three = new CCSMatrix(vocabularySize, vocabularySize);

        // User Degree Matrix.
        for (int j = 0; j < totalUsers; j++) {
            Vector row = adjMatrixUsersPosts.getRow(j);
            double value = row.sum();
            value = Math.sqrt(1.0 / value);
            degreeMatrix_one.set(j, j, value);
        }

        // Posts Degree Matrix.
        for (int j = 0; j < totalPosts; j++) {
            Vector userPosts = adjMatrixUsersPosts.getColumn(j);
            Vector featurePosts = adjMatrixPostsVocabularyBinary.getRow(j);
            double value = userPosts.sum() + featurePosts.sum();
            value = Math.sqrt(1.0 / value);
            degreeMatrix_two.set(j, j, value);
        }

        // Feature Degree Matrix.
        for (int j = 0; j < vocabularySize; j++) {
            Vector featurePosts = adjMatrixPostsVocabularyBinary.getColumn(j);
            double value = featurePosts.sum();
            value = Math.sqrt(1.0 / value);
            degreeMatrix_three.set(j, j, value);
        }

        logger.info("Degree Matrix computation completed");

        // Normalized laplacian matrix.
        Matrix sNormUserPosts = degreeMatrix_one.multiply(adjMatrixUsersPosts).multiply(degreeMatrix_two);
        Matrix sNormPostsFeatures = degreeMatrix_two.multiply(adjMatrixPostsVocabularyBinary).multiply(degreeMatrix_three);
        logger.info("Normalized Laplace Matrix computation completed");

        
        //Split the matrices into source and target.
        
        logger.info("Execution Complete...");
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
