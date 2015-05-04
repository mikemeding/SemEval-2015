package edu.uml.semeval.featureextraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uml.lexicon.SubjectiveLexicon;
import edu.uml.lexicon.SubjectiveLexiconEntry;
import edu.uml.lexicon.SubjectiveLexiconEntry.PartOfSpeech;
import edu.uml.lexicon.SubjectiveLexiconEntry.Polarity;
import edu.uml.semeval.Data;

public class SubjectiveLexiconFeatureExtractor implements FeatureExtractor {

    private SubjectiveLexicon subjectiveLexicon;

    private Map<String, PartOfSpeech> arkTweetNLPTagToSubjectiveLexiconTag;

    public SubjectiveLexiconFeatureExtractor(SubjectiveLexicon subjectiveLexicon) {
        this.subjectiveLexicon = subjectiveLexicon;

        arkTweetNLPTagToSubjectiveLexiconTag = new HashMap<>();
        arkTweetNLPTagToSubjectiveLexiconTag.put("A", PartOfSpeech.ADJECTIVE);
        arkTweetNLPTagToSubjectiveLexiconTag.put("N", PartOfSpeech.NOUN);
        arkTweetNLPTagToSubjectiveLexiconTag.put("V", PartOfSpeech.VERB);
        arkTweetNLPTagToSubjectiveLexiconTag.put("R", PartOfSpeech.ADVERB);
    }
    
    public double[] extractFeatures(Data data) {

        ArrayList<Double> featureList = new ArrayList<>();
        Counts origCounts = getCounts(data.getOrigCleanSplit(), data.getOrigArkTweet(), data.getOrigTrendStart(), data.getOrigTrendEnd());
        Counts candCounts = getCounts(data.getCandCleanSplit(), data.getCandArkTweet(), data.getCandTrendStart(), data.getCandTrendEnd());
        
        addFeatures(origCounts, featureList);
        addFeatures(candCounts, featureList);
        
        addFeatures(origCounts, candCounts, featureList);
        
        double[] features = new double[featureList.size()];
        for (int i = 0; i < featureList.size(); i++) {
            features[i] = featureList.get(i);
        }

        return features;
    }
    
    private Counts getCounts(String[] words, String[] tags, int startTrend, int endTrend) {

        Counts counts = new Counts();

        for (int i = 0; i < words.length; i++) {

            if(i < startTrend || i > endTrend) {
                PartOfSpeech tag = arkTweetNLPTagToSubjectiveLexiconTag.get(tags[i]);
                String token = words[i].replace("#", "");

                for (SubjectiveLexiconEntry entry : subjectiveLexicon.getEntries(tag, token)) {

                    if (entry.isStrongSubjective()) {

                        if (entry.getPolarity() == Polarity.BOTH) {
                            counts.strongPositiveCount++;
                            counts.strongNegativeCount++;
                        } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                            counts.strongNegativeCount++;
                        } else if (entry.getPolarity() == Polarity.POSITIVE) {
                            counts.strongPositiveCount++;
                        }

                        if (tag == PartOfSpeech.ADJECTIVE) {
                            if (entry.getPolarity() == Polarity.BOTH) {
                                counts.strongPositiveAdjectiveCount++;
                                counts.strongNegativeAdjectiveCount++;
                            } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                                counts.strongNegativeAdjectiveCount++;
                            } else if (entry.getPolarity() == Polarity.POSITIVE) {
                                counts.strongPositiveAdjectiveCount++;
                            }
                        }

                    } else {
                        if (entry.getPolarity() == Polarity.BOTH) {
                            counts.weakPositiveCount++;
                            counts.weakNegativeCount++;
                        } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                            counts.weakNegativeCount++;
                        } else if (entry.getPolarity() == Polarity.POSITIVE) {
                            counts.weakPositiveCount++;
                        }

                        if (tag == PartOfSpeech.ADJECTIVE) {
                            if (entry.getPolarity() == Polarity.BOTH) {
                                counts.weakPositiveAdjectiveCount++;
                                counts.weakNegativeAdjectiveCount++;
                            } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                                counts.weakNegativeAdjectiveCount++;
                            } else if (entry.getPolarity() == Polarity.POSITIVE) {
                                counts.weakPositiveAdjectiveCount++;
                            }
                        }
                    }
                }    
            }
        }
        
        counts.compareStrong = counts.strongNegativeCount > counts.strongPositiveCount;
        counts.compareWeak = counts.weakNegativeCount > counts.weakPositiveCount;
        counts.compareStrongAdjective = counts.strongNegativeAdjectiveCount > counts.strongPositiveAdjectiveCount;
        counts.compareWeakAdjective = counts.weakNegativeAdjectiveCount > counts.weakPositiveAdjectiveCount;
        
        counts.compareWeakToStrong = (counts.weakNegativeCount + counts.weakPositiveCount) > (counts.strongNegativeCount + counts.strongPositiveCount);
        counts.comparePositiveToNegative = (counts.strongPositiveCount + counts.weakPositiveCount) > (counts.strongNegativeCount + counts.weakNegativeCount);
        counts.compareWeakToStrongAdjective = (counts.weakNegativeAdjectiveCount + counts.weakPositiveAdjectiveCount) > (counts.strongNegativeAdjectiveCount + counts.strongPositiveAdjectiveCount);
        counts.comparePositiveToNegativeAdjective = (counts.strongPositiveAdjectiveCount + counts.weakPositiveAdjectiveCount) > (counts.strongNegativeAdjectiveCount + counts.weakNegativeAdjectiveCount);
        
        return counts;
    }
    
    private void addFeatures(Counts counts1, Counts counts2, List<Double> features) {
        
        features.add(counts1.compareStrong && counts2.compareStrong ? 1.0 : 0.0);
        features.add(counts1.compareStrongAdjective && counts2.compareStrongAdjective ? 1.0 : 0.0);
        features.add(counts1.compareWeak && counts2.compareWeak ? 1.0 : 0.0);
        features.add(counts1.compareWeakAdjective && counts2.compareWeakAdjective ? 1.0 : 0.0);
        
        features.add(counts1.comparePositiveToNegative && counts2.comparePositiveToNegative ? 1.0 : 0.0);
        features.add(counts1.comparePositiveToNegativeAdjective && counts2.comparePositiveToNegativeAdjective ? 1.0 : 0.0);
        features.add(counts1.compareWeakToStrong && counts2.compareWeakToStrong ? 1.0 : 0.0);
        features.add(counts1.compareWeakToStrongAdjective && counts2.compareWeakToStrongAdjective ? 1.0 : 0.0);
    }
    
    private void addFeatures(Counts counts, List<Double> features) {

        features.add(counts.strongPositiveCount > 0 ? 1.0 : 0.0);
        features.add(counts.strongNegativeCount > 0 ? 1.0 : 0.0);
        features.add(counts.weakPositiveCount > 0 ? 1.0 : 0.0);
        features.add(counts.weakNegativeCount > 0 ? 1.0 : 0.0);

        features.add(counts.strongPositiveAdjectiveCount > 0 ? 1.0 : 0.0);
        features.add(counts.strongNegativeAdjectiveCount > 0 ? 1.0 : 0.0);
        features.add(counts.weakPositiveAdjectiveCount > 0 ? 1.0 : 0.0);
        features.add(counts.weakNegativeAdjectiveCount > 0 ? 1.0 : 0.0);

        features.add(counts.compareStrong ? 1.0 : 0.0);
        features.add(counts.compareWeak ? 1.0 : 0.0);
        features.add(counts.compareStrongAdjective ? 1.0 : 0.0);
        features.add(counts.compareWeakAdjective ? 1.0 : 0.0);

        features.add(counts.compareWeakToStrong ? 1.0 : 0.0);
        features.add(counts.comparePositiveToNegative ? 1.0 : 0.0);
        features.add(counts.compareWeakToStrongAdjective ? 1.0 : 0.0);
        features.add(counts.comparePositiveToNegativeAdjective ? 1.0 : 0.0);
    }
    
    private class Counts {
        int strongPositiveCount = 0;
        int strongNegativeCount = 0;
        int weakPositiveCount = 0;
        int weakNegativeCount = 0;

        int strongPositiveAdjectiveCount = 0;
        int strongNegativeAdjectiveCount = 0;
        int weakPositiveAdjectiveCount = 0;
        int weakNegativeAdjectiveCount = 0;
        
        boolean compareStrong = false;
        boolean compareWeak = false;
        boolean compareStrongAdjective = false;
        boolean compareWeakAdjective = false;
        
        boolean compareWeakToStrong = false;
        boolean comparePositiveToNegative = false;
        boolean compareWeakToStrongAdjective = false;
        boolean comparePositiveToNegativeAdjective = false;
    }
}
