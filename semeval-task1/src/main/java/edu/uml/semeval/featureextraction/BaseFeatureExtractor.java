package edu.uml.semeval.featureextraction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uml.semeval.Data;
import edu.uml.semeval.PorterStemmer;

public class BaseFeatureExtractor implements FeatureExtractor {

    public double[] extractFeatures(Data data) {
        ArrayList<Double> features = new ArrayList<Double>();

        String[] origSplit = data.getOrigCleanSplit();
        String[] candSplit = data.getCandCleanSplit();
        
        addFeatures(origSplit, candSplit, features);
        
        //stemmed words
        String[] origStrStemmed = new String[origSplit.length];
        String[] candStrStemmed = new String[candSplit.length];
        
        for(int i = 0; i < origSplit.length; i++) {
            origStrStemmed[i] = stemWord(origSplit[i]);
        }
        
        for(int i = 0; i < candSplit.length; i++) {
            candStrStemmed[i] = stemWord(candSplit[i]);
        }
        
        addFeatures(origStrStemmed, candStrStemmed, features);
        
        double[] featureArray = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            featureArray[i] = features.get(i);
        }
        
        return featureArray;
    }
    
    protected void addFeatures(String[] origSplit, String[] candSplit, List<Double> features) {
        Set<String> orig1Gram = buildNgramSet(origSplit, 1);
        Set<String> cand1Gram = buildNgramSet(candSplit, 1);
        
        calculateFeaturesForNGrams(orig1Gram, cand1Gram, features);
        
        Set<String> orig2Gram = buildNgramSet(origSplit, 2);
        Set<String> cand2Gram = buildNgramSet(candSplit, 2);
        
        calculateFeaturesForNGrams(orig2Gram, cand2Gram, features);
        
        Set<String> orig3Gram = buildNgramSet(origSplit, 3);
        Set<String> cand3Gram = buildNgramSet(candSplit, 3);
        
        calculateFeaturesForNGrams(orig3Gram, cand3Gram, features);
    }

    protected Set<String> buildNgramSet(String[] split, int n) {
        
        HashSet<String> set = new HashSet<String>();
        
        for(int i = 0; i < split.length - (n - 1); i++) {
            
            String token = split[i];
            
            for(int j = 1; j < n; j++) {
                token += " " + split[i + j]; 
            }
            
            set.add(token);
        }
        
        return set;
    }
    
    protected int intersection(Set<String> set1, Set<String> set2) {
        
        int count = 0;
        
        for(String token: set1) {
            if(set2.contains(token)) {
                count++;
            }
        }
        
        return count;
    }
    
    protected void calculateFeaturesForNGrams(Set<String> origNGram, Set<String> candNGram, List<Double> features) {
        int intersect = intersection(origNGram, candNGram);
        
        double precisionNGram = 0.0;
        double recallNGram = 0.0;
        double f1NGram = 0.0;
        
        if(!origNGram.isEmpty()) {
            precisionNGram = (double) intersect / origNGram.size();
        }
        
        if(!candNGram.isEmpty()) {
            recallNGram = (double) intersect / candNGram.size();
        }
        
        if(precisionNGram + recallNGram > 0.0) {
            f1NGram = 2 * precisionNGram * recallNGram / (precisionNGram + recallNGram);
        }
        
        features.add(precisionNGram); 
        features.add(recallNGram);
        features.add(f1NGram);
    }
    
    private String stemWord(String word) {
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.add(word.toCharArray(), word.length());
        stemmer.stem();
        
        return new String(stemmer.getResultBuffer()).substring(0, stemmer.getResultLength());
    }
}
