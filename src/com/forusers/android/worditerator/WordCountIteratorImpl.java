package com.forusers.android.worditerator;

import android.content.Context;

public class WordCountIteratorImpl implements WordIterator {
    public WordCountIteratorImpl() {
        index = 0;
    }

    @Override
    public boolean open(Context fname, int id) {
        index = 0;
        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public void gotoIndex(int word) {
        index = word;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Word next() {
        index++;
        return word.setWord(String.format("Word%03d", index));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private int index;
    private Word word = new Word();
}
