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
package ml.arunreddy.research.datasets;

/**
 * References to various machine learning and NLP datasets.
 * @version $Id$
 */
public interface DataSets
{

    public static final String SEMEVAL_TWITTER_2013_TRAIN = "semeval/Devdata-SemEval2013.txt.gz";
    
    public static final String SEMEVAL_TWITTER_2013_TEST = "semeval/Testdata-SemEval2013.txt.gz";
    
    public static final String SEMEVAL_TWITTER_2013_VALIDATION = "semeval/Trainingsdata-SemEval2013.txt.gz";
    
    public static final String ROOT_DIRECTORY = System.getenv("DATASETS");
    
    public static final String TU_DIABETES = "tu-diabetes/tu-diabetes.txt.gz";
}
