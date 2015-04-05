package edu.uml.semeval.featureextraction;

import java.util.ArrayList;

import edu.uml.semeval.Data;

public class ArkTweetNgramFeatureExtractor extends BaseModFeatureExtractor {

    @Override
    public double[] extractFeatures(Data data) {
        
        ArrayList<Double> features = new ArrayList<Double>();
        
//        addFeatures(data.getOrigArkTweet(), data.getCandArkTweet(), features);
        addFeatures(data.getOrigArkTweet(), data.getOrigTrendStart(), data.getOrigTrendEnd(), data.getCandArkTweet(), data.getCandTrendStart(), data.getCandTrendEnd(), features);
        
        double[] featureArray = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            featureArray[i] = features.get(i);
        }
        
        return featureArray;
    }

}
