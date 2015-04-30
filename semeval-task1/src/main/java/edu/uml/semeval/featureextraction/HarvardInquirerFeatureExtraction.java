package edu.uml.semeval.featureextraction;

import java.util.ArrayList;
import java.util.Map;

import edu.uml.lexicon.HarvardInquirer;
import edu.uml.semeval.Data;

public class HarvardInquirerFeatureExtraction implements FeatureExtractor {

    // All categories
//    public static final String[] CATEGORIES_TO_LOOK_FOR = "Positiv Negativ Pstv Affil Ngtv Hostile Strong Power Weak Submit Active Passive Pleasur Pain Feel Arousal EMOT Virtue Vice Ovrst Undrst Academ Doctrin Econ@ Exch ECON Exprsv Legal Milit Polit@ POLIT Relig Role COLL Work Ritual SocRel Race Kin@ MALE Female Nonadlt HU ANI PLACE Social Region Route Aquatic Land Sky Object Tool Food Vehicle BldgPt ComnObj NatObj BodyPt ComForm COM Say Need Goal Try Means Persist Complet Fail NatrPro Begin Vary Increas Decreas Finish Stay Rise Exert Fetch Travel Fall Think Know Causal Ought Perceiv Compare Eval@ EVAL Solve Abs@ ABS Quality Quan NUMB ORD CARD FREQ DIST Time@ TIME Space POS DIM Rel COLOR Self Our You Name Yes No Negate Intrj IAV DAV SV IPadj IndAdj PowGain PowLoss PowEnds PowAren PowCon PowCoop PowAuPt PowPt PowDoct PowAuth PowOth PowTot RcEthic RcRelig RcGain RcLoss RcEnds RcTot RspGain RspLoss RspOth RspTot AffGain AffLoss AffPt AffOth AffTot WltPt WltTran WltOth WltTot WlbGain WlbLoss WlbPhys WlbPsyc WlbPt WlbTot EnlGain EnlLoss EnlEnds EnlPt EnlOth EnlTot SklAsth SklPt SklOth SklTot TrnGain TrnLoss TranLw MeansLw EndsLw ArenaLw PtLw Nation Anomie NegAff PosAff SureLw If NotLw TimeSpc FormLw".split(" ");
//    public static final String[] CATEGORIES_TO_LOOK_FOR = "Positiv Negativ Hostile Strong Power Weak Active Passive Pleasur Pain Virtue Vice Ovrst Undrst Race".split(" ");
    
    // Categories with more than 3000 hits in either the original or candidate in the training set
    public static final String[] CATEGORIES_TO_LOOK_FOR = "Positiv Negativ Pstv Affil Ngtv Strong Power Active Passive Ovrst SocRel Quan Time@ Space Self IAV DAV SV PowTot EnlTot TimeSpc".split(" ");
    
    private HarvardInquirer harvardInquirer;

    public HarvardInquirerFeatureExtraction(HarvardInquirer harvardInquirer) {
        this.harvardInquirer = harvardInquirer;
    }
    
    public double[] extractFeatures(Data data) {
        
        ArrayList<Double> features = new ArrayList<Double>();
        
        boolean[] origFeatures = extractFeatures(data.getOrigCleanSplit(), data.getOrigArkTweet(), data.getOrigTrendStart(), data.getOrigTrendEnd());
        boolean[] candFeatures = extractFeatures(data.getCandCleanSplit(), data.getCandArkTweet(), data.getCandTrendStart(), data.getCandTrendEnd());
        
        for(boolean feature: origFeatures) {
            features.add(feature? 1.0 : 0.0);
        }
        
        for(boolean feature: candFeatures) {
            features.add(feature? 1.0 : 0.0);
        }
        
        int origFeatureCount = 0;
        int candFeatureCount = 0;
        int mutualFeatureCount = 0;

        for(int i = 0; i < origFeatures.length; i++) {
            // both sentences contain the same feature
//            features.add((origFeatures[i] && candFeatures[i])? 1.0 : 0.0);
            
            if(origFeatures[i]) {
                origFeatureCount++;
            }
            
            if(candFeatures[i]) {
                candFeatureCount++;
            }
            
            if(origFeatures[i] && candFeatures[i]) {
                mutualFeatureCount++;
            }
        }
        
        double precision = 0.0;
        double recall = 0.0;
        double f1 = 0.0;
        
        if(mutualFeatureCount > 0) {
            precision = (double) mutualFeatureCount / origFeatureCount;
            recall = (double) mutualFeatureCount / candFeatureCount;
        }
        
        if(precision + recall > 0.0) {
            f1 = 2 * precision * recall / (precision + recall);
        }
        
        features.add(precision);
        features.add(recall);
        features.add(f1);
        
        double[] featureArray = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            featureArray[i] = features.get(i);
        }
        
        return featureArray;
    }

    protected boolean[] extractFeatures(String[] splitSent, String[] arkTags, int trendStart, int trendEnd) {
        
        boolean[] features = new boolean[CATEGORIES_TO_LOOK_FOR.length];
        
        for(int i = 0; i < splitSent.length; i++) {
            String token = splitSent[i];

            if(i < trendStart || i > trendEnd) {
                setFeatureForToken(token, arkTags[i], features);
            }
        }
        
        return features;
    }
    
    protected void setFeatureForToken(String token, String tag, boolean[] features) {
        for (Map<String, String> entry : harvardInquirer.getEntriesForWord(token)) {
            if (hasTag(tag, entry)) {
                for(int index = 0; index < CATEGORIES_TO_LOOK_FOR.length; index++) {
                    if(entry.containsKey(CATEGORIES_TO_LOOK_FOR[index])) {
                        features[index] = true;
                    } 
                }
            }
        }
    }

    private static boolean hasTag(String tag, Map<String, String> entry) {

        if (!entry.containsKey("Othtags")) return false;

        String[] othtags = entry.get("Othtags").split(" ");
        for (int i = 0; i < othtags.length; i++) {
            othtags[i] = othtags[i].toUpperCase();
        }

        if (tag.equals("A")) {
            // adjective
            for (String othtag : othtags) {
                if (othtag.equals("MODIF")) return true;
            }
        } else if (tag.equals("N")) {
            // noun
            for (String othtag : othtags) {
                if (othtag.equals("NOUN")) return true;
            }
        } else if (tag.equals("V")) {
            // verb
            for (String othtag : othtags) {
                if (othtag.equals("VERB") || othtag.equals("SUPV")) return true;
            }
        } else if (tag.equals("R")) {
            // adverb
            for (String othtag : othtags) {
                if (othtag.equals("LY")) return true;
            }
        } else if (tag.equals("O")) {
            // pronoun
            for (String othtag : othtags) {
                if (othtag.equals("PRON")) return true;
            }
        } else if (tag.equals("P")) {
            // preposition
            for (String othtag : othtags) {
                if (othtag.equals("PREP")) return true;
            }
        } else if (tag.equals("S") || tag.equals("Z")) {
            //
            for (String othtag : othtags) {
                if (othtag.equals("DET")) return true;
            }
        }

        return false;
    }
}
