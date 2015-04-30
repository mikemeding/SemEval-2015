package edu.uml.semeval.featureextraction;

import org.apache.lucene.wordnet.SynonymMap;

import edu.uml.lexicon.HarvardInquirer;

public class HarvardInquirerWithWordnetSynonymFeatureExtractor extends HarvardInquirerFeatureExtraction {

    private SynonymMap synonymMap;
    
    public HarvardInquirerWithWordnetSynonymFeatureExtractor(HarvardInquirer harvardInquirer, SynonymMap synonymMap) {
        super(harvardInquirer);
        
        this.synonymMap = synonymMap;
    }

    @Override
    protected boolean[] extractFeatures(String[] splitSent, String[] arkTags, int trendStart, int trendEnd) {
        
        boolean[] features = new boolean[CATEGORIES_TO_LOOK_FOR.length];
        
        for(int i = 0; i < splitSent.length; i++) {
            String token = splitSent[i];

            if(i < trendStart || i > trendEnd) {
                setFeatureForToken(token, arkTags[i], features);
                
                for(String synonym: synonymMap.getSynonyms(token)) {
                    setFeatureForToken(synonym, arkTags[i], features);
                }
            }
        }
        
        return features;
    }
}
