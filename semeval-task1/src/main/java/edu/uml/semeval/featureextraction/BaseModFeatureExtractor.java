package edu.uml.semeval.featureextraction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uml.semeval.Data;

public class BaseModFeatureExtractor extends BaseFeatureExtractor {

    public double[] extractFeatures(Data data) {
        
        ArrayList<Double> features = new ArrayList<Double>();
        
        String[] origSplit = data.getOrigCleanSplit();
        String[] candSplit = data.getCandCleanSplit();
        
        addFeatures(origSplit, data.getOrigTrendStart(), data.getOrigTrendEnd(), candSplit, data.getCandTrendStart(), data.getCandTrendEnd(), features);
        
        double[] featureArray = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            featureArray[i] = features.get(i);
        }
        
        return featureArray;
    }

    protected void addFeatures(String[] origSplit, int origTrendStart, int origTrendEnd, String[] candSplit, int candTrendStart, int candTrendEnd, List<Double> features) {
        Set<String> orig1Gram = buildNgramSet(origSplit, 1, origTrendStart, origTrendEnd);
        Set<String> cand1Gram = buildNgramSet(candSplit, 1, candTrendStart, candTrendEnd);
        
        calculateFeaturesForNGrams(orig1Gram, cand1Gram, features);
        
        Set<String> orig2Gram = buildNgramSet(origSplit, 2, origTrendStart, origTrendEnd);
        Set<String> cand2Gram = buildNgramSet(candSplit, 2, candTrendStart, candTrendEnd);
        
        calculateFeaturesForNGrams(orig2Gram, cand2Gram, features);
        
        Set<String> orig3Gram = buildNgramSet(origSplit, 3, origTrendStart, origTrendEnd);
        Set<String> cand3Gram = buildNgramSet(candSplit, 3, candTrendStart, candTrendEnd);
        
        calculateFeaturesForNGrams(orig3Gram, cand3Gram, features);
    }

    protected Set<String> buildNgramSet(String[] split, int n, int trendStart, int trendEnd) {
        
        HashSet<String> set = new HashSet<String>();
        
        // start to before beginning of trend
        for(int i = 0; i < trendStart - n; i++) {
            
            String token = split[i];
            
            for(int j = 1; j < n; j++) {
                token += " " + split[i + j]; 
            }
            
            set.add(token);
        }
        
        // after end of trend to end of sentence
        for(int i = trendEnd + 1; i < split.length - (n - 1); i++) {
            
            String token = split[i];
            
            for(int j = 1; j < n; j++) {
                token += " " + split[i + j]; 
            }
            
            set.add(token);
        }
        
        return set;
    }
}
