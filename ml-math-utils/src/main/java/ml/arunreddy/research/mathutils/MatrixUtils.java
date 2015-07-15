/*
 * Copyright (C) 2015 Arun Reddy Nelakurthi
* 
* This file is part of ml-math-utils.
*
* ml-math-utils is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* ml-math-utils is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ml.arunreddy.research.mathutils;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.matrix.sparse.CCSMatrix;

/**
 * This class contains various matrix utility methods build on top of la4j {@link http://la4j.org/} library.
 * 
 * @version $Id$
 */
public class MatrixUtils
{

    /**
     * Returns the degree matrix (row sum of given matrices returned as a diagonal matrix). Used for Laplacian
     * Normalization.
     * 
     * @param matrixArray
     * @return
     * @throws MathUtilsException
     */
    public static Matrix calculateDegreeMatrix(Matrix... matrixArray) throws MathUtilsException
    {

        if (matrixArray.length < 1) {
            throw new MathUtilsException("Array is empty..!!");
        }
        int degreeMatrixRowCount = matrixArray[0].rows();
        Matrix degreeMatrix = new CCSMatrix(degreeMatrixRowCount, degreeMatrixRowCount);
        for (int i = 0; i < matrixArray.length; i++) {
            Matrix matrix = matrixArray[i];
            // Check if the number of rows of the matrix equals degree matrix row count.
            if (degreeMatrixRowCount != matrix.rows()) {
                throw new MathUtilsException(
                    "Rows of the degree matrix doesn't match with matrix-#(" + i + ") in the given argument.");
            }
            for (int j = 0; j < degreeMatrixRowCount; j++) {
                Vector rowVector = matrix.getRow(j);
                double currentValue = degreeMatrix.get(j, j);
                degreeMatrix.set(j, j, rowVector.sum() + currentValue);
            }
        }

        return degreeMatrix;
    }

    /**
     * Calculate the Laplacian Normal Matrix from the given adjacency and degree matrices.
     * 
     * @param degreeMatrixA
     * @param adjacencyMatrix
     * @param degreeMatrixB
     * @return
     */
    public static Matrix calculateLaplacianNorm(Matrix degreeMatrixA, Matrix adjacencyMatrix, Matrix degreeMatrixB)
    {
        return degreeMatrixA.multiply(adjacencyMatrix).multiply(degreeMatrixB);
    }

}
