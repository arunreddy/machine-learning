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

/**
 * @version $Id$
 */
public class UCrossData
{

    private final Matrix usersAndPostsMatrix;

    private final Matrix postsAndFeaturesMatrix;

    private final Matrix postsAndLabelsMatrix;
    
    private final int noOfSourceDomainPosts;

    /**
     * @return the noOfSourceDomainPosts
     */
    public int getNoOfSourceDomainPosts()
    {
        return noOfSourceDomainPosts;
    }

    /**
     * 
     */
    public UCrossData(Matrix usersAndPostsMatrix, Matrix postsAndFeaturesMatrix, Matrix postsAndLabelsMatrix, int noOfSourceDomainPosts)
    {
        this.usersAndPostsMatrix = usersAndPostsMatrix;
        this.postsAndFeaturesMatrix = postsAndFeaturesMatrix;
        this.postsAndLabelsMatrix = postsAndLabelsMatrix;
        this.noOfSourceDomainPosts = noOfSourceDomainPosts;
    }

    /**
     * @return the usersAndPostsMatrix
     */
    public Matrix getUsersAndPostsMatrix()
    {
        return usersAndPostsMatrix;
    }

    /**
     * @return the postsAndFeaturesMatrix
     */
    public Matrix getPostsAndFeaturesMatrix()
    {
        return postsAndFeaturesMatrix;
    }

    /**
     * @return the postsAndLabelsMatrix
     */
    public Matrix getPostsAndLabelsMatrix()
    {
        return postsAndLabelsMatrix;
    }

}
