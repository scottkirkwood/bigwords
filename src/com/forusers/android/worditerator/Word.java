package com.forusers.android.worditerator;

public class Word {
    public Word() {
    }
    
    public Word setWord(String curWord) {
        word = curWord;
        return this;
    }
    
    public String getWord() {
        return word;
    }
    
    /**
     * How much extra time to give to this word?
     * 
     * @return Returns the addtional time in milliseconds.
     */
    public int extraMsDelay() {
        return 0;
    }
    
    private String word;
}
