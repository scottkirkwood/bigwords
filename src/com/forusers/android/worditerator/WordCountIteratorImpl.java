package com.forusers.android.worditerator;

public class WordCountIteratorImpl implements WordIterator {
    public WordCountIteratorImpl() {
        index = 0;
    }
    
    public boolean open(String fname) {
        index = 0;
        return true;
    }

    public void close() {
    }

    public String nextWord() {
        index++;
        return String.format("Word%03d", index);            
    }

    public void gotoIndex(int word) {
        index = word;
    }

    public int getWordIndex() {
        return index;
    }

    private int index;
}
