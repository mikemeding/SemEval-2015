package edu.uml.semeval;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;
//import edu.uml.lexicon.ChatspeakTranslator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SemEvalData {

      public static final String TRAINING_DATA_FILE = "dataset/train.data";
      public static final String DEV_DATA_FILE = "dataset/dev.data";
//    public static final String TRAINING_DATA_FILE = "semeval-task1/dataset/train.data";
//    public static final String DEV_DATA_FILE = "semeval-task1/dataset/dev.data";

    private List<Data> dataset;

    private Tagger tagger;

//    private ChatspeakTranslator chatspeakTranslator = new ChatspeakTranslator("semeval-task1/resources/ChatspeakTranslator/NetlingoLexicon.txt");

    public SemEvalData(Tagger tagger, String dataFilePath, String labelFilePath) throws IOException {
        dataset = new ArrayList<Data>();
        this.tagger = tagger;
        
        BufferedReader br = new BufferedReader(new FileReader(dataFilePath));
        BufferedReader br2 = new BufferedReader(new FileReader(labelFilePath));
        
        String dataLine;
        String labelLine;
        while ((dataLine = br.readLine()) != null && (labelLine = br2.readLine()) != null) {
            String[] split = dataLine.split("\t");
            
            String trendname = split[1];
            String[] trendnameCleanSplit = cleanString(trendname).split(" ");
            
            String origsent = split[2];
//          String origsent = chatspeakTranslator.translateInitalismLine(split[2]);
            String candsent = split[3];
//          String candsent = chatspeakTranslator.translateInitalismLine(split[3]);

            String origClean = cleanString(origsent);
            String candClean = cleanString(candsent);

            String[] origCleanSplit = origClean.split(" ");
            String[] candCleanSplit = candClean.split(" ");

            int origTrendStart = getStartIndex(origCleanSplit, trendnameCleanSplit);
            int origTrendEnd = origTrendStart + trendnameCleanSplit.length - 1;

            int candTrendStart = getStartIndex(candCleanSplit, trendnameCleanSplit);
            int candTrendEnd = candTrendStart + trendnameCleanSplit.length - 1;

            String[] labelSplit = labelLine.split("\t");
            boolean isTrue = labelSplit[0].equals("true");
            boolean isFalse = labelSplit[0].equals("false");
            
            if(isTrue || isFalse) {
                Data datapoint = new Data(split[0], trendname, origsent, candsent, isTrue, split[4],
                        split[6], trendnameCleanSplit,
                        origCleanSplit, origTrendStart, origTrendEnd,
                        candCleanSplit, candTrendStart, candTrendEnd,
                        arkTag(origClean), arkTag(candClean));
                dataset.add(datapoint);
            }
        }
    }
    
    public SemEvalData(Tagger tagger, String filePath) throws IOException {

        dataset = new ArrayList<Data>();
        this.tagger = tagger;

//        tagger.loadModel("semeval-task1/resources/ArkTweet/model.ritter_ptb_alldata_fixed.20130723");

        BufferedReader br = new BufferedReader(new FileReader(filePath));

        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split("\t");

            String trendname = split[1];
//            String trendname = chatspeakTranslator.translateInitalismLine(split[1]);
            String[] trendnameCleanSplit = cleanString(trendname).split(" ");

            String origsent = split[2];
//            String origsent = chatspeakTranslator.translateInitalismLine(split[2]);
            String candsent = split[3];
//            String candsent = chatspeakTranslator.translateInitalismLine(split[3]);

            String origClean = cleanString(origsent);
            String candClean = cleanString(candsent);

            String[] origCleanSplit = origClean.split(" ");
            String[] candCleanSplit = candClean.split(" ");

            int origTrendStart = getStartIndex(origCleanSplit, trendnameCleanSplit);
            int origTrendEnd = origTrendStart + trendnameCleanSplit.length - 1;

            int candTrendStart = getStartIndex(candCleanSplit, trendnameCleanSplit);
            int candTrendEnd = candTrendStart + trendnameCleanSplit.length - 1;

            int count = Integer.parseInt(split[4].substring(1, 2));
            boolean paraphrase = (count >= 3);

            // skip middle
            if (count < 2 || count > 2) {
                Data datapoint = new Data(split[0], trendname, origsent, candsent, paraphrase, split[5],
                        split[6], trendnameCleanSplit,
                        origCleanSplit, origTrendStart, origTrendEnd,
                        candCleanSplit, candTrendStart, candTrendEnd,
                        arkTag(origClean), arkTag(candClean));
//                Data datapoint = new Data(split[0], trendname, origsent, candsent, paraphrase, arkTagToString(origClean),
//                        arkTagToString(candClean), trendnameCleanSplit,
//                        origCleanSplit, origTrendStart, origTrendEnd,
//                        candCleanSplit, candTrendStart, candTrendEnd,
//                        arkTag(origClean), arkTag(candClean));
                dataset.add(datapoint);
            }
        }

        br.close();
    }

    private String arkTagToString(String str){
        String[] arkTagString = arkTag(str);
        StringBuilder sb = new StringBuilder();
        for(String item : arkTagString){
            sb.append(item);
            sb.append(" ");
        }
        return sb.toString();
    }

    private String cleanString(String str) {
        return str.replace("'", "").replace(".", "").replace("-", "").toLowerCase();
    }

    private String[] arkTag(String str) {

        List<TaggedToken> tagged = tagger.tokenizeAndTag(str);

        String[] tags = new String[tagged.size()];

        for (int i = 0; i < tagged.size(); i++) {
            tags[i] = tagged.get(i).tag;
        }

        return tags;
    }

    private int getStartIndex(String[] str, String[] contains) {
        for (int i = 0; i < str.length; i++) {
            boolean found = true;

            for (int j = 0; j < contains.length; j++) {
                if (i + j < str.length) {
                    if (!(str[i + j].contains(contains[j]))) {
                        found = false;
                    }
                }
            }

            if (found) {
                return i;
            }
        }

//        return -1;
        return 1;
    }

    public List<Data> getData() {
        return dataset;
    }
}
