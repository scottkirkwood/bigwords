package com.forusers.android.worditerator;

import java.util.Iterator;

public interface WordIterator extends Iterator<Word> {
    public boolean open(String fname);
    public void close();
    public int getIndex();
    public void gotoIndex(int word);
}