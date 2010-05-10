package com.forusers.android.worditerator;

public class WordCountIteratorImpl implements WordIterator {
    public WordCountIteratorImpl() {
        index = 0;
    }

    @Override
    public boolean open(String fname) {
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
    public String next() {
        index++;
        return String.format("Word%03d", index);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private int index;
}
