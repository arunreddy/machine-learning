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
package ml.arunreddy.research.datasets.dbsisters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.ohmdb.api.Db;
import com.ohmdb.api.Ohm;
import com.ohmdb.api.Table;

import ml.arunreddy.datasets.tudiabetes.Sentiment;;

/**
 * 
 * @version $Id$
 */
public class DbSistersDataImporter
{

    /**
     * 
     */
    public DbSistersDataImporter()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        // TODO Auto-generated method stub
        Db db = Ohm.db("/home/arun/media/datasets/ohmdb/diabetes-sisters-old.db");
        Table<Sentiment> table = db.table(Sentiment.class);
        
        File file = new File("/home/arun/code/phd/conf/obesity/matlab/diabetes-sisters.csv");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int counter =0;
        long[] ids = table.ids();
        
        while(reader.ready()){
            String line = reader.readLine();
            long id = ids[counter];
            Sentiment sentiment = table.get(id);
            int value = Integer.parseInt(line);
            
        }
        
        StringBuilder builder = new StringBuilder();
        for(long id : table.ids()){
            Sentiment sentiment = table.get(id);
            builder.append(sentiment.getText()+"\t"+sentiment.getUser()+"\t"+sentiment.getLabel()+"\n");
                
        }
        
        FileWriter writer = new FileWriter(new File("/home/arun/code/phd/conf/obesity/matlab/diabetes-sisters2.csv"));
       System.out.println(builder.toString());
        writer.write(builder.toString());
        writer.flush();
        writer.close();
        
        db.shutdown();
    }

}
