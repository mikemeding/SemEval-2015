package edu.uml.semeval.featureextraction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.wordnet.SynonymMap;

import edu.uml.semeval.Data;

public class WordnetSynonymFeatureExtractor implements FeatureExtractor {
    
    private SynonymMap synonymMap;
    
    public WordnetSynonymFeatureExtractor(SynonymMap synonymMap) {
        this.synonymMap = synonymMap;
    }

    @Override
    public double[] extractFeatures(Data data) {
        
        ArrayList<Double> features = new ArrayList<Double>();
        
        Set<String> origWordSet = removeTrend(data.getOrigCleanSplit(), data.getOrigTrendStart(), data.getOrigTrendEnd());
        Set<String> candWordSet = removeTrend(data.getCandCleanSplit(), data.getCandTrendStart(), data.getCandTrendEnd());
        
        Set<String> origSynWordSet = synonyms(origWordSet);
        Set<String> candSynWordSet = synonyms(candWordSet);
        
//        int synonymIntersection = intersection(origSynWordSet, candSynWordSet);
        
        
        double precisionNGram = 0.0;
        double recallNGram = 0.0;
        double f1NGram = 0.0;
        
        if(!origSynWordSet.isEmpty()) {
            precisionNGram = (double) intersection(origWordSet, candSynWordSet) / origWordSet.size();
        }
        
        if(!candSynWordSet.isEmpty()) {
            recallNGram = (double) intersection(origSynWordSet, candWordSet) / candWordSet.size();
        }
        
        if(precisionNGram + recallNGram > 0.0) {
            f1NGram = 2 * precisionNGram * recallNGram / (precisionNGram + recallNGram);
        }
        
        features.add(precisionNGram); 
        features.add(recallNGram);
        features.add(f1NGram);
        
        double[] featureArray = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            featureArray[i] = features.get(i);
        }
        
        return featureArray;
    }
    
    private int intersection(Set<String> set1, Set<String> set2) {
        
        int count = 0;
        
        for(String str: set1) {
            if(set2.contains(str)) {
                count++;
            }
        }
        
        return count;
    }
    
    private Set<String> removeTrend(String[] words, int trendStart, int trendEnd) {
        HashSet<String> clean = new HashSet<String>();
        
        for(int i = 0; i < words.length; i++) {
            if(i < trendStart || i > trendEnd) {
                clean.add(words[i]);
            }
        }
        
        return clean;
    }
    
    private Set<String> synonyms(Set<String> words) {
        
        HashSet<String> synonyms = new HashSet<String>();
        
        for(String word: words) {
            synonyms.add(word);
            for(String synonym: synonymMap.getSynonyms(word)) {
                synonyms.add(synonym);
            }
        }

        return synonyms;
    }
}
