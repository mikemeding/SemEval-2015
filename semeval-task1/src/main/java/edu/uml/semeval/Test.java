package edu.uml.semeval;

import java.io.IOException;

import edu.uml.semeval.featureextraction.BaseModFeatureExtractor;

public class Test {

    public static void main(String[] args) throws IOException {
        
        SemEvalData parsedData = new SemEvalData(SemEvalData.TRAINING_DATA_FILE);
        
//        Data data = parsedData.getData().get(4);
//
//        System.out.println(data.getOrigsent());
//        System.out.println(data.getCandsent());
//        System.out.println(data.getTrendname());
//        
//        new BaseModFeatureExtractor().extractFeatures(data);
    }
}
