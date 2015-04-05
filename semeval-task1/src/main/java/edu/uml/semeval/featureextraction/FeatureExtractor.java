package edu.uml.semeval.featureextraction;

import edu.uml.semeval.Data;

public interface FeatureExtractor {

    public double[] extractFeatures(Data data);
}
