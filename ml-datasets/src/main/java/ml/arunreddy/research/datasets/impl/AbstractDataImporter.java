package ml.arunreddy.research.datasets.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ohmdb.api.Db;
import com.ohmdb.api.Ohm;
import com.ohmdb.api.Table;

import ml.arunreddy.research.datasets.DataSets;
import ml.arunreddy.research.datasets.adt.Sentiment;

/**
 * Abstract DataImporter class with implementations to common methods.
 */
public abstract class AbstractDataImporter
{

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataImporter.class);

    protected Db db;

    /**
     * Constructor takes the filename of ohmdb, which typically resides in the root directory of the project/sub-module.
     *
     * @param dbName filename of the ohdb database.
     */
    public AbstractDataImporter(String dbName)
    {
        this(dbName,false);
    }

    public AbstractDataImporter(String dbName, boolean clearDatabase){
        System.out.println(DataSets.ROOT_DIRECTORY);
        String dbFilePath = DataSets.ROOT_DIRECTORY+"ohmdb/"+dbName;
        File file = new File(dbFilePath);
        if(clearDatabase && file.exists()){
            file.delete();
        }
        this.db = Ohm.db(dbFilePath);
    }
    
    protected BufferedReader getBufferedReader(File file, boolean gzip)
    {
        BufferedReader reader = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            // If the file is gzip compressed - decompress the file.
            if (gzip) {
                inputStream = new GZIPInputStream(inputStream);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
        } catch (FileNotFoundException e) {
            logger.error("Given file {} is not found.", file.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Error in uncompressing the file {}.", file.getAbsolutePath());
            e.printStackTrace();
        }
        return reader;
    }

    protected void writeSentimentListToDb(List<Sentiment> sentimentList)
    {
        Table<Sentiment> sentimentTable = this.db.table(Sentiment.class);
        for (Sentiment sentiment : sentimentList) {
            sentimentTable.insert(sentiment);
        }
        logger.debug("Inserted {} records into the sentiment table.", sentimentList.size());
    }

    /**
     * Finalize method executed by the garbage collector.
     * 
     * @throws Throwable
     */
    protected void finalize() throws Throwable
    {
        this.cleanUp();
    }
    
    /**
     * Clean up the importer.
     * <ul>
     *  <li> Shutdown the DB after its use.</li>
     *  </ul>
     */
    protected void cleanUp()
    {
        if (db != null) {
            logger.debug("Shutting down the database.");
            db.shutdown();
        }
    }
    
}
