package edu.uml.semeval;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uml.lexicon.ChatspeakTranslator;
import org.apache.lucene.wordnet.SynonymMap;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import edu.uml.lexicon.HarvardInquirer;
import edu.uml.semeval.featureextraction.ArkTweetNgramFeatureExtractor;
import edu.uml.semeval.featureextraction.BaseFeatureExtractor;
import edu.uml.semeval.featureextraction.BaseModFeatureExtractor;
import edu.uml.semeval.featureextraction.FeatureExtractor;
import edu.uml.semeval.featureextraction.HarvardInquirerFeatureExtraction;
import edu.uml.semeval.featureextraction.WordnetSynonymFeatureExtractor;

public class MLTest {

    public static void main(String[] args) throws IOException {

        HarvardInquirer harvardInquirer = new HarvardInquirer("semeval-task1/resources/Harvard_inquirer/inqtabs.txt");
        SynonymMap synonymMap = new SynonymMap(new FileInputStream("semeval-task1/resources/Wordnet/prolog/wn_s.pl"));
        ChatspeakTranslator chatspeakTranslator = new ChatspeakTranslator();

        ArrayList<FeatureExtractor> featureExtractors = new ArrayList<FeatureExtractor>();
        featureExtractors.add(new BaseFeatureExtractor());
        featureExtractors.add(new BaseModFeatureExtractor());
        featureExtractors.add(new ArkTweetNgramFeatureExtractor());
        // Negation detection feature extractor


//        featureExtractors.add(new HarvardInquirerFeatureExtraction(new HarvardInquirer("resources/Harvard_inquirer/inqtabs.txt")));
        featureExtractors.add(new HarvardInquirerFeatureExtraction(harvardInquirer));
//        featureExtractors.add(new SentiWordNetFeatureExtraction(new SentiWordNet("resources/SentiWordNet/SentiWordNet_3.0.0_20130122.txt")));
//        featureExtractors.add(new SubjectiveLexiconFeatureExtractor(new SubjectiveLexicon("resources/SubjectiveLexicon/subjclueslen1-HLTEMNLP05.tff")));
        featureExtractors.add(new WordnetSynonymFeatureExtractor(synonymMap));
//        featureExtractors.add(new HarvardInquirerWithWordnetSynonymFeatureExtractor(harvardInquirer, synonymMap));

        // Relative file paths not working for me. (revert otherwise)
//        String current = new java.io.File( "." ).getCanonicalPath();
//        System.out.println("Current dir:"+current);
//        featureExtractors.add(new HarvardInquirerFeatureExtraction(new HarvardInquirer(current+"/semeval-task1/resources/Harvard_inquirer/inqtabs.txt")));

        SemEvalData rawTrainData = new SemEvalData(SemEvalData.TRAINING_DATA_FILE);
        SemEvalData rawDevData = new SemEvalData(SemEvalData.DEV_DATA_FILE);

        MLDataSet trainingData = buildDataSet(rawTrainData, featureExtractors);
        MLDataSet devData = buildDataSet(rawDevData, featureExtractors);

        System.out.println("Dataset built!");

        int numberOfIterations = 1000;
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, trainingData.getInputSize()));
//        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 20));
        network.addLayer(new BasicLayer(new ActivationSoftMax(), true, 2));
        network.getStructure().finalizeStructure();
        network.reset();
        MLRegression mlRegression = network;
        MLTrain train = new ResilientPropagation(network, trainingData);
//        MLTrain train = new Backpropagation(network, trainingData);

        int epoch = 0;

        do {
            train.iteration();
            epoch++;
            if (epoch % 10 == 0) System.out.printf("Epoch #%5d \t Error: %8f \n", epoch, train.getError());
        } while (train.getError() > 0.001 && epoch <= numberOfIterations);

        System.out.printf("Final Epoch #%5d \t Error: %8f \n", epoch, train.getError());
        train.finishTraining();

        System.out.println("Training: ");
        printResults(mlRegression, trainingData);

        System.out.println("Dev: ");
        printResults(mlRegression, devData);
        Encog.getInstance().shutdown();
    }

    private static MLDataSet buildDataSet(SemEvalData semEvalData, List<FeatureExtractor> featureExtractors) {
        BasicMLDataSet dataSet = new BasicMLDataSet();
        for (Data data : semEvalData.getData()) {

            ArrayList<Double> mergedFeatures = new ArrayList<Double>();

            for (FeatureExtractor fe : featureExtractors) {
                double[] temp = fe.extractFeatures(data);
                for (double d : temp) {
                    mergedFeatures.add(d);
                }
            }

            double[] featuresArray = new double[mergedFeatures.size()];
            for (int i = 0; i < mergedFeatures.size(); i++) {
                featuresArray[i] = mergedFeatures.get(i);
            }

            double[] labelArray = new double[2];
            if (data.isParaphrase()) {
                labelArray[0] = 1.0;
                labelArray[1] = 0.0;
            } else {
                labelArray[0] = 0.0;
                labelArray[1] = 1.0;
            }

            dataSet.add(new BasicMLData(featuresArray), new BasicMLData(labelArray));
        }
        return dataSet;
    }

    private static void printResults(MLRegression mlRegression, MLDataSet dataSet) {
        int correct = 0;
        double tp = 0;
        double fp = 0;
        double fn = 0;
        double tn = 0;

        double f1 = 0.0;

        // test the neural network
        for (int i = 0; i < dataSet.size(); i++) {

            MLDataPair pair = dataSet.get(i);
            MLData output = mlRegression.compute(pair.getInput());

            int ideal = (int) pair.getIdeal().getData(0);
            int actual = output.getData(0) > 0.5 ? 1 : 0;

            if (actual == ideal) {
                correct++;
            }
            if (actual == 1 && ideal == 1) {
                tp += 1.0;
            }
            if (actual == 1 && ideal == 0) {
                fp += 1.0;
//                System.out.println("fp \t\t" + tweets.get(i).getTweet());
            }
            if (actual == 0 && ideal == 1) {
                fn += 1.0;
//                System.out.println("fn \t\t" + tweets.get(i).getTweet());
            }
            if (actual == 0 && ideal == 0) {
                tn += 1.0;
            }
        }

        System.out.println("tp = " + tp + ", tn = " + tn + ", fp = " + fp + ", fn = " + fn);

        double precision = (tp / (tp + fp));
        double recall = (tp / (tp + fn));

//        F = 2 * P * R / (P + R)
        f1 = 2 * precision * recall / (precision + recall);

        System.out.println(correct + " of " + dataSet.size() + " correctly tagged... " + ((double) correct / dataSet.size()));
        System.out.println("precision: " + precision);
        System.out.println("recall: " + recall);
        System.out.println("f1: " + f1);
    }
}
