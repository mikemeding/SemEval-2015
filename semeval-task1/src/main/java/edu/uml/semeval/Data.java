package edu.uml.semeval;

public class Data {

    private int trendid;

    private String trendname;

    private String origsent;
    private String candsent;

    private String origsenttag;
    private String candsenttag;
    
    private String[] trendnameCleanSplit;

    private String[] origCleanSplit;
    private String[] candCleanSplit;
    
    private int origTrendStart;
    private int origTrendEnd;
    
    private int candTrendStart;
    private int candTrendEnd;

    private String[] origArkTweet;
    private String[] candArkTweet;

    private boolean paraphrase;

    public Data(String trendid, String trendname, String origsent, String candsent, boolean paraphrase,
            String origsenttag, String candsenttag, String[] trendnameCleanSplit, 
            String[] origCleanSplit, int origTrendStart, int origTrendEnd,
            String[] candCleanSplit, int candTrendStart, int candTrendEnd,
            String[] origArkTweet, String[] candArkTweet) {
        
        this.trendid = Integer.parseInt(trendid);
        this.trendname = trendname;
        this.origsent = origsent;
        this.candsent = candsent;
        this.origsenttag = origsenttag;
        this.candsenttag = candsenttag;

        this.paraphrase = paraphrase;

        this.origCleanSplit = origCleanSplit;
        this.candCleanSplit = candCleanSplit;
        
        this.origArkTweet = origArkTweet;
        this.candArkTweet = candArkTweet;
        
        this.trendnameCleanSplit = trendnameCleanSplit;
        
        this.origTrendStart = origTrendStart;
        this.origTrendEnd = origTrendEnd;
        
        this.candTrendStart = candTrendStart;
        this.candTrendEnd = candTrendEnd;
    }

    public int getTrendid() {
        return trendid;
    }

    public String getTrendname() {
        return trendname;
    }

    public String getOrigsent() {
        return origsent;
    }

    public String getCandsent() {
        return candsent;
    }

    public String getOrigsenttag() {
        return origsenttag;
    }

    public String getCandsenttag() {
        return candsenttag;
    }

    public boolean isParaphrase() {
        return paraphrase;
    }

    public String[] getTrendnameCleanSplit() {
        return trendnameCleanSplit;
    }

    public String[] getOrigCleanSplit() {
        return origCleanSplit;
    }

    public String[] getCandCleanSplit() {
        return candCleanSplit;
    }

    public String[] getOrigArkTweet() {
        return origArkTweet;
    }

    public String[] getCandArkTweet() {
        return candArkTweet;
    }

    public int getOrigTrendStart() {
        return origTrendStart;
    }

    public int getOrigTrendEnd() {
        return origTrendEnd;
    }

    public int getCandTrendStart() {
        return candTrendStart;
    }

    public int getCandTrendEnd() {
        return candTrendEnd;
    }

    @Override
    public String toString() {
        return "Data [trendid=" + trendid + ", trendname=" + trendname + ", origsent=" + origsent
                + ", candsent=" + candsent + ", origsenttag=" + origsenttag + ", candsenttag="
                + candsenttag + ", paraphrase=" + paraphrase + "]";
    }
}
