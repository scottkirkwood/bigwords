package com.forusers.android.worditerator;

public interface WordIterator {
    public boolean open(String fname);
    public void close();
    public String nextWord();
    public void gotoIndex(int word);
    public int getWordIndex();
}
