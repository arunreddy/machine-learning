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

import org.la4j.Matrix;
import org.la4j.matrix.sparse.CCSMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ml.arunreddy.research.mathutils.MathUtilsException;
import ml.arunreddy.research.mathutils.MatrixUtils;

/**
 * This class implements U-Cross classifier.
 * 
 * @version $Id$
 */
public class UCrossClassifier
{
    /**
     * Matrices.
     */
    private final Matrix normUsersAndPostsMatrix;

    private final Matrix normPostsAndFeaturesMatrix;

    private final Matrix postsAndLabelsMatrix;

    private final int numOfSourceDomainPosts;

    private static final Logger logger = LoggerFactory.getLogger(UCrossClassifier.class);

    /**
     * Constructor for UCrossClassifier.
     * 
     * @param A_12 {@See Matrix} of users and posts.
     * @param A_24 {@See Matrix} of posts and features.
     * @param Y_S {@See Matrix} of source domain labels.
     * @param Y_T {@See Matrix} of target domain labels.
     */
    public UCrossClassifier(Matrix usersAndPostsMatrix, Matrix postsAndFeaturesMatrix, Matrix postsAndLabelsMatrix,
        int numOfSourceDomainPosts)
    {
        // Initialize the matrices.
        this.postsAndLabelsMatrix = postsAndLabelsMatrix;
        this.numOfSourceDomainPosts = numOfSourceDomainPosts;
        logger.debug("UCrossClassifier instantiated.");

        Matrix normUsersAndPostsMatrix = null;
        Matrix normPostsAndFeaturesMatrix = null;

        // Degree matrices and normalized laplacian matrices.
        try {
            Matrix degreeMatrix_one = MatrixUtils.calculateDegreeMatrix(usersAndPostsMatrix);
            Matrix degreeMatrix_two =
                MatrixUtils.calculateDegreeMatrix(postsAndFeaturesMatrix, usersAndPostsMatrix.transpose());
            Matrix degreeMatrix_three = MatrixUtils.calculateDegreeMatrix(postsAndFeaturesMatrix.transpose());

            normUsersAndPostsMatrix =
                MatrixUtils.calculateLaplacianNorm(degreeMatrix_one, usersAndPostsMatrix, degreeMatrix_two);
            normPostsAndFeaturesMatrix =
                MatrixUtils.calculateLaplacianNorm(degreeMatrix_two, postsAndFeaturesMatrix, degreeMatrix_three);

        } catch (MathUtilsException ex) {
            logger.error("MathUtilsException: {}", ex.getMessage());
        }

        this.normPostsAndFeaturesMatrix = normPostsAndFeaturesMatrix;
        this.normUsersAndPostsMatrix = normUsersAndPostsMatrix;
        logger.debug("Laplacian Normalization of matrices is complete.");

    }

    public double classify(int noOfSamplesFromTarget, int iterations, double alpha)
    {

        // Rearrange labeled examples.
        int totalUsers = this.normUsersAndPostsMatrix.rows();
        int totalPosts = this.normPostsAndFeaturesMatrix.rows();
        int totalFeatures = this.normPostsAndFeaturesMatrix.columns();

        int sourceDomainPosts = this.numOfSourceDomainPosts + noOfSamplesFromTarget;
        int targetDomainPosts = totalPosts - sourceDomainPosts;

        int[] userIndices = new int[totalUsers];
        int[] sourceDomainPostsIndices = new int[sourceDomainPosts];
        int[] targetDomainPostsIndices = new int[targetDomainPosts];
        int[] featureIndices = new int[totalFeatures];

        // Populate index arrays.
        for (int i = 0; i < totalUsers; i++)
            userIndices[i] = i;

        for (int i = 0; i < sourceDomainPosts; i++)
            sourceDomainPostsIndices[i] = i + totalUsers;

        for (int i = 0; i < targetDomainPosts; i++)
            sourceDomainPostsIndices[i] = i + totalUsers + sourceDomainPosts;

        for (int i = 0; i < totalFeatures; i++)
            sourceDomainPostsIndices[i] = i + totalUsers + sourceDomainPosts + targetDomainPosts;

        Matrix s12 = this.normUsersAndPostsMatrix.select(userIndices, sourceDomainPostsIndices);
        Matrix s13 = this.normUsersAndPostsMatrix.select(userIndices, targetDomainPostsIndices);
        Matrix s24 = this.normPostsAndFeaturesMatrix.select(sourceDomainPostsIndices, featureIndices);
        Matrix s34 = this.normPostsAndFeaturesMatrix.select(targetDomainPostsIndices, featureIndices);

        Matrix yU = new CCSMatrix(totalUsers, 1);
        Matrix yS = this.postsAndLabelsMatrix.sliceTopLeft(sourceDomainPosts, 1);
        Matrix yT_Truth = this.postsAndLabelsMatrix.sliceBottomRight(sourceDomainPosts, 1);
        Matrix yT = new CCSMatrix(targetDomainPosts, 1);
        Matrix yF = new CCSMatrix(totalFeatures, 1);

        Matrix fU = new CCSMatrix(totalUsers, 1);
        Matrix fT = new CCSMatrix(targetDomainPosts, 1);
        Matrix fF = new CCSMatrix(totalFeatures, 1);

        // Algorithm.
        for (int iteration = 0; iteration < iterations; iterations++) {
            fU = s12.multiply(yS).multiply(alpha).add(s13.multiply(fT).multiply(alpha)).add(yU.multiply(1 - alpha));
            fF = s24.transpose().multiply(yS).multiply(alpha).add(s34.transpose().multiply(fT).multiply(alpha))
                .add(yF.multiply(1 - alpha));
            fT = s13.transpose().multiply(fU).multiply(alpha).add(s34.multiply(fF).multiply(alpha))
                .add(yT.multiply(1 - alpha));
        }

        int error = 0;
        for (int i = 0; i < targetDomainPosts; i++) {
            if (fT.get(i, 1) * yT_Truth.get(i, 1) <= 0) {
                error++;
            }
        }

        double errorRate = (double) error / (double) targetDomainPosts;

        return errorRate;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {

    }

}
