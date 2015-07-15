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
 * 
 * @version $Id$
 */
public class UCrossData
{
    
    private final Matrix A_12;
    private final Matrix A_13;
    private final Matrix A_24;
    private final Matrix A_34;

    /**
     * 
     */
    public UCrossData(Matrix A_12, Matrix A_13, Matrix A_24, Matrix A_34)
    {
        //Initialize the matrices.
        this.A_12 = A_12;
        this.A_13 = A_13;
        this.A_24 = A_24;
        this.A_34 = A_34;
    }

}
