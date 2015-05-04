package edu.uml.semeval.featureextraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uml.lexicon.SentiWordNet;
import edu.uml.lexicon.SentiWordNetEntry;
import edu.uml.lexicon.SentiWordNetFeature;
import edu.uml.semeval.Data;

public class SentiWordNetFeatureExtraction implements FeatureExtractor {

    private SentiWordNet sentiWordNet;
    private Map<String, String> arkTweetNLPTagToSentiWordNetTag;

    public SentiWordNetFeatureExtraction(SentiWordNet sentiWordNet) {
        this.sentiWordNet = sentiWordNet;

        arkTweetNLPTagToSentiWordNetTag = new HashMap<>();

        arkTweetNLPTagToSentiWordNetTag.put("A", "a"); // adjective
        arkTweetNLPTagToSentiWordNetTag.put("N", "n"); // noun
        arkTweetNLPTagToSentiWordNetTag.put("V", "v"); // verb
        arkTweetNLPTagToSentiWordNetTag.put("R", "r"); // adverb
    }

    public double[] extractFeatures(Data data) {

        SentiWordNetFeature origFeatures = createFeaturesObject(data.getOrigCleanSplit(), data.getOrigArkTweet(), data.getOrigTrendStart(), data.getOrigTrendEnd());
        SentiWordNetFeature candFeatures = createFeaturesObject(data.getCandCleanSplit(), data.getCandArkTweet(), data.getCandTrendStart(), data.getCandTrendEnd());
        
        ArrayList<Double> featureList = new ArrayList<>();

        addFeatures(origFeatures, featureList);
        addFeatures(candFeatures, featureList);
        
        addFeature(origFeatures, candFeatures, featureList);
        
        double[] features = new double[featureList.size()];
        for (int i = 0; i < featureList.size(); i++) {
            features[i] = featureList.get(i);
        }

        return features;
    }
    
    private void addFeature(SentiWordNetFeature origFeatures, SentiWordNetFeature candFeatures, List<Double> features) {
        features.add(origFeatures.isMorePositive() && candFeatures.isMorePositive()? 1.0 : 0.0);
        features.add(origFeatures.isMorePositiveAdjective() && candFeatures.isMorePositiveAdjective()? 1.0 : 0.0);
        features.add(origFeatures.isNegativeAdjectiveMajority() && candFeatures.isNegativeAdjectiveMajority()? 1.0 : 0.0);
        features.add(origFeatures.isNegativeMajority() && candFeatures.isNegativeMajority()? 1.0 : 0.0);
        features.add(origFeatures.isObjectiveAdjectiveMajority() && candFeatures.isObjectiveAdjectiveMajority()? 1.0 : 0.0);
        features.add(origFeatures.isObjectiveMajority() && candFeatures.isObjectiveMajority()? 1.0 : 0.0);
        features.add(origFeatures.isPositiveAdjectiveMajority() && candFeatures.isPositiveAdjectiveMajority()? 1.0 : 0.0);
        features.add(origFeatures.isPositiveMajority() && candFeatures.isPositiveMajority()? 1.0 : 0.0);
    }
    
    private void addFeatures(SentiWordNetFeature sentiWordNetFeature, List<Double> features) {
        features.add(sentiWordNetFeature.getAveragePositiveScore());
        features.add(sentiWordNetFeature.getAverageNegativeScore());
        features.add(sentiWordNetFeature.getAverageObjectiveScore());

        features.add(sentiWordNetFeature.getNonZeroAveragePositiveScore());
        features.add(sentiWordNetFeature.getNonZeroAverageNegativeScore());
        features.add(sentiWordNetFeature.getNonZeroAverageObjectiveScore());

        features.add(sentiWordNetFeature.getNonZeroAveragePositiveAdjectiveScore());
        features.add(sentiWordNetFeature.getNonZeroAverageNegativeAdjectiveScore());
        features.add(sentiWordNetFeature.getNonZeroAverageObjectiveAdjectiveScore());
        
        features.add(sentiWordNetFeature.isPositiveMajority() ? 1.0 : 0.0);
        features.add(sentiWordNetFeature.isNegativeMajority() ? 1.0 : 0.0);
        features.add(sentiWordNetFeature.isObjectiveMajority() ? 1.0 : 0.0);

        features.add(sentiWordNetFeature.isPositiveAdjectiveMajority() ? 1.0 : 0.0);
        features.add(sentiWordNetFeature.isNegativeAdjectiveMajority() ? 1.0 : 0.0);
        features.add(sentiWordNetFeature.isObjectiveAdjectiveMajority() ? 1.0 : 0.0);

        features.add(sentiWordNetFeature.isMorePositive() ? 1.0 : 0.0);
        features.add(sentiWordNetFeature.isMorePositiveAdjective() ? 1.0 : 0.0);
    }

    private SentiWordNetFeature createFeaturesObject(String[] words, String[] tags, int startTrend, int endTrend) {

        int wordCount = 0;

        double sumPositiveScore = 0.0;
        double sumNegativeScore = 0.0;
        double sumObjectiveScore = 0.0;

        int nonZeroPositiveCount = 0;
        int nonZeroNegativeCount = 0;
        int nonZeroObjectiveCount = 0;

        double sumPositiveAdjectiveScore = 0.0;
        double sumNegativeAdjectiveScore = 0.0;
        double sumObjectiveAdjectiveScore = 0.0;

        int nonZeroPositiveAdjectiveCount = 0;
        int nonZeroNegativeAdjectiveCount = 0;
        int nonZeroObjectiveAdjectiveCount = 0;
        
        for(int i = 0; i < words.length; i++) {
            
            if(i < startTrend || i > endTrend) {
                
                String token = words[i];
                String tag = tags[i];
             
                token = token.replace("#", "");
                
                List<SentiWordNetEntry> entries = sentiWordNet.getSentiWordNetEntries(
                        arkTweetNLPTagToSentiWordNetTag.get(tag), token);
                for (SentiWordNetEntry entry : entries) {
                    wordCount++;
                    sumPositiveScore += entry.getPositiveScore();
                    sumNegativeScore += entry.getNegativeScore();
                    sumObjectiveScore += entry.getObjectiveScore();

                    if (entry.getPositiveScore() > 0.0) nonZeroPositiveCount++;
                    if (entry.getNegativeScore() > 0.0) nonZeroNegativeCount++;
                    if (entry.getObjectiveScore() > 0.0) nonZeroObjectiveCount++;

                    if (tag.equals("A")) {
                        sumPositiveAdjectiveScore += entry.getPositiveScore();
                        sumNegativeAdjectiveScore += entry.getNegativeScore();
                        sumObjectiveAdjectiveScore += entry.getObjectiveScore();

                        if (entry.getPositiveScore() > 0.0) nonZeroPositiveAdjectiveCount++;
                        if (entry.getNegativeScore() > 0.0) nonZeroNegativeAdjectiveCount++;
                        if (entry.getObjectiveScore() > 0.0) nonZeroObjectiveAdjectiveCount++;
                    }
                }
            }
        }

        return new SentiWordNetFeature(wordCount, sumPositiveScore, sumNegativeScore,
                sumObjectiveScore, nonZeroPositiveCount, nonZeroNegativeCount,
                nonZeroObjectiveCount, sumPositiveAdjectiveScore, sumNegativeAdjectiveScore,
                sumObjectiveAdjectiveScore, nonZeroPositiveAdjectiveCount,
                nonZeroNegativeAdjectiveCount, nonZeroObjectiveAdjectiveCount);
    }
}
