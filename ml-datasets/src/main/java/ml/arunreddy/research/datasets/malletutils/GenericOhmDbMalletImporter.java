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
package ml.arunreddy.research.datasets.malletutils;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;

import com.ohmdb.api.Db;
import com.ohmdb.api.Ohm;
import com.ohmdb.api.Table;

import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;
import ml.arunreddy.research.datasets.adt.Sentiment;

/**
 * @version $Id$
 */
public class GenericOhmDbMalletImporter
{

    public static void main(String[] args) throws Exception
    {
        Db amazonHealthDb = Ohm.db("/home/arun/media/datasets/ohmdb/amazon-health.db");
        Db dbSistersDb = Ohm.db("/home/arun/media/datasets/ohmdb/diabetes-sisters.db");

        Table<Sentiment> amazonHealthTable = amazonHealthDb.table(Sentiment.class);
        Table<Sentiment> dbSistersTable = dbSistersDb.table(Sentiment.class);

        StringBuilder builder = new StringBuilder();

        int rowCount = 1;
        // Source.
        for (long id : amazonHealthTable.ids()) {
            Sentiment sentiment = amazonHealthTable.get(id);
            int sentimentScore = Integer.parseInt(sentiment.getLabel().trim());
            if (sentimentScore > 3) {
                builder.append("AH_POS_" + rowCount + "__"+sentiment.getUser().trim()+"    1    " + cleanText(sentiment.getText()) + "\n");
            } else if (sentimentScore < 3) {
                builder.append("AH_NEG_" + rowCount + "__"+sentiment.getUser().trim()+"    0    " + cleanText(sentiment.getText()) + "\n");
            }

            rowCount++;
        }

        System.out.println(dbSistersTable.ids().length);
        // Target
        for (long id : dbSistersTable.ids()) {
            Sentiment sentiment = dbSistersTable.get(id);
            int sentimentScore = Integer.parseInt(sentiment.getLabel().trim());
            System.out.println(sentimentScore);
            if (sentimentScore > 0) {
                builder.append("DB_POS_" + rowCount + "__"+sentiment.getUser().trim()+"    1    " + cleanText(sentiment.getText()) + "\n");
            } else if (sentimentScore < 0) {
                builder
                    .append("DB_NEG_" + rowCount + "__"+sentiment.getUser().trim()+"    0    " + cleanText(sentiment.getText()) + "\n");
            }

            rowCount++;
        }

        // Read into Mallet.
        StringReader reader = new StringReader(builder.toString());

        FileWriter writer = new FileWriter("textfile.txt");
        writer.write(builder.toString());
        writer.flush();
        writer.close();

        InstanceList instanceList = new InstanceList(new MalletPipe().buildPipe());

        instanceList.addThruPipe(new CsvIterator(reader, "(\\w+)\\s+(\\w+)\\s*(.*)", 3, 2, 1) // (data, target, name)
                                                                                              // field indices
        );

        System.out.println(instanceList.getAlphabet().size());
        System.out.println(instanceList.size());
        instanceList.save(new File("/home/arun/media/datasets/mallet/amazon-health-db-sisters.instances"));
        System.out.println(rowCount);

        amazonHealthDb.shutdown();
        dbSistersDb.shutdown();
    }

    /**
     * @param text
     * @return
     */
    private static String cleanText(String text)
    {
        // TODO Auto-generated method stub
        return text.replaceAll(":", " ").trim();
    }
}
