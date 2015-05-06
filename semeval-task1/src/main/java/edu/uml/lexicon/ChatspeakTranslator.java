package edu.uml.lexicon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mike on 4/14/15.
 */
public class ChatspeakTranslator {

    private Map<String, String> initialismMap;
    private String chatspeakLexiconFile = "semeval-task1/resources/ChatspeakTranslator/NetlingoLexicon.txt";

    public ChatspeakTranslator() {
        try {
            this.initialismMap = new HashMap<>();
            readFile(chatspeakLexiconFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatspeakTranslator(String fileName) {
        try {
            this.initialismMap = new HashMap<>();
            readFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all items in the lexicon into the initalismMap. Lexicon is TSV.
     *
     * @param fileName
     * @throws IOException
     */
    private void readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        String line = null;
        while ((line = br.readLine()) != null) {

            // trim trailing whitespace
            line = line.trim();

            // ignore blank line
            if (line.length() == 0) {
                continue;
            }

            // comment so skip
            if (line.charAt(0) == '#') {
                continue;
            }

            // TSV
            String[] fields = line.split("\t");

            // add lowercase initalism
            String initalism = fields[0].toLowerCase();
            String translation = fields[1];
            initialismMap.put(initalism, translation);

            // add uppercase initalism
            initalism = initalism.toUpperCase();
            initialismMap.put(initalism, translation);

        }
        br.close();
    }

    /**
     * attempt to translate a given single word. null if no translation found.
     *
     * @param word
     * @return a translation or null if not found
     */
    public String translateInitalismWord(String word) {
        return initialismMap.getOrDefault(word, null);
    }

    /**
     * translate an entire line of words
     *
     * @param line
     * @return the translated line of words
     */
    public String translateInitalismLine(String line) {
        StringBuilder sb = new StringBuilder();
        for (String word : line.split(" ")) {
            String translation = translateInitalismWord(word);
            if (translation != null) {
                sb.append(translation);
                sb.append(" ");
            } else {
                sb.append(word);
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * translate an array of words
     *
     * @param array the translated array
     * @return
     */
    public String[] translateInitalismArray(String[] array) {
        for (int x = 0; x < array.length; x++) {
            String translation = translateInitalismWord(array[x]);
            if (translation != null) {
                array[x] = translation; // update item if translation exists
            }
        }
        return array;
    }
}
