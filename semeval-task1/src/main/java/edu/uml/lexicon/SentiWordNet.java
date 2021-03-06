package edu.uml.lexicon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SentiWordNet {
    
    private Map<String, Map<String, List<SentiWordNetEntry>>> posMap;
    private Set<String> wordSet;
    
    public SentiWordNet(String fileName) throws IOException {
        posMap = new HashMap<>();
        wordSet = new HashSet<>();
        readFile(fileName);
    }
    
    public boolean containsWord(String word) {
        return wordSet.contains(word);
    }
    
    @SuppressWarnings("unchecked")
    public List<SentiWordNetEntry> getSentiWordNetEntries(String tag, String word) {
        
        Map<String, List<SentiWordNetEntry>> wordMap = posMap.get(tag);
        
        if(wordMap != null && wordMap.get(word) != null) {
            return wordMap.get(word);
        }
        
        return Collections.EMPTY_LIST;
    }
    
    private void readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        
        String line = null;
        while((line = br.readLine()) != null) {
            
            line = line.trim();
            
            if(line.length() == 0) {
                continue;
            }
            
            if(line.charAt(0) == '#') {
                // comment so skip
                continue;
            }
            
            String[] fields = line.split("\t");
            
            String pos = fields[0];
            int id = Integer.parseInt(fields[1]);
            
            double positiveScore = Double.parseDouble(fields[2]);
            double negativeScore = Double.parseDouble(fields[3]);
            
            String synsetTerms = fields[4];
            String gloss = fields[5];
            
            SentiWordNetEntry entry = new SentiWordNetEntry(pos, id, positiveScore, negativeScore, synsetTerms, gloss);
            addToMap(entry);
        }
        
        br.close();
    }
    
    private void addToMap(SentiWordNetEntry entry) {
        
        Map<String, List<SentiWordNetEntry>> wordMap = posMap.get(entry.getPos());
        
        if(wordMap == null) {
            wordMap = new HashMap<>();
            
            posMap.put(entry.getPos(), wordMap);
        }
        
        for(String word: entry.getSynsetTerms().split(" ")) {
            word = word.substring(0, word.length() - 2);
            wordSet.add(word);
            
            List<SentiWordNetEntry> entries = wordMap.get(word);
            if(entries == null) {
                entries = new ArrayList<>();
                
                wordMap.put(word, entries);
            }
            
            entries.add(entry);
        }
    }
}
