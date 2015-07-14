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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import com.ohmdb.api.Db;
import com.ohmdb.api.Ohm;
import com.ohmdb.api.Table;

import ml.arunreddy.research.datasets.adt.Sentiment;;

/**
 * 
 * @version $Id$
 */
public class DBMigrator
{

    public static void main(String[] args) throws Exception
    {
        File file2 = new File("/home/arun/media/datasets/ohmdb/diabetes-sisters.db");
        if(file2.exists()){
            file2.delete();
        }
        
        
        Db db = Ohm.db("/home/arun/media/datasets/ohmdb/diabetes-sisters.db");
        Db db2 = Ohm.db("/home/arun/media/datasets/ohmdb/diabetes-sisters-old.db");
    
        Table<ml.arunreddy.datasets.tudiabetes.Sentiment> sentimentListOld = db2.table(ml.arunreddy.datasets.tudiabetes.Sentiment.class);
        Table<Sentiment> sentimentList = db.table(Sentiment.class);
        
        System.out.println(sentimentListOld.size());
   
        
        
        
        File file = new File("/home/arun/code/phd/conf/obesity/matlab/diabetes-sisters.csv");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int counter =0;
        ArrayList<String> arrayList = new ArrayList<String>();
        while(reader.ready()){
            String line = reader.readLine();
            arrayList.add(line);
        }
        
        System.out.println(arrayList.size());
        
        
        for(long id:sentimentListOld.ids()){
            ml.arunreddy.datasets.tudiabetes.Sentiment oldSentiment = sentimentListOld.get(id);
            Sentiment sentiment = new Sentiment();
            sentiment.setUser(oldSentiment.getUser());
            sentiment.setText(oldSentiment.getText());
            sentiment.setLabel(arrayList.get(counter++));
            sentimentList.insert(sentiment);
            
        }
        
        System.out.println(sentimentList.size());
        db.shutdown();
        db2.shutdown(); 
    }

}
