package edu.uml.semeval;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.lucene.wordnet.SynonymMap;

import edu.uml.lexicon.HarvardInquirer;
import edu.uml.semeval.featureextraction.BaseModFeatureExtractor;
import edu.uml.semeval.featureextraction.FeatureExtractor;
import edu.uml.semeval.featureextraction.HarvardInquirerFeatureExtraction;
import edu.uml.semeval.featureextraction.WordnetSynonymFeatureExtractor;

public class Test {

    public static void main(String[] args) throws IOException {
        
        SemEvalData parsedData = new SemEvalData(SemEvalData.TRAINING_DATA_FILE);
        
//        Data data = parsedData.getData().get(4);
//
//        System.out.println(data.getOrigsent());
//        System.out.println(data.getCandsent());
//        System.out.println(data.getTrendname());
//        System.out.println(data.getOrigsenttag());
        
//        String[] splitTags = data.getOrigsenttag().split(" ");
//        
//        for(String tags: splitTags) {
//            System.out.println(tags);
//        }
        
//        FeatureExtractor fe = new BaseModFeatureExtractor();
//        HarvardInquirerFeatureExtraction fe = new HarvardInquirerFeatureExtraction(new HarvardInquirer("resources/Harvard_inquirer/inqtabs.txt"));
        WordnetSynonymFeatureExtractor fe = new WordnetSynonymFeatureExtractor(new SynonymMap(new FileInputStream("resources/Wordnet/prolog/wn_s.pl")));
        
//        int[] mutualCount = new int[HarvardInquirerFeatureExtraction.CATEGORIES_TO_LOOK_FOR.length];
//        int[] exclusiveCount = new int[HarvardInquirerFeatureExtraction.CATEGORIES_TO_LOOK_FOR.length];
//        int[] orCount = new int[HarvardInquirerFeatureExtraction.CATEGORIES_TO_LOOK_FOR.length];
//        
        for(Data data: parsedData.getData()) {
            
            double[] features = fe.extractFeatures(data);
            System.out.println(features[0] + " " + features[1] + " " + features[2] + " " + features[3]);
//            
//            boolean[] origFeatures = fe.extractFeatures(data.getOrigCleanSplit(), data.getOrigArkTweet(), data.getOrigTrendStart(), data.getOrigTrendEnd());
//            boolean[] candFeatures = fe.extractFeatures(data.getCandCleanSplit(), data.getCandArkTweet(), data.getCandTrendStart(), data.getCandTrendEnd());
//            
//            for(int i = 0; i < mutualCount.length; i++) {
//
//                if(origFeatures[i] && candFeatures[i]) {
//                    mutualCount[i]++;
//                }
//                
//                if(origFeatures[i] != candFeatures[i]) {
//                    exclusiveCount[i]++;
//                }
//                
//                if(origFeatures[i] || candFeatures[i]) {
//                    orCount[i]++;
//                }
//            }
//        }
//        
//        for(int i = 0; i < HarvardInquirerFeatureExtraction.CATEGORIES_TO_LOOK_FOR.length; i++) {
//            if(orCount[i] > 3000) {
//                System.out.print(HarvardInquirerFeatureExtraction.CATEGORIES_TO_LOOK_FOR[i] + " ");
//            }
        }
    }
}
