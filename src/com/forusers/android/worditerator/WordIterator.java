package com.forusers.android.worditerator;

import java.util.Iterator;

import android.content.Context;

public interface WordIterator extends Iterator<Word> {
    public boolean open(Context context, int id);
    public void close();
    public int getIndex();
    public void gotoIndex(int word);
}