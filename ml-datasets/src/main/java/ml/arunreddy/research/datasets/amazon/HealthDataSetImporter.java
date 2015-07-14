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
package ml.arunreddy.research.datasets.amazon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import com.ohmdb.api.Db;
import com.ohmdb.api.Ohm;
import com.ohmdb.api.Table;

import ml.arunreddy.research.datasets.adt.Sentiment;

/**
 * 
 * @version $Id$
 */
public class HealthDataSetImporter
{

    /**
     * 
     */
    public HealthDataSetImporter()
    {
    
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        File usersFile = new File("/home/arun/code/phd/conf/cikm-2015/data/amazonhealth/users.csv");
        ArrayList<String> usersList = new ArrayList<String>();
        BufferedReader userReader = new BufferedReader(new FileReader(usersFile));
        while(userReader.ready()){
            String line = userReader.readLine();
            usersList.add(line.trim());
        }
        userReader.close();
        
        File file = new File("/home/arun/code/phd/conf/cikm-2015/data/amazonhealth/health_processed-reviews.csv");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        
        Db db = Ohm.db("/home/arun/media/datasets/ohmdb/amazon-health.db");
        Table<Sentiment> sentimentTable = db.table(Sentiment.class);
        
        ArrayList<String> userReviewList = new ArrayList<String>();
        
        while(reader.ready()){
            String line = reader.readLine();
            userReviewList.add(line);
        }
        
        System.out.println(usersList.size());
        System.out.println(userReviewList.size());
        
        for (int i = 0; i < usersList.size(); i++) {
            String user = usersList.get(i);
            String line = userReviewList.get(i);
            String[] fields = line.split("\t");
            long id = Long.parseLong(fields[0]);
            Sentiment sentiment = new Sentiment();
            sentiment.setId(id);
            sentiment.setText(fields[2]);
            sentiment.setLabel(fields[1]);
            int index = (int)id -1;
            sentiment.setUser(user);
            sentimentTable.insert(sentiment);
        }
       
        System.out.println(sentimentTable.size());
        db.shutdown();
    }

}
