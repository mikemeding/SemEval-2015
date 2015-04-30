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
        addSubjectiveLexiconFeatures(data.getOrigCleanSplit(), data.getOrigArkTweet(), data.getOrigTrendStart(), data.getOrigTrendEnd(), featureList);
        addSubjectiveLexiconFeatures(data.getCandCleanSplit(), data.getCandArkTweet(), data.getCandTrendStart(), data.getCandTrendEnd(), featureList);
        
        double[] features = new double[featureList.size()];
        for (int i = 0; i < featureList.size(); i++) {
            features[i] = featureList.get(i);
        }

        return features;
    }
    
    private void addSubjectiveLexiconFeatures(String[] words, String[] tags, int startTrend, int endTrend, List<Double> features) {

        int strongPositiveCount = 0;
        int strongNegativeCount = 0;
        int weakPositiveCount = 0;
        int weakNegativeCount = 0;

        int strongPositiveAdjectiveCount = 0;
        int strongNegativeAdjectiveCount = 0;
        int weakPositiveAdjectiveCount = 0;
        int weakNegativeAdjectiveCount = 0;

        for (int i = 0; i < words.length; i++) {

            if(i < startTrend || i > endTrend) {
                PartOfSpeech tag = arkTweetNLPTagToSubjectiveLexiconTag.get(tags[i]);
                String token = words[i].replace("#", "");

                for (SubjectiveLexiconEntry entry : subjectiveLexicon.getEntries(tag, token)) {

                    if (entry.isStrongSubjective()) {

                        if (entry.getPolarity() == Polarity.BOTH) {
                            strongPositiveCount++;
                            strongNegativeCount++;
                        } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                            strongNegativeCount++;
                        } else if (entry.getPolarity() == Polarity.POSITIVE) {
                            strongPositiveCount++;
                        }

                        if (tag == PartOfSpeech.ADJECTIVE) {
                            if (entry.getPolarity() == Polarity.BOTH) {
                                strongPositiveAdjectiveCount++;
                                strongNegativeAdjectiveCount++;
                            } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                                strongNegativeAdjectiveCount++;
                            } else if (entry.getPolarity() == Polarity.POSITIVE) {
                                strongPositiveAdjectiveCount++;
                            }
                        }

                    } else {
                        if (entry.getPolarity() == Polarity.BOTH) {
                            weakPositiveCount++;
                            weakNegativeCount++;
                        } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                            weakNegativeCount++;
                        } else if (entry.getPolarity() == Polarity.POSITIVE) {
                            weakPositiveCount++;
                        }

                        if (tag == PartOfSpeech.ADJECTIVE) {
                            if (entry.getPolarity() == Polarity.BOTH) {
                                weakPositiveAdjectiveCount++;
                                weakNegativeAdjectiveCount++;
                            } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                                weakNegativeAdjectiveCount++;
                            } else if (entry.getPolarity() == Polarity.POSITIVE) {
                                weakPositiveAdjectiveCount++;
                            }
                        }
                    }
                }    
            }
        }

        features.add(strongPositiveCount > 0 ? 1.0 : 0.0);
        features.add(strongNegativeCount > 0 ? 1.0 : 0.0);
        features.add(weakPositiveCount > 0 ? 1.0 : 0.0);
        features.add(weakNegativeCount > 0 ? 1.0 : 0.0);

        features.add(strongPositiveAdjectiveCount > 0 ? 1.0 : 0.0);
        features.add(strongNegativeAdjectiveCount > 0 ? 1.0 : 0.0);
        features.add(weakPositiveAdjectiveCount > 0 ? 1.0 : 0.0);
        features.add(weakNegativeAdjectiveCount > 0 ? 1.0 : 0.0);

        features.add(strongNegativeCount > strongPositiveCount ? 1.0 : 0.0);
        features.add(weakNegativeCount > weakPositiveCount ? 1.0 : 0.0);
        features.add(strongNegativeAdjectiveCount > strongPositiveAdjectiveCount ? 1.0 : 0.0);
        features.add(weakNegativeAdjectiveCount > weakPositiveAdjectiveCount ? 1.0 : 0.0);

        features.add((weakNegativeCount + weakPositiveCount) > (strongNegativeCount + strongPositiveCount) ? 1.0 : 0.0);
        features.add((strongPositiveCount + weakPositiveCount) > (strongNegativeCount + weakNegativeCount) ? 1.0 : 0.0);
        features.add((weakNegativeAdjectiveCount + weakPositiveAdjectiveCount) > (strongNegativeAdjectiveCount + strongPositiveAdjectiveCount) ? 1.0 : 0.0);
        features.add((strongPositiveAdjectiveCount + weakPositiveAdjectiveCount) > (strongNegativeAdjectiveCount + weakNegativeAdjectiveCount) ? 1.0 : 0.0);
    }
}
