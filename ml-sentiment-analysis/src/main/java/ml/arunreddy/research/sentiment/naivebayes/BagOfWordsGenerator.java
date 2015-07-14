package ml.arunreddy.research.sentiment.naivebayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.PrintInputAndTarget;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceNGrams;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelVector;
import ml.arunreddy.research.sentiment.stanford.impl.StanfordSentimentAnalyzer;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class BagOfWordsGenerator
{

    Pipe pipe;

    File parentDirectory;

    int[] correct = new int[22];

    StanfordSentimentAnalyzer analyzer = new StanfordSentimentAnalyzer();

    public BagOfWordsGenerator()
    {
        pipe = buildPipe();
    }

    public Pipe buildPipe()
    {
        ArrayList pipeList = new ArrayList();

        // Read data from File objects
        pipeList.add(new Input2CharSequence("UTF-8"));

        // Regular expression for what constitutes a token.
        // This pattern includes Unicode letters, Unicode numbers,
        // and the underscore character. Alternatives:
        // "\\S+" (anything not whitespace)
        // "\\w+" ( A-Z, a-z, 0-9, _ )
        // "[\\p{L}\\p{N}_]+|[\\p{P}]+" (a group of only letters and numbers OR
        // a group of only punctuation marks)
        Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{N}_]+");

        // Tokenize raw strings
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));

        // Normalize all tokens to all lowercase
        pipeList.add(new TokenSequenceLowercase());
        //
        int[] ngrams = {1, 2};
        pipeList.add(new TokenSequenceNGrams(ngrams));

        // Remove stopwords from a standard English stoplist.
        // options: [case sensitive] [mark deletions]
        pipeList.add(new TokenSequenceRemoveStopwords(false, false));

        // Rather than storing tokens as strings, convert
        // them to integers by looking them up in an alphabet.
        pipeList.add(new TokenSequence2FeatureSequence());

        // Do the same thing for the "target" field:
        // convert a class label string to a Label object,
        // which has an index in a Label alphabet.
        pipeList.add(new Target2Label());

        // Now convert the sequence of features to a sparse vector,
        // mapping feature IDs to counts.
        pipeList.add(new FeatureSequence2FeatureVector());

        // Print out the features and the label
        pipeList.add(new PrintInputAndTarget());

        return new SerialPipes(pipeList);
    }

    public void loadData() throws Exception
    {
        File file = new File("/home/arun/code/phd/machine-learning/final-senti.dataset");
        File outFile = new File("/home/arun/code/phd/machine-learning/final-senti.dat");

        InstanceList instanceList = new InstanceList(pipe);

        instanceList.addThruPipe(new CsvIterator(new FileReader(file), "(\\w+)\\s+(\\w+)\\s+(.*)", 3, 2, 1) // (data,
                                                                                                            // target,
                                                                                                            // name)
                                                                                                            // field
                                                                                                            // indices
        );

        BufferedReader reader = new BufferedReader(new FileReader(file));
        int count = 0;
        while (reader.ready()) {
            String line = reader.readLine();
            String line2 = line.substring(line.indexOf('\t'), line.length()).trim();
            String line3 = line2.substring(line2.indexOf('\t'), line2.length()).trim();
            Instance instance = instanceList.get(count);
            instance.unLock();
            instance.setSource(line3);
            instance.lock();
            count++;
        }

        System.out.println(instanceList.getAlphabet().size());
        System.out.println(instanceList.size());
        instanceList.save(outFile);
        reader.close();
    }

    public Classifier getNaiveBayesClassifier(InstanceList instanceList)
    {
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();
        return trainer.train(instanceList);
    }

    public double weightedClassifiers(Classifier classifier01, Classifier classifier02, InstanceList instanceList,
        double ratio)
    {
        int correct = 0;

        for (Instance instance : instanceList) {
            Classification classification01 = classifier01.classify(instance);
            Classification classification02 = classifier02.classify(instance);

            double neg_01 = 0.0;
            double pos_01 = 0.0;
            double neg_02 = 0.0;
            double pos_02 = 0.0;

            LabelVector labelVector01 = classification01.getLabelVector();
            if (labelVector01.getMaxValuedIndex() == 0) {
                neg_01 = labelVector01.getMaxValue();
                pos_01 = 1.0 - neg_01;
            } else {
                pos_01 = labelVector01.getMaxValue();
                neg_01 = 1.0 - pos_01;
            }

            LabelVector labelVector02 = classification02.getLabelVector();
            if (labelVector02.getMaxValuedIndex() == 0) {
                neg_02 = labelVector02.getMaxValue();
                pos_02 = 1.0 - neg_02;
            } else {
                pos_02 = labelVector02.getMaxValue();
                neg_02 = 1.0 - pos_02;
            }

            double pos = ratio * pos_01 + (1.0 - ratio) * pos_02;
            double neg = ratio * neg_01 + (1.0 - ratio) * neg_02;

            String goldLabel = instance.getLabeling().toString();
            String predictedLabel = "";

            if (pos > neg) {
                predictedLabel = "1";
            } else {
                predictedLabel = "0";
            }

            if (goldLabel.equalsIgnoreCase(predictedLabel)) {
                correct++;
            }
        }

        double accuracy = (double) correct / (double) instanceList.size();

        // System.out.println("Total :["+instanceList.size()+"] and Correct :["+correct+"] and Accuracy
        // :["+accuracy+"]");

        return accuracy;

    }

    public void testAndPrint(Classifier classifier, InstanceList instanceList)
    {

        int correct = 0;

        for (Instance instance : instanceList) {
            Classification classification = classifier.classify(instance);
            String goldLabel = instance.getLabeling().toString();
            String predictedLabel = classification.getLabeling().getBestLabel().toString();
            if (goldLabel.equalsIgnoreCase(predictedLabel)) {
                correct++;
            }
        }

        double accuracy = (double) correct / (double) instanceList.size();

        System.out.println(
            "Total :[" + instanceList.size() + "] and Correct :[" + correct + "] and Accuracy :[" + accuracy + "]");

    }

    public void results()
    {
        File outFile = new File("/home/arun/code/phd/machine-learning/final-senti.dat");
        InstanceList instanceList = InstanceList.load(outFile);

        System.out.println(instanceList.size());
        System.out.println(instanceList.getAlphabet().size());

        InstanceList sourceInstanceList = instanceList.subList(0, 10662);

        for (Instance instance : sourceInstanceList) {
            System.out.println(instance.getSource());
        }
        System.exit(0);

        InstanceList targetTrainInstanceList = instanceList.subList(10662, 13578);
        InstanceList testTrainInstanceList = instanceList.subList(13578, 15460);

        InstanceList sourceAndTargetCombined = instanceList.cloneEmpty();
        sourceAndTargetCombined.addAll(sourceInstanceList);
        sourceAndTargetCombined.addAll(targetTrainInstanceList);

        Classifier sourceModel = getNaiveBayesClassifier(sourceInstanceList);
        Classifier targetModel = getNaiveBayesClassifier(targetTrainInstanceList);
        Classifier sourceAndTargetModel = getNaiveBayesClassifier(sourceAndTargetCombined);

        //
        testAndPrint(sourceModel, testTrainInstanceList);
        testAndPrint(targetModel, testTrainInstanceList);
        testAndPrint(sourceAndTargetModel, testTrainInstanceList);

        for (double i = 0.0; i <= 1.1; i = i + 0.05) {
            double accuracy = weightedClassifiers(sourceModel, targetModel, testTrainInstanceList, i);

            System.out.println(i + ", " + accuracy);
        }
    }

    public void incrementalClassifier()
    {
        File outFile = new File("/home/arun/code/phd/machine-learning/final-senti.dat");
        InstanceList instanceList = InstanceList.load(outFile);

        System.out.println(instanceList.size());
        System.out.println(instanceList.getAlphabet().size());

        InstanceList sourceInstanceList = instanceList.subList(0, 10662);
        InstanceList targetTrainInstanceList = instanceList.subList(10662, 13578);
        InstanceList testTrainInstanceList = instanceList.subList(13578, 15460);

        System.out.println(targetTrainInstanceList.size());

        InstanceList targetTrainInstanceListNegative = targetTrainInstanceList.subList(0, 1458);
        InstanceList targetTrainInstanceListPositive = targetTrainInstanceList.subList(1458, 2916);

        Map<String, Double> stanfordValues = new HashMap<String, Double>();
        for (Instance instance : testTrainInstanceList) {
            double value = analyzer.getSentimentScore(instance.getSource().toString());
            stanfordValues.put(instance.getName().toString(), new Double(value));
        }

        double[] accuracy_zero = new double[30];
        double[] accuracy_half = new double[30];
        double[] accuracy_full = new double[30];

        double[] accuracy_zero_std = new double[30];
        double[] accuracy_half_std = new double[30];
        double[] accuracy_full_std = new double[30];

        
        for (int i = 0; i <= 1458; i = i + 50) {

            double[] accuracy_zero_itr = new double[20];
            double[] accuracy_half_itr = new double[20];
            double[] accuracy_full_itr = new double[20];



            for (int j = 0; j < 20; j++) {
                targetTrainInstanceListNegative.shuffle(new Random());
                targetTrainInstanceListPositive.shuffle(new Random());

                InstanceList targetTrain = targetTrainInstanceListNegative.subList(0, 1458 - i);
                targetTrain.addAll(targetTrainInstanceListPositive.subList(0, 1458 - i));
                Classifier targetModel = getNaiveBayesClassifier(targetTrain);

                accuracy_zero_itr[j] =
                    weightedStanfordClassifierWithRatio(targetModel, testTrainInstanceList, 0, stanfordValues);
                accuracy_half_itr[j] =
                    weightedStanfordClassifierWithRatio(targetModel, testTrainInstanceList, 0.5, stanfordValues);
                accuracy_full_itr[j] =
                    weightedStanfordClassifierWithRatio(targetModel, testTrainInstanceList, 1.0, stanfordValues);
            }
            
            accuracy_zero[i/50] = new Mean().evaluate(accuracy_zero_itr);
            accuracy_half[i/50] = new Mean().evaluate(accuracy_half_itr);
            accuracy_full[i/50] = new Mean().evaluate(accuracy_full_itr);
            
            accuracy_zero_std[i/50] = new StandardDeviation().evaluate(accuracy_zero_itr);
            accuracy_half_std[i/50] = new StandardDeviation().evaluate(accuracy_half_itr);
            accuracy_full_std[i/50] = new StandardDeviation().evaluate(accuracy_full_itr);
            
        }
        
        System.out.println("=========  MEAN  ==============");
        for(int i=0;i<accuracy_full.length;i++){
            System.out.println(i+"\t"+accuracy_zero[i]+"\t"+accuracy_half[i]+"\t"+accuracy_full[i]);
        }
        
        System.out.println("=========  STD  ==============");
        for(int i=0;i<accuracy_full.length;i++){
            System.out.println(i+"\t"+accuracy_zero_std[i]+"\t"+accuracy_half_std[i]+"\t"+accuracy_full_std[i]);
        }
        
    }

    public static void main(String[] args) throws Exception
    {
        BagOfWordsGenerator generator = new BagOfWordsGenerator();
        // generator.loadData();

        // generator.tenFoldCrossValidation();

        // generator.results();

        generator.incrementalClassifier();
    }

    private void tenFoldCrossValidation() throws Exception
    {
        // TODO Auto-generated method stub
        File outFile = new File("/home/arun/code/phd/machine-learning/final-senti.dat");
        InstanceList instanceList = InstanceList.load(outFile);

        System.out.println(instanceList.size());
        System.out.println(instanceList.getAlphabet().size());

        // InstanceList negTest = instanceList.subList(0, 1000);
        // InstanceList sourceInstanceList = instanceList.subList(1000, 9662);
        // InstanceList posTest = instanceList.subList(9662, 10662);

        InstanceList sourceInstanceList = instanceList.subList(10662, 13578);
        InstanceList testTrainInstanceList = instanceList.subList(13578, 15460);

        sourceInstanceList.shuffle(new Random());
        sourceInstanceList.shuffle(new Random());

        // ******************************** CROSS VALIDATION
        // ********************************//
        // CrossValidationIterator crossValidationIterator =
        // sourceInstanceList.crossValidationIterator(10);
        // while(crossValidationIterator.hasNext()){
        // InstanceList[] listsArray = crossValidationIterator.next();
        //
        // InstanceList train = listsArray[0];
        // InstanceList test = listsArray[1];
        //
        // Classifier sourceModel = getNaiveBayesClassifier(train); //
        // CrossValidationIterator crossValidationIterator =
        // sourceInstanceList.crossValidationIterator(10);
        // while(crossValidationIterator.hasNext()){
        // InstanceList[] listsArray = crossValidationIterator.next();
        //
        // InstanceList train = listsArray[0];
        // InstanceList test = listsArray[1];
        //
        // Classifier sourceModel = getNaiveBayesClassifier(train);
        //
        // this.weightedStanfordClassifiers(sourceModel, test);
        // }

        // for (int i = 0; i < correct.length; i++) {
        // double accuracy = (double)correct[i] / 2916;
        // System.out.println("---"+accuracy);
        // }
        //
        // this.weightedStanfordClassifiers(sourceModel, test);
        // }

        // for (int i = 0; i < correct.length; i++) {
        // double accuracy = (double)correct[i] / 2916;
        // System.out.println("---"+accuracy);
        // }

        // ******************************** CROSS VALIDATION END
        // ****************************//

        // **************************** TESTING
        // ****************************************
        Classifier sourceModel = getNaiveBayesClassifier(sourceInstanceList);
        for (Instance instance : testTrainInstanceList) {
            Classification classification02 = sourceModel.classify(instance);
            double value = 0; // analyzer.getSentimentScore(instance.getSource().toString());
            LabelVector labelVector02 = classification02.getLabelVector();

            double neg_01 = 0.0;
            double pos_01 = 0.0;
            double neg_02 = 0.0;
            double pos_02 = 0.0;

            if (labelVector02.getMaxValuedIndex() == 0) {
                neg_02 = labelVector02.getMaxValue();
                pos_02 = 1.0 - neg_02;
            } else {
                pos_02 = labelVector02.getMaxValue();
                neg_02 = 1.0 - pos_02;
            }
            System.out.println(value);
            System.out.println(labelVector02.getMaxValue());

            int ctr = 0;
            String goldLabel = instance.getLabeling().toString();
            for (double i = 0.0; i <= 1.1; i = i + 0.05) {

                double semValue = pos_02; // i * value + (1.0 - i) * pos_02;
                String predictedLabel = "";

                if (semValue > 0.5) {
                    predictedLabel = "1";
                } else if (semValue < 0.5) {
                    predictedLabel = "0";
                }

                if (goldLabel.equalsIgnoreCase(predictedLabel)) {
                    this.correct[ctr]++;
                }
                ctr++;
            }
        }

        System.out.println("**************** RESULTS ****************************");

        for (int i = 0; i < correct.length; i++) {
            System.out.println(correct[i]);
        }

        // ******************************************************************************
    }

    public double weightedStanfordClassifierWithRatio(Classifier classifier, InstanceList instanceList, double ratio,
        Map<String, Double> stanfordValues)
    {

        int correct = 0;

        for (Instance instance : instanceList) {
            Classification classification02 = classifier.classify(instance);

            double value = stanfordValues.get(instance.getName().toString());
            LabelVector labelVector02 = classification02.getLabelVector();

            double neg_01 = 0.0;
            double pos_01 = 0.0;
            double neg_02 = 0.0;
            double pos_02 = 0.0;

            if (labelVector02.getMaxValuedIndex() == 0) {
                neg_02 = labelVector02.getMaxValue();
                pos_02 = 1.0 - neg_02;
            } else {
                pos_02 = labelVector02.getMaxValue();
                neg_02 = 1.0 - pos_02;
            }

            int ctr = 0;
            String goldLabel = instance.getLabeling().toString();
            double semValue = ratio * value + (1.0 - ratio) * pos_02;
            String predictedLabel = "";

            if (semValue > 0.5) {
                predictedLabel = "1";
            } else {
                predictedLabel = "0";
            }

            if (goldLabel.equalsIgnoreCase(predictedLabel)) {
                correct++;
            }

        }

        return (double) correct / (double) instanceList.size();
    }

    public void weightedStanfordClassifiers(Classifier classifier02, InstanceList instanceList)
    {

        for (Instance instance : instanceList) {
            Classification classification02 = classifier02.classify(instance);
            System.out.println("----------------------------" + instance.getName());

            double value = analyzer.getSentimentScore(instance.getSource().toString());
            LabelVector labelVector02 = classification02.getLabelVector();

            double neg_01 = 0.0;
            double pos_01 = 0.0;
            double neg_02 = 0.0;
            double pos_02 = 0.0;

            if (labelVector02.getMaxValuedIndex() == 0) {
                neg_02 = labelVector02.getMaxValue();
                pos_02 = 1.0 - neg_02;
            } else {
                pos_02 = labelVector02.getMaxValue();
                neg_02 = 1.0 - pos_02;
            }

            int ctr = 0;
            String goldLabel = instance.getLabeling().toString();

            for (double i = 0.0; i <= 1.1; i = i + 0.05) {

                double semValue = i * value + (1.0 - i) * pos_02;
                String predictedLabel = "";

                if (semValue > 0.5) {
                    predictedLabel = "1";
                } else {
                    predictedLabel = "0";
                }

                if (goldLabel.equalsIgnoreCase(predictedLabel)) {
                    this.correct[ctr]++;
                }
                ctr++;
            }

        }
    }
}
