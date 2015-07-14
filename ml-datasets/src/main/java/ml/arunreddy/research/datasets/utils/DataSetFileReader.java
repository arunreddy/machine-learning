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
package ml.arunreddy.research.datasets.utils;

import java.io.File;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ml.arunreddy.research.datasets.DataSets;

/**
 * @version $Id$
 */
public class DataSetFileReader
{

    private static final Logger logger = LoggerFactory.getLogger(DataSetFileReader.class);

    /**
     * 
     */
    public DataSetFileReader()
    {
        // TODO Auto-generated constructor stub
    }

    public static InputStream readGZippedFile()
    {

        return null;
    }

    public static File getFile(String relativePathToFile)
    {
        File file = new File(DataSets.ROOT_DIRECTORY + relativePathToFile);
        if (file.exists()) {
            return file;
        }
        logger.error("File {} is missing.", file);
        return null;
    }

}
