package edu.uml.semeval.featureextraction;

import edu.uml.semeval.Data;

import java.util.ArrayList;

/**
 * Created by mike on 4/14/15.
 */
public class NegationFeatureExtractor implements FeatureExtractor {

    @Override
    public double[] extractFeatures(Data data) {
        ArrayList<Double> features = new ArrayList<Double>();



        // parse ArrayList to array
        double[] featureArray = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            featureArray[i] = features.get(i);
        }
        return featureArray;
    }
}
