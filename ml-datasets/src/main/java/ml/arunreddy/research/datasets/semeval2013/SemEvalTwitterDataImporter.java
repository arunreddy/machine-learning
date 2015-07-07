package ml.arunreddy.research.datasets.semeval2013;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ml.arunreddy.research.datasets.adt.Sentiment;
import ml.arunreddy.research.datasets.impl.AbstractDataImporter;

/**
 * Data importer for Sem Eval twitter data for sentiment analysis.
 */
public class SemEvalTwitterDataImporter extends AbstractDataImporter
{

    private static final Logger logger = LoggerFactory.getLogger(SemEvalTwitterDataImporter.class);

    public static final String SEMEVAL_TWITTER_2013_DB_NAME = "semeval-twitter-2013.db";

    /**
     * Constructor takes the filename of ohmdb, which typically resides in the root directory of the project/sub-module.
     *
     * @param dbName filename of the ohdb database.
     */
    public SemEvalTwitterDataImporter(String dbName)
    {
        super(dbName);
    }

    /**
     * Reads sentiment data from each line. The semeval twitter data for sentiment analysis is of the following format:
     * <id1>\t<id2>\t<label>\t<tweet text>
     *
     * @param filePath
     * @param instanceType
     */
    public List<Sentiment> readSentimentData(String filePath, int instanceType)
    {

        List<Sentiment> sentimentList = new ArrayList<Sentiment>();
        File file = new File(filePath);
        BufferedReader reader = getBufferedReader(file, filePath.endsWith("gz") ? true : false);
        if (reader != null) {
            try {
                while (reader.ready()) {
                    String line = reader.readLine();
                    String[] fields = line.split("\t");
                    Sentiment sentiment = new Sentiment(fields[0], fields[3]);
                    sentiment.setLabel(fields[2]);
                    sentiment.setInstanceType(instanceType);
                    sentimentList.add(sentiment);

                }
            } catch (IOException e) {
                logger.error("Error reading the given file {}.", filePath);
                e.printStackTrace();
            }
        }
        return sentimentList;
    }

    public static void main(String[] args) throws Exception
    {

        // 2013 Database.
        SemEvalTwitterDataImporter semEvalTwitterDataImporter =
            new SemEvalTwitterDataImporter(SEMEVAL_TWITTER_2013_DB_NAME);

        // Training Data.
        URL trainingFileUrl = SemEvalTwitterDataImporter.class.getClassLoader()
            .getResource("semeval/2013/Trainingsdata-SemEval2013.txt.gz");
        List<Sentiment> sentimentList =
            semEvalTwitterDataImporter.readSentimentData(trainingFileUrl.getFile(), Sentiment.TRAIN_INSTANCE);
        semEvalTwitterDataImporter.writeSentimentListToDb(sentimentList);

        // Test Data.
        URL testFileUrl =
            SemEvalTwitterDataImporter.class.getClassLoader().getResource("semeval/2013/Testdata-SemEval2013.txt.gz");
        sentimentList = semEvalTwitterDataImporter.readSentimentData(testFileUrl.getFile(), Sentiment.TEST_INSTANCE);
        semEvalTwitterDataImporter.writeSentimentListToDb(sentimentList);

        // Validation Data.
        URL validationFileUrl =
            SemEvalTwitterDataImporter.class.getClassLoader().getResource("semeval/2013/Devdata-SemEval2013.txt.gz");
        sentimentList =
            semEvalTwitterDataImporter.readSentimentData(validationFileUrl.getFile(), Sentiment.VALIDATION_INSTANCE);
        semEvalTwitterDataImporter.writeSentimentListToDb(sentimentList);

        // 2014 Database.

        // 2015 Database.

    }
}
